package com.dicts;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FileUtil {

    public static Dictionary loadDictionaryFromFile(File file) throws ParserConfigurationException, SAXException, IOException {
        Map<String, String> entries = new HashMap<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        NodeList wordNodes;
        try {
            wordNodes = (NodeList) xpath.evaluate("//entry", document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < wordNodes.getLength(); i++) {
            Element wordNode = (Element) wordNodes.item(i);
            String word = wordNode.getAttribute("key");
            String translation = wordNode.getTextContent();
            entries.put(word, translation);
        }

        Dictionary dictionary = new Dictionary();
        dictionary.addEntries(entries);
        return dictionary;
    }

    public static void saveDictionaryToFile(Dictionary dictionary, File file) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement("dictionary");
        document.appendChild(rootElement);

        for (String word : dictionary.getWords()) {
            Element entryElement = document.createElement("entry");
            entryElement.setAttribute("key", word);
            entryElement.setTextContent(dictionary.getTranslation(word));
            rootElement.appendChild(entryElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);

        transformer.transform(source, result);
    }
}