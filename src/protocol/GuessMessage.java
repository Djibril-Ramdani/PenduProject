package protocol;

/**
 * Représente un message GUESS envoyé par le joueur au GameMaster.
 * * Spécification du PDF :
 * - Format : "GUESS" suivi d'une ligne contenant la lettre .
 * - Validation : La lettre doit être minuscule ou bien "_" .
 */
public class GuessMessage {

    private final char letter;

    /**
     * Constructeur pour la RÉCEPTION (côté GameMaster).
     * Parse la ligne reçue et valide le contenu.
     *
     * @param letterStr La ligne de texte contenant la lettre proposée.
     * @throws IllegalArgumentException Si la ligne est vide, contient plus d'un caractère, ou un caractère interdit.
     */
    public GuessMessage(String letterStr) throws IllegalArgumentException {
        // On verifie si y'a du contenu
        if (letterStr == null || letterStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Message GUESS invalide : Aucune lettre fournie.");
        }

        // On nettoie pour avoir que la lettre
        String cleaned = letterStr.trim();

        // On verfie la taille ( on veut 1)
        if (cleaned.length() != 1) {
            throw new IllegalArgumentException("Message GUESS invalide : Une seule lettre attendue (reçu: " + cleaned + ").");
        }

        char c = cleaned.charAt(0);

        // Condition pour être validé
        //
        boolean isLowerCase = (c >= 'a' && c <= 'z');
        boolean isUnderscore = (c == '_');

        if (!isLowerCase && !isUnderscore) {
            throw new IllegalArgumentException("Message GUESS invalide : Caractère " + c + " interdit. Seules les minuscules et '_' sont autorisés.");
        }

        this.letter = c;
    }

    /**
     * Constructeur pour l'ENVOI (côté PlayerGuesser).
     * Crée un message prêt à partir avec une lettre déjà validée ou brute.
     *
     * @param letter Le caractère à envoyer.
     */
    public GuessMessage(char letter) {
        char c = Character.toLowerCase(letter);
        if (!((c >= 'a' && c <= 'z') || c == '_')) {
            throw new IllegalArgumentException("Message GUESS invalide : Caractère"  + c + "interdit. Seules les minuscules et '_' sont autorisés.");
        }

        this.letter = c;
    }

    /**
     * Génère la chaîne complète à envoyer sur le réseau.
     * Respecte le protocole ligne par ligne.
     * * @return Le message formaté : "GUESS\n<lettre>\n"
     */
    public String toNetworkString() {
        return "GUESS\n" + this.letter + "\n";
    }

    /**
     * Récupère la lettre contenue dans le message.
     * @return La lettre validée.
     */
    public char getLetter() {
        return letter;
    }
}