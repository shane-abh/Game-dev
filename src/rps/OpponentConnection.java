package rps;

import rps.Message;
import rps.Opponent;
import rps.Player;
import rps.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
/*
    This class is used to send the TCP messages between players
*/
public class OpponentConnection {
    private static final int TIMEOUT_READ_BLOCK = 1800000;
    private static final int TIMEOUT_CONNECTION = 30000;

    //checks if the socket is connected or not
    private boolean connected = false;

    private Socket opponentSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /*
       Connects to the new socket using the port and the IP entered by the opponent.
       We use Inputstreams to send messages to one another
    */
    public void connect(Opponent opponent) throws IOException {
        opponentSocket = new Socket();
        opponentSocket.connect(new InetSocketAddress(opponent.ip, opponent.port), TIMEOUT_CONNECTION);
        opponentSocket.setSoTimeout(TIMEOUT_READ_BLOCK);
        connected = true;

        out = new ObjectOutputStream(opponentSocket.getOutputStream());
        in = new ObjectInputStream(opponentSocket.getInputStream());

    }

    /*
        This function is called when a new opponent joins the game and it is used to let the other opponents
        know about them joining in the game.
    */
    public void sendJoinMessage(Player player, Observer gameObserver) throws IOException, ClassNotFoundException {
        Message msg = new Message(Message.TYPE_JOIN, player.toOpponent(), null);
        out.writeObject(msg);

        Message message = (Message) in.readObject();
        Opponent newOpponent = message.opponent;
        gameObserver.opponentWantsToJoin(newOpponent, message.opponents);
    }

    
    /*
        Closes the socket and streams once the message is sent
    */
    public void disconnect() throws IOException {
        in.close();
        out.close();
        opponentSocket.close();
    }

    /*
        Lets everyone know about your username
    */
    public void announceMyself(Opponent opponent) throws IOException {
        Message msg = new Message(Message.TYPE_ANNOUNCE, opponent);
        out.writeObject(msg);
    }

    /*
        This function is used to send messages between players and notify each other after they
        have sent a move
    */
    public void sendMove(Opponent opponent, String move, Integer round) throws IOException {
        Message msg = new Message(Message.TYPE_MOVE, opponent, move, round);
        out.writeObject(msg);
    }
}
