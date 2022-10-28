package enigma;

import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author Aayush Gupta
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {

        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _rotors = new Rotor[numRotors];
        _allRotors = allRotors;
        _plugBoard = null;

    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotors[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {

        if (_numRotors != rotors.length) {
            throw new EnigmaException("Incorrect number of Rotors provided.");
        }

        for (int i = 0; i < rotors.length; i++) {
            for (int k = 0; k < rotors.length; k++) {
                if (i != k && rotors[i].equals(rotors[k])) {
                    throw new EnigmaException("Please try again. "
                            + "Rotors are repeated.");
                }
            }
        }

        for (int i = 0; i < rotors.length; i++) {
            for (Rotor rotor : _allRotors) {
                if (rotors[i].equals(rotor.name())) {
                    _rotors[i] = rotor;
                    break;
                }
            }

            if (_rotors[i] == null) {
                throw new EnigmaException("Please try again."
                        + " Rotor not found in the Machine.");
            }
        }

        if (!_rotors[0].reflecting()) {
            throw new EnigmaException("Reflector is either not "
                    + "present or in incorrect location");
        }

    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Please enter"
                    + " valid number of settings.");
        }

        for (int i = 0; i < _numRotors - 1; i++) {
            _rotors[i + 1].set(setting.charAt(i));
        }

    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugBoard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar
                                (getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }

        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        for (int i = _numRotors - numPawls(); i < _numRotors; i++) {
            if (i == _numRotors - 1) {
                _rotors[i].advance();
            } else {
                if (_rotors[i].rotates()) {
                    if (_rotors[i + 1].atNotch()) {
                        _rotors[i].advance();
                        _rotors[i + 1].advance();
                        i++;
                        if (i == _numRotors - 1) {
                            break;
                        }
                    }
                }
            }
        }

    }


    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        advanceRotors();
        if (plugboard() != null) {
            c = _plugBoard.permute(c);
        }
        for (int i = _numRotors - 1; i >= 0; i--) {
            c = _rotors[i].convertForward(c);
        }
        for (int i = 1; i < _numRotors; i++) {
            c = _rotors[i].convertBackward(c);
        }
        if (plugboard() != null) {
            c = _plugBoard.permute(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String newMsg = "";
        for (int i = 0; i < msg.length(); i++) {
            char result = _alphabet.toChar
                    (convert(_alphabet.toInt(msg.charAt(i))));
            newMsg = newMsg + result;
        }
        return newMsg;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors in the machine. */
    private final int _numRotors;

    /** Number of pawls in the machine. */
    private final int _pawls;

    /** Machine's plugboard permutation. */
    private Permutation _plugBoard;

    /** Rotors in the machine. */
    private Rotor[] _rotors;

    /** Collection of all rotors available to the machine. */
    private final Collection<Rotor> _allRotors;

}
