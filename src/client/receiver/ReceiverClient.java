package client.receiver;

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

public class ReceiverClient implements Runnable, CommunicationConstants {
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private String name;
    private String role;
    private String currentVideo;
    private String receiverInfo;
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

    public ReceiverClient(String server, int port, String name, TextArea textArea, Canvas overlay, MediaView mediaView,
                          VideoPlayer videoPlayer, ComboBox emotionMenu, Slider emotionStrengthMeter,
                          Slider emotionConfidenceMeter, Slider strengthConfidenceMeter, Text emotionPromptText,
                          Text strengthPromptText, Text emotionConfidencePromptText, Text strengthConfidencePromptText,
                          Button swapButton, Button quitButton){
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
        // out.println(MESSAGE_SENT + messageTimestamp);
    }

    public void sendData(String emotion, double emotionStrength, double emotionConfidence,
                         double emotionStrengthConfidence, String receiverTimestamp){
        out.println(RECEIVER_DATA + "," + emotion + "," + emotionStrength + "," + emotionConfidence + "," +
                emotionStrengthConfidence + "," + receiverTimestamp);
    }

    /*
     * Main loop where receiver client thread listens for messages from the server.
     */
    @Override
    public void run() {
        try {
            String response = in.nextLine();
            role = response;
            out.println(RECEIVER_NAME + name);
            while (in.hasNextLine()){
                response = in.nextLine();
                if (response.startsWith(VIDEO)){
                    this.currentVideo = response.substring(1);
                } else if (response.startsWith(MESSAGE)){
                    String msg = response.substring(1);
                    this.textArea.appendText(msg + "\n");
                } else if (response.startsWith(SHOW_FEEDBACK)){
                    videoPlayer.getMediaPlayer().setOnEndOfMedia(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.resetDisplay();
                        // Platform.runLater(() -> this.resetDisplay());
                    });
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
                            "Your turn to be the sender!",
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
                System.out.println("Could not close Receiver.");
                e.printStackTrace();
            }
        }
    }

    public void resetDisplay(){
        if (!this.videoPlayer.loadNextVideo()){
            this.gameEnded = true;
            Platform.runLater(() -> this.gameOver());
            out.println(RECEIVER_QUIT);
            return;
        }
        this.mediaView.setVisible(false);
        this.mediaView.setDisable(true);
        this.mediaView.setMediaPlayer(videoPlayer.getMediaPlayer());
        disableOverlay();
        this.emotionMenu.setValue("(Select one)");
        this.emotionStrengthMeter.setValue(0);
        this.emotionConfidenceMeter.setValue(0);
        this.strengthConfidenceMeter.setValue(0);
    }

    public void disableOverlay(){
        this.overlay.setVisible(false);
        this.overlay.setDisable(true);
    }

    public void gameOver(){
        this.mediaView.setVisible(false);
        this.mediaView.setDisable(true);
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

