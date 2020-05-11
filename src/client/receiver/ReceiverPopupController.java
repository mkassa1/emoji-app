package client.receiver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReceiverPopupController implements Initializable {
    @FXML
    private TextField languageField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField genderField;
    @FXML
    private RadioButton never;
    @FXML
    private RadioButton rarely;
    @FXML
    private RadioButton sometimes;
    @FXML
    private RadioButton allTheTime;
    @FXML
    private RadioButton left;
    @FXML
    private RadioButton right;
    @FXML
    private RadioButton both;
    private ToggleGroup emojiFrequency;
    private ToggleGroup handedness;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.emojiFrequency = new ToggleGroup();
        this.never.setToggleGroup(emojiFrequency);
        this.rarely.setToggleGroup(emojiFrequency);
        this.sometimes.setToggleGroup(emojiFrequency);
        this.allTheTime.setToggleGroup(emojiFrequency);
        this.handedness = new ToggleGroup();
        this.left.setToggleGroup(handedness);
        this.right.setToggleGroup(handedness);
        this.both.setToggleGroup(handedness);
    }

    @FXML
    private void onSubmitExitForm(ActionEvent event) {
        if (languageField.getText() == null || emojiFrequency.getSelectedToggle() == null ||
                handedness.getSelectedToggle() == null || genderField.getText() == null) {
            return;
        }
        String nativeLanguage = languageField.getText();
        String emojiFrequencyAsString = emojiFrequency.getSelectedToggle().toString();
        int start = emojiFrequencyAsString.indexOf("'") + 1;
        int end = emojiFrequencyAsString.lastIndexOf("'");
        String emojiUse = emojiFrequencyAsString.substring(start, end);
        String age = ageField.getText();
        String gender = genderField.getText();
        String handednessAsString = handedness.getSelectedToggle().toString();
        start = handednessAsString.indexOf("'") + 1;
        end = handednessAsString.lastIndexOf("'");
        String handednessEntry = handednessAsString.substring(start, end);
        writeData(nativeLanguage, emojiUse, age, gender, handednessEntry);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
    }

    public void writeData(String nativeLanguage, String emojiUse, String age, String gender, String handednessEntry) {
        try {
            File outputFile = new File("src\\data\\out.txt");
            FileWriter fw = new FileWriter(outputFile, true);
            BufferedWriter w = new BufferedWriter(fw);
            w.write("--- RECEIVER DATA ---\n");
            w.write("Native language: " + nativeLanguage + "\n");
            w.write("Emoji use: " + emojiUse + "\n");
            w.write("Age: " + age + "\n");
            w.write("Gender: " + gender + "\n");
            w.write("Handedness: " + handednessEntry + "\n");
            w.flush();
        } catch(IOException e){
            System.out.println("Could not write to results file.");
            e.printStackTrace();
        }
    }
}
