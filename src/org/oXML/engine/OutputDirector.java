package org.oXML.engine;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import org.oXML.ObjectBoxException;
import org.oXML.type.*;
import org.oXML.util.Log;

public class OutputDirector{
    private ResultDirector results;
    private List stack;

    public OutputDirector(){
        stack =  new ArrayList();
        results = new ResultDirector();
        stack.add(results);
    }

    public void addHandler(ResultHandler handler){
        results.addHandler(handler);
    }

//     public Node getContextNode(){
// 	NodeCollector nc = (NodeCollector)stack.get(0);
// 	return nc.getContents();
//     }

    public void setOutputNodeset(Nodeset outputNodeset){
        stack.add(0, new NodeCollector(outputNodeset));
    }

    public void resetOutputNodeset()
        throws ObjectBoxProcessingException{
	getResultDirector().clear();
	stack.remove(0);
//         NodeCollector collector = (NodeCollector)stack.remove(0);
// 	return collector.getContent();
    }

//     /**
//      * clear any cached nodes
//      */
//     public void clear()
//         throws ObjectBoxProcessingException{
// 	getResultDirector().clear();
//     }

    public void output(Node value)
        throws ObjectBoxProcessingException{
        getResultDirector().output(value);
    }

    public void pop(Name name)
        throws ObjectBoxProcessingException{
        getResultDirector().pop(name);
    }

    public void start(String contenttype){
        getResultDirector().start(contenttype);
    }

    public void end()
        throws ObjectBoxProcessingException{
	getResultDirector().clear();
        getResultDirector().end();
    }

    private AbstractResultDirector getResultDirector(){
//          throws ObjectBoxException{
//          if(stack.isEmpty())
//              throw new ObjectBoxException("no resulthandler");
        return (AbstractResultDirector)stack.get(0);
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
