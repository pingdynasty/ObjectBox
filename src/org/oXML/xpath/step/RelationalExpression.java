package org.oXML.xpath.step;

import org.oXML.type.*;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

/**
 * tbd - how compare a nodeset and boolean ???
 * eg test="/foo/bar < true()" ???
 * or even - how compare two booleans? or two strings - lexicographically??
 * xalan gave me false on 'true() <= false()' and '"abc" <= "abc"'.
 */
public class RelationalExpression implements Expression
{
    public static final int LESS_THAN = 0;
    public static final int LESS_THAN_OR_EQUAL = 1;
    public static final int GREATER_THAN = 2;
    public static final int GREATER_THAN_OR_EQUAL = 3;
    private static final String OPERATIONS[] = {"lt", "lte", "gt", "gte"};

    private Expression lhs;
    private Expression rhs;
    private int op;

    public RelationalExpression(Expression lhs, Expression rhs, 
                                int operation) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = operation;
    }

    public void bind(Resolver ctxt)
        throws ObjectBoxException{
	lhs.bind(ctxt);
	rhs.bind(ctxt);
    }

    public int operation()
    {
        return op;
    }

    public String getOperation()
    {
        return OPERATIONS[op];
    }

    public Node evaluate(RuntimeContext ctxt)
        throws ObjectBoxException{
        Node left = lhs.evaluate(ctxt);
        Node right = rhs.evaluate(ctxt);
        boolean result = false;

        // determine how to compare the two expression results based on their type
        if(left.getType().instanceOf(NodesetNode.TYPE)){
            if(right.getType().instanceOf(NodesetNode.TYPE))
                result = compare(left.getChildNodes(), right.getChildNodes());
            else if(right.getType().instanceOf(NumberNode.TYPE))
                result = compare(left.getChildNodes(), right.numberValue());
            else if(right.getType().instanceOf(StringNode.TYPE))
                result = compare(left.getChildNodes(), right.stringValue());
            else if(right.getType().instanceOf(BooleanNode.TYPE))
                result = compare(left.getChildNodes(), right.booleanValue());
            else
                throw new XPathException("unknown result type "+right.getType().getName());
        }else if(right.getType().instanceOf(NodesetNode.TYPE)){
            if(left.getType().instanceOf(NumberNode.TYPE))
                result = compare(left.numberValue(), right.getChildNodes());
            else if(left.getType().instanceOf(StringNode.TYPE))
                result = compare(left.stringValue(), right.getChildNodes());
            else if(left.getType().instanceOf(BooleanNode.TYPE))
                result = compare(left.booleanValue(), right.getChildNodes());
            else
                throw new XPathException("unknown result type "+left.getType().getName());
        }else{
            // XPath v1.0, section 3.4 Booleans
            // When neither object to be compared is a node-set and the 
            // operator is <=, <. >= or >, then the objects are compared by 
            // converting both objects to numbers and comparing the numbers
            // according to IEEE 754.
            result = compare(left.numberValue(), right.numberValue());
        }
        return BooleanNode.booleanNode(result);
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * true iff there is a node in 'lhs' and a node in 'rhs'
     * such that the result of performing the comparison on 
     * their string values of the two nodes is true.
     */
    private boolean compare(Nodeset lhs, Nodeset rhs)
        throws XPathException{
        return compare(lhs.getIterator(), rhs.getIterator());
    }

    private boolean compare(NodeIterator lhs, NodeIterator rhs)
        throws XPathException{
        for(Node left = lhs.nextNode(); left != null; left = lhs.nextNode()){
            String lvalue = left.stringValue();
            if(lvalue != null){
                for(Node right = rhs.nextNode(); right != null; 
                    right = rhs.nextNode()){
                    if(compare(lvalue, right.stringValue()))
                       return true;
                }
            }
        }
        return false;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * true iff there is a node such that the result of performing
     * the comparison on the number to be compared and the result
     * of converting the string value of that node to a number
     * using the number function is true.
     */
    private boolean compare(Nodeset lhs, double rhs)
        throws XPathException{
        return compare(lhs.getIterator(), rhs);
    }

    private boolean compare(NodeIterator lhs, double rhs)
        throws XPathException{
        for(Node left = lhs.nextNode(); left != null; 
            left = lhs.nextNode()){
            double value = StringNode.numberValue(left.stringValue());
            if(compare(value, rhs))
                return true;
        }
        return false;
    }

    private boolean compare(double lhs, Nodeset rhs)
        throws XPathException{
        return compare(lhs, rhs.getIterator());
    }

    private boolean compare(double lhs, NodeIterator rhs)
        throws XPathException{
        for(Node right = rhs.nextNode(); right != null; 
            right = rhs.nextNode()){
            double value = StringNode.numberValue(right.stringValue());
            if(compare(lhs, value))
                return true;
        }
        return false;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * true iff there is a node such that the result of performing
     * the comparison on the string value of the node and the
     * other string is true.
     */
    private boolean compare(Nodeset lhs, String rhs)
        throws XPathException{
        return compare(lhs.getIterator(), rhs);
    }

    private boolean compare(NodeIterator lhs, String rhs)
        throws XPathException{
        for(Node left = lhs.nextNode(); left != null; 
            left = lhs.nextNode()){
            String value = left.stringValue();
            if(compare(value, rhs))
                return true;
        }
        return false;
    }

    private boolean compare(String lhs, Nodeset rhs)
        throws XPathException{
        return compare(lhs, rhs.getIterator());
    }

    private boolean compare(String lhs, NodeIterator rhs)
        throws XPathException{
        for(Node right = rhs.nextNode(); right != null; 
            right = rhs.nextNode()){
            String value = right.stringValue();
            if(compare(rhs, value))
                return true;
        }
        return false;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * true iff the result of performing the comparison on the
     * boolean and on the result of converting the nodeset to a
     * boolean using the boolean function is true.
     */
    private boolean compare(Nodeset lhs, boolean rhs)
        throws XPathException{
        // how compare two booleans???
        throw new XPathException("cannot perform comparison 'nodeset"+getOperation()+"boolean");
//          switch(op){
//          case LESS_THAN:
//              return left < rhs;
//          case LESS_THAN_OR_EQUAL:
//              return  left <= rhs;
//          case GREATER_THAN:
//              return left > rhs;
//          case GREATER_THAN_OR_EQUAL:
//              return left >= rhs;
//          default:
//              throw new XPathException("unknown operation type!");
//          }
    }

    private boolean compare(boolean lhs, Nodeset rhs)
        throws XPathException{
        // how compare two booleans???
        throw new XPathException("cannot perform comparison 'nodeset"+getOperation()+"boolean");
    }

    private boolean compare(String lhs, String rhs)
        throws XPathException
    {
        // what to do? compare lexicographically or turn into numbers?
        int result = lhs.compareTo(rhs);
        switch(op){
        case LESS_THAN:
            return result < 0;
        case LESS_THAN_OR_EQUAL:
            return result <= 0;
        case GREATER_THAN:
            return result > 0;
        case GREATER_THAN_OR_EQUAL:
            return result >= 0;
        default:
            throw new XPathException("unknown operation type!");
        }
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * Numbers are compared for equality according to IEEE 754.
     */
    private boolean compare(double left, double right)
        throws XPathException
    {
        switch(op){
        case LESS_THAN:
            return left < right;
        case LESS_THAN_OR_EQUAL:
            return left <= right;
        case GREATER_THAN:
            return left > right;
        case GREATER_THAN_OR_EQUAL:
            return left >= right;
        default:
            throw new XPathException("unknown operation type!");
        }
    }

    public String toString()
    {
        return getClass().getName()+"["+lhs+getOperation()+rhs+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
 	atts.addAttribute("", "name", "name", "CDATA", getOperation());
	handler.startElement("", "operation", "operation", atts);
	atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement("", "param", "param", atts);
	lhs.write(handler);
	handler.endElement("", "param", "param");
	handler.startElement("", "param", "param", atts);
	rhs.write(handler);
	handler.endElement("", "param", "param");
	handler.endElement("", "operation", "operation");
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
