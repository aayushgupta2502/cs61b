package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Aayush Gupta
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        setUp(m, _input.nextLine());

        while (_input.hasNextLine()) {
            String nextLine = _input.nextLine();
            if (nextLine.contains("*")) {
                setUp(m, nextLine);
            } else {
                nextLine = nextLine.replace(" ", "");
                printMessageLine(m.convert(nextLine));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            if (_alphabet.contains('(')
                    || _alphabet.contains(')')
                    || _alphabet.contains('*')) {
                throw new EnigmaException("Invalid characters"
                        + " in alphabet.");
            }
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();

            if (numRotors < pawls) {
                throw new EnigmaException("Invalid number"
                        + " of pawls (less than rotors).");
            }
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            String rotorType = _config.next();

            String cycles = "";
            while (_config.hasNext("\\(.+\\)")) {
                cycles += _config.next("\\(.+\\)");
            }

            Permutation perm = new Permutation(cycles, _alphabet);

            if (rotorType.substring(0, 1).equals("R")) {
                return new Reflector(rotorName, perm);
            } else if (rotorType.substring(0, 1).equals("N")) {
                return new FixedRotor(rotorName, perm);
            } else {
                String notches = rotorType.substring(1);
                return new MovingRotor(rotorName, perm, notches);
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String clean = settings.replace("*", "");
        String [] rotors = new String[M.numRotors()];

        if (clean.contains("(")) {
            String clean2 = clean.substring(0, clean.indexOf("("));
            String [] check = clean2.split(" ");
            if (M.numRotors() + 2 != check.length) {
                throw error("Incorrect number of rotors.");
            }
        } else {
            String [] check2 = clean.split(" ");
            if (M.numRotors() + 2 != check2.length) {
                throw error("Incorrect number of rotors.");
            }
        }

        for (int i = 0; i < rotors.length; i++) {
            while (clean.charAt(0) == ' ') {
                clean = clean.substring(1);
            }
            rotors[i] = clean.substring(0,
                    clean.indexOf(' '));
            clean = clean.substring(clean.indexOf(' '));
        }

        M.insertRotors(rotors);

        while (clean.charAt(0) == ' ') {
            clean = clean.substring(1);
        }

        if (clean.contains("(")) {
            M.setRotors(clean.substring(0, clean.indexOf(' ')));
            clean = clean.substring(clean.indexOf(" "));
            while (clean.charAt(0) == ' ') {
                clean = clean.substring(1);
                Permutation p = new Permutation(clean, _alphabet);
                M.setPlugboard(p);
            }
        } else {
            M.setRotors(clean);
        }
    }


    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        while (msg.length() > 5) {
            _output.print(msg.substring(0, 5) + " ");
            msg = msg.substring(5);
        }
        _output.println(msg);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** Collection of all rotors available to the machine. */
    private ArrayList<Rotor> _allRotors = new ArrayList<>();

}
