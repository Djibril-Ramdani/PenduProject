package protocol;

import java.util.Arrays;
import java.util.List;

/**
 * Représente le message DISPLAY envoyé par le GameMaster aux PlayerDisplay.
 * Format :
 * DISPLAY
 * <mot_masque>
 * <lettres_proposees>
 * <nb_erreurs>
 * <etat_partie>
 */
public class DisplayMessage {

    // Les données transportées
    public final String maskedWord;
    public final String guessedLetters;
    public final int errorCount;
    public final String gameState;

    /**
     * Constructeur de parsing (Utilisé par PlayerDisplay à la réception).
     * Lit les 4 lignes et vérifie toutes les règles de cohérence du PDF.
     */
    public DisplayMessage(String maskedWord, String guessedLetters, String errorCountStr, String gameState) {

        // 1. Validation du mot masqué
        if (maskedWord == null || maskedWord.trim().isEmpty()) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le mot masqué ne peut pas être vide.");
        }
        this.maskedWord = maskedWord;
        this.guessedLetters = (guessedLetters == null) ? "" : guessedLetters.trim();

        // 2. Validation du nombre d'erreurs
        try {
            this.errorCount = Integer.parseInt(errorCountStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le nombre d'erreurs doit être un entier.");
        }

        if (this.errorCount < 0 || this.errorCount > 8) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le nombre d'erreurs doit être entre 0 et 8.");
        }

        // 3. Validation de la cohérence Erreurs vs Propositions
        // On compte combien de lettres ont été proposées (en découpant les espaces)
        long nbLettersProposed = 0;
        if (!this.guessedLetters.isEmpty()) {
            nbLettersProposed = Arrays.stream(this.guessedLetters.split(" ")).count();
        }

        if (this.errorCount > nbLettersProposed) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Impossible d'avoir plus d'erreurs ("
                    + this.errorCount + ") que de lettres proposées (" + nbLettersProposed + ").");
        }

        // 4. Validation de l'état de la partie
        // Doit être PLAYING, WIN ou LOSE
        if (!gameState.equals("PLAYING") && !gameState.equals("WIN") && !gameState.equals("LOSE")) {
            throw new IllegalArgumentException("Message DISPLAY invalide : État inconnu '" + gameState + "'.");
        }
        this.gameState = gameState;
    }

    /**
     * Constructeur d'envoi (Utilisé par GameMaster).
     * Prend les données brutes et prépare l'objet.
     */
    public DisplayMessage(String maskedWord, String guessedLetters, int errorCount, String gameState) {
        this.maskedWord = maskedWord;
        this.guessedLetters = guessedLetters;
        this.errorCount = errorCount;
        this.gameState = gameState;
    }

    /**
     * Formate le message pour l'envoi réseau.
     * Le premier mot "DISPLAY" sera ajouté par le GameMaster lors de l'envoi,
     * ou on peut l'inclure ici, mais par convention le header est souvent géré à part.
     * Ici, on renvoie la totalité pour simplifier.
     */
    public String toNetworkString() {
        return "DISPLAY\n" +
                maskedWord + "\n" +
                guessedLetters + "\n" +
                errorCount + "\n" +
                gameState + "\n";
    }

    // Getters pour l'affichage côté client
    public String getMaskedWord() { return maskedWord; }
    public String getGuessedLetters() { return guessedLetters; }
    public int getErrorCount() { return errorCount; }
    public String getGameState() { return gameState; }
}