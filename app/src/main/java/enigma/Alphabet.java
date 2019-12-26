package enigma;

/**
 * An alphabet of encodable characters.  Provides a mapping from characters
 * to and from indices into the alphabet.
 *
 * @author Junyi Cao
 */
public class Alphabet {

    /**
     * A new alphabet containing CHARS.  Character number #k has index
     * K (numbering from 0). No character may be duplicated.
     */
    Alphabet(String chars) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < chars.length(); i++) {
            char c = chars.charAt(i);
            if (buffer.indexOf(String.valueOf(c)) != -1) {
                throw EnigmaException.error(
                        "Duplicated character `%c` detected!", c);
            } else if (c == '*' || c == '(' || c == ')' || c == ' ') {
                throw EnigmaException.error("Alphabet contains `%c` which "
                        + "is not allowed.", c);
            } else {
                buffer.append(c);
            }
        }
        characters = new String(buffer);
    }

    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return characters.length();
    }

    /**
     * Returns true if preprocess(CH) is in this alphabet.
     */
    boolean contains(char ch) {
        return characters.indexOf(ch) != -1;
    }

    /**
     * Returns character number INDEX in the alphabet, where
     * 0 <= INDEX < size().
     */
    char toChar(int index) {
        return characters.charAt(index);
    }

    /**
     * Returns the index of character preprocess(CH), which must be in
     * the alphabet. This is the inverse of toChar().
     */
    int toInt(char ch) {
        int index = characters.indexOf(ch);
        if (index == -1) {
            throw EnigmaException.error("`%c` is NOT in the alphabet!", ch);
        }
        return characters.indexOf(ch);
    }

    /**
     * Encode-able characters concerned.
     */
    private String characters;

    /**
     * Return the characters of the alphabet.
     */
    @Override
    public String toString() {
        return characters;
    }
}
