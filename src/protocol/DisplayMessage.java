package protocol;

import java.util.Arrays;

/**
 * Représente le message DISPLAY envoyé par le GameMaster vers les PlayerDisplay.
 * Basé sur ta capture d'écran :
 * DISPLAY
 * <mot_masque>
 * <lettres_proposees>
 * <nb_erreurs>
 * <etat_partie>
 */
public class DisplayMessage {

    private final String motMasque;     // Exemple : "_ o _ _ _ r"
    private final String lettresproposees ; // Exemple : "o r z"
    private final int nberreurs;        // Entre 0 et 8
    private final String etatpartie;      // PLAYING, WIN ou LOSE

    /**
     * Constructeur pour la RÉCEPTION (côté PlayerDisplay).
     * Lit les données reçues et vérifie qu'elles sont cohérentes.
     */
    public DisplayMessage(String motMasque, String lettresproposees, String nberreurs, String etatpartie) {

       // 1. Validation du mot masqué
        if (motMasque == null || motMasque.trim().isEmpty()) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le mot masqué est vide.");
        }
        this.motMasque = motMasque;

        // Les lettres proposées peuvent être vides au début, on gère le null
        this.lettresproposees = (lettresproposees ==  null) ? "" : lettresproposees.trim();

        // 2. Validation du nombre d'erreurs
        try {
            this.nberreurs = Integer.parseInt(nberreurs.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le nombre d'erreurs n'est pas un nombre.");
        }

        // Le PDF dit max 8 erreurs
        if (this.nberreurs < 0 || this.nberreurs > 8) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Le nombre d'erreurs doit être entre 0 et 8.");
        }

        // Petite vérification logique : on ne peut pas avoir plus d'erreurs que de lettres jouées
        long nbLettresJouees = 0;
        if (!this.lettresproposees.isEmpty()) {
            nbLettresJouees = Arrays.stream(this.lettresproposees.split("\\s+")).count();
        }
        if (this.nberreurs > nbLettresJouees) {
            throw new IllegalArgumentException("Message DISPLAY invalide : Incohérence (plus d'erreurs que de lettres jouées).");
        }

       // 3. Validation de l'état de la partie
        if (!etatpartie.equals("PLAYING") && !etatpartie.equals("WIN") && !etatpartie.equals("LOSE")) {
            throw new IllegalArgumentException("Message DISPLAY invalide : État inconnu '" + etatpartie + "'.");
        }
        this.etatpartie = etatpartie;
    }

    /**
     * Constructeur pour l'ENVOI (côté GameMaster).
     * Construit l'objet proprement avant de l'envoyer.
     */
    public DisplayMessage(String motMasque, String lettresproposees, int nberreurs, String etatpartie) {
        this.motMasque = motMasque;
        this.lettresproposees = lettresproposees;
        this.nberreurs = nberreurs;
        this.etatpartie = etatpartie;
    }

    /**
     * Formate le message pour le réseau selon le format exact de la capture.
     * Header "DISPLAY" + 4 lignes.
     */
    public String toNetworkString() {
        return "DISPLAY\n" +
                this.motMasque + "\n" +
                this.lettresproposees + "\n" +
                this.nberreurs + "\n" +
                this.etatpartie + "\n";
    }

    // Getters pour pouvoir afficher les infos plus tard
    public String getmotMasque() { return motMasque; }
    public String getGuessedLetters() { return lettresproposees; }
    public int getErrorCount() { return nberreurs; }
    public String getGameState() { return etatpartie; }
}