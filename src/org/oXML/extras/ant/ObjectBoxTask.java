package org.oXML.extras.ant;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.oXML.util.Log;
import org.oXML.type.Program;
import org.oXML.engine.InterpretedProgram;
import org.oXML.engine.ResultHandler;
import org.oXML.engine.StreamResultHandler;
import org.oXML.ObjectBoxException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.util.Iterator;
import org.oXML.type.Name;
import org.oXML.type.StringNode;
import org.oXML.engine.*;
import org.apache.tools.ant.types.Parameter;

public class ObjectBoxTask extends Task {

    private AntClassLoader loader;
    private List fileSets;
    private List params;
    private File destDir;
    private File inFile;
    private File outFile;
    private String suffix;
    private String encoding;
    private int mode = RUN_MODE;
    private List exts;
    private boolean defaultExtensions = true;
    private boolean sml = false;
    private static ProgramFactory factory;
    private static final String DEFAULT_SUFFIX = ".xml";

    private static final int RUN_MODE = 1;
    private static final int UNIT_TEST_MODE = 2;
    private static final int COMPILATION_MODE = 3;

    /** Classpath to use when trying to load the ObjectBox */
    private Path classpath = null;

    public ObjectBoxTask()
	throws ObjectBoxException{
	fileSets = new ArrayList();
	params = new ArrayList();
	exts = new ArrayList();
	Log.timestamp(false);
    }

    public void addConfiguredFileSet(FileSet set){
	fileSets.add(set);
    }

    public void setLoglevel(String level){
	if(level == null || level.equalsIgnoreCase("info"))
	    Log.setLevel(Log.INFO_LEVEL);
	else if(level.equalsIgnoreCase("debug"))
	    Log.setLevel(Log.DEBUG_LEVEL);
	else if(level.equalsIgnoreCase("warning"))
	    Log.setLevel(Log.WARNING_LEVEL);
	else if(level.equalsIgnoreCase("error"))
	    Log.setLevel(Log.ERROR_LEVEL);
	else
	    throw new BuildException("invalid log level: "+level+". valid levels are info, debug, warning or error.");
    }

    public void setEncoding(String encoding) {
	this.encoding = encoding;
    }

    public void setMode(String mode) 
        throws BuildException {
        if(mode.equalsIgnoreCase("test"))
            this.mode = UNIT_TEST_MODE;
        else if(mode.equalsIgnoreCase("run"))
            this.mode = RUN_MODE;
        else if(mode.equalsIgnoreCase("compile"))
            this.mode = COMPILATION_MODE;
        else
	    throw new BuildException("invalid mode: "+mode+". valid modes are run, test or compile");
    }

    public void setDefaultExtensions(boolean defaultExtensions) {
	this.defaultExtensions = defaultExtensions;
    }

    public void setSML(boolean sml) {
	this.sml = sml;
    }

    public void setDestdir(File destDir) {
	this.destDir = destDir;
    }

    public void setSuffix(String suffix) {
	this.suffix = suffix;
    }

    public void setOut(File outFile) {
	this.outFile = outFile;
    }

    public void setIn(File inFile) {
	this.inFile = inFile;
    }

    public Parameter createParam(){
	Parameter param = new Parameter();
	params.add(param);
	return param;
    }

    public Parameter createExtension(){
	Parameter extension = new Parameter();
	exts.add(extension);
	return extension;
    }

    /**
     * Set the optional classpath
     *
     * @param classpath the classpath to use
     */
    public void setClasspath(Path classpath) {
        createClasspath().append(classpath);
    }

    /**
     * Set the optional classpath
     *
     * @return a path instance to be configured by the Ant core.
     */
    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

    /**
     * Set the reference to an optional classpath to the XSL processor
     *
     * @param r the id of the Ant path instance to act as the classpath
     *          for loading the XSL processor
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * Load named class either via the system classloader or a given
     * custom classloader.
     *
     * @param classname the name of the class to load.
     * @return the requested class.
     * @exception Exception if the class could not be loaded.
     */
    private Class loadClass(String classname) throws Exception {
        Class c = Class.forName(classname, true, loader);
	return c;
    }

    private void resolve()
	throws BuildException {
	if(loader == null) {
	    // create our class loader
            loader = new AntClassLoader(getClass().getClassLoader(), 
					getProject(), classpath, true);
	}
        if(factory == null)
            factory = new ProgramFactory(loader);
        try{
            if(defaultExtensions)
                factory.loadDefaultExtensions();
            for(int i=0; i<exts.size(); ++i){
                Parameter p = (Parameter)exts.get(i);
                factory.loadExtension(p.getName());
            }
	}catch(Throwable exc){
	    Log.exception(exc, "while setting up o:XML task");
	    throw new BuildException("failed to setup o:XML task", exc);
        }
    }

    public void execute(File input, File output)
	throws BuildException {
            String filename = input.getPath();
        try{
            log("input: "+filename);
            // load necessary classes (using the right classloader)
            resolve();
        }catch(Throwable exc){
            exc.printStackTrace();
        }
        try{
	    loader.setThreadContextLoader();
	    InterpretedProgram program;
            switch(mode){
            case RUN_MODE :
                // todo: check if program is already in cache and compiled
                if(sml)
                    program = factory.getSMLProgram(filename);
                else
                    program = factory.getProgram(filename);
                run(program, output);
                break;
            case UNIT_TEST_MODE :
                if(sml)
                    program = factory.getTestSMLProgram(filename);
                else
                    program = factory.getTestProgram(filename);
                run(program, output);
                break;
            case COMPILATION_MODE :
                program = factory.getProgram(filename);
                factory.compile(program);
                break;
            }
	}catch(Throwable exc){
	    Log.exception(exc, "while processing o:XML program: "+filename);
	    throw new BuildException("failed to process o:XML program: "+filename, exc);
        }finally{
	    loader.resetThreadContextLoader();
	}
    }

    public void run(InterpretedProgram program, File output)
        throws Exception {

        if(output != null){
            log("output: "+output);
            // check if we need to create the output directory
            File parent = output.getAbsoluteFile().getParentFile();
            if(parent != null && !parent.exists())
                parent.mkdirs();
        }

        if(!program.compiled())
            factory.compile(program);

        // create output handler
        ResultHandler handler;
        OutputStream out = null;
        if(output == null || program.getContentType().equals(Program.NO_CONTENT_TYPE)){
            handler = new NullResultHandler();
        }else{
            out = new FileOutputStream(output);
            if(encoding == null)
                handler = new StreamResultHandler(out);
            else
                handler = new StreamResultHandler(out, encoding);
        }

        // create runtime context
        RuntimeContext env = new RuntimeContext(program);
        env.addResultHandler(handler);

        // add parameters
        Iterator it = params.iterator();
        while(it.hasNext()){
            Parameter param = (Parameter)it.next();
            Name name = new Name(param.getName());
            env.setVariable(program.createParameter(name, param.getValue()));
        }

        // execute
        Log.trace("executing: "+program.getSystemId());
        program.run(env);
        if(out != null)
            out.close();
    }

    protected File getTargetFile(String in, File dir){
	if(dir == null)
	    dir = getProject().getBaseDir();
	if(!dir.exists())
	    dir.mkdirs();
	String suf = suffix == null ? DEFAULT_SUFFIX : suffix;
	String name = in.substring(0, in.lastIndexOf(".")) + suf;
	return new File(dir, name);
    }

    public void execute() throws BuildException {

	Project project = getProject();

	if(inFile == null){
	    // we should have a nested fileset
	    if(fileSets.isEmpty())
		throw new BuildException("either 'in' or a nested fileset must be specified.");
	    if(outFile != null)
		throw new BuildException("'out' cannot be used with a nested fileset, only with 'in'.");

	    Iterator it = fileSets.iterator();
	    while(it.hasNext()){
		FileSet set = (FileSet)it.next();
		DirectoryScanner scanner = set.getDirectoryScanner(project);
		String[] files = scanner.getIncludedFiles();
		File baseDir = scanner.getBasedir();
		if(destDir == null)
		    destDir = baseDir;
		for(int i = 0; i < files.length; i++) {
		    File in = new File(baseDir, files[i]);
		    File out = getTargetFile(files[i], destDir);
		    execute(in, out);
		}
	    }
	}else{
	    // we've got a single program file
	    if(!fileSets.isEmpty())
		throw new BuildException("only one of 'in' and nested fileset may be specified.");
	    File out = outFile;
	    if(out == null){
		if(destDir != null){
		    out = getTargetFile(inFile.getName(), destDir);
		}else if(suffix != null){
		    File dir = inFile.getAbsoluteFile().getParentFile();
		    out = getTargetFile(inFile.getName(), dir);
		}
		// otherwise we've got a null output, which is fine
	    }else{
		if(destDir != null)
		    throw new BuildException("only one of 'out' and 'destdir' may be specified.");
		if(suffix != null)
		    throw new BuildException("only one of 'out' and 'suffix' may be specified.");
	    }
	    execute(inFile, out);
	}
    }
}
/*
  ObjectBox - o:XML compiler and interpretor
  for more information see http://www.o-xml.org/objectbox
  Copyright (C) 2002/2003 Martin Klang, Alpha Plus Technology Ltd
  email: martin at hack.org

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
