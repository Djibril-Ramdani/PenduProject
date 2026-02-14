package game;

import java.util.ArrayList;
import java.util.List;


public class KnownDisplays {

    // classe interne pour stocker IP/Port
    public static class PlayerEntry {
        private final String ip;
        private final int port;

        public PlayerEntry(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public String getIp() { return ip; }
        public int getPort() { return port; }

        @Override
        public String toString() {
            return ip + ":" + port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerEntry that = (PlayerEntry) o;
            return port == that.port && ip.equals(that.ip);
        }

        @Override
        public int hashCode() {
            int result = ip.hashCode();
            result = 31 * result + port;
            return result;
        }
    }

    // La liste qui contient tous les joueurs
    private final List<PlayerEntry> displays;

    public KnownDisplays() {
        this.displays = new ArrayList<>();
    }


    public void add(String ip, int port) {
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("Adresse IP n'est pas valide.");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Le port doit être entre 1 et 65535");
        }

        PlayerEntry newPlayer = new PlayerEntry(ip, port);

        // éviter les doublons
        if (displays.contains(newPlayer)) {
            System.out.println("Joueur déjà enregistré : " + newPlayer);
            return;
        }

        displays.add(newPlayer);
        System.out.println("Nouveau joueur enregistré : " + newPlayer);
    }

    public List<PlayerEntry> getAll() {
        return new ArrayList<>(displays);
    }

}