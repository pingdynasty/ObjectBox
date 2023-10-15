package org.oXML.extras.db;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.StringNode;
import org.oXML.type.Function;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public class QuoteFunction extends Function {

    public static final Name NAME = new Name(DatabaseExtensions.DB_NS, "quote");
    public static final Type[] SIGNATURE = new Type[]{org.oXML.type.Node.TYPE};
    public static final char SQUOTE = '\'';

    public QuoteFunction() {
        super(NAME, SIGNATURE);
    }

    public Node invoke(Node node, Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
	return invoke(args, ctxt);
    }

    public Node invoke(Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        assert args.length == 1;
        return new StringNode(quote(args[0].stringValue(), SQUOTE));
    }

    public static Node quote(Node value){
	return new StringNode(quote(value.stringValue(), SQUOTE));
    }

    public static String quote(String arg, char quote){
        if(arg == null || arg.equals(""))
            return ""+quote+quote;

        StringBuffer res = new StringBuffer();
        res.append(quote);
        char[] chars = arg.toCharArray();
        boolean escaped = false;
        for(int i=0; i<chars.length; ++i){
            res.append(chars[i]);
            if(escaped){
                escaped = false;
            }else{
                if(chars[i] == quote)
                    res.append(quote);
                else if(chars[i] == '\\')
                    escaped = true;
            }
        }
        res.append(quote);
        return res.toString();
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
