package main;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import protocol.GuessMessage;
import protocol.HelloMessage;

public class PlayerGuesser {

    // Port fixe du GameMaster
    private static final int GM_PORT = 2025;

    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length != 3) {
            System.err.println("Usage: java PlayerGuesser <IP_GameMaster> <IP_PlayerDisplay> <Port_PlayerDisplay>");
            return;
        }

        String gmIp = args[0];
        String displayIp = args[1];
        int displayPort = Integer.parseInt(args[2]);

        try {
            // Envoi automatique du message HELLO au démarrage
            System.out.println("Connexion au GameMaster (" + gmIp + ")...");

            // Utilisation de la méthode spécifique pour HELLO
            sendHelloCustom(gmIp, displayIp, displayPort);

            System.out.println("Enregistré avec succès !");

            // Boucle de jeu
            Scanner scanner = new Scanner(System.in);
            boolean playing = true;

            while (playing) {
                System.out.print("Entrez une lettre (ou '_' pour quitter) : ");
                String input = scanner.nextLine().trim();

                // Si ligne vide, on ignore et on recommence
                if (input.isEmpty()) continue;

                // Si "_", on arrête le programme
                if (input.equals("_")) {
                    System.out.println("Arrêt du PlayerGuesser.");
                    playing = false;
                    sendGuess(gmIp, '_');
                    break;
                }

                // Vérification locale : doit être une seule lettre
                if (input.length() != 1) {
                    System.out.println("Erreur : Veuillez entrer une seule lettre.");
                    continue;
                }

                char letter = input.charAt(0);

               // Tentative d'envoi du message GUESS
                try {
                    sendGuess(gmIp, letter);
                    // Si succès, on revient au début de la boucle
                } catch (IOException e) {
                    // En cas d'échec réseau
                    System.err.println("Erreur de communication avec le GameMaster : " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    // Si la lettre est rejetée
                    System.err.println("Lettre invalide : " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur critique : Impossible de joindre le GameMaster.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur de configuration HELLO : " + e.getMessage());
        }
    }

    // --- FONCTIONS RÉSEAU ---

    /**
     * Envoie le message HELLO en utilisant TA classe HelloMessage.
     * Utilise tes méthodes verificationHello() et envoyerHello().
     */
    private static void sendHelloCustom(String gmIp, String myIp, int myPort) throws IOException {
        // 1. Création de l'objet message
        HelloMessage msg = new HelloMessage(myIp, myPort);

        // 2. Vérification avec ta méthode
        if (!msg.verificationHello()) {
            throw new IllegalArgumentException("Message HELLO invalide (IP vide ou Port 2025 utilisé).");
        }

        // 3. Connexion et envoi
        try (Socket socket = new Socket(gmIp, GM_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            // On récupère la string formatée via ta méthode envoyerHello()
            // envoyerHello() retourne "HELLO \n IP \n PORT"
            writer.print(msg.envoyerHello());
        }
    }

    /**
     * Envoie le message GUESS au GameMaster.
     * Utilise la classe GuessMessage standard du PDF.
     */
    private static void sendGuess(String gmIp, char letter) throws IOException {
        // Création et validation du message
        GuessMessage msg = new GuessMessage(letter);

        // Connexion et envoi [cite: 55]
        try (Socket socket = new Socket(gmIp, GM_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            // On utilise toNetworkString() comme défini dans le GuessMessage standard
            writer.print(msg.toNetworkString());
        }
    }
}