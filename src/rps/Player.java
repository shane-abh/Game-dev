package rps;

import java.io.IOException;
import rps.OpponentConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/*
* This class contians the main logic of the game.
* It compares the moves between the opponents and decides who scores a point.
* It is also responsible for adding opponents and makes sure each move they make is legal or not. 
*/

public class Player {
    public String ip;
    public Integer port;
    public String username;
    

    private HashMap<Integer, String> moves;                 //stores user's moves
    private HashMap<Integer, ArrayList<String>> opponentMoves;      //stores opponents moves
    private ArrayList<Opponent> opponents;      //stores number of opponents joined in one game.
    
    //Variables used to make the game functional
    private int roundCount = 0;
    private int score = 0;
    private int losses = 0;
    private int wins = 0;
    private int levels = 0;
    public String winner;
    int lost = 0;

    public boolean canStartNewRound = false;

    public Player(String ip, Integer port) throws UnknownHostException {
        this.opponents = new ArrayList<Opponent>();
        this.opponentMoves = new HashMap<Integer, ArrayList<String>>();
        this.moves = new HashMap<Integer, String>();
        this.ip = ip;
        this.port = port;
        this.username = port.toString();
    }

    
    /*
        Adds new Opponet and send the other opponent a message
    */
    public boolean addOpponent(Opponent opponent) {
        if (opponents.contains(opponent) || (ip.equals(opponent.ip) && port.equals(opponent.port))) {
            return false;
        }

        opponents.add(opponent);
        CompletableFuture.runAsync(() -> {
            try {
                OpponentConnection oppConnection = new OpponentConnection();
                oppConnection.connect(opponent);
                oppConnection.announceMyself(this.toOpponent());
                oppConnection.disconnect();
            } catch (Exception e) {
                System.out.println("Announce failed.");
            }
        });

        return true;
    }

    public ArrayList<Opponent> addOpponents(ArrayList<Opponent> opponents) {
        ArrayList<Opponent> added = new ArrayList<Opponent>();
        for (Opponent opponent : opponents) {
            if (addOpponent(opponent)) {
                added.add(opponent);
            }
        }

        return added;
    }

    /*
        Used while sending a message to the other player
    */
    public Opponent toOpponent() {
        return new Opponent(ip, port, username);
    }

    public HashMap<Integer, String> getMoves() {
        return moves;
    }

    public ArrayList<Opponent> getOpponents() {
        return opponents;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public int getScore() {
        return score;
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public int getLevels() {
        return levels;
    }

    public String getWinner() {
        return winner;
    }
    
    

    @Override
    public String toString() {
        return "P - " + username + " [" + ip + ":" + port + "]";
    }
    
    /*
        Each time a player clicks on an option this function is invoked and the other user is 
        notified about move.
        Stores player moves inside the HashMap.
        Updates score after each move
    */
    public void makeMove(String move) {
        if (!move.equals(Game.MOVE_ROCK) && !move.equals(Game.MOVE_PAPER) && !move.equals(Game.MOVE_SCISSORS)) {
            throw new IllegalArgumentException("Illegal move detected!");
        }

        canStartNewRound = false;
        moves.put(roundCount, move);
        int round = roundCount;

        //Notifies the other player 
        for (Opponent opponent : opponents) {
            CompletableFuture.runAsync(() -> {
                try {
                    OpponentConnection oppConnection = new OpponentConnection();
                    oppConnection.connect(opponent);
                    oppConnection.sendMove(this.toOpponent(), move, round);
                    oppConnection.disconnect();
                } catch (IOException e) {
                    System.out.println("Unable to send move.");
                }
            });
        }

        updateScore();
    }
    

    /*
        Stores opponent moves in the HashMap
    */
    public boolean addOpponentMove(Opponent opponent, String move, int round) {
        if (!opponents.contains(opponent)) {
            // only allow known opponents to participate.
            return false;
        }

        if (!opponentMoves.containsKey(round)) {
            opponentMoves.put(round, new ArrayList<String>());
        }

        opponentMoves.get(round).add(move);
        updateScore();
        return true;
    }

    protected void updateScore() {
        int score = 0;
        int losses = 0;
        int tie=0;
        
        boolean isTie= false;
        
        
        //Decides who gets the point
        for (int i = 0; i <= this.roundCount; i++) {
            //Checks if both players have chosen an option
            if (!this.moves.containsKey(i) || !this.opponentMoves.containsKey(i)) 
                continue;

            for (String opponentMove : this.opponentMoves.get(i)) {
                //Compares the player move and the opponent move using the Game class
                switch (Game.play(this.moves.get(i), opponentMove)) {
                    case 1:
                        score++;
                        break;
                    case 0:
                        losses++;
                        break;
                    default:
                        tie++;
                        isTie = true;
                        break;
                }
               
            }
        }

        this.score = score;
        this.losses = losses;

        // check whether we can start a new round
        if (!this.moves.containsKey(this.roundCount)) return;
        if (getNumberOfPlays(this.roundCount) == 0) return;
        if (this.opponents.size() != getNumberOfPlays(this.roundCount)) return;

        this.roundCount++;
        
        //Increments level after three rounds
        if(this.roundCount%3==0){

            if(!isTie){

                //Checks who scored in each level
                if(this.score>this.losses){
                    this.wins++;
                }else if(this.score<this.losses){
                    this.lost++;
                }else if(this.score == this.losses){
                    System.out.println("Its a tie");
                }
                    isTie=false;                  
            }
                this.levels++;
            
        }
       
        canStartNewRound = true;
    }

    public int getNumberOfPlays(int turn) {
        if (!this.opponentMoves.containsKey(turn)) return 0;
        return this.opponentMoves.get(turn).size();
    }
}
