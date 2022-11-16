

package com.baypackets.ase.util;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.*;

/**
 */
public class DOMUtils {
  private static final String ATTRIB_ID = "id";
private static String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";

  static public String getAttribute (Element el, String attrName) {
    String sRet = null;
    Attr   attr = el.getAttributeNode(attrName);

    if (attr != null) {
      sRet = attr.getValue();
    }
    return sRet;
  }

  static public String getAttributeNS (Element el,
                                       String namespaceURI,
                                       String localPart) {
    String sRet = null;
    Attr   attr = el.getAttributeNodeNS (namespaceURI, localPart);

    if (attr != null) {
      sRet = attr.getValue ();
    }

    return sRet;
  }

  static public String getChildCharacterData (Element parentEl) {
    if (parentEl == null) {
      return null;
    } 
    Node          tempNode = parentEl.getFirstChild();
    StringBuffer  strBuf   = new StringBuffer(64);
    CharacterData charData;

    while (tempNode != null) {
      switch (tempNode.getNodeType()) {
        case Node.TEXT_NODE :
        case Node.CDATA_SECTION_NODE : charData = (CharacterData)tempNode;
                                       strBuf.append(charData.getData());
                                       break;
      }
      tempNode = tempNode.getNextSibling();
    }
    return strBuf.toString();
  }

  public static Element getFirstChildElement (Element elem) {
    for (Node n = elem.getFirstChild (); n != null; n = n.getNextSibling ()) {
      if (n.getNodeType () == Node.ELEMENT_NODE) {
        return (Element) n;
      }
    }
    return null;
  }


  public static Element getNextSiblingElement (Element elem) {
    for (Node n = elem.getNextSibling (); n != null; n = n.getNextSibling ()) {
      if (n.getNodeType () == Node.ELEMENT_NODE) {
        return (Element) n;
      }
    }
    return null;
  }
  public static void logXml (Element elem) throws Exception{
    
    TransformerFactory tranFactory = TransformerFactory.newInstance();
    Transformer aTransformer = tranFactory.newTransformer();
    Source src = new DOMSource(elem);
    Result dest = new StreamResult(System.out);
    aTransformer.transform(src, dest);
  }

  public static Element findChildElementWithAttribute (Element elem, 
                   String attrName,
                   String attrValue) {
    for (Node n = elem.getFirstChild (); n != null; n = n.getNextSibling ()) {
      if (n.getNodeType () == Node.ELEMENT_NODE) {
        if (attrValue.equals (DOMUtils.getAttribute ((Element) n, attrName))) {
          return (Element) n;
        }
      }
    }
    return  null;
  }

  public static int countKids (Element elem, short nodeType) {
    int nkids = 0;
    for (Node n = elem.getFirstChild (); n != null; n = n.getNextSibling ()) {
      if (n.getNodeType () == nodeType) {
        nkids++;
      }
    }
    return nkids;
  }

  public static String getNamespaceURIFromPrefix (Node context,
                                                  String prefix) {
    short nodeType = context.getNodeType ();
    Node tempNode = null;

    switch (nodeType)
    {
      case Node.ATTRIBUTE_NODE :
      {
        tempNode = ((Attr) context).getOwnerElement ();
        break;
      }
      case Node.ELEMENT_NODE :
      {
        tempNode = context;
        break;
      }
      default :
      {
        tempNode = context.getParentNode ();
        break;
      }
    }

    while (tempNode != null && tempNode.getNodeType () == Node.ELEMENT_NODE)
    {
      Element tempEl = (Element) tempNode;
      String namespaceURI;
      
      if (prefix == null) {
        namespaceURI = getAttribute(tempEl, "xmlns");
      } else {
        namespaceURI = getAttributeNS(tempEl, NS_URI_XMLNS, prefix);
        if (namespaceURI == null) {
          // SAX parser (maybe others?) need this
          namespaceURI = getAttribute(tempEl, "xmlns:" + prefix);
        }
      }

      if (namespaceURI != null)
      {
        return namespaceURI;
      }
      else
      {
        tempNode = tempEl.getParentNode ();
      }
    }

    return null;
  }

  public static Element getElementByID(Element el, String id)
  {
	  if (el == null)
		  return null;
	  String thisId = el.getAttribute(ATTRIB_ID);
	  if (id.equals(thisId))
		  return el;
	  
	  NodeList list = el.getChildNodes();
	  for (int i = 0; i < list.getLength(); i++) {
		  Node node = list.item(i);
		  if (node instanceof Element) {
			  Element ret = getElementByID((Element)node, id);
			  if (ret != null)
				  return ret;
		  }
	  }
	  
	  return null;
  }
}

