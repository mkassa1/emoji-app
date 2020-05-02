package client.sender;

import game.CommunicationConstants;
import game.VideoPlayer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SenderClient implements Runnable, CommunicationConstants {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private String name;
    private String role;
    private String currentVideo;
    private String messageSentTimestamp = "";
    private boolean gameEnded;

    // display elements
    private TextArea textArea;
    private Canvas overlay;
    private MediaView mediaView;
    private VideoPlayer videoPlayer;
    private ComboBox emotionMenu;
    private Slider emotionStrengthMeter;
    private Slider emotionConfidenceMeter;
    private Slider strengthConfidenceMeter;
    private Text emotionPromptText;
    private Text strengthPromptText;
    private Text emotionConfidencePromptText;
    private Text strengthConfidencePromptText;
    private Button swapButton;
    private Button quitButton;

    public SenderClient(String server, int port, String name, TextArea textArea, Canvas overlay, MediaView mediaView, VideoPlayer videoPlayer,
                        ComboBox emotionMenu, Slider emotionStrengthMeter, Slider emotionConfidenceMeter,
                        Slider strengthConfidenceMeter, Text emotionPromptText, Text strengthPromptText,
                        Text emotionConfidencePromptText, Text strengthConfidencePromptText, Button swapButton,
                        Button quitButton){
        try {
            this.socket = new Socket(server, port);
            this.in = new Scanner(socket.getInputStream());
            this.out = new PrintWriter(socket.getOutputStream(), true);

            this.name = name;
            this.textArea = textArea;
            this.overlay = overlay;
            this.mediaView = mediaView;
            this.videoPlayer = videoPlayer;
            this.emotionMenu = emotionMenu;
            this.emotionStrengthMeter = emotionStrengthMeter;
            this.emotionConfidenceMeter = emotionConfidenceMeter;
            this.strengthConfidenceMeter = strengthConfidenceMeter;
            this.emotionPromptText = emotionPromptText;
            this.strengthPromptText = strengthPromptText;
            this.emotionConfidencePromptText = emotionConfidencePromptText;
            this.strengthConfidencePromptText = strengthConfidencePromptText;
            this.swapButton = swapButton;
            this.quitButton = quitButton;

        } catch(IOException e){
            System.out.println("Could not connect.");
            e.printStackTrace();
            return;
        }
    }

    public void sendMessage(String message){
        out.println(MESSAGE + message);
    }

    public void setMessageTimestamp(String messageTimestamp){
        this.messageSentTimestamp = messageTimestamp;
    }

    public void sendData(String video, String emotion, double emotionStrength, double emotionConfidence,
                         double emotionStrengthConfidence, int numTimesReplayed, String senderTimestamp){
        out.println(SENDER_DATA + "," + video + "," + emotion + "," + emotionStrength + "," + emotionConfidence + "," +
                emotionStrengthConfidence + "," + numTimesReplayed + "," + senderTimestamp + "," + messageSentTimestamp);
    }

    /*
     * Main loop where sender client thread listens for messages from the server.
     */
    @Override
    public void run() {
        try {
            String response = in.nextLine();
            role = response;
            out.println(SENDER_NAME + name);
            while (in.hasNextLine()){
                response = in.nextLine();
                if (response.startsWith(VIDEO)){
                    this.currentVideo = response.substring(1);
                } else if (response.startsWith(MESSAGE)){
                    String msg = response.substring(1);
                    this.textArea.appendText(msg + "\n");
                } else if (response.equals(CLIENTS_CONNECTED)){
                    // disable "waiting.." overlay
                    disableOverlay();
                } else if (response.startsWith(SHOW_FEEDBACK)){
                    // disable "waiting.." overlay
                    disableOverlay();
                    String[] data = response.split(",");
                    Platform.runLater(() -> this.emotionMenu.setValue(data[1]));
                    this.emotionStrengthMeter.setValue(Double.valueOf(data[2]));
                    this.emotionConfidenceMeter.setValue(Double.valueOf(data[3]));
                    this.strengthConfidenceMeter.setValue(Double.valueOf(data[4]));
                    this.emotionPromptText.setText("Receiver's entered emotion:");
                    this.strengthPromptText.setText("Receiver's entered emotion strength:");
                    this.emotionConfidencePromptText.setText("Receiver's confidence in emotion:");
                    this.strengthConfidencePromptText.setText("Receiver's confidence in emotion strength:");
                    Thread.sleep(5000);
                    resetDisplay();
                } else if (response.startsWith(SWAP)){
                    this.swapButton.setVisible(true);
                    this.swapButton.setDisable(false);
                    GraphicsContext gc = this.overlay.getGraphicsContext2D();
                    gc.setFill(Color.color(0.5, 0.5, 0.5, 0.9));
                    gc.clearRect(0, 0, 1000, 800);
                    gc.fillRect(0, 0, 1000, 800);
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.setTextBaseline(VPos.TOP);
                    gc.setFont(Font.font(40.0));
                    gc.setFill(Color.BLACK);
                    gc.fillText(
                            "Your turn to be the receiver!",
                            Math.round(overlay.getWidth()  / 2),
                            Math.round(overlay.getHeight() / 2)
                    );
                    this.overlay.setDisable(false);
                    this.overlay.setVisible(true);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e){
                System.out.println("Could not close Sender.");
                e.printStackTrace();
            }

        }
    }

    public void resetDisplay(){
        if (!this.videoPlayer.loadNextVideo()){
            this.gameEnded = true;
            this.gameOver();
            out.println(SENDER_QUIT);
            return;
        }
        this.mediaView.setMediaPlayer(videoPlayer.getMediaPlayer());
        Platform.runLater(() -> this.emotionMenu.setValue("(Select one)"));
        this.emotionStrengthMeter.setValue(0);
        this.emotionConfidenceMeter.setValue(0);
        this.strengthConfidenceMeter.setValue(0);
        this.emotionPromptText.setText("Identify the emotion in the video:");
        this.strengthPromptText.setText("Rate the strength of this emotion:");
        this.emotionConfidencePromptText.setText("How confident are you that the receiver will choose" +
                " the correct emotion?");
        this.strengthConfidencePromptText.setText("How confident are you that the receiver will " +
                "accurately rate the strength of the emotion?");
    }

    public void disableOverlay(){
        this.overlay.setVisible(false);
        this.overlay.setDisable(true);
    }

    public void gameOver(){
        this.quitButton.setVisible(true);
        this.quitButton.setDisable(false);
        GraphicsContext gc = this.overlay.getGraphicsContext2D();
        gc.setFill(Color.color(0.5, 0.5, 0.5, 0.9));
        gc.clearRect(0, 0, 1000, 800);
        gc.fillRect(0, 0, 1000, 800);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.setFont(Font.font(40.0));
        gc.setFill(Color.BLACK);
        gc.fillText(
                "Game over!",
                Math.round(overlay.getWidth()  / 2),
                Math.round(overlay.getHeight() / 2)
        );
        this.overlay.setDisable(false);
        this.overlay.setVisible(true);
        try {
            Thread.sleep(2000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
