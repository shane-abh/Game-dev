package rps;

import java.io.Serializable;

public class Opponent implements Serializable {
    public String ip;
    public Integer port;
    public String username;

    public Opponent(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        this.username = port.toString();
    }

    public Opponent(String ip, Integer port, String username) {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Opponent opponent = (Opponent) o;

        if (ip != null ? !ip.equals(opponent.ip) : opponent.ip != null) return false;
        if (port != null ? !port.equals(opponent.port) : opponent.port != null) return false;
        return username != null ? username.equals(opponent.username) : opponent.username == null;
    }

    @Override
    public String toString() {
        return username + " [" + ip + ":" + port + "]";
    }
}
