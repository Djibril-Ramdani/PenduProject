package protocol;

public class GuessMessage {

    private final char letter;

    public GuessMessage(String letterStr) throws IllegalArgumentException {
        // On verifie si y'a du contenu
        if (letterStr == null || letterStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Message GUESS invalide : Aucune lettre fournie.");
        }

        // On nettoie pour avoir que la lettre
        String cleaned = letterStr.trim();

        // On verfie la taille
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


    public GuessMessage(char letter) {
        char c = Character.toLowerCase(letter);
        if (!((c >= 'a' && c <= 'z') || c == '_')) {
            throw new IllegalArgumentException("Message GUESS invalide : Caractère"  + c + "interdit. Seules les minuscules et '_' sont autorisés.");
        }

        this.letter = c;
    }

    public String toNetworkString() {
        return "GUESS\n" + this.letter + "\n";
    }

    public char getGuess() {
        return letter;
    }
}