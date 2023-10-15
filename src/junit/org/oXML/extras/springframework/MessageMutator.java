package org.oXML.extras.springframework;

/**
 * @author Martin Klang
 */
public interface MessageMutator {

    public void mutate(Messenger msgr);

    public Messenger createMessenger();

}
