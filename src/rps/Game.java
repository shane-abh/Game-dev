package rps;

/*
    This class contains the instructions of the game and compares
    the move of the players and opponent and return the following values.
*/
public class Game {
    public static final String MOVE_ROCK = "rock";
    public static final String MOVE_PAPER = "paper";
    public static final String MOVE_SCISSORS = "scissors";

    public static int play(String move, String opponentMove) {
        if (move.equals(MOVE_ROCK) && opponentMove.equals(MOVE_SCISSORS)) return 1;
        if (move.equals(MOVE_PAPER) && opponentMove.equals(MOVE_ROCK)) return 1;
        if (move.equals(MOVE_SCISSORS) && opponentMove.equals(MOVE_PAPER)) return 1;
        
        if(move.equals(opponentMove) || opponentMove.equals(move)){
            return 2;
        }
        return 0;
    }
}
