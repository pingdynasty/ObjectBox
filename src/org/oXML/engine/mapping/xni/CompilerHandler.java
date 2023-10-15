package org.oXML.engine.mapping.xni;

import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.Augmentations;


import org.oXML.xpath.XPathException;
import org.oXML.xpath.NamespaceException;
import org.oXML.xpath.Expression;
import org.oXML.xpath.parser.Parser;
import org.oXML.xpath.parser.ExpressionParser;
import org.oXML.xpath.parser.MixedExpressionParser;
import org.oXML.xpath.PrefixResolver;
import org.oXML.xpath.Resolver;

import org.oXML.util.Log;

import org.oXML.engine.template.*;

public class CompilerHandler implements XMLDocumentHandler {

    private XMLLocator locator;
    private NamespaceContext nsContext;
    private Template template;

    private String encoding;

    private Map types;

    // Public methods
   
//     public void setDocumentHandler(XMLDocumentHandler handler) {
// 	Log.warning("setDocumentHandler: "+handler);
//     }

//     public void setDocumentSource(XMLDocumentSource source) {
// 	Log.warning("setDocumentSource: "+source);
//     }
    
//     public XMLDocumentSource getDocumentSource() {
// 	Log.warning("getDocumentSource");
//         return null;
//     }

    class XNIResolver extends PrefixResolver implements Resolver {

	public Type getType(Name name) {
	    return (Type)types.get(name);
	}

	public String getNamespaceURI(String prefix) {
	    return nsContext.getURI(prefix);
	}


    }

    // XMLDocumentHandler methods
    
    public void startDocument(XMLLocator locator, String encoding, 
			      NamespaceContext nsContext, Augmentations augs)
        throws XNIException {
	this.locator = locator;
	this.encoding = encoding;
	this.nsContext = nsContext;
    }

    public void endDocument(Augmentations augs)
        throws XNIException {
	template = new DocumentTemplate(template);
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
    
    public void startGeneralEntity(String name, XMLResourceIdentifier resId,
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
    
    public void ignorableWhitespace(XMLString text, Augmentations augs)
        throws XNIException {
    }
    
    public void startCDATA(Augmentations augs)
        throws XNIException {
    }
    
    public void endCDATA(Augmentations augs)
        throws XNIException {
    }
    
    
} // class PassThroughFilter