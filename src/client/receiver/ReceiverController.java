package client.receiver;

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
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReceiverController implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField message;
    @FXML
    private MediaView mediaView;
    private VideoPlayer videoPlayer;
    @FXML
    private Button videoButton;
    @FXML
    private Text emotionPromptText;
    @FXML
    private Text strengthPromptText;
    @FXML
    private Text emotionConfidencePromptText;
    @FXML
    private Text strengthConfidencePromptText;
    @FXML
    private MenuButton emojiButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button swapButton;
    @FXML
    private Button quitButton;
    @FXML
    private Canvas overlay;
    @FXML
    private ComboBox emotionMenu;
    @FXML
    private Slider emotionStrengthMeter;
    @FXML
    private Slider emotionConfidenceMeter;
    @FXML
    private Slider strengthConfidenceMeter;

    private String receiverTimestamp;
    private String messageSentTimestamp;
    private EmojiMappings map;
    private String name;
    private boolean isPlaying = false;
    private boolean showVideoPlayback = true;

    private ReceiverClient client;

    @FXML
    public void showReceiverFeedback(ActionEvent event) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.receiverTimestamp = timestamp.toString();

        /* video playback will be parameterized
        if (showVideoPlayback){
        } */
        setOverlay();
        String emotion = (String) emotionMenu.getValue();
        double emotionStrength = emotionStrengthMeter.getValue();
        double emotionConfidence = emotionConfidenceMeter.getValue();
        double emotionStrengthConfidence = strengthConfidenceMeter.getValue();
        this.mediaView.setVisible(true);
        this.mediaView.setDisable(false);
        this.videoPlayer.play();
        client.sendData(emotion, emotionStrength, emotionConfidence, emotionStrengthConfidence, receiverTimestamp);
    }

    @FXML
    private void onEmojiSelect(ActionEvent event){
        String emoji = (((Control)event.getSource()).getStyle());
        int start = emoji.indexOf("url(") + 4;
        int end = emoji.indexOf(")", start);
        String emojiPath = emoji.substring(start, end);
        String emojiUnicode = map.get(emojiPath);
        this.message.setText(message.getText() + emojiUnicode);
    }

    @FXML
    private void onClickSwap(ActionEvent event) throws Exception{
        Parent newView = FXMLLoader.load(getClass().getResource("/client/sender/sender_screen.fxml"));
        Scene newScene = new Scene(newView);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(newScene);
        window.show();
    }

    @FXML
    private void onClickQuit(ActionEvent event) throws Exception {
        Parent exitForm = FXMLLoader.load(getClass().getResource("receiver_popup.fxml"));
        Scene exitFormScene = new Scene(exitForm);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(exitFormScene);
        window.show();
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Message sent at: " + timestamp);
        this.messageSentTimestamp = timestamp.toString();
        String msg = name + " > " + message.getText();
        client.sendMessage(msg);
        client.setMessageTimestamp(messageSentTimestamp);
        message.clear();
    }

    public void setOverlay(){
        GraphicsContext gc = this.overlay.getGraphicsContext2D();
        gc.setFill(Color.color(0.5, 0.5, 0.5, 0.9));
        gc.fillRect(0, 0, 1000, 800);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.setFont(Font.font(40.0));
        gc.setFill(Color.BLACK);
        gc.fillText(
                "Here's what the sender watched:",
                Math.round(overlay.getWidth()  / 2),
                Math.round(overlay.getHeight() / 5)
        );
        this.overlay.setDisable(false);
        this.overlay.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Prompt for player's name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Start");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> this.name = name);

        // initialize emoji picker
        this.map = new EmojiMappings();
        map.init();
        this.emojiButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcons.SMILE_ALT, "20px"));

        // initialize video player
        this.videoPlayer = new VideoPlayer();

        message.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                this.sendButton.fire();
            }
        });

        this.swapButton.setVisible(false);
        this.swapButton.setDisable(true);
        this.quitButton.setVisible(false);
        this.quitButton.setDisable(true);

        if (mediaView != null){
            this.mediaView.setMediaPlayer(videoPlayer.getMediaPlayer());
        }
        this.mediaView.setVisible(false);
        this.mediaView.setDisable(true);

        // Start the client's thread
        this.client = new ReceiverClient("127.0.0.1", 8000, this.name, this.textArea, this.overlay, this.mediaView, this.videoPlayer,
                this.emotionMenu,  this.emotionStrengthMeter, this.emotionConfidenceMeter, this.strengthConfidenceMeter,
                this.emotionPromptText, this.strengthPromptText, this.emotionConfidencePromptText, this.strengthConfidencePromptText,
                this.swapButton, this.quitButton);

        new Thread(client).start();
    }

}


