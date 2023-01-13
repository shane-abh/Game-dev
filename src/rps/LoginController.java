/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rps;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class LoginController {
    protected Player player;
    protected Stage primaryStage;
    protected ReceiverServer server;

    @FXML
    public TextField myIpText;

    @FXML
    public TextField usernameInput;

    @FXML
    public TextField joinInput;
    
    @FXML
    //Called when user clicks on New Game button
    public void onNewGameAction(ActionEvent actionEvent) throws IOException {
        changeToGameState();
    }

    @FXML
    //Called when user clicks on Join Game button
    public void onJoinGameAction(ActionEvent actionEvent) throws IOException {
        changeToGameState(true);
    }
    
    @FXML
    //Called when user clicks on Credits button
    public void onCreditsAction(ActionEvent event) throws IOException {
        changeToCreditsState();
    }

    @FXML
    //Called when user clicks on Help button
    public void onHelpAction(ActionEvent event) throws IOException {
        changeToHelpState();
    }

    @FXML
    //Called when user clicks on Quit button
    public void onQuitAction(ActionEvent event) {
        Platform.exit();
    }   
    
    //Used a false call to show that join button wasn't clicked
    protected void changeToGameState() throws IOException {
        changeToGameState(false);
    }

    /*
        Initializes Opponent object and loads the game view and calls the GameController class
    */
    protected void changeToGameState(boolean clickedJoin) throws IOException {
        // Input validation
        if(usernameInput.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setTitle("Warning!!");
             alert.setContentText("Enter Username");
             alert.showAndWait();
            
        }else{
             player.username = usernameInput.getText();      

            // load game view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            Parent root = loader.load();

        
            Opponent opponent = null;
            // checked if join was clicked
            if (clickedJoin) {
                String[] input = joinInput.getText().split(":");
                //input 0 is IP and input 1 is port
                opponent = new Opponent(input[0], Integer.parseInt(input[1]));
            }

            // gets controller
            GameController gameController = (GameController) loader.getController();
            gameController.init(player, primaryStage, server, opponent);

            // show scene
            primaryStage.getScene().setRoot(root);
            primaryStage.show();
        }
    }

    //loads the help submenu
    private void changeToHelpState() throws IOException {
            Parent parent = FXMLLoader.load(getClass().getResource("help.fxml"));
            primaryStage = new Stage(StageStyle.DECORATED);
            primaryStage.setTitle("Rock, Paper & Scissors");
            primaryStage.setScene(new Scene(parent));
            primaryStage.show();
    }

    //loads the credits submenu
    private void changeToCreditsState() throws IOException {
            Parent parent = FXMLLoader.load(getClass().getResource("credits.fxml"));
            primaryStage = new Stage(StageStyle.DECORATED);
            primaryStage.setTitle("Rock, Paper & Scissors");
            primaryStage.setScene(new Scene(parent));
            primaryStage.show();
    }

    /*
     The player's information is updated in the view, and the player and stage are made available to the controller.    
     */
    public void init(Player player, Stage stage, ReceiverServer server) {
        this.player = player;
        this.primaryStage = stage;
        this.server = server;
        
        
        myIpText.setText(player.ip + ":" + player.port);
        usernameInput.setPromptText("Enter Username ");
    }
}
