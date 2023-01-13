package rps;

import rps.Opponent;
import rps.Observer;

import java.io.IOException;
import java.net.ServerSocket;

public class ReceiverServer implements Runnable {
    private ServerSocket serverSocket;
    public Integer port;
    public String ip;
    public Observer observer;

    /*
     * This function uses a random port to open a new socket.     
     */
    public ReceiverServer(String ip) {
        this.ip = ip;

        try {
            serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            System.out.println("listening on port: " + port);
        } catch (Exception e) {
            System.out.println("Could not create serverSocket.");
        }
    }

    /*
     *  Starts a new thread accepting socket connections.
     */
    public void start() {
        new Thread(this).start();
    }

    /*
     * Start handling messages and finally close socket.
     */
    @Override
    public void run() {
        try {
            while (true) {
                // handle messages
                new MessageHandler(serverSocket.accept(), observer).handle();
            }
        } catch (IOException e) {
            System.out.println("ReceiverServer experienced an error and will terminate! " + e.getMessage());
        } finally {
            terminate();
        }
    }

     /*
     * Terminates the server.
     */
    public void terminate() {
        if (serverSocket instanceof ServerSocket) {
            try {
                serverSocket.close();
                System.out.println("Port " + port + " closed.");
                port = null;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not close serverSocket.");
            }
        }
    }
}
