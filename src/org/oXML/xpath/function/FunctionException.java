package org.oXML.xpath.function;

import java.util.List;
import java.util.ArrayList;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;

public class FunctionException extends XPathException {

    List frames = new ArrayList();

    public FunctionException(Throwable cause) {
	super(cause);
    }

    public FunctionException(String msg) {
	super(msg);
    }

    public FunctionException(String msg, Throwable cause) {
	super(msg, cause);
    }

    public FunctionException(StackFrame frame, Throwable cause) {
	super(cause);
	addStackFrame(frame);
    }

    public void addStackFrame(StackFrame frame){
	frames.add(frame);
    }

    public StackFrame[] getStackFrames(){
	return (StackFrame[])frames.toArray();
    }

    public String getMessage(){
	StringBuffer buf = new StringBuffer();
	buf.append(super.getMessage());
	for(int i=0; i<frames.size();++i)
	    buf.append("\n\tat: ").append(frames.get(i));
	return buf.toString();
    }

//     public String toString(){
// 	StringBuffer buf = new StringBuffer(super.toString());
// 	for(int i=0; i<frames.size();++i)
// 	    buf.append("\n\tat: ").append(frames.get(i));
// 	return buf.toString();
//     }
}