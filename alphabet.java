package enigma;

import java.util.HashMap;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Aayush Gupta
 */
class Alphabet {

    /** Hashmap that contains the alphabet. */
    private HashMap<Integer, Character> _alphabet;

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {

        _alphabet = new HashMap<Integer, Character>();
        for (int i = 0; i < chars.length(); i++) {
            char holder = chars.charAt(i);
            _alphabet.put(i, holder);
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabet.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (char character : _alphabet.values()) {
            if (character == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabet.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int counter = 0;
        for (char character : _alphabet.values()) {
            if (character == ch) {
                break;
            }
            counter++;
        }
        return counter;
    }

}
