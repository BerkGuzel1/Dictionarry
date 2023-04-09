package com.dicts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainApp extends Application {

    private Stage primaryStage;

    private Dictionary dictionary = new Dictionary();

    private DictionaryController dictionaryController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dictionar/dictionary.fxml"));
        Parent root = loader.load();

        dictionaryController = loader.getController();
        dictionaryController.setMainApp(this);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void loadDictionaryFromFile(File file) {
        try {
            dictionary = FileUtil.loadDictionaryFromFile(file);
            dictionaryController.populateWordListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDictionaryToFile(File file) {
        try {
            FileUtil.saveDictionaryToFile(dictionary, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showEditDialog(String word) {
        TextInputDialog dialog = new TextInputDialog(dictionary.getTranslation(word));
        dialog.setTitle("Edit Translation");
        dialog.setHeaderText("Edit the translation for the word \"" + word + "\":");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newTranslation = result.get();
            dictionary.editWord(word, newTranslation);
            dictionaryController.populateWordListView();
        }
    }


    public Dictionary getDictionary() {
        return dictionary;
    }

    public static void main(String[] args) {
        launch(args);
    }
}