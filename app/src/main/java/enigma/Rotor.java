package enigma;

/**
 * Superclass that represents a rotor in the enigma machine.
 *
 * @author Junyi Cao
 */
class Rotor {

    /**
     * A rotor named NAME whose permutation is given by PERM.
     */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        position = 0;
        alphabetRing = 0;
    }

    /**
     * Return my name.
     */
    String name() {
        return _name;
    }

    /**
     * Return my alphabet.
     */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /**
     * Return my permutation.
     */
    Permutation permutation() {
        return _permutation;
    }

    /**
     * Return the size of my alphabet.
     */
    int size() {
        return _permutation.size();
    }

    /**
     * Return true iff I have a ratchet and can move.
     */
    boolean rotates() {
        return false;
    }

    /**
     * Return true iff I reflect.
     */
    boolean reflecting() {
        return false;
    }

    /**
     * Return my current setting.
     */
    int setting() {
        return permutation().wrap(position - alphabetRing);
    }

    /**
     * Set setting() to POSN.
     */
    void set(int posn) {
        posn = permutation().wrap(posn);
        if (posn >= alphabet().size() || posn < 0) {
            throw EnigmaException.error(
                    "posn: `%d` is out of the size of the alphabet "
                            + "(%s).", posn, alphabet());
        }
        position = posn;
    }

    /**
     * Set setting() to character CPOSN.
     */
    void set(char cposn) {
        if (!alphabet().contains(cposn)) {
            throw EnigmaException.error(
                    "cposn: `%c` is not in the alphabet "
                            + "(%s).", cposn, alphabet());
        }
        position = alphabet().toInt(cposn);
    }

    /**
     * Return the conversion of P (an integer in the range 0..size()-1)
     * according to my permutation.
     */
    int convertForward(int p) {
        int increaseP = permutation().wrap(p + setting());
        int converted = permutation().permute(increaseP);
        return permutation().wrap(converted - setting());
    }

    /**
     * Return the conversion of E (an integer in the range 0..size()-1)
     * according to the inverse of my permutation.
     */
    int convertBackward(int e) {
        int increaseE = permutation().wrap(e + setting());
        int converted = permutation().invert(increaseE);
        return permutation().wrap(converted - setting());
    }

    /**
     * Returns true iff I am positioned to allow the rotor to my left
     * to advance.
     */
    boolean atNotch() {
        return false;
    }

    /**
     * Advance me one position, if possible. By default, does nothing.
     */
    void advance() {
    }

    /**
     * Return the alphabet ring.
     */
    int alphabetRing() {
        return alphabetRing;
    }

    /**
     * Set the alphabet ring to RING.
     */
    void setAlphabetRing(int ring) {
        alphabetRing = ring;
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /**
     * My name.
     */
    private final String _name;

    /**
     * The permutation implemented by this rotor in its 0 position.
     */
    private Permutation _permutation;

    /**
     * The current position of the rotor.
     */
    private int position;

    /**
     * The character in position 0 in the alphabet ring.
     */
    private int alphabetRing;
}
