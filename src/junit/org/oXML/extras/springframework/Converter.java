package org.oXML.extras.springframework;

import org.w3c.dom.Node;

/**
 * @author Martin Klang
 */
public interface Converter {

    public Node convert(Node input);

    public void validate(Node input)
        throws Exception;

}
