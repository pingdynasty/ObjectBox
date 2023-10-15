package org.oXML.xpath.filter;

import org.oXML.type.*;
import org.oXML.xpath.step.NodeType;
import org.oXML.util.Log;

/**
 * implementation of NodeFilter that filters out nodes
 * with matching type
 */
public class NodeTypeFilter implements NodeFilter
{
    private int type;

    public NodeTypeFilter(int type)
    {
        this.type = type;
    }

    public boolean acceptNode(Node n){
        switch(type){
            case NodeType.NODE:
                return true;
            case NodeType.TEXT:
                return n.getType().instanceOf(StringNode.TYPE);
            case NodeType.PROCESSING_INSTRUCTION:
                return n.getType().instanceOf(ProcessingInstructionNode.TYPE);
            case NodeType.COMMENT:
                return n.getType().instanceOf(CommentNode.TYPE);
            default :
                Log.error("unknown nodetype: "+n);
                throw new RuntimeException("unknown nodetype: "+n);
        }
    }

    public String toString()
    {
        return getClass().getName()+"["+NodeType.typeName(type)+"]";
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
