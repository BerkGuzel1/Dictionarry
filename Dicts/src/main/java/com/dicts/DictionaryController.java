package com.dicts;

import javafx.event.ActionEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DictionaryController {

    @FXML
    private TextField searchTextField;

    @FXML
    private TextArea translationTextArea;

    @FXML
    private ListView<String> wordListView;

    private Dictionary dictionary;
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.dictionary = mainApp.getDictionary();
    }

    @FXML
    void searchForWord(ActionEvent event) {
        String searchTerm = searchTextField.getText();
        if (!searchTerm.isEmpty()) {
            String translation = dictionary.getTranslation(searchTerm);
            translationTextArea.setText(translation);
        }
    }

    @FXML
    void editWord() {
        int selectedIndex = wordListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedWord = wordListView.getSelectionModel().getSelectedItem();
            mainApp.showEditDialog(selectedWord);
        }
    }


    @FXML
    void loadDictionaryFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Dictionary File");
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            mainApp.loadDictionaryFromFile(file);
            populateWordListView();
        }
    }


    @FXML
    void saveDictionaryToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Dictionary File");
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            mainApp.saveDictionaryToFile(file);
        }
    }


    public void populateWordListView() {
        wordListView.getItems().setAll(dictionary.getWords());
    }
}