package enigma;

/**
 * Class that represents a rotating rotor in the enigma machine.
 *
 * @author Junyi Cao
 */
class MovingRotor extends Rotor {

    /**
     * A rotor named NAME whose permutation in its default setting is
     * PERM, and whose notches are at the positions indicated in NOTCHES.
     * The Rotor is initally in its 0 setting (first character of its
     * alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _originNotches = notches;
        _notches = new int[notches.length()];
        for (int i = 0; i < notches.length(); i++) {
            _notches[i] = alphabet().toInt(notches.charAt(i));
        }
    }

    @Override
    boolean atNotch() {
        for (int notch : _notches) {
            if (setting() == notch) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set(setting() + alphabetRing() + 1);
    }

    @Override
    boolean rotates() {
        return true;
    }

    /**
     * Adjusting the notches by considering the alphabet ring.
     */
    void setNotches() {
        for (int i = 0; i < _originNotches.length(); i++) {
            int origin = alphabet().toInt(_originNotches.charAt(i));
            _notches[i] = permutation().wrap(origin - alphabetRing());
        }
    }

    /**
     * The notches a moving rotor has.
     */
    private int[] _notches;

    /**
     * Record the origin notches.
     */
    private String _originNotches;
}
