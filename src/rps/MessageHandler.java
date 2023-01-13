package rps;

import rps.Player;
import rps.Message;
import rps.Opponent;
import rps.Observer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
    Class used for Handling message type and sending them using the ObjectOutputStream
*/

public class MessageHandler {
    private Socket socket;
    Observer observer;

    public MessageHandler(Socket socket, Observer observer) {
        this.socket = socket;
        this.observer = observer;
    }

    public void handle() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // receive message from opponent
            Message message = (Message) in.readObject();
            Opponent opponent = null;

            switch (message.type) {
                case Message.TYPE_JOIN:
                    opponent = message.opponent;
                    Player player = observer.opponentWantsToJoin(opponent, message.opponents);

                    // respond
                    Message msg = new Message(Message.TYPE_JOIN, player.toOpponent(), player.getOpponents());
                    out.writeObject(msg);
                    break;
                case Message.TYPE_ANNOUNCE:
                    observer.opponentAnnouncesHimself(message.opponent);
                    break;
                case Message.TYPE_MOVE:
                    observer.opponentSentMove(message.opponent, message.move, message.round);
                    break;
                default:
                    System.out.println("Unsupported message received...");
            }

            socket.close();
        } catch (Exception e) {
            System.out.println("MessageHandler error --> " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}
