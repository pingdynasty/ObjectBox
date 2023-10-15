package org.oXML.xpath.iterator;

import org.oXML.xpath.filter.NodeFilter;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NumberNode;
import org.oXML.type.NodeIterator;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.Expression;
import org.oXML.xpath.iterator.SingleNodeIterator;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/**
 */
public class PredicateIterator implements NodeIterator{

    private Nodeset parent;
    private NodeIterator iterator;
    private Expression predicate;
    private RuntimeContext context;
    private static final int whatToShow = NodeFilter.SHOW_ALL;
    private boolean done;
    private int position = 0;

    public PredicateIterator(Nodeset parent, Expression predicate,
                             RuntimeContext context){
        this.parent = parent;
        this.predicate = predicate;
        this.context = context;
        this.done = false;
        this.iterator = parent.getIterator();
    }

    public Node nextNode(){
        if(done)
            return null;

        Node next = iterator.nextNode();
        if(next == null){
            done = true;
            return null;
        }

// XPath 1.0
// Several kinds of expressions change the context node; only predicates change the context position and context size
        Node saved = context.getContextNode();
        context.setContextSize(parent.size());
        // we set the context to be a copy of our nodeset so that iterating 
        // over it won't change the result we return
        context.setContextNode(next); // tbd FIXME!!! why copy???
//          context.setContextNode(next.copy());
        context.setContextPosition(iterator.position());
        try{
            Node result = predicate.evaluate(context);
            // if result of expr is number, use as position
            // otherwise convert result to boolean
            if(result.getType().instanceOf(NumberNode.TYPE)){
                done = true;
                int index = (int)result.numberValue();
                position = 1;
                return parent.getNode(index-1);
            }

            if(result.booleanValue()){
                ++position;
                return next;
            }else
                return nextNode();

        }catch(ObjectBoxException exc){
            Log.exception(exc);
            exc.printStackTrace();
            throw new RuntimeException(exc.toString());
//              return null;
        }finally{
            // reset to saved context node
            context.setContextNode(saved);
        }
    }

    public int position(){
        return position;
    }

//      public NodeFilter getFilter(){
//          return parent.getFilter();
//      }

//      public void setFilter(NodeFilter filter){
//          parent.setFilter(filter);
//      }

//      public void setWhatToShow(int i){
//          parent.setWhatToShow(i);
//      }

//      public int getWhatToShow(){
//          return parent.getWhatToShow();
//      }

    public String toString()
    {
        return super.toString()+"["+parent+","+predicate+","+done+"]";
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
