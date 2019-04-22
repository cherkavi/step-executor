package com.cherkavi.productconfig.cd.uploader.service.jenkins;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

public class ArtifactApiParser {

    public static String findWarFileRelativePath(String path) throws IOException, SAXException, ParserConfigurationException {
        Document document = read(new File(path));
        NodeList nodeList = getNodeList(document, "/workflowRun/artifact");
        for(int index=0;index<nodeList.getLength();index++){
            Node node = nodeList.item(index);
            if(!(node instanceof Element)){
                continue;
            }
            Element element = (Element)node;
            try{
                if(element.getElementsByTagName("fileName").item(0).getTextContent().endsWith(".war")){
                    return element.getElementsByTagName("relativePath").item(0).getTextContent();
                }
            }catch(RuntimeException re){
                continue;
            }
        }
        throw new IllegalArgumentException("can't find necessary element");
    }

    private static NodeList getNodeList(Document document, String path){
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            return (NodeList)xpath.evaluate(path, document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    private static Document read(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException {
                return null;
//                if (systemId.contains("foo.dtd")) {
//                    return new InputSource(new StringReader(""));
//                } else {
//                    return null;
//                }
            }
        });
        return db.parse(file);
    }

}
