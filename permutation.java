package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Aayush Gupta
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String clean = cycles;
        clean = clean.replace("(", " ");
        clean = clean.replace(")", " ");
        _cycles = clean.split(" ");

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String[] newCycle = new String[_cycles.length + 1];
        for (int i = 0; i < _cycles.length; i++) {
            newCycle[i] = _cycles[i];
        }
        newCycle[_cycles.length + 1] = cycle;
        _cycles = newCycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {

        char match = _alphabet.toChar(wrap(p));
        char map = '_';

        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == match) {
                    map = _cycles[i].charAt((j + 1) % _cycles[i].length());
                    break;
                }
            }
        }
        if (map == '_') {
            return p;
        }
        return _alphabet.toInt(map);

    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char match = _alphabet.toChar(wrap(c));
        char map = '_';

        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == match) {
                    if (j == 0) {
                        map = _cycles[i].
                                charAt(_cycles[i].length() - 1);
                    } else {
                        map = _cycles[i].
                                charAt(((j - 1) % _cycles[i].length()));
                    }
                }
            }
        }
        if (map == '_') {
            return c;
        }
        return _alphabet.toInt(map);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int match = _alphabet.toInt(p);
        int index = permute(match);
        return _alphabet.toChar(index);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int match = _alphabet.toInt(c);
        int index = invert(match);
        return _alphabet.toChar(index);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _cycles.length; i++) {
            if (_cycles[i].length() == 1) {
                return false;
            }
        }
        return false;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles within this permutation. */
    private String[] _cycles;
}
