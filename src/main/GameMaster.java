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

public class GameMaster {


    private static final int MASTER_PORT = 2025;


    public static void main(String[] args) {
        // Vérification des arguments
        if (args.length != 1) {
            System.err.println("Usage: java main.GameMaster <secretWord>");
            System.exit(1);
        }

        String secretWord = args[0].trim().toLowerCase();
        if (secretWord.isBlank()) {
            System.err.println("Mot secret invalide: vide.");
            System.exit(1);
        }

        // Refu strict: uniquement a-z (pas d'accents, pas d'espaces, pas de chiffres)
        for (int i = 0; i < secretWord.length(); i++) {
            char c = secretWord.charAt(i);
            if (c < 'a' || c > 'z') {
                System.err.println("Mot invalide: seulement les caractères a-z sont acceptés.");
                System.exit(1);
            }
        }

        // Initialisation du jeu et de la liste des displays
        KnownDisplays knownDisplays = new KnownDisplays();
        GameState gameState = new GameState(secretWord);

        // Lancement serveur
        try (ServerSocket serverSocket = new ServerSocket(MASTER_PORT)) {
            System.out.println("GameMaster démarré sur le port " + MASTER_PORT);
            System.out.println("Mot secret chargé (" + secretWord.length() + " lettres).");

            // Boucle infinie : on accepte une connexion, on traite, on ferme.
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket, knownDisplays, gameState);
                } catch (Exception e) {
                    System.err.println("[GameMaster] Erreur pendant le traitement d'un client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[GameMaster] Impossible de démarrer le serveur: " + e.getMessage());
        }
    }


    private static void handleClient(Socket clientSocket, KnownDisplays knownDisplays, GameState gameState) throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)
        );

        // Lire l'entête (HELLO ou GUESS)
        String header = reader.readLine();
        if (header == null) {
            return; // connexion vide
        }
        header = header.trim();

        // Dispatcher selon le type de message
        if ("HELLO".equals(header)) {
            handleHello(reader, knownDisplays);

        } else if ("GUESS".equals(header)) {
            handleGuess(reader, knownDisplays, gameState);

        } else {
            // Message inconnu => on ignore
            System.err.println("[GameMaster] Header inconnu: " + header);
        }
    }


    private static void handleHello(BufferedReader reader, KnownDisplays knownDisplays) throws IOException {
        try {
            HelloMessage hello = new HelloMessage(reader); // lit ip + port depuis le reader
            knownDisplays.add(hello.getIp(), hello.getPort());
        } catch (Exception e) {
            System.err.println("[GameMaster] HELLO invalide: " + e.getMessage());
        }
    }

    private static void handleGuess(BufferedReader reader, KnownDisplays knownDisplays, GameState gameState) throws IOException {

        String guessLine = reader.readLine();
        if (guessLine == null) {
            System.err.println("[GameMaster] GUESS incomplet (lettre manquante).");
            return;
        }

        // Validation protocolaire via la classe GuessMessage
        GuessMessage guessMsg;
        try {
            guessMsg = new GuessMessage(guessLine);
        } catch (IllegalArgumentException e) {
            System.err.println("[GameMaster] GUESS invalide ignoré: " + e.getMessage());
            return;
        }

        char letter = guessMsg.getGuess();

        // Mise à jour du jeu
        gameState.guessLetter(letter);

        // Construire la string des lettres proposées
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

    private static void broadcastDisplay(List<KnownDisplays.PlayerEntry> targets, DisplayMessage displayMessage) {
        for (KnownDisplays.PlayerEntry entry : targets) {
            try (Socket displaySocket = new Socket(entry.getIp(), entry.getPort());
                 PrintWriter out = new PrintWriter(
                         new OutputStreamWriter(displaySocket.getOutputStream(), StandardCharsets.UTF_8),
                         true
                 )) {

                // Envoi du message réseau
                out.print(displayMessage.toNetworkString());
                out.flush();

            } catch (IOException e) {
                System.err.println("[GameMaster] Impossible d'envoyer DISPLAY à " + entry + ": " + e.getMessage());
            }
        }
    }
}

