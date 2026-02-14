package protocol;

import java.util.Arrays;

public class DisplayMessage {

    private final String motMasque;     // Exemple : "_ o _ _ _ r"
    private final String lettresproposees ; // Exemple : "o r z"
    private final int nberreurs;        // Entre 0 et 8
    private final String etatpartie;      // PLAYING, WIN ou LOSE


    public DisplayMessage(String motMasque, String lettresproposees, String nberreurs, String etatpartie) {

       // Validation du mot masqué
        if (motMasque == null || motMasque.trim().isEmpty()) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le mot masqué est vide.");
        }
        this.motMasque = motMasque;

        // Les lettres proposées peuvent être vides au début
        this.lettresproposees = (lettresproposees ==  null) ? "" : lettresproposees.trim();

        // Validation du nombre d'erreurs
        try {
            this.nberreurs = Integer.parseInt(nberreurs.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le nombre d'erreurs n'est pas un nombre.");
        }

        // max 8 erreurs
        if (this.nberreurs < 0 || this.nberreurs > 8) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le nombre d'erreurs doit être entre 0 et 8.");
        }

        // on ne peut pas avoir plus d'erreurs que de lettres jouées
        long nbLettresJouees = 0;
        if (!this.lettresproposees.isEmpty()) {
            nbLettresJouees = Arrays.stream(this.lettresproposees.split("\\s+")).count();
        }
        if (this.nberreurs > nbLettresJouees) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Incohérence (plus d'erreurs que de lettres jouées).");
        }

       // Validation de l'état de la partie
        if (!etatpartie.equals("PLAYING") && !etatpartie.equals("WIN") && !etatpartie.equals("LOSE")) {
            throw new IllegalArgumentException("Message DISPLAY invalide : État inconnu '" + etatpartie + "'.");
        }
        this.etatpartie = etatpartie;
    }

    public DisplayMessage(String motMasque, String lettresproposees, int nberreurs, String etatpartie) {
        this.motMasque = motMasque;
        this.lettresproposees = lettresproposees;
        this.nberreurs = nberreurs;
        this.etatpartie = etatpartie;
    }

    public String toNetworkString() {
        return "DISPLAY\n" +
                this.motMasque + "\n" +
                this.lettresproposees + "\n" +
                this.nberreurs + "\n" +
                this.etatpartie + "\n";
    }

    // Getters pour pouvoir afficher les infos plus tard
    public String getMaskedWord() { return motMasque; }
    public String getGuessedLetters() { return lettresproposees; }
    public int getErrorCount() { return nberreurs; }
    public String getGameState() { return etatpartie; }
}