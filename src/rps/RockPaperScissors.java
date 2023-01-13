package rps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;

public class RockPaperScissors extends Application {
    private Player player;
    private ReceiverServer server;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();

        // Identify local ip address
        InetAddress localAddress = InetAddress.getLocalHost();
        String ip = localAddress.getHostAddress();

        // Prepare new receiver server
        server = new ReceiverServer(ip);

        // Create player model
        player = new Player(ip, server.port);
    }

    
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Rock, Paper & Scissors");
        Scene scene = new Scene(root, 244.0, 600.0);

        // get controller
        LoginController loginController = (LoginController) loader.getController();
        loginController.init(player, primaryStage, server);

        // show scene
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.terminate();
    }
}
