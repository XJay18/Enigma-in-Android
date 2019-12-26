package enigma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents a complete enigma machine.
 *
 * @author Junyi Cao
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            ArrayList<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        if (_numRotors <= 1) {
            throw EnigmaException.error(
                    "config error, numRotors should be greater than 1, "
                            + "but found %d.", numRotors
            );
        }
        _numPawls = pawls;
        if (_numPawls < 0 || _numPawls >= numRotors) {
            throw EnigmaException.error(
                    "config error, numPawls should be in [0, numRotors), "
                            + "but found %d.", pawls
            );
        }
        _allRotors = allRotors;
        _rotorSlots = new Rotor[numRotors];
        _plugboard = new Permutation("", alpha);
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _numPawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        if (rotors.length != numRotors()) {
            throw EnigmaException.error(
                    "setting error, the number of rotors "
                            + "to be inserted: %d doesn't equal to "
                            + "numRotors: %d.", rotors.length, numRotors()
            );
        }
        Set<String> rotorNames = new HashSet<>();
        for (int i = 0; i < numRotors(); i++) {
            String name = rotors[i];
            for (Rotor r : _allRotors) {
                if (r.name().equals(name)) {
                    if (i == 0 && !(r instanceof Reflector)) {
                        throw EnigmaException.error("config error, the first "
                                + "rotor isn't a reflector.\n"
                                + "first rotor: %s", r);
                    }
                    if (rotorNames.contains(r.name())) {
                        throw EnigmaException.error("setting error, "
                                + "duplicate rotor loaded in "
                                + "the rotor Slots.\n"
                                + "duplicate rotor: %s", r);
                    }
                    _rotorSlots[i] = r;
                    rotorNames.add(r.name());
                    break;
                }
            }
            if (_rotorSlots[i] == null) {
                throw EnigmaException.error(
                        "setting error, %s not found in the "
                                + "available rotors by configuration.",
                        name
                );
            }
        }
        checkRotors();
    }

    /**
     * Check whether the rotors in rotor Slots are properly configured.
     */
    private void checkRotors() {
        for (int i = 0; i < numRotors() - numPawls(); i++) {
            if (_rotorSlots[i] instanceof MovingRotor) {
                throw EnigmaException.error("setting error, "
                        + "The %dth position (0 indexed) of the rotor Slots "
                        + "should NOT contain a MovingRotor, but does.\n"
                        + "It contains: %s", i, _rotorSlots[i]);
            }
        }
        for (int i = numRotors() - numPawls(); i < numRotors(); i++) {
            if (!(_rotorSlots[i] instanceof MovingRotor)) {
                throw EnigmaException.error("setting error, "
                        + "The %dth position (0 indexed) of the rotor Slots "
                        + "should contain a MovingRotor, but not.\n"
                        + "Instead, it contains: %s", i, _rotorSlots[i]);
            }
        }
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers
     * to the leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw EnigmaException.error(
                    "config error, "
                            + "the number of setting chars: %d "
                            + "is not 1 less than numRotors: %d.",
                    setting.length(), numRotors()
            );
        }
        for (int i = 1; i < numRotors(); i++) {
            char position = setting.charAt(i - 1);
            if (!_rotorSlots[i].reflecting()) {
                _rotorSlots[i].set(position);
            }
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /**
     * Set the alphabet rings by RINGS.
     */
    void setRings(String rings) {
        if (rings.length() != numRotors() - 1) {
            throw EnigmaException.error(
                    "config error, "
                            + "the number of alphabet rings: %d "
                            + "is not 1 less than numRotors: %d.",
                    rings.length(), numRotors()
            );
        }
        for (int i = 1; i < numRotors(); i++) {
            int ring = _rotorSlots[i].alphabet().toInt(rings.charAt(i - 1));
            _rotorSlots[i].setAlphabetRing(ring);
            if (_rotorSlots[i].rotates()) {
                ((MovingRotor) _rotorSlots[i]).setNotches();
            }
        }
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * <p>
     * the machine.
     */
    int convert(int c) {
        rotatesRotors(false);
        c = _plugboard.permute(c);
        for (int i = _rotorSlots.length - 1; i >= 0; i--) {
            c = _rotorSlots[i].convertForward(c);
        }
        for (int i = 1; i < _rotorSlots.length; i++) {
            c = _rotorSlots[i].convertBackward(c);
        }
        c = _plugboard.permute(c);
        return c;
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        char[] convertedMsg = new char[msg.length()];
        for (int i = 0; i < msg.length(); i++) {
            int msgInt = _alphabet.toInt(msg.charAt(i));
            int msgConvInt = convert(msgInt);
            convertedMsg[i] = _alphabet.toChar(msgConvInt);
        }
        return new String(convertedMsg);
    }

    /**
     * Rotate the moving rotors in the rotorSlots after
     * each character comes in.
     *
     * @param verbose whether to print the settings during encoding/decoding.
     */
    private void rotatesRotors(boolean verbose) {
        boolean[] toAdvance = new boolean[_rotorSlots.length];
        Arrays.fill(toAdvance, false);
        for (int i = 1; i < _rotorSlots.length; i++) {
            if (_rotorSlots[i - 1].rotates() && _rotorSlots[i].atNotch()) {
                toAdvance[i - 1] = toAdvance[i] = true;
            }
        }
        for (int i = 1; i < _rotorSlots.length; i++) {
            if (toAdvance[i] || i == _rotorSlots.length - 1) {
                _rotorSlots[i].advance();
            }
        }

        if (verbose) {
            for (int i = 1; i < _rotorSlots.length; i++) {
                System.out.print(
                        _alphabet.toChar(_rotorSlots[i].setting()));
            }
            System.out.println();
        }
    }

    /**
     * Common alphabet of my rotors.
     */
    private final Alphabet _alphabet;

    /**
     * Number of total rotors.
     */
    private int _numRotors;

    /**
     * Number of total pawls, i.e, moving rotors.
     */
    private int _numPawls;

    /**
     * All available rotors by configuration.
     */
    private ArrayList<Rotor> _allRotors;

    /**
     * The rotors in each specific rotor slots.
     */
    private Rotor[] _rotorSlots;

    /**
     * The plugboard.
     */
    private Permutation _plugboard;
}
