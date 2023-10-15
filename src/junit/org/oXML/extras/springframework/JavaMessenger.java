package org.oXML.extras.springframework;

/**
 * @author Martin Klang
 */
public class JavaMessenger implements Messenger {

    private String msg;

    public JavaMessenger(String msg){
        this.msg = msg;
    }

    public void setMessage(String msg){
        this.msg = msg;
    }

    public String getMessage(){
        return msg;
    }
}
