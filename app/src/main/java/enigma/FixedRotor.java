package enigma;

/**
 * Class that represents a rotor that has no ratchet and does not advance.
 *
 * @author Junyi Cao
 */
class FixedRotor extends Rotor {

    /**
     * A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM.
     */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        set(0);
    }

    @Override
    void advance() {
        throw EnigmaException.error("%s is a FixedRotor "
                + "which cannot advance.", this);
    }
}
