package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère la liste des joueurs enregistrés (couples IP + Port).
 * Référence PDF : "KnownDisplays: gère la liste des couples (IP, port) des PlayerDisplay."
 */
public class KnownDisplays {

    // Une petite classe interne pour stocker proprement le couple IP/Port
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

    /**
     * Enregistre un nouveau PlayerDisplay.
     * Appelé quand le GameMaster reçoit un message HELLO.
     * "Le GameMaster enregistre le couple (IP, port)"
     */
    public void add(String ip, int port) {
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("Adresse IP n'est pas valide.");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Le port doit être entre 1 et 65535");
        }

        PlayerEntry newPlayer = new PlayerEntry(ip, port);

        // Modif 3: éviter les doublons
        if (displays.contains(newPlayer)) {
            System.out.println("Joueur déjà enregistré : " + newPlayer);
            return;
        }

        displays.add(newPlayer);
        System.out.println("Nouveau joueur enregistré : " + newPlayer);
    }

    /**
     * Retourne la liste des joueurs pour permettre la diffusion.
     * Le GameMaster va boucler sur cette liste pour envoyer les messages DISPLAY.
     * "...envoie l'état complet du jeu à tous les PlayerDisplay enregistrés"
     */
    // Remplace getAll() par celui-ci (Modif 2)
    public List<PlayerEntry> getAll() {
        return new ArrayList<>(displays);
    }

}