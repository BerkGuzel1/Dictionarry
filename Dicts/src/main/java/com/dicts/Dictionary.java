package com.dicts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Dictionary {
    private Map<String, String> entries;

    public Dictionary() {
        entries = new HashMap<>();
    }

    public void addEntries(Map<String, String> newEntries) {
        entries.putAll(newEntries);
    }

    public String getTranslation(String word) {
        return entries.getOrDefault(word, "No translation found for " + word);
    }

    public Set<String> getWords() {
        return entries.keySet();
    }

    public void addWord(String word, String translation) {
        entries.put(word, translation);
    }

    public void editWord(String word, String newTranslation) {
        if (entries.containsKey(word)) {
            entries.put(word, newTranslation);
        }
    }

    public void deleteWord(String word) {
        entries.remove(word);
    }
}
