package main;

import protocol.DisplayMessage;
import game.GameState;
import protocol.GuessMessage;
import protocol.HelloMessage;
import game.KnownDisplays;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * GameMaster = serveur central du jeu du pendu.
 *
 * Responsabilités (selon le sujet) :
 * 1) Écouter sur un port (ici 2025)
 * 2) Recevoir des messages de type HELLO (enregistrer les PlayerDisplay)
 * 3) Recevoir des messages de type GUESS (mise à jour de l'état de jeu)
 * 4) Après un GUESS valide, envoyer un DISPLAY à tous les PlayerDisplay connus
 *
 * Contraintes :
 * - Mono-thread : pas de thread, on traite une connexion à la fois.
 * - Le mot secret est fourni en argument du programme (args[0]).
 */
public class GameMaster {

    /** Port TCP d'écoute du GameMaster */
    private static final int MASTER_PORT = 2025;

    /**
     * Point d'entrée.
     * Usage: java main.GameMaster <secretWord>
     */
    public static void main(String[] args) {
        // -------- 1) Vérification des arguments (mode "refuse strict") --------
        if (args.length != 1) {
            System.err.println("Usage: java main.GameMaster <secretWord>");
            System.exit(1);
        }

        String secretWord = args[0].trim().toLowerCase();
        if (secretWord.isBlank()) {
            System.err.println("Mot secret invalide: vide.");
            System.exit(1);
        }

        // Refuse strict: uniquement a-z (pas d'accents, pas d'espaces, pas de chiffres)
        for (int i = 0; i < secretWord.length(); i++) {
            char c = secretWord.charAt(i);
            if (c < 'a' || c > 'z') {
                System.err.println("Mot invalide: seulement les caractères a-z sont acceptés.");
                System.exit(1);
            }
        }

        // -------- 2) Initialisation du jeu et de la liste des displays --------
        KnownDisplays knownDisplays = new KnownDisplays();
        GameState gameState = new GameState(secretWord);

        // -------- 3) Lancement serveur --------
        try (ServerSocket serverSocket = new ServerSocket(MASTER_PORT)) {
            System.out.println("GameMaster démarré sur le port " + MASTER_PORT);
            System.out.println("Mot secret chargé (" + secretWord.length() + " lettres).");

            // Boucle infinie : on accepte une connexion, on traite, on ferme.
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket, knownDisplays, gameState);
                } catch (Exception e) {
                    // Important: on ne veut pas que le serveur meure pour un client buggué
                    System.err.println("[GameMaster] Erreur pendant le traitement d'un client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[GameMaster] Impossible de démarrer le serveur: " + e.getMessage());
        }
    }

    /**
     * Traite une connexion entrante.
     * Le client envoie soit:
     * - HELLO\n<ip>\n<port>\n
     * - GUESS\n<letter>\n
     */
    private static void handleClient(Socket clientSocket, KnownDisplays knownDisplays, GameState gameState) throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
        );

        // 1) Lire l'entête (HELLO ou GUESS), et être tolérant (trim)
        String header = reader.readLine();
        if (header == null) {
            return; // connexion vide
        }
        header = header.trim();

        // 2) Dispatcher selon le type de message
        if ("HELLO".equals(header)) {
            handleHello(reader, knownDisplays);

        } else if ("GUESS".equals(header)) {
            handleGuess(reader, knownDisplays, gameState);

        } else {
            // Message inconnu => on ignore
            System.err.println("[GameMaster] Header inconnu: " + header);
        }
    }

    /**
     * Traitement du message HELLO :
     * On lit ip + port puis on enregistre dans KnownDisplays.
     */
    private static void handleHello(BufferedReader reader, KnownDisplays knownDisplays) throws IOException {
        try {
            HelloMessage hello = new HelloMessage(reader); // lit ip + port depuis le reader
            knownDisplays.add(hello.getIp(), hello.getPort());
        } catch (Exception e) {
            System.err.println("[GameMaster] HELLO invalide: " + e.getMessage());
        }
    }

    /**
     * Traitement du message GUESS :
     * - lit la lettre
     * - valide via GuessMessage(String) (ton parse réseau)
     * - met à jour GameState
     * - diffuse un DisplayMessage à tous les displays enregistrés
     *
     * Choix demandé: si GUESS invalide => on ignore et on ne diffuse pas (A).
     */
    private static void handleGuess(BufferedReader reader, KnownDisplays knownDisplays, GameState gameState) throws IOException {

        String guessLine = reader.readLine();
        if (guessLine == null) {
            System.err.println("[GameMaster] GUESS incomplet (lettre manquante).");
            return;
        }

        // Validation protocolaire via ta classe GuessMessage (réception)
        GuessMessage guessMsg;
        try {
            // Ici on utilise le constructeur qui valide "a-z ou _"
            guessMsg = new GuessMessage(guessLine);
        } catch (IllegalArgumentException e) {
            // Choix A: on ignore, pas de diffusion
            System.err.println("[GameMaster] GUESS invalide ignoré: " + e.getMessage());
            return;
        }

        char letter = guessMsg.getGuess();

        // Mise à jour du jeu
        gameState.guessLetter(letter);

        // Construire la string des lettres proposées dans l'ordre, séparées par ", "
        String proposedLetters = gameState.getGuessedLettersString();

        // Déterminer l'état du jeu (WIN/LOSE/PLAYING)
        String state = gameState.getCurrentState().name();
        // Créer le DisplayMessage
        String maskedWord = gameState.getMaskedWord();
        String errors = String.valueOf(gameState.getErrorCount());

        DisplayMessage displayMessage = new DisplayMessage(maskedWord, proposedLetters,errors,state);

        // Diffuser à tous les PlayerDisplay enregistrés
        broadcastDisplay(knownDisplays.getAll(), displayMessage);
    }

    /**
     * Envoie le DISPLAY à tous les displays connus.
     * Choix demandé: si un display est HS => on ignore l'erreur et on continue (A).
     */
    private static void broadcastDisplay(List<KnownDisplays.PlayerEntry> targets, DisplayMessage displayMessage) {
        for (KnownDisplays.PlayerEntry entry : targets) {
            try (Socket displaySocket = new Socket(entry.getIp(), entry.getPort());
                 PrintWriter out = new PrintWriter(
                         new OutputStreamWriter(displaySocket.getOutputStream(), StandardCharsets.UTF_8),
                         true
                 )) {

                // Envoi du message réseau
                // print() pour respecter exactement les \n fournis par toNetworkString()
                out.print(displayMessage.toNetworkString());
                out.flush();

            } catch (IOException e) {
                // Choix A: on ignore
                System.err.println("[GameMaster] Impossible d'envoyer DISPLAY à " + entry + ": " + e.getMessage());
            }
        }
    }
}

