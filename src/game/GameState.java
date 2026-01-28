package game;

import java.util.HashSet;
import java.util.Set;

public class GameState {

    // Énumération pour les 3 états possibles du jeu (PDF page 3)
    public enum State {
        PLAYING, WIN, LOSE
    }

    private final String motSecret;
    private final Set<Character> lettresProposees;
    private int nombreErreurs;
    private static final int ERREURS_MAX = 8;

    // Constructeur : Initialise une nouvelle partie
    public GameState(String mot) {

        this.motSecret = mot.toLowerCase();
        this.lettresProposees = new HashSet<>();
        this.nombreErreurs = 0;
    }

    // Méthode principale : Traite une proposition de lettre
    public boolean guessLetter(char lettre) {
        // 1. Si la lettre a déjà été proposée, on l'ignore
        if (lettresProposees.contains(lettre)) {
            return false; // Rien ne change
        }

        // 2. On ajoute la lettre aux propositions
        lettresProposees.add(lettre);

        // 3. Si la lettre n'est PAS dans le mot secret, c'est une erreur (PDF page 2)
        if (motSecret.indexOf(lettre) == -1) {
            nombreErreurs++;
        }

        return true;
    }

    // Retourne l'état actuel de la partie (PLAYING, WIN, LOSE)
    public State getCurrentState() {
        if (nombreErreurs >= ERREURS_MAX) {
            return State.LOSE; // Défaite si 8 erreurs
        }
        if (isWordGuessed()) {
            return State.WIN;  // Victoire si tout est trouvé
        }
        return State.PLAYING;  // Sinon, on continue
    }

    // Vérifie si toutes les lettres du mot ont été trouvées
    private boolean isWordGuessed() {
        for (char c : motSecret.toCharArray()) {
            if (!lettresProposees.contains(c)) {
                return false;
            }
        }
        return true;
    }

    // Génère le "mot masqué" pour l'affichage (ex: "_ o _ _")
    public String getMaskedWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : motSecret.toCharArray()) {
            if (lettresProposees.contains(c)) {
                sb.append(c); // Affiche la lettre si trouvée
            } else {
                sb.append("_"); // Affiche _ sinon
            }
        }
        return sb.toString();
    }

    public int getErrorCount() {
        return nombreErreurs;
    }


    public String getGuessedLettersString() {
        return String.join(" ", lettresProposees.stream()
                .map(String::valueOf)
                .toArray(String[]::new));
    }
}
