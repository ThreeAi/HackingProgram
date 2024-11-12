package edu.program.hackingprogram;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static edu.program.hackingprogram.KeyFinder.findKeyUsingFullBruteForce;
import static edu.program.hackingprogram.KeyFinder.loadDictionary;
import static edu.program.hackingprogram.KeyLengthFinder.findKeyLength;
import static edu.program.hackingprogram.VigenereCipher.decrypt;

public class HackController {
    @FXML
    private TextArea input_textarea;
    @FXML
    private TextArea output_textarea;
    @FXML
    private TextArea key_output_textarea;
    @FXML
    private Label status_label;
    @FXML
    private Label status_second_label;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void importText() { importFileContent(input_textarea);}

    @FXML
    public void exportText() { exportFileContent(output_textarea);}

    @FXML
    public void exportKey() { importFileContent(key_output_textarea);}

    // Метод для взлома текста
    @FXML
    private void hackText() {
        String encryptedText = input_textarea.getText();
        if (encryptedText.isEmpty()) {
            status_label.setText("Please enter text to hack.");
            return;
        }

        try {
            int keyLength = findKeyLength(encryptedText);
            status_label.setText(String.valueOf(keyLength));
            Set<String> dictionary = loadDictionary("word_list.txt");
            String findKey = findKeyUsingFullBruteForce(encryptedText, keyLength, dictionary, status_second_label);
            String decryptedText = decrypt(encryptedText, findKey);

            output_textarea.setText(decryptedText);
            key_output_textarea.setText(findKey);
//            status_label.setText("Decryption complete.");
        } catch (Exception e) {
            e.printStackTrace();
            status_label.setText("Error decrypting text.");
        }
    }

    private void importFileContent(TextArea targetTextArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для импорта");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
                targetTextArea.setText(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportFileContent(TextArea sourceTextArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(sourceTextArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}