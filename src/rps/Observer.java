package rps;

import rps.Opponent;
import rps.Player;

import java.util.ArrayList;

//An Interface Implemented in the GameObserver class

public interface Observer {
    public void updateScoreView();
    public void addLog(String message);
    public Player opponentWantsToJoin(Opponent opponent, ArrayList<Opponent> opponents);
    void opponentAnnouncesHimself(Opponent newOpponent);
    void opponentSentMove(Opponent opponent, String move, int round);
}
