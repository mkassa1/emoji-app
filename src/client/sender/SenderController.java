package client.sender;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import game.EmojiMappings;
import game.VideoPlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class SenderController implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextFlow messageFlow;
    @FXML
    private TextField message;
    @FXML
    private MediaView mediaView;
    private VideoPlayer videoPlayer;
    @FXML
    private Button videoButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button swapButton;
    @FXML
    private Button quitButton;
    @FXML
    private Text emotionPromptText;
    @FXML
    private Text strengthPromptText;
    @FXML
    private Text emotionConfidencePromptText;
    @FXML
    private Text strengthConfidencePromptText;
    @FXML
    private ComboBox emotionMenu;
    @FXML
    private MenuButton emojiButton;
    @FXML
    private HBox messageBox;
    @FXML
    private StackPane messagePane;
    @FXML
    private Canvas overlay;
    @FXML
    private Slider emotionStrengthMeter;
    @FXML
    private Slider emotionConfidenceMeter;
    @FXML
    private Slider strengthConfidenceMeter;
    private String name;
    private SenderClient client;
    private boolean isPlaying = false;
    private String messageSentTimestamp;
    private String senderTimestamp;
    private EmojiMappings map;
    private boolean showVideoPlayback = true;

    @FXML
    public void controlVideo(ActionEvent event){
        if (!isPlaying){
            videoButton.setText("pause");
            videoPlayer.play();
        } else {
            videoButton.setText("play");
            videoPlayer.pause();
        }
        isPlaying = !isPlaying;
    }

    @FXML
    public void restartVideo(ActionEvent event){
        if (videoPlayer.onEndOfVideo()){
            videoPlayer.restart();
        }
    }

    @FXML
    public void showSenderFeedback(ActionEvent event) throws IOException {
        GraphicsContext gc = this.overlay.getGraphicsContext2D();
        gc.setFill(Color.color(0.5, 0.5, 0.5, 0.9));
        gc.fillRect(0, 0, 1000, 800);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font(40.0));
        gc.setFill(Color.BLACK);
        gc.fillText(
                "Waiting for receiver...",
                Math.round(overlay.getWidth()  / 2),
                Math.round(overlay.getHeight() / 2)
        );
        this.overlay.setDisable(false);
        this.overlay.setVisible(true);
        String video = this.videoPlayer.getMediaPlayer().getMedia().getSource();
        String emotion = (String) emotionMenu.getValue();
        double emotionStrength = emotionStrengthMeter.getValue();
        double emotionConfidence = emotionConfidenceMeter.getValue();
        double emotionStrengthConfidence = strengthConfidenceMeter.getValue();
        int numTimesPlayed = videoPlayer.getNumTimesPlayed();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.senderTimestamp = timestamp.toString();

        this.client.sendData(video, emotion, emotionStrength, emotionConfidence, emotionStrengthConfidence,
                numTimesPlayed, senderTimestamp);
    }

    @FXML
    private void onEmojiSelect(ActionEvent event) {
        String emoji = (((Control)event.getSource()).getStyle());
        int start = emoji.indexOf("url(") + 4;
        int end = emoji.indexOf(")", start);
        String emojiPath = emoji.substring(start, end);
        String emojiUnicode = map.get(emojiPath);
        this.message.setText(message.getText() + emojiUnicode);
    }

    @FXML // is causing errors
    private void onClickSwap(ActionEvent event) throws Exception {
        /*Parent newView = FXMLLoader.load(getClass().getResource("/client/receiver/receiver_screen.fxml"));
        Scene newScene = new Scene(newView);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(newScene);
        window.show(); */
    }

    @FXML
    private void onClickQuit(ActionEvent event) throws Exception {
        Parent exitForm = FXMLLoader.load(getClass().getResource("sender_popup.fxml"));
        Scene exitFormScene = new Scene(exitForm);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(exitFormScene);
        window.show();
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.messageSentTimestamp = timestamp.toString();
        String msg = name + " > " + message.getText();
        client.sendMessage(msg);
        client.setMessageTimestamp(messageSentTimestamp);
        message.clear();
    }

    // adds gray overlay
    public void setOverlay(){
        GraphicsContext gc = this.overlay.getGraphicsContext2D();
        gc.setFill(Color.color(0.5, 0.5, 0.5, 0.9));
        gc.fillRect(0, 0, 1000, 800);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font(40.0));
        gc.setFill(Color.BLACK);
        gc.fillText(
                "Waiting for receiver...",
                Math.round(overlay.getWidth()  / 2),
                Math.round(overlay.getHeight() / 2)
        );
        this.overlay.setDisable(false);
        this.overlay.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Prompt for sender's name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Welcome!");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> this.name = name);

        // initialize emoji picker
        this.map= new EmojiMappings();
        map.init();
        this.emojiButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcons.SMILE_ALT, "20px"));

        this.message.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                this.sendButton.fire();
            }
        });

        this.swapButton.setVisible(false);
        this.swapButton.setDisable(true);
        this.quitButton.setVisible(false);
        this.quitButton.setDisable(true);

        // initialize video player
        this.videoPlayer = new VideoPlayer();
        if (mediaView != null){
            this.mediaView.setMediaPlayer(videoPlayer.getMediaPlayer());
        }

        setOverlay();

        // Start the sender's client thread
        this.client = new SenderClient("127.0.0.1", 8000, this.name, this.textArea, this.overlay, this.mediaView, this.videoPlayer,
                this.emotionMenu, this.emotionStrengthMeter, this.emotionConfidenceMeter, this.strengthConfidenceMeter,
                this.emotionPromptText, this.strengthPromptText, this.emotionConfidencePromptText, this.strengthConfidencePromptText,
                this.swapButton,  this.quitButton);
        new Thread(client).start();
    }

}



