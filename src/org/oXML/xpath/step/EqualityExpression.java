package org.oXML.xpath.step;

import org.oXML.type.*;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

public class EqualityExpression implements Expression{
    private Expression lhs;
    private Expression rhs;
    private boolean notequals;

    public EqualityExpression(Expression lhs, Expression rhs, 
                              boolean notequals){
        this.lhs = lhs;
        this.rhs = rhs;
        this.notequals = notequals;
    }

    public EqualityExpression(Expression lhs, Expression rhs){
        this(lhs, rhs, false);
    }

    public void bind(Resolver ctxt)
        throws ObjectBoxException{
	lhs.bind(ctxt);
	rhs.bind(ctxt);
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
        }else if(left.getType().instanceOf(BooleanNode.TYPE)){
            if(right.getType().instanceOf(NodesetNode.TYPE))
                result = compare(right.getChildNodes(), left.booleanValue());
            else if(right.getType().instanceOf(BooleanNode.TYPE))
                result = compare(left.booleanValue(), right.booleanValue());
            else
// If at least one object to be compared is a boolean, then each object to be compared is converted to a boolean as if by applying the boolean function.
                result = compare(left.booleanValue(), right.booleanValue());
        }else if(left.getType().instanceOf(NumberNode.TYPE)){
            if(right.getType().instanceOf(NodesetNode.TYPE))
                result = compare(right.getChildNodes(), left.numberValue());
            else if(right.getType().instanceOf(NumberNode.TYPE))
                result = compare(left.numberValue(), right.numberValue());
            else if(right.getType().instanceOf(BooleanNode.TYPE))
                result = compare(left.booleanValue(), right.booleanValue());
            else
// Otherwise, if at least one object to be compared is a number, then each object to be compared is converted to a number as if by applying the number function.
                result = compare(left.numberValue(), right.numberValue());
        }else if(left.getType().instanceOf(StringNode.TYPE)){
            if(right.getType().instanceOf(NodesetNode.TYPE))
                result = compare(right.getChildNodes(), left.stringValue());
            else if(right.getType().instanceOf(StringNode.TYPE))
                result = compare(left.stringValue(), right.stringValue());
            else if(right.getType().instanceOf(BooleanNode.TYPE))
                result = compare(left.booleanValue(), right.booleanValue());
            else if(right.getType().instanceOf(NumberNode.TYPE))
                result = compare(left.numberValue(), right.numberValue());
            else
// Otherwise, both objects to be compared are converted to strings as if by applying the string function.
                result = compare(left.stringValue(), right.stringValue());
        }else
            throw new XPathException("unknown result type: "+left.getType().getName());

        return BooleanNode.booleanNode(notequals ^ result);
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * true iff there is a node in 'lhs' and a node in 'rhs'
     * such that the result of performing the comparison on 
     * their string values of the two nodes is true.
     */
    private boolean compare(Nodeset lhs, Nodeset rhs){
        return compare(lhs.getIterator(), rhs.getIterator());
    }

    private boolean compare(NodeIterator lhs, NodeIterator rhs){
        for(Node left = lhs.nextNode(); left != null; 
            left = lhs.nextNode()){
            String lvalue = left.stringValue();
            if(lvalue != null){
                for(Node right = rhs.nextNode(); right != null; 
                    right = rhs.nextNode()){
                    String rvalue = right.stringValue();
                    if(compare(lvalue, rvalue))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * true iff the result of performing the comparison on the
     * boolean and on the result of converting the nodeset to a
     * boolean using the boolean function is true.
     */
    private boolean compare(Nodeset lhs, boolean rhs){
        return (!lhs.isEmpty()) == rhs;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * true iff there is a node such that the result of performing
     * the comparison on the number to be compared and the result
     * of converting the string value of that node to a number
     * using the number function is true.
     */
    private boolean compare(Nodeset lhs, double rhs){
        return compare(lhs.getIterator(), rhs);
    }
    private boolean compare(NodeIterator lhs, double rhs){
        for(Node left = lhs.nextNode(); left != null; 
            left = lhs.nextNode()){
            double value = StringNode.numberValue(left.stringValue());
            if(compare(value, rhs))
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
    private boolean compare(Nodeset lhs, String rhs){
        return compare(lhs.getIterator(), rhs);
    }

    private boolean compare(NodeIterator lhs, String rhs){
        for(Node left = lhs.nextNode(); left != null;
            left = lhs.nextNode()){
            String value = left.stringValue();
            if(compare(value, rhs))
                return true;
        }
        return false;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * Numbers are compared for equality according to IEEE 754.
     */
    private boolean compare(double lhs, double rhs)
    {
        return lhs == rhs;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * Two booleans are equal if either both are true or both are false.
     */
    private boolean compare(boolean lhs, boolean rhs)
    {
        return lhs == rhs;
    }

    /**
     * XPath v1.0, section 3.4 Booleans
     * Two strings are equal if they consist of the same sequence of UCS 
     * characters.
     */
    private boolean compare(String lhs, String rhs)
    {
        return lhs.equals(rhs);
    }

    public String toString()
    {
        return getClass().getName()+(notequals ? "<notequals>" : "<equals>")+"["+lhs+","+rhs+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
 	atts.addAttribute("", "name", "name", "CDATA", 
			  notequals ? "notequals" : "equals");
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
