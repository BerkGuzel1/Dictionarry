package com.dicts;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DictionaryXMLLoader {

    public static Map<String, String> loadFromTEIStream(InputStream inputStream) throws Exception {
        Map<String, String> entries = new HashMap<>();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        NodeList entryList = doc.getElementsByTagName("entry");

        for (int i = 0; i < entryList.getLength(); i++) {
            Element entry = (Element) entryList.item(i);
            String headword = entry.getElementsByTagName("hw").item(0).getTextContent();
            String translation = entry.getElementsByTagName("def").item(0).getTextContent();
            entries.put(headword, translation);
        }

        return entries;
    }
}