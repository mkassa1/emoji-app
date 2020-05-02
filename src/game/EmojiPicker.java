package game;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class EmojiPicker extends Application implements Initializable {

    @FXML
    private MenuButton emojiButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField textInput;
    @FXML
    private TextFlow textFlow;
    @FXML
    private HBox hbox;
    // this map will be used to write chosen emoji's unicode to out file
    private HashMap<String, String> emojiCodes;
    private double stringLength = 0.0; // length of the String build up so far
    private double numEmojis = 0.0; // number of emojis placed down so far
    private int maxNumEmojis;
    /*@FXML
    private ScrollPane pane; */

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        /*this.textFlow = new TextFlow();
        // textFlow.setPadding(new Insets(10));
        textFlow.setLineSpacing(10);
        this.textInput = new TextField();
        VBox container = new VBox();
        container.getChildren().addAll(textFlow, new HBox(textInput, emojiButton));
        VBox.setVgrow(textFlow, Priority.ALWAYS); */
        Parent root = FXMLLoader.load(getClass().getResource("emoji_picker.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Emoji Picker");
        stage.setOnCloseRequest(event->System.exit(0));
        stage.show();
    }

    @FXML
    public void onClickButton(ActionEvent event){
        // do nothing
    }

    @FXML
    public void onTyped(ActionEvent event){
        // increment cursor and add spaces!!
    }

    @FXML
    public void onEmojiSelect(ActionEvent event){
        // System.out.println(textInput.getText().length());
        this.stringLength = (textInput.getText().length()*3 + numEmojis);
        // System.out.println(textInput.getText().length());
        // double origPosn = textFlow.getTranslateX();
        // textInput.setLayoutX(stringLength); // not working
        String emoji = (((Control)event.getSource()).getStyle());
        int start = emoji.indexOf("url(") + 4;
        int end = emoji.indexOf(")", start);
        String emojiPath = emoji.substring(start, end);
        ImageView imgV = new ImageView(emojiPath);
        imgV.setFitHeight(20);
        imgV.setFitWidth(20);
        // double lastCharPosn = this.textInput.getText().charAt(textInput.getText().length()).getX();
        imgV.setTranslateX(stringLength);
        // System.out.println(textFlow.getChildren());

        // this.textInput.positionCaret(textInput.getCaretPosition() + 50); // not working either
        textFlow.getChildren().add(imgV);
        // this.textInput.setText(textInput.getText() + "     ");
        // this.stringLength++;
        this.numEmojis++;
        // System.out.println("Emoji style " + emoji.substring(start, end));
        // this.textInput.setLayoutX(this.stringLength);
        //String emojiPath =
        // textInput.setText(textInput.getText() + emojiCodes.get(emoji));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double origFlowPosn = this.textFlow.getTranslateX();
        // this.textFlow.setTranslateX(textInput.getText().length());
        this.emojiButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcons.SMILE_ALT, "20px"));
        // textFlow.setLayoutX(this.textInput.getLayoutX() + 0.5);
        //this.hbox.setHgrow(this.textFlow, Priority.ALWAYS);
        //this.textInput.prefWidthProperty().bind(this.textFlow.widthProperty().subtract(emojiButton.prefWidthProperty()));
        //this.textInput.prefWidthProperty().bind(textFlow.widthProperty().subtract(emojiButton.prefWidthProperty()));

        // emojiButton.setTextFill(Color.YELLOW);
        // Image smileyIcon = new Image(getClass().getResourceAsStream("/data/images/smily-icon.jpg"));
        // smileyIcon = smileyIcon.getScaledInstance(10, 10, Image.SCALE_DEFAULT);
        /*ImageView imgV = new ImageView(smileyIcon);
        imgV.setFitHeight(20);
        imgV.setFitWidth(20);
        this.emojiButton.setGraphic(imgV); */
        scrollPane = new ScrollPane(){
            @Override
            public void requestFocus(){}
        };
        initEmojiCodes();
        // pane.setStyle("-fx-background-color: white;");
    }

    public void initEmojiCodes(){
        this.emojiCodes = new HashMap<String, String>();
        emojiCodes.put("grinning_face", "\ud83d\ude00");
    }

    /*@Override
    public void start(Stage stage)
    {
        // Set title for the stage
        stage.setTitle("Emoji Picker");

        // Create a tile pane
        TilePane r = new TilePane();

        // Create a label
        Label description_label =
                new Label("This is a CustomMenuItem example ");

        // Create a menu
        Menu menu = new Menu("Menu");

        // Create menuitems
        CustomMenuItem menuitem_1 =
                new CustomMenuItem(new Button("MenuItem 1"));
        CustomMenuItem menuitem_2 =
                new CustomMenuItem(new Label("MenuItem 2"));
        CustomMenuItem menuitem_3 =
                new CustomMenuItem(new CheckBox("MenuItem 3"));

        // Add menu items to menu
        menu.getItems().add(menuitem_1);
        menu.getItems().add(menuitem_2);
        menu.getItems().add(menuitem_3);

        // Create a menubar
        MenuBar menubar = new MenuBar();

        // Add menu to menubar
        menubar.getMenus().add(menu);

        // Create a VBox
        VBox vbox = new VBox(menubar);

        // Create a scene
        Scene scene = new Scene(vbox, 200, 200);

        // Set the scene
        stage.setScene(scene);

        stage.show();
    } */
}
