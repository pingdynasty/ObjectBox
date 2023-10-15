package org.oXML.type;

import org.oXML.util.Log;
import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

public class FunctionLoader {

    public static void load(String type) {
	try {
	    ResourceBundle rb =
		ResourceBundle.getBundle("org.oXML.type."+type);
	    
	    Enumeration keys = rb.getKeys();
            
	    while (keys.hasMoreElements()) {
		String key = (String)keys.nextElement();
// 		String value = rb.getString(key);
		if(!key.equals("parent"))
		    Class.forName(key);
	    }
	}catch(NoSuchElementException exc) {
            exc.printStackTrace();
            System.err.println("Syntax error in resource bundle, loading type: "+type);
	}catch(ClassNotFoundException exc) {
            exc.printStackTrace();
            System.err.println("Syntax error in resource bundle, loading type: "+type);
        }catch(MissingResourceException exc) {
            exc.printStackTrace();
            System.err.println("Initialization error: " + exc.toString()
			       +", loading type: "+type);
        }
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
