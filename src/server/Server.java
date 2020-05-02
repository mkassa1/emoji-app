package server;

import game.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket listener;

    public Server(int port) {
        try {
            listener = new ServerSocket(port);
            System.out.println("Server has started...");
            ExecutorService pool = Executors.newFixedThreadPool(200);
            Game game = new Game();
            pool.execute(game.new Player(listener.accept(), "Sender"));
            System.out.println("Sender has connected...");
            pool.execute(game.new Player(listener.accept(), "Receiver"));
            System.out.println("Receiver has connected...");
        } catch (Exception e){
            System.out.println("Error connecting to clients.");
            e.printStackTrace();
        } finally {
            try{
                listener.close();
            } catch(IOException e){
                System.out.println("Could not close Server.");
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new Server(8000);
    }
}
