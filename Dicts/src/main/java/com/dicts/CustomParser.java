package org.example.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CustomParser {
        public HashMap<String, String> parseDictionary(String path, Dictionary.Language currLanguage) {
                HashMap<String, String> currDictionary = new HashMap<>();

                String entryPatternString = "<entry(.*?)</entry>";
                Pattern entryPattern = Pattern.compile(entryPatternString, Pattern.DOTALL);

                String sourceWordPatternString = "<orth>(.*?)</orth>";
                Pattern sourceWordPattern = Pattern.compile(sourceWordPatternString, Pattern.DOTALL);

                String targetWordPatternString1 = "<sense[^>]*>\\s*<cit[^>]*>\\s*<quote[^>]*>(.*?)</quote>";
                Pattern targetWordPattern1 = Pattern.compile(targetWordPatternString1, Pattern.DOTALL);

                String targetWordPatternString2 = "<def>(.*?)</def>";
                Pattern targetWordPattern2 = Pattern.compile(targetWordPatternString2, Pattern.DOTALL);

                StringBuilder contentBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                                contentBuilder.append(line);
                                contentBuilder.append(System.lineSeparator());
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }

                String fileContent = contentBuilder.toString();
                Matcher entryMatcher = entryPattern.matcher(fileContent);
                while (entryMatcher.find()) {
                        String entryText = entryMatcher.group(1);

                        String sourceWord = "";
                        String targetWord = "";

                        Matcher sourceWordMatcher = sourceWordPattern.matcher(entryText);
                        while (sourceWordMatcher.find()) {
                                sourceWord = sourceWordMatcher.group(1).trim();
                        }

                        Matcher targetWordMatcher = targetWordPattern1.matcher(entryText);
                        while (targetWordMatcher.find()) {
                                targetWord = targetWordMatcher.group(1).trim();
                                break;
                        }

                        if (targetWord.isEmpty()) {
                                Matcher targetWordMatcher2 = targetWordPattern2.matcher(entryText);
                                while (targetWordMatcher2.find()) {
                                        targetWord = targetWordMatcher2.group(1).trim();
                                        break;
                                }
                        }

                        if (!sourceWord.isEmpty() || !targetWord.isEmpty()) {
                                currDictionary.put(sourceWord.toLowerCase(), targetWord.toLowerCase());
                        }
                }

                System.out.printf("File read: %s - %d\n", path, currDictionary.size());

                return currDictionary;
        }
        public static class TEIEditor {
                public static void modifyTEIFile(File teiFile, String word, String updatedTranslation) throws ParserConfigurationException, SAXException, IOException, TransformerException {
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(teiFile);

                        NodeList entryList = doc.getElementsByTagName("entry");
                        for (int i = 0; i < entryList.getLength(); i++) {
                                Element entryElement = (Element) entryList.item(i);
                                Element formElement = (Element) entryElement.getElementsByTagName("form").item(0);
                                Element orthElement = (Element) formElement.getElementsByTagName("orth").item(0);
                                if (orthElement.getTextContent().equals(word)) {
                                        Element transElement = (Element) entryElement.getElementsByTagName("quote").item(0);
                                        if (transElement == null) {
                                                transElement = (Element) entryElement.getElementsByTagName("def").item(0);
                                        }
                                        transElement.setTextContent(updatedTranslation);
                                }
                        }

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);
                        StreamResult result = new StreamResult(teiFile);
                        transformer.transform(source, result);
                }
        }
        public static void addWordToXML(String path, String word, String newTranslation) throws ParserConfigurationException, SAXException, IOException, TransformerException {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(path);

                NodeList entryNodes = doc.getElementsByTagName("entry");
                Element entryElement = null;
                for (int i = 0; i < entryNodes.getLength(); i++) {
                        Element currentElement = (Element) entryNodes.item(i);
                        NodeList formNodes = currentElement.getElementsByTagName("form");
                        for (int j = 0; j < formNodes.getLength(); j++) {
                                Element formElement = (Element) formNodes.item(j);
                                Element orthElement = (Element) formElement.getElementsByTagName("orth").item(0);
                                if (orthElement.getTextContent().equals(word)) {
                                        entryElement = currentElement;
                                        break;
                                }
                        }
                        if (entryElement != null) {
                                break;
                        }
                }

                if (entryElement == null) {
                        entryElement = doc.createElement("entry");
                        Element formElement = doc.createElement("form");
                        Element orthElement = doc.createElement("orth");
                        orthElement.setTextContent(word);
                        formElement.appendChild(orthElement);
                        entryElement.appendChild(formElement);

                        Element senseElement = doc.createElement("sense");
                        entryElement.appendChild(senseElement);

                        Element citElement = doc.createElement("cit");
                        citElement.setAttribute("type", "trans");
                        Element quoteElement = doc.createElement("quote");
                        quoteElement.setTextContent(newTranslation);
                        citElement.appendChild(quoteElement);
                        senseElement.appendChild(citElement);

                        Node dictionaryNode = doc.getElementsByTagName("dictionary").item(0);
                        if (dictionaryNode == null) {
                                dictionaryNode = doc.createElement("dictionary");
                                doc.appendChild(dictionaryNode);
                        }
                        dictionaryNode.appendChild(entryElement);
                } else {
                        Element senseElement = (Element) entryElement.getElementsByTagName("sense").item(0);
                        Element citElement = doc.createElement("cit");
                        citElement.setAttribute("type", "trans");
                        Element quoteElement = doc.createElement("quote");
                        quoteElement.setTextContent(newTranslation);
                        citElement.appendChild(quoteElement);
                        senseElement.appendChild(citElement);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(path));
                transformer.transform(source, result);
        }
}


