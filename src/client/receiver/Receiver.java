package client.receiver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Receiver extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("receiver_screen.fxml"));
        primaryStage.setTitle("Emotions Through Text: Receiver");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/game/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event->System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
