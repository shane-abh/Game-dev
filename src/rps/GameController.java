/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rps;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.Alert;

/**
 * FXML Controller class
 *
 * @author Lenovo
 */
public class GameController {
    
    //Initializes Objects
    protected Player player;
    protected Stage primaryStage;
    protected ReceiverServer server;
    private final OpponentConnection opponentConnection = new OpponentConnection();

    @FXML
    public Label usernameLabel;

    @FXML
    public GridPane mainGrid;

    @FXML
    public TextField myIpText;

    @FXML
    public Label scoreWinLabel;
    
    @FXML
    public Label wins;
    
    @FXML
    public Label wins_label;
    
    @FXML
    public Label LevelLabel;
    
    @FXML
    public Label level;    

    @FXML
    public Label scoreLossLabel;

    @FXML
    public Label roundNumberLabel;

    @FXML
    public Label playerCountLabel;

    @FXML
    public Button rockBtn;

    @FXML
    public Button paperBtn;

    @FXML
    public Button scissorsBtn;

    @FXML
    public TextArea logTextarea;

    /*
     The player's information is updated in the view, and the player and stage are made available to the controller.     
     */
    public void init(Player player, Stage stage, ReceiverServer server, Opponent opponent) {
        this.player = player;
        this.primaryStage = stage;
        this.server = server;

        // start receiver server
        server.observer = new GameObserver();
        server.start();

        // update view with ip and username
        myIpText.setText(player.ip + ":" + player.port);
        usernameLabel.setText(player.username);
        
        
        //Checking if any other player joined the game
        if (opponent == null) return;

        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            try {
                opponentConnection.connect(opponent);
                opponentConnection.sendJoinMessage(player, new GameObserver());
                opponentConnection.disconnect();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Unable to join game.");
            }
        });
    }
    
    /*
        Called when user clicks on an option
    */

    @FXML
    public void onMoveRockAction(ActionEvent actionEvent) {
        makeMove(Game.MOVE_ROCK);
    }

    @FXML
    public void onMovePaperAction(ActionEvent actionEvent) {
        makeMove(Game.MOVE_PAPER);
    }

    @FXML
    public void onMoveScissorsAction(ActionEvent actionEvent) {
        makeMove(Game.MOVE_SCISSORS);
    }

    /*
        Calls the makeMove function from the player class and disables the 
        buttons of the opponent once the player chooses an option.
    */
    public void makeMove(String move) {
        rockBtn.setDisable(true);
        paperBtn.setDisable(true);
        scissorsBtn.setDisable(true);

        player.makeMove(move);
        server.observer.updateScoreView();
    }

    //Observer is an interface
    private class GameObserver implements Observer {
        
        /**
         * Updates the scores in the UI of each player's interface
        */
        @Override
        public void updateScoreView() {
            Platform.runLater(() -> {
                
                //checks if game is over
                boolean isGameOver = false; 
                
                scoreWinLabel.setText("" + player.getScore());
                scoreLossLabel.setText("" + player.getLosses());
                roundNumberLabel.setText("" + player.getRoundCount());

                wins.setText(""+player.getWins());
                level.setText(""+player.getLevels());
                
               
               
                
                
                if(player.getLevels()==3){
                    
                    isGameOver = true;
                    
                    
                    //Let's player know about the results of the game
                    if(player.getScore()>player.getLosses() ){
                        String p = player.username+ " has won the game";
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Game Over");

                        alert.setContentText(p);
                        alert.showAndWait();
                        server.terminate();
                        
                    }else if (player.getScore()<player.getLosses()){
                        String p = player.username+ " has lost the game";
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Game Over");

                        alert.setContentText(p);
                        alert.showAndWait();
                        server.terminate();
                     }
                     if (player.getScore()==player.getLosses()){
                        String p = "It's a tie";
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Game Over");

                        alert.setContentText(p);
                        alert.showAndWait();
                        server.terminate();
                    }
                    
                
                }
                
                
                if (!player.canStartNewRound) return;

                //Disable button is game is over or else start another round
                if(!isGameOver){
                    rockBtn.setDisable(false);
                    paperBtn.setDisable(false);
                    scissorsBtn.setDisable(false);
                    addLog(" --- Next round! ---");
                    player.canStartNewRound = false;
                }else{
                    rockBtn.setDisable(true);
                    paperBtn.setDisable(true);
                    scissorsBtn.setDisable(true);
                }
            });
        }

        //Displays the Log messages for each user with a timestamp object
        @Override
        public void addLog(String message) {
            Platform.runLater(() -> {
                String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                logTextarea.appendText( usernameLabel.getText() +"("+timestamp + ") :   "+ " "+ message + "\n");
                 
            });
        }
        
        
        //Adds Opponents and updates the UI
        @Override
        public Player opponentWantsToJoin(Opponent opponent, ArrayList<Opponent> opponents) {
            ArrayList<Opponent> newOpponents = new ArrayList<Opponent>();
            if (player.addOpponent(opponent)) {
                newOpponents.add(opponent);
            }

            for (Opponent o : opponents) {
                if (player.addOpponent(o)) {
                    newOpponents.add(o);
                }
            }

            // update view
            Platform.runLater(() -> {
                playerCountLabel.setText("" + player.getOpponents().size());

                for (Opponent o : newOpponents) {
                    addLog(o.username + " joined");
                }
            });

            return player;
        }
        
        
        //Let's the player know that opponent has joined
        @Override
        public void opponentAnnouncesHimself(Opponent newOpponent) {
            if (!player.addOpponent(newOpponent)) return;

            Platform.runLater(() -> {
                playerCountLabel.setText("" + player.getOpponents().size());
                addLog(newOpponent.username + " joined");
            });
        }

        //Let's the player know that opponent has sent a move
        @Override
        public void opponentSentMove(Opponent opponent, String move, int round) {
            if (!player.addOpponentMove(opponent, move, round)) return;

            Platform.runLater(() -> {
                addLog(opponent.username + " sent a move.");
                updateScoreView();
            });
        }
    }
}
