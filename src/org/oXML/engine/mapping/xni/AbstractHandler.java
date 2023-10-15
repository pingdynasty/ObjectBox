package org.oXML.engine.mapping.xni;

import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.Augmentations;

public class AbstractHandler implements XMLDocumentHandler {

    private XMLDocumentHandler parent;
    
    public void startDocument(XMLLocator locator, String encoding, 
			      NamespaceContext namespaceContext, Augmentations augs)
        throws XNIException {
    }
    
    public void xmlDecl(String version, String encoding, 
                        String standalone, Augmentations augs) throws XNIException {
    }
    
    public void doctypeDecl(String rootElement, String publicId, 
                            String systemId, Augmentations augs) throws XNIException {
    }
    
    public void comment(XMLString text, Augmentations augs)
        throws XNIException {
    }
    
    public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException {
    }

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {
    }
    
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {
    }
    
    public void endElement(QName element, Augmentations augs)
        throws XNIException {
    }
    
    public void startGeneralEntity(String name, 
				   XMLResourceIdentifier resId,
				   String encoding, Augmentations augs) 
        throws XNIException {
    }
    
    public void textDecl(String version, String encoding, Augmentations augs)
        throws XNIException {
    }
    
    public void endGeneralEntity(String name, Augmentations augs)
        throws XNIException {
    }
    
    public void characters(XMLString text, Augmentations augs)
        throws XNIException {
    }
    
    public void ignorableWhitespace(XMLString text , Augmentations augs)
        throws XNIException {
    }
    
    public void startCDATA(Augmentations augs)
        throws XNIException {
    }
    
    public void endCDATA(Augmentations augs)
        throws XNIException {
    }
    
    public void endDocument(Augmentations augs)
        throws XNIException {
    }

    public void setDocumentSource(XMLDocumentSource source) {
    }
    
    public XMLDocumentSource getDocumentSource() {
        return null;
    }
    
}