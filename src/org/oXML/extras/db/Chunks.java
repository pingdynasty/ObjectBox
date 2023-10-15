package org.oXML.extras.db;

import java.util.List;
import java.util.ArrayList;
import org.oXML.util.Log;

public class Chunks {

    private List items;
    
    public Chunks()
    {
        items = new ArrayList();
    }
    
    public void add(String chunk)
    {
        if(chunk != null && !chunk.equals(""))
            items.add(chunk);
    }
    
    public void add(Object chunk)
    {
        if(chunk != null)
            items.add(chunk);
    }

    public Object[] toArray()
    {
        return items.toArray();
    }

    public String toString()
    {
        StringBuffer result = new StringBuffer();
        Object[] chunks = items.toArray();
        for(int i=0; i<chunks.length; ++i)
            result.append(chunks[i].toString());
        return result.toString();
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
