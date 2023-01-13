package rps;

import rps.Opponent;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    public static final String TYPE_JOIN = "join";
    public static final String TYPE_ANNOUNCE = "announce";
    public static final String TYPE_MOVE = "move";

    public String type;
    public Opponent opponent;
    public ArrayList<Opponent> opponents;
    public String move;
    public int round;

    /**
     * TYPE_JOIN
     * Constructor used for to convey the number of opponents and how many users joined
     */
    public Message(String type, Opponent opponent, ArrayList<Opponent> opponents) {
        if (!type.equals(TYPE_JOIN)) throw new IllegalArgumentException("Invalid type.");
        this.type = type;
        this.opponent = opponent;
        this.opponents = opponents;

        if (this.opponents == null) this.opponents = new ArrayList<Opponent>();
    }

    /**
     * TYPE_ANNOUNCE
     * Constructor used to announce themselves to all opponents
     */
    public Message(String type, Opponent opponent) {
        if (!type.equals(TYPE_ANNOUNCE)) throw new IllegalArgumentException("Invalid type.");
        this.type = type;
        this.opponent = opponent;
    }

    /**
     * TYPE_MOVE
     * Constructor used to notify player about their move to all opponents
     */
    public Message(String type, Opponent opponent, String move, int round) {
        if (!type.equals(TYPE_MOVE)) throw new IllegalArgumentException("Invalid type.");
        this.type = type;
        this.opponent = opponent;
        this.move = move;
        this.round = round;
    }

    @Override
    public String toString() {
        return type;
    }
}
