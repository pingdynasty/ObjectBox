package org.oXML.type;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.oXML.util.Log;

/**
 * keeps a map of <code>Function</code> arrays indexed by FunctionKey
 */
public class Functions{

    // map key: FunctionKey, value: Function[]
    private Map functions;
    private Type type;

    private int compare(Function lhs, Function rhs){
// 	Type ltype = lhs.getDeclaringType();
// 	Type rtype = rhs.getDeclaringType();
// 	if(ltype == rtype)
// 	    return 0;
// 	if(ltype == null && rtype == null)
// 	    return 0;
// 	if(ltype == null)
// 	    return 1;
// // 	    throw new RuntimeException("invalid function: "+ltype);
// 	if(rtype == null)
// 	    return -1;
// // 	    throw new RuntimeException("invalid function: "+rtype);
// 	if(ltype.instanceOf(rtype))
// 	    return -1;
// 	if(rtype.instanceOf(ltype))
// 	    return 1;
// 	return 0;
	if(type == null)
	    return 0;
	int ldist = type.distance(lhs.getDeclaringType(), Type.MAX_TYPE_DISTANCE);
	int rdist = type.distance(rhs.getDeclaringType(), Type.MAX_TYPE_DISTANCE);
	return ldist - rdist;
    }

    public Functions(Type type){
	this.type = type;
        this.functions = new HashMap();
    }

    public Functions(Type type, Function[] functions){
        this(type);
        addFunctions(functions);
    }

    public void addFunction(Function function){
// 	throws FunctionException{
        FunctionKey key = function.getKey();
        Function[] match = (Function[])functions.get(key);
        if(match == null){
            match = new Function[]{function};
	    functions.put(key, match);
	}else{
            // extend array to hold the new function too
            Function[] all = new Function[match.length + 1];
	    for(int i=0; i<all.length; ++i){
		if(i == match.length){
		    all[i] = function;
		}else if(match[i] == function){
		    // we've got this function already
		    return;
		}else if(compare(match[i], function) > 0){
		    all[i] = function;
		    function = match[i];
		}else{
		    all[i] = match[i];
		}
	    }
	    functions.put(key, all);
        }
    }

    public boolean contains(Function function){
        Function[] match = (Function[])functions.get(function.getKey());
	if(match == null)
	    return false;
	for(int i=0; i<match.length; ++i)
	    if(match[i] == function)
		return true;
	return false;
    }

    public void addFunctions(Function[] funs){
        // tbd this could be optimized with System.arraycopy(), 
	// but we wouldn't be able to check if we've got the function already
        for(int i=0; i<funs.length; ++i)
            addFunction(funs[i]);
    }

    public void addFunctions(Functions other){
        Iterator it = other.functions.values().iterator();
        while(it.hasNext())
            addFunctions((Function[])it.next());
    }

    public Function getFunction(Name name, Type[] params){
        FunctionCall call = new FunctionCall(name, params);
        FunctionMatch match =  getFunction(call, FunctionMatch.NO_MATCH);
        return match.getFunction();
    }

    /**
     * Get the function that is the closest match on name and parameter types.
     * @param closest The closest match so far
     */
    public FunctionMatch getFunction(FunctionCall call,
                                     FunctionMatch closest){

        Function[] matches = (Function[])functions.get(call);
        if(matches == null)
            // we have no matching names
            return closest;

        int min = closest.getDistance();
        Type[] params = call.getParameters();
        for(int i=0; i<matches.length; ++i){
            int dist = matches[i].distance(params, min);
            if(dist < min){
                min = dist;
                closest = new FunctionMatch(matches[i], dist);
            }
        }
        return closest;
    }

    public List getFunctions(){
        List funs = new ArrayList();
        Iterator it = functions.values().iterator();
        while(it.hasNext()){
            Function[] func = (Function[])it.next();
            for(int i=0; i<func.length; ++i)
                funs.add(func[i]);
        }
        return funs;
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
