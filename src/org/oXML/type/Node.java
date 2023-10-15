package org.oXML.type;

import org.oXML.ObjectBoxException;

public interface Node {

    public static final Type TYPE = NodeNode.TYPE;

    /**  get the type of this Node */
    public Type getType();

    /**  cast to specified type */
    public Node cast(Type type); 

    /**  downcast to actual type */
    public Node cast();

    // data conversion utility functions for Java engine
    public StringNode toStringNode()
        throws ObjectBoxException;
    public NumberNode toNumberNode()
        throws ObjectBoxException;
    public BooleanNode toBooleanNode()
        throws ObjectBoxException;
    public BytesNode toBytesNode()
        throws ObjectBoxException;
    public double numberValue();                    // number()
    public boolean booleanValue();                  // boolean()
    public byte[] byteValue();                      // bytes()
    public String stringValue();                    // string()

    // parent operations (Node)
   // should return DocumentNode
    public Node getDocument();                      // document()
    // should return ParentNode
    public Node getParent();                        // parent()
    // should take ParentNode
    public void setParent(Node parent);             // parent(Node)
    public Node getPreviousSibling();               // previous()
    public Node getNextSibling();                   // next()

    // child nodeset operations (Parent/Nodeset)
    public Nodeset getChildNodes();                 // nodes()
    public void addChildNode(Node child);           // append(Node)
    public void removeChild(Node child);            // remove(Node)
    public void insert(int pos, Node insert);       // insert(Number, Node)

    // Element operations
    public java.util.Collection getAttributes();    // attributes()
    public AttributeNode getAttribute(Name name);   // attribute(Name)
    public void setAttribute(AttributeNode attribute);     // attribute(Attribute)
    public String getAttributeValue(Name name);
    public void setAttribute(Name name, String value);
    public Name getName();                          // name()
    public void setName(Name name);                 // name(Name)

    public Node copy(boolean deep);


    public boolean equals(Object other);
    public boolean equals(Node other);

    /**
     * lookup and call the named function on this Node
     */
    public Node invoke(Name name, Node[] args)
        throws ObjectBoxException;

}