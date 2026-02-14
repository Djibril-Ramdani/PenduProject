package game;

import java.util.HashSet;
import java.util.Set;

public class GameState {

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

    // méthode principale : Traite une proposition de lettre
    public boolean guessLetter(char lettre) {
        //Si la lettre a déjà été proposée, on l'ignore
        if (lettresProposees.contains(lettre)) {
            return false;
        }

        // On ajoute la lettre aux propositions
        lettresProposees.add(lettre);

        // Si la lettre n'est PAS dans le mot secret c'est une erreur
        if (motSecret.indexOf(lettre) == -1) {
            nombreErreurs++;
        }

        return true;
    }


    public State getCurrentState() {
        if (nombreErreurs >= ERREURS_MAX) {
            //Défaite si on a fait 8 erreurs
            return State.LOSE;
        }
        if (isWordGuessed()) {
            //On a gagné si le mot est trouvé
            return State.WIN;
        }
        //Sinon on continue le jeu
        return State.PLAYING;
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

    // Génère le mot masqué pour l'affichage
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
