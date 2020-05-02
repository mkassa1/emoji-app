package game;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Game implements CommunicationConstants {
    private Player sender;
    private Player receiver;
    private int turns = -1;
    private int subRounds = 0;
    private int round = 1;
    private int score = 0;
    private FileWriter fw;
    private File outputFile;
    private String guess;
    private String answer;

    // game data
    private String videoSenderViewed;
    private int numTimesPlayed;
    private String message;
    private String messageSentTimestamp;
    private String senderEmotion;
    private String senderEmotionIntensity;
    private String senderConfidenceEmotion;
    private String senderConfidenceIntensity;
    private String senderTimestamp;
    private String receiverEmotion;
    private String receiverEmotionIntensity;
    private String receiverConfidenceEmotion;
    private String receiverConfidenceIntensity;
    private String receiverTimestamp;

    public class Player implements Runnable, CommunicationConstants {
        private Socket socket;
        private Scanner input;
        private PrintWriter output;
        private String name;
        private String role;
        private Player opponent;
        private boolean closed = false;
        private boolean gameEnded;

        public Player(Socket socket, String role) {
            this.socket = socket;
            this.role = role;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void run() {
            try {
                setup();
                setUpOutFile();
                setTurns();
                processCommands();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (opponent != null && opponent.output != null && !gameEnded) {
                    opponent.output.println("OPPONENT LEFT");
                    gameEnded = true;
                }
                try {
                    socket.close();
                    fw.flush();
                    fw.close();
                } catch (IOException e) {}
            }
        }

        // initializes the sender and receiver
        private void setup() throws IOException {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);

            output.println("WELCOME " + role);

            if (role.equals("Sender")) {
                sender = this;
                output.println("MESSAGE Waiting for receiver to connect...");
                sender.output.println("INSN");
            } else if (role.equals("Receiver")) {
                receiver = this;
                opponent = sender;
                sender.opponent = this;

                receiver.output.println("INSN");

                receiver.output.println("MESSAGE Waiting for game to start...");
                sender.output.println("MESSAGE Waiting for game to start...");
                sender.output.println(CLIENTS_CONNECTED);
            }
        }

        // opens output file where results will be written
        private void setUpOutFile() throws Exception {
            outputFile = new File("src\\data\\out.txt");
            fw = new FileWriter(outputFile);
        }

        /* reads input from "turns.txt" to set the 'turns' parameter
         *  -1 or 0: players never swap
         *  1: players swap every other round
         *  n: players swap after n rounds
         */
        private void setTurns() {
            int t;
            try {
                File f = new File("src\\data\\turns.txt");
                FileReader r = new FileReader(f);
                t = (int) r.read();
                r.close();
            } catch(Exception e) {
                t = -1;
            }
            turns = t - '0';
        }

        /* main loop where server thread listens for messages from sender and receiver clients
         * primarily takes in data from client to write to results file
         */
        private void processCommands() throws IOException {
            while (input.hasNextLine()) {
                String command = input.nextLine();
                if (command.startsWith("QUIT")) {
                    gameEnded = true;
                    try {
                        fw.write(command.substring(5));
                        fw.write("\n");
                    } catch (IOException e) {
                        fw = new FileWriter(outputFile, true);
                        fw.write(command.substring(5));
                    } catch (Exception e) {}
                    return;
                } else if (command.startsWith(RECEIVER_NAME)){
                    receiver.setName(command.substring(1));
                } else if (command.startsWith(SENDER_NAME)){
                    sender.setName(command.substring(1));
                }
                else if (command.startsWith(MESSAGE)){ // for text messages between receiver + sender
                    String msg = command.substring(1);
                    message = msg.substring(msg.indexOf(">") + 2);
                    sender.output.println(MESSAGE + msg);
                    receiver.output.println(MESSAGE + msg);
                } else if (command.startsWith(RECEIVER_DATA)){
                    String[] data = command.split(",");
                    receiverEmotion = data[1];
                    receiverEmotionIntensity = data[2];
                    receiverConfidenceEmotion = data[3];
                    receiverConfidenceIntensity = data[4];
                    receiverTimestamp = data[5];
                    sender.output.println(SHOW_FEEDBACK + "," + receiverEmotion + "," + receiverEmotionIntensity + ","
                            + receiverConfidenceEmotion + "," + receiverConfidenceIntensity);
                    writeResults();
                    if (subRounds == turns) {
                        swapRoles();
                    }
                } else if (command.startsWith(SENDER_DATA)){
                    String[] data = command.split(",");
                    String video = data[1];
                    videoSenderViewed = video.substring(video.lastIndexOf("/") + 1);
                    senderEmotion = data[2];
                    senderEmotionIntensity = data[3];
                    senderConfidenceEmotion = data[4];
                    senderConfidenceIntensity = data[5];
                    numTimesPlayed = Integer.valueOf(data[6]);
                    senderTimestamp = data[7];
                    messageSentTimestamp = data[8];
                    receiver.output.println(SHOW_FEEDBACK + video);
                } else if (command.startsWith(SENDER_QUIT) | command.startsWith(RECEIVER_QUIT)){
                    return;
                } else if (command.startsWith(RECEIVER_QUIT)){
                    return;
                } else if (command.startsWith(MESSAGE_SENT)){
                    messageSentTimestamp = command.substring(2);
                } else if (command.startsWith("CLOSED INSN")) {
                    char mark = command.charAt(12);
                    boolean lastToClose = false;
                    if (mark == 'S') {
                        sender.closed = true;
                        if (receiver.closed) {
                            lastToClose = true;
                        } else {
                            sender.output.println("MESSAGE Waiting for game to start...");
                        }
                    } else if (mark == 'R') {
                        receiver.closed = true;
                        if (sender != null && sender.closed) {
                            lastToClose = true;
                        }
                    }
                    if (lastToClose) {
                        sender.output.println("MESSAGE Guess an image. Score: 0");
                        receiver.output.println("MESSAGE Act out the highlighted image. Score: 0");
                    }
                }
            }
        }
    }

    /*public boolean gameEnded() {
        return gameEnded;
    } */

    public void swapRoles() {
        subRounds = 0;
        Player temp = receiver;
        receiver = sender;
        sender = temp;

        receiver.output.println(SWAP);
        sender.output.println(SWAP);
    }

    public void writeResults() {
        try {
            fw.write("---ROUND " + round + "---" + "\n");
            fw.write("Sender: " + sender.getName() + " | Receiver: " + receiver.getName() + "\n");
            fw.write("Video: " + videoSenderViewed + "\n");
            fw.write("Number of times video was played: " + numTimesPlayed + "\n");
            fw.write("Sender sent this message: " + message + " [" + messageSentTimestamp + "]" +  "\n");
            fw.write("Sender's emotion: " + senderEmotion + " | ");
            fw.write("Sender's emotion intensity: " + senderEmotionIntensity + " | ");
            fw.write("Sender's confidence in receiver's emotion choice: " + senderConfidenceEmotion + " | ");
            fw.write("Sender's confidence in receiver's emotion intensity choice: " + senderConfidenceIntensity + " [" + senderTimestamp + "]" + "\n");
            fw.write("Receiver's emotion: " + receiverEmotion + " | ");
            fw.write("Receiver's emotion intensity: " + receiverEmotionIntensity + " | ");
            fw.write("Receiver's confidence in chosen emotion: " + receiverConfidenceEmotion + " | ");
            fw.write("Receiver's confidence in chosen emotion intensity: " + receiverConfidenceIntensity + " [" + receiverTimestamp + "]" + "\n");

            round++;
            if (turns != -1) {
                subRounds++;
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
