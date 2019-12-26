package enigma;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a permutation of a range of integers starting at 0 corresponding
 * to the characters of an alphabet.
 *
 * @author Junyi Cao
 */
class Permutation {

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _derangement = true;
        permutations = new Character[_alphabet.size()][2];
        permTrace = new boolean[_alphabet.size()];
        Arrays.fill(permTrace, false);
        Matcher m = Pattern.compile("\\(.*?\\)").matcher(cycles);
        while (m.find()) {
            String cycle = m.group();
            if (cycle.charAt(0) == '('
                    && cycle.charAt(cycle.length() - 1) == ')') {
                addCycle(cycle.substring(1, cycle.length() - 1));
            } else {
                throw EnigmaException.error(
                        "parentheses of a cycle in the permutation "
                                + "is missing.\ncycle: %s", cycle);
            }
        }
        for (int i = 0; i < permTrace.length; i++) {
            if (!permTrace[i]) {
                permutations[i][0] = permutations[i][1] = _alphabet.toChar(i);
                _derangement = false;
            }
        }
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (cycle.length() == 1) {
                _derangement = false;
            }
            int index = alphabet().toInt(cycle.charAt(i));
            if (permutations[index][0] != null
                    && permutations[index][1] != null) {
                throw EnigmaException.error("config error, duplicate "
                                + "character `%c` in permutation.",
                        cycle.charAt(i));
            }
            if (i == cycle.length() - 1) {
                permutations[index][0] = cycle.charAt(0);
            } else {
                permutations[index][0] = cycle.charAt(i + 1);
            }
            if (i == 0) {
                permutations[index][1] = cycle.charAt(cycle.length() - 1);
            } else {
                permutations[index][1] = cycle.charAt(i - 1);
            }
            permTrace[index] = true;
        }
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return alphabet().size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        p = wrap(p);
        return alphabet().toInt(permutations[p][0]);
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        c = wrap(c);
        return alphabet().toInt(permutations[c][1]);
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        int index = alphabet().toInt(p);
        return permutations[index][0];
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        int index = alphabet().toInt(c);
        return permutations[index][1];
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        return _derangement;
    }

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;

    /**
     * Record the chars of permutations, the second array [0] stands for
     * permute, while [1] stands for inverse.
     */
    private Character[][] permutations;

    /**
     * Record whether the corresponding character has appeared in any cycle.
     */
    private boolean[] permTrace;

    /**
     * True iff this permutation is a derangement (i.e., a permutation
     * for which no value maps to itself).
     */
    private boolean _derangement;
}
