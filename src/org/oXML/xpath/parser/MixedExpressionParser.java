package org.oXML.xpath.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.oXML.xpath.Expression;
import org.oXML.xpath.MixedExpression;
import org.oXML.xpath.step.LiteralExpression;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;

public class MixedExpressionParser extends ExpressionParser {

    public Expression parse(String expression)
	throws ParserException {
	List list = new ArrayList();

	StringTokenizer tokenizer = new StringTokenizer(expression, "{}\"\'", true);
	int nTokens = tokenizer.countTokens();

	// implementation initially borrowed from Xalan, adapted for o:XML
	if(nTokens < 2){
	    list.add(new LiteralExpression(expression));  // then do the simple thing
	}else{
	    StringBuffer buffer = new StringBuffer();
	    StringBuffer exprBuffer = new StringBuffer();
	    String t = null;  // base token
	    String lookahead = null;  // next token
	    while (tokenizer.hasMoreTokens()){
		if(lookahead != null){
		    t = lookahead;
		    lookahead = null;
		}else{
		    t = tokenizer.nextToken();
		}
		if(t.length() == 1){
		    switch (t.charAt(0)){
		    case ('\"') :
		    case ('\'') : {
			// just keep on going, since we're not in an attribute template
			buffer.append(t);
			break;
		    }
		    case ('{') : {
			// Attribute Value Template start
			lookahead = tokenizer.nextToken();
			if (lookahead.equals("{")){
			    // Double curlys mean escape to show curly
			    buffer.append(lookahead);
			    lookahead = null;
			    break;  // from switch
			}
			/*
			  else if(lookahead.equals("\"") || lookahead.equals("\'"))
			  {
			  // Error. Expressions can not begin with quotes.
			  error = "Expressions can not begin with quotes.";
			  break; // from switch
			  }
			*/
			else {
			    if (buffer.length() > 0){
				list.add(new LiteralExpression(buffer.toString()));
				buffer.setLength(0);
			    }
			    exprBuffer.setLength(0);
			    while (null != lookahead){
				if(lookahead.length() == 1){
				    switch (lookahead.charAt(0)){
				    case '\'' :
				    case '\"' : {
					// String start
					exprBuffer.append(lookahead);
					String quote = lookahead;
					// Consume stuff 'till next quote
					lookahead = tokenizer.nextToken();
					while (!lookahead.equals(quote)) {
					    exprBuffer.append(lookahead);
					    lookahead = tokenizer.nextToken();
					}
					exprBuffer.append(lookahead);
					lookahead = tokenizer.nextToken();
					break;
				    }
				    case '{' : {
					// What's another curly doing here?
					throw new RuntimeException("Error: Can not have \"{\" within expression.");
				    }
				    case '}' : {
					// Proper close of attribute template.
					// Evaluate the expression.
					buffer.setLength(0);
					list.add(super.parse(exprBuffer.toString()));
					lookahead = null;  // breaks out of inner while loop
					break;
				    }
				    default : {
					// part of the template stuff, just add it.
					exprBuffer.append(lookahead);
					lookahead = tokenizer.nextToken();
				    }
				    }  // end inner switch
				}  // end if lookahead length == 1
				else {
				    // part of the template stuff, just add it.
				    exprBuffer.append(lookahead);
				    lookahead = tokenizer.nextToken();
				}
			    }  // end while(!lookahead.equals("}"))
			}
			break;
		    }
		    case ('}') : {
			lookahead = tokenizer.nextToken();
			if (lookahead.equals("}")) {
			    // Double curlys mean escape to show curly
			    buffer.append(lookahead);
			    lookahead = null;  // swallow
			}else{
			    // Illegal, I think...
			    throw new RuntimeException("Found \"}\" but no attribute template open!");
			}
			buffer.append("}");
			// leave the lookahead to be processed by the next round.
			break;
		    }
		    default : {
			// Anything else just add to string.
			buffer.append(t);
		    }
		    }  // end switch t
		}  // end if length == 1
		else{
		    // Anything else just add to string.
		    buffer.append(t);
		}
	    }  // end while(tokenizer.hasMoreTokens())
	    if (buffer.length() > 0) {
		list.add(new LiteralExpression(buffer.toString()));
		buffer.setLength(0);
	    }
	}
	// } // end else nTokens > 1
	return new MixedExpression(list);
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
