package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère la liste des joueurs enregistrés (couples IP + Port).
 * Référence PDF : "KnownDisplays: gère la liste des couples (IP, port) des PlayerDisplay."
 */
public class KnownDisplay {

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
    }

    // La liste qui contient tous les joueurs
    private final List<PlayerEntry> displays;

    public KnownDisplay() {
        this.displays = new ArrayList<>();
    }

    /**
     * Enregistre un nouveau PlayerDisplay.
     * Appelé quand le GameMaster reçoit un message HELLO.
     * "Le GameMaster enregistre le couple (IP, port)"
     */
    public void add(String ip, int port) {
        // Optionnel : On pourrait vérifier si le joueur existe déjà pour éviter les doublons,
        // mais le sujet ne l'exige pas explicitement. On ajoute simplement.
        PlayerEntry newPlayer = new PlayerEntry(ip, port);
        displays.add(newPlayer);
        System.out.println("Nouveau joueur enregistré : " + newPlayer);
    }

    /**
     * Retourne la liste des joueurs pour permettre la diffusion.
     * Le GameMaster va boucler sur cette liste pour envoyer les messages DISPLAY.
     * "...envoie l'état complet du jeu à tous les PlayerDisplay enregistrés"
     */
    public List<PlayerEntry> getAll() {
        return displays;
    }
}