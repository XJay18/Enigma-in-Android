package enigma;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/**
 * Enigma simulator.
 *
 * @author Junyi Cao
 */
public final class Simulator {

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    public Simulator(String input) {

        String config = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" +
                " 5 3\n" +
                " I MQ      (AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)\n" +
                " II ME     (FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)\n" +
                " III MV    (ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)\n" +
                " IV MJ     (AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)\n" +
                " V MZ      (AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)\n" +
                " VI MZM    (AJQDVLEOZWIYTS) (CGMNHFUX) (BPRK) \n" +
                " VII MZM   (ANOUPFRIMBZTLWKSVEGCJYDHXQ) \n" +
                " VIII MZM  (AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)\n" +
                " Beta N    (ALBEVFCYODJWUGNMQTZSKPR) (HIX)\n" +
                " Gamma N   (AFNIRLBSQWVXGUZDKMTPCOYJHE)\n" +
                " B R       (AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) (MP)\n" +
                "           (RX) (SZ) (TV)\n" +
                " C R       (AR) (BD) (CO) (EJ) (FN) (GT) (HK) (IV) (LM) (PW)\n" +
                "           (QZ) (SX) (UY)\n";

        _config = new Scanner(config);

        _input = new Scanner(input);

        _output = new StringBuffer();
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    public void process() {
        Machine machine = readConfig();
        while (_input.hasNextLine()) {
            boolean setOK = setUp(machine, _input.nextLine());
            while (setOK && _input.hasNext("[^*].*")) {
                printMessageLine(
                        machine.convert(
                                _input.nextLine().replaceAll(
                                        "\\s+", "")));
            }
        }
    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            String alpha = _config.nextLine();
            _alphabet = new Alphabet(alpha);
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();

            StringBuffer stringBuffer = new StringBuffer();
            while (_config.hasNextLine()) {
                stringBuffer.append(_config.nextLine());
            }

            Pattern pattern = Pattern.compile("\\s?[^()\\s]+"
                    + "\\s(M[^()\\s]*|R|N)\\s+"
                    + "(\\([^()]+\\)\\s*)*");
            Matcher matcher = pattern.matcher(new String(stringBuffer));
            ArrayList<Rotor> rotors = new ArrayList<>();

            while (matcher.find()) {
                String[] rotorConfig = new String[2];
                String cs = matcher.group().trim().replaceAll(
                        "[\\n|\\r]", " ").replaceAll(
                        "\\r\\n", " ");
                Pattern initial = Pattern.compile("\\s?[^()\\s]+"
                        + "\\s(M[^()\\s]*|R|N)\\s+");
                Pattern cycles = Pattern.compile("\\(.*?\\)");
                Matcher m;
                m = initial.matcher(cs);
                if (m.find()) {
                    rotorConfig[0] = m.group();
                } else {
                    throw error("config error, not specify the rotor name "
                            + "or type.\n setting: %s", cs);
                }
                m = cycles.matcher(cs);
                StringBuffer cyclesBuffer = new StringBuffer();
                while (m.find()) {
                    cyclesBuffer.append(m.group());
                }
                rotorConfig[1] = new String(cyclesBuffer);
                Rotor rotor = readRotor(rotorConfig);
                rotors.add(rotor);
            }
            return new Machine(_alphabet, numRotors, numPawls, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Return a rotor, reading its description from ROTOR.
     */
    private Rotor readRotor(String[] rotor) {
        try {
            if (rotor.length != 2) {
                throw error("config error, rotor configuration wrong.");
            }
            String[] initial = rotor[0].split("\\s+");
            if (initial.length != 2) {
                throw error("config error, either rotor's name "
                        + "or type is missing.\n origin: %s", rotor[0]);
            }
            switch (initial[1].charAt(0)) {
                case 'M': {
                    if (initial[1].length() < 2) {
                        throw error(
                                "config error, doesn't assign any "
                                        + "notches to a moving rotor %s.\n"
                                        + "origin: %s", initial[0], rotor[0]
                        );
                    }
                    return new MovingRotor(initial[0],
                            new Permutation(rotor[1], _alphabet),
                            initial[1].substring(1));
                }
                case 'N': {
                    return new FixedRotor(initial[0],
                            new Permutation(rotor[1], _alphabet));
                }
                case 'R': {
                    return new Reflector(initial[0],
                            new Permutation(rotor[1], _alphabet));
                }
                default: {
                    throw error(
                            "config error, a rotor must be "
                                    + "either `R`, `N`, `M` configured, "
                                    + "but found %s",
                            initial[1].charAt(0));
                }
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     *
     * @return Whether the machine has been set up correctly.
     */
    private boolean setUp(Machine M, String settings) {
        if (settings.matches("\\s*")) {
            return false;
        }
        String[] rotors = new String[M.numRotors()];
        String[] line = settings.split("\\s+");
        if (line.length == 0) {
            throw error("input error, setting of rotors is wrong.");
        }
        if (line.length < M.numRotors() + 2) {
            throw error("input error, setting of rotors is wrong.\n"
                    + "setting: %s", settings);
        }
        if (!line[0].equals("*")) {
            throw error("input error, the setting format "
                    + "of rotors is incorrect.\n"
                    + "setting: %s", settings);
        }
        int index = 1;
        for (int i = 0; i < M.numRotors(); i++, index++) {
            rotors[i] = line[index];
        }
        M.insertRotors(rotors);
        M.setRotors(line[index++]);
        if (index < line.length
                && !line[index].matches("\\(.*?\\)")) {
            M.setRings(line[index++]);
        }
        StringBuffer plugboard = new StringBuffer();
        for (int i = index; i < line.length; i++) {
            plugboard.append(line[i]);
        }
        Permutation pb = new Permutation(new String(plugboard), _alphabet);
        M.setPlugboard(pb);
        return true;
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            if ((i + 1) % 5 == 0 && i != msg.length() - 1) {
                _output.append(msg.charAt(i));
                _output.append(" ");
            } else {
                _output.append(msg.charAt(i));
            }
        }
        _output.append("\n");
    }

    public String getEncode() {
        return new String(_output);
    }

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;

    /**
     * Source of input messages.
     */
    private Scanner _input;

    /**
     * Source of machine configuration.
     */
    private Scanner _config;

    /**
     * File for encoded/decoded messages.
     */
    private StringBuffer _output;

}
