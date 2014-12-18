package task.mail.properties;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MyXMLProperties {

	private static final Logger logger = Logger.getLogger(MyXMLProperties.class);

	private Document doc;

	public void load(InputStream is) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			// Document doc = docBuilder.parse (new File("book.xml"));
			doc = docBuilder.parse(is);
			// normalize text representation
			doc.getDocumentElement().normalize();
		} catch (SAXParseException e) {
			logger.error("SAXParseException in MyXMLProperties.load ");
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public String getProperty(String tagName) {
		NodeList uniqueKeyList = doc.getElementsByTagName(tagName);
		Node uniqueKey = uniqueKeyList.item(0);
		return uniqueKey.getTextContent();
	}
	
	/**
	 * 解析xml文件的内容
	 * @param filepath
	 * @param tagName
	 * @param keyString
	 * @param valueString
	 * @return
	 */
	public HashMap<String, String> getPropertys(String filepath,String tagName,String keyString,String valueString) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			File file = new File(filepath);
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList node = doc.getElementsByTagName(tagName); 
			for(int i=0;i<node.getLength();i++){ 
				String key = doc.getElementsByTagName(keyString).item(i).getFirstChild().getNodeValue().toString();
				String value = doc.getElementsByTagName(valueString).item(i).getFirstChild().getNodeValue();
				map.put(key,value); 
			}
		} catch (Exception e) {
			logger.error("解析xml文件的内容" + e.getMessage());
			e.printStackTrace();
		} 
		return map;
	}
	
	/**
	 * 
	 * @param tagName
	 * @param keyString
	 * @param valueString
	 * @return
	 */
	public HashMap<String, String> getPropertys(String tagName,String keyString,String valueString) {
		HashMap<String, String> map = null;
		NodeList listOfFields = doc.getElementsByTagName(tagName);
		if (listOfFields != null) {
			map = new HashMap<String, String>();
			for (int s = 0; s < listOfFields.getLength(); s++) {
				Node firstFieldNode = listOfFields.item(s);
				if (firstFieldNode.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap namenode = firstFieldNode.getAttributes();
					String name = null;
					String property = null;
					for (int i = 0; i < namenode.getLength(); i++) {
						if (keyString.equals(namenode.item(i).getNodeName()))
							name = namenode.item(i).getNodeValue();
						if (valueString.equals(namenode.item(i).getNodeName()))
							property = namenode.item(i).getNodeValue();
						if(property != null && !"0".equals(property))
							map.put(name, property);
					}
				}// end of if clause
			}// end of for loop with s var
		}
		map.remove(null);
		return map;
	}

}
