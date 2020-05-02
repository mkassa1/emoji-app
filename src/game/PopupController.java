package game;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PopupController {
    private Stage stage;
    @FXML
    private Button submitButton;
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

    public void initialize() {
        this.emojiFrequency = new ToggleGroup();
        this.never = new RadioButton();
        this.never.setToggleGroup(emojiFrequency);
        this.rarely = new RadioButton();
        this.rarely.setToggleGroup(emojiFrequency);
        this.sometimes = new RadioButton();
        this.sometimes.setToggleGroup(emojiFrequency);
        this.allTheTime = new RadioButton();
        this.allTheTime.setToggleGroup(emojiFrequency);
        this.handedness = new ToggleGroup();
        this.left = new RadioButton();
        this.left.setToggleGroup(handedness);
        this.right = new RadioButton();
        this.right.setToggleGroup(handedness);
        this.both = new RadioButton();
        this.both.setToggleGroup(handedness);
        this.submitButton = new Button();
        this.languageField = new TextField();
        this.ageField = new TextField();
        this.genderField = new TextField();
    }

    public Button getSubmitButton(){
        return submitButton;
    }

    public String getNativeLanguage(){
        return languageField.getText();
    }

    public String getEmojiFrequency(){
        return emojiFrequency.getSelectedToggle().toString();
    }

    public String getAge(){
        return ageField.getText();
    }

    public String getGender(){
        return genderField.getText();
    }

    public String getHandedness(){
        return handedness.getSelectedToggle().toString();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    public void close(){
        this.stage.close();
    }
}
