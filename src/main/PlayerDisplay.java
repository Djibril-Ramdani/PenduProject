package main;

import java.io.*;
import java.net.*;
import protocol.DisplayMessage; // On utilise ta classe pour stocker proprement les infos

public class PlayerDisplay {

    public static void main(String[] args) {
        // 1. Vérification de l'argument (Port d'écoute) [cite: 104]
        if (args.length != 1) {
            System.err.println("Usage: java PlayerDisplay <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        // Vérification de sécurité : le port ne doit pas être 2025 [cite: 104]
        if (port == 2025) {
            System.err.println("Erreur : Le port 2025 est réservé au GameMaster.");
            return;
        }

        System.out.println("=== JEU DU PENDU - AFFICHAGE ===");
        System.out.println("En attente de connexion du GameMaster sur le port " + port + "...");

        // 2. Écoute passive (Serveur TCP)
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            boolean gameRunning = true;

            while (gameRunning) {
                // Attente d'une connexion du GameMaster
                try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // 3. Lecture de la première ligne (Header)
                    String header = reader.readLine();

                    if ("DISPLAY".equals(header)) {
                        // 4. Lecture des 4 lignes de données
                        String maskedWord = reader.readLine();
                        String guessedLetters = reader.readLine();
                        String errorCountStr = reader.readLine();
                        String gameState = reader.readLine();

                        // Création de l'objet message pour valider les données
                        DisplayMessage msg = new DisplayMessage(maskedWord, guessedLetters, errorCountStr, gameState);

                        // 5. Affichage propre à l'écran [cite: 109]
                        afficherInterface(msg);

                        // 6. Arrêt si la partie est finie
                        if ("WIN".equals(msg.getGameState()) || "LOSE".equals(msg.getGameState())) {
                            System.out.println(">>> Fin de la partie. Fermeture de l'affichage. <<<");
                            gameRunning = false;
                        }

                    } else {
                        // Si le header n'est pas DISPLAY, on ignore ou on affiche une erreur
                        System.err.println("Message ignoré (Header invalide) : " + header);
                    }

                } catch (IOException e) {
                    System.err.println("Erreur de lecture : " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Données corrompues reçues : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Impossible d'ouvrir le port " + port + " : " + e.getMessage());
        }
    }

    /**
     * Affiche l'état du jeu avec un joli dessin ASCII.
     */
    private static void afficherInterface(DisplayMessage msg) {
        // On "nettoie" un peu la console (saut de lignes) pour la lisibilité
        System.out.println("\n\n\n\n\n");
        System.out.println("========================================");

        // Affichage du dessin du pendu selon le nombre d'erreurs
        dessinerPendu(msg.getErrorCount());

        System.out.println(" MOT À DEVINER :  " + espacerMot(msg.getMaskedWord()));
        System.out.println("----------------------------------------");
        System.out.println(" Erreurs : " + msg.getErrorCount() + " / 8");
        System.out.println(" Lettres déjà essayées : [" + msg.getGuessedLetters() + "]");
        System.out.println("----------------------------------------");

        if ("WIN".equals(msg.getGameState())) {
            System.out.println("       VICTOIRE ! BRAVO !       ");
        } else if ("LOSE".equals(msg.getGameState())) {
            System.out.println("       PERDU... LE MOT ÉTAIT CACHÉ.       ");
        } else {
            System.out.println("       EN COURS...       ");
        }
        System.out.println("========================================");
    }

    // Ajoute des espaces entre les lettres pour faire plus joli (ex: "_ a _" au lieu de "_a_")
    private static String espacerMot(String word) {
        return word.replace("", " ").trim();
    }

    // Petit bonus graphique : Dessine le pendu étape par étape
    private static void dessinerPendu(int erreurs) {
        switch (erreurs) {
            case 0: System.out.println("\n\n\n\n"); break;
            case 1: System.out.println("\n\n\n__________"); break;
            case 2: System.out.println("      |\n      |\n      |\n______|___"); break;
            case 3: System.out.println("  ____|\n      |\n      |\n______|___"); break;
            case 4: System.out.println("  ____|\n  |   |\n      |\n______|___"); break;
            case 5: System.out.println("  ____|\n  |   |\n  O   |\n______|___"); break;
            case 6: System.out.println("  ____|\n  |   |\n  O   |\n  |   |\n______|___"); break;
            case 7: System.out.println("  ____|\n  |   |\n  O   |\n /|\\  |\n______|___"); break;
            case 8: System.out.println("  ____|\n  |   |\n  O   |\n /|\\  |\n / \\  |\n______|___"); break;
        }
    }
}