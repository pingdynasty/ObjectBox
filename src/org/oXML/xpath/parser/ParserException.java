package org.oXML.xpath.parser;

import org.oXML.xpath.XPathException;
import org.oXML.util.Log;

public class ParserException extends XPathException
{
    private String expr;

    public ParserException(String reason){
	super(reason);
    }

    /**
     * @param reason the reason for failure.
     * @param expr the XPath (sub)expression that caused the exception.
     */
    public ParserException(String reason, String expr)
    {
        super(reason, expr);
	this.expr = expr;
    }

    /**
     * @param expr the XPath (sub)expression that caused the exception.
     * @param cause the nested exception.
     */
    public ParserException(String expr, Throwable cause)
    {
        super("invalid expression: "+expr, cause);
	this.expr = expr;
    }

    public void setExpression(String expression){
	this.expr = expr;
    }

    public String getMessage(){
	return "invalid expression: "+expr;
    }
}
/*
 * The Pingdynasty Software License, Version 1
 *
 * Copyright (c) 2000-2002 Pingdynasty / Alpha Plus Technology Ltd.  
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by 
 *        Pingdynasty (http://www.pingdynasty.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "ObjectBox" and "Pingdynasty" must not be used to endorse 
 *    or promote products derived from this software without prior 
 *    written permission.
 *    For written permission, please contact mars@pingdynasty.org.
 *
 * 5. Products derived from this software may not be called "ObjectBox",
 *    nor may "ObjectBox" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
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
