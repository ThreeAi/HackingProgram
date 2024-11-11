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

public class HackController {
    @FXML
    private Button text_import_button;
    @FXML
    private Button text_export_button;
    @FXML
    private Button key_export_button;
    @FXML
    private Button hack_button;
    @FXML
    private TextArea input_textarea;
    @FXML
    private TextArea output_textarea;
    @FXML
    private TextArea key_output_textarea;
    @FXML
    private Label status_label;

    private VigenereCipherBreaker vigenereCracker = new VigenereCipherBreaker();

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
    private void hackText() {
        String encryptedText = input_textarea.getText();
        if (encryptedText.isEmpty()) {
            status_label.setText("Please enter text to hack.");
            return;
        }

        try {
            // Attempt to break the cipher, using Kasiski or Index of Coincidence to guess key length
            String key = vigenereCracker.findKey(encryptedText);
            String decryptedText = vigenereCracker.decrypt(encryptedText, key);

            // Display results
            output_textarea.setText(decryptedText);
            key_output_textarea.setText(key);
            status_label.setText("Decryption complete.");
        } catch (Exception e) {
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