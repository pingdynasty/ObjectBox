package org.oXML.type;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;

public class Threads {

    // Represents the o:XML Threads in the current VM.
    // Each thread is associated with a RuntimeContext.
    // Threads in the same, multi-threaded Program may share the same output handler.

//     private Program program;

//     public Threads(Program program){
// 	this.program = program;
//     }

    public static class NoContextForThreadException
	extends ObjectBoxException {

	public NoContextForThreadException(){
	    super("no runtime context available for this thread");
	}
    }

    private static ThreadLocal contexts = new ThreadLocal(){
// 	    protected synchronized Object initialValue() {
// 		return new Runtimecontext(program);
// 	    }
	};

    /**
     * Get the RuntimeContext for the calling Thread
     */
    public static RuntimeContext getContext()
	throws NoContextForThreadException {
	RuntimeContext rc = (RuntimeContext)contexts.get();
	if(rc == null)
	    throw new NoContextForThreadException();
	return rc;
    }

    /**
     * Creates a RuntimeContext for the calling Thread
     */
    public static RuntimeContext createContext(Program program){
	RuntimeContext rc = new RuntimeContext(program);
	contexts.set(rc);
	return rc;
    }

    /**
     * Set the RuntimeContext for the calling Thread
     */
    public static void setContext(RuntimeContext context){
	contexts.set(context);
    }
}