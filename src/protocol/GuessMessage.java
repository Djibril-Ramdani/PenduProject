package protocol;

/**
 * Représente le message GUESS envoyé par le PlayerGuesser au GameMaster.
 * Format :
 * GUESS
 * <lettre>
 */
public class GuessMessage {

    private final char letter;

    /**
     * Constructeur de parsing (Réception côté GameMaster).
     * @param letterStr La ligne contenant la lettre reçue.
     * @throws IllegalArgumentException Si la lettre n'est pas valide.
     */
    public GuessMessage(String letterStr) throws IllegalArgumentException {
        // 1. Vérification que la chaîne n'est pas vide
        if (letterStr == null || letterStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Message GUESS invalide : Aucune lettre fournie.");
        }

        String cleaned = letterStr.trim();

        // 2. Vérification de la longueur (doit être exactement 1 caractère)
        if (cleaned.length() != 1) {
            throw new IllegalArgumentException("Message GUESS invalide : Une seule lettre attendue.");
        }

        char c = cleaned.charAt(0);

        // 3. Validation du contenu (Règle PDF page 4 : minuscule ou "_")
        // On accepte '_' (pour quitter) OU une lettre minuscule entre 'a' et 'z'.
        if (c != '_' && (c < 'a' || c > 'z')) {
            throw new IllegalArgumentException("Message GUESS invalide : Caractère interdit '" + c + "'. Seules les minuscules ou '_' sont autorisées.");
        }

        this.letter = c;
    }

    /**
     * Constructeur pour l'envoi (Côté PlayerGuesser).
     */
    public GuessMessage(char letter) {
        this.letter = letter;
    }

    /**
     * Formate le message pour l'envoi réseau.
     * Exemple : "GUESS\na\n"
     */
    public String toNetworkString() {
        return "GUESS\n" + this.letter + "\n";
    }

    public char getLetter() {
        return letter;
    }
}