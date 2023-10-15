package org.oXML.xpath.iterator;

import org.oXML.type.*;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.util.Log;

/**
 * NodeIterator that shows nodes that:
 * a) are presented by the parent NodeIterator (set at construction time)
 * b) are accepted by the NodeFilter (set at construction time)
 */
public abstract class SubNodeIterator implements NodeIterator{

    protected NodeIterator parent;
    private NodeFilter filter;
    private int whatToShow;
    private Node current = null;
    private int position = 0;

    /**
     * construct a SubNodeIterator returning all types of nodes (SHOW_ALL)
     */
    public SubNodeIterator(NodeIterator parent, NodeFilter filter){
        this(parent, filter, NodeFilter.SHOW_ALL);
    }

    /**
     * construct a SubNodeIterator returning only the specified type of node
     * @see org.w3c.dom.traversal.NodeFilter
     */
    public SubNodeIterator(NodeIterator parent, NodeFilter filter, int whatToShow){
        this.parent = parent;
        this.filter = filter;
        this.whatToShow = whatToShow;
    }

//      /** 
//       * copy ctor
//       */
//      protected SubNodeIterator(SubNodeIterator other)
//      {
//          super(other);
//          parent = other.parent;
//          filter = other.filter;
//          whatToShow = other.whatToShow;
//          current = other.current;
//      }

    public int position(){
        return position;
    }

    public void setWhatToShow(int whatToShow){
        this.whatToShow = whatToShow;
    }

    public final Node nextNode(){
        do
            // increment
            current = getNextNode();
        // until we find one we like
        while(current != null && !allowed(current));

        ++position;
        return current;
    }

    /**
     * overload to create a SubNodeIterator that has a different iteration
     * pattern than the parent NodeIterator.
     */
    abstract protected Node getNextNode();

    public int getWhatToShow(){
        return whatToShow;
    }

    public NodeFilter getFilter(){
        return filter;
    }

    public void setFilter(NodeFilter filter){
        this.filter = filter;
    }

    private boolean allowed(Node node){
        // what to show takes precedence over filter
        switch(whatToShow){
            case NodeFilter.SHOW_ALL:
                // fall through
                break;
            case NodeFilter.SHOW_ELEMENT:
                if(!node.getType().instanceOf(ElementNode.TYPE))
                    return false;
                break;
            case NodeFilter.SHOW_ATTRIBUTE:
                if(!node.getType().instanceOf(AttributeNode.TYPE))
                    return false;
                break;
            case NodeFilter.SHOW_TEXT:
                if(!node.getType().instanceOf(StringNode.TYPE))
                    return false;
                break;
            case NodeFilter.SHOW_CDATA_SECTION:
                if(!node.getType().instanceOf(StringNode.TYPE))
                    return false;
                break;
            case NodeFilter.SHOW_DOCUMENT:
                if(!node.getType().instanceOf(DocumentNode.TYPE))
                    return false;
                break;
//          case NodeFilter.SHOW_ENTITY:
//              if(!node.getType().instanceOf(Node.ENTITY_EXPRESSION))
//                  return false;
//              break;
//          case NodeFilter.SHOW_ENTITY_REFERENCE:
//              if(!node.getType().instanceOf(Node.ENTITY_REFERENCE_EXPRESSION))
//                  return false;
//              break;
	case NodeFilter.SHOW_PROCESSING_INSTRUCTION:
	    if(!node.getType().instanceOf(ProcessingInstructionNode.TYPE))
		return false;
	    break;
        case NodeFilter.SHOW_COMMENT:
            if(!node.getType().instanceOf(CommentNode.TYPE))
                return false;
            break;
//          case NodeFilter.SHOW_DOCUMENT_TYPE:
//              if(!node.getType().instanceOf(Node.DOCUMENT_TYPE_EXPRESSION))
//                  return false;
//              break;
//              case NodeFilter.SHOW_NOTATION:
//                  if(!node.getType().instanceOf(Node.NOTATION_EXPRESSION))
//                      return false;
//                  break;
            default:
                throw new RuntimeException("unhandled node type: "+node.getType());
        }

        // now check if the filter likes it
        try{
            // tbd exception handling
            return filter.acceptNode(node);
        }catch(Exception exc){
            Log.exception(exc);
            return false;
        }
    }

    public String toString(){
        return getClass().getName()+"["+parent+","+filter+","+current+"]";
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
