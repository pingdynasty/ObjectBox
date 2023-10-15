package org.oXML.engine.mapping.xni;

import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.Augmentations;

public class PassThroughFilter
    implements XMLDocumentHandler {
    
    // Data
    
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;

    protected XMLLocator locator;

    
    // Public methods
   
    public void setDocumentHandler(XMLDocumentHandler handler) {
        fDocumentHandler = handler;
    }
    
    // XMLDocumentHandler methods
    
    public void startDocument(XMLLocator
    locator, String encoding, NamespaceContext namespaceContext, Augmentations augs)
        throws XNIException {
	this.locator = locator;
        if (fDocumentHandler != null) {
            fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
        }
    }
    
    public void xmlDecl(String version, String encoding, 
                        String standalone, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.xmlDecl(version, encoding, standalone,
                augs);
        }
    }
    
    public void doctypeDecl(String rootElement, String publicId, 
                            String systemId, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.doctypeDecl(rootElement, publicId,
                    systemId, augs);
        }
    }
    
    public void comment(XMLString text,
        Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.comment(text, augs);
        }
    }
    
    public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.processingInstruction(target, data, augs);
        }
    }

    public void startElement(QName
    element, XMLAttributes
        attributes, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.startElement(element, attributes, augs);
        }
    }
    
    public void emptyElement(QName
    element, XMLAttributes
        attributes, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.emptyElement(element, attributes, augs);
        }
    }
    
    public void endElement(QName element,
        augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(element, augs);
        }
    }
    
    public void startGeneralEntity(String name, 
            XMLResourceIdentifier resId,
            String encoding, Augmentations augs) 
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.startEntity(name, 
                                         resId, encoding, augs);
        }
    }
    
    public void textDecl(String version, String encoding, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.textDecl(version, encoding, augs);
        }
    }
    
    public void endGeneralEntity(String name, Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endEntity(name, augs);
        }
    }
    
    public void characters(XMLString text,
        Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.characters(text, augs);
        }
    }
    
    public void ignorableWhitespace(XMLString text ,
        Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }
    
    public void startCDATA(Augmentations
        augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.startCDATA(augs);
        }
    }
    
    public void endCDATA(Augmentations augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endCDATA(augs);
        }
    }
    
    public void endDocument(Augmentations
            augs)
        throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endDocument(augs);
        }
    }

    public void setDocumentSource(XMLDocumentSource source) {
        fDocumentSource = source;
    }
    
    public XMLDocumentSource getDocumentSource(XMLDocumentSource source) {
        return fDocumentSource;
    }
    
    
} // class PassThroughFilter