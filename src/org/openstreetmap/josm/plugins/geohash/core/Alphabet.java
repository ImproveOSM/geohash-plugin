package org.openstreetmap.josm.plugins.geohash.core;

import java.util.Collection;
import java.util.HashMap;


/**
 *
 *
 * @author Mihai Chintoanu
 */
class Alphabet {

    public static final Alphabet INSTANCE = new Alphabet();

    private final char[] characters = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    private final HashMap<Character, Integer> indexForCharacter = new HashMap<>();

    private Alphabet() {
        int i = 0;
        for (final char c : characters) {
            indexForCharacter.put(c, i++);
        }
    }

    public int bitsPerCharacter() {
        return 5;
    }

    public Collection<Character> characters() {
        return indexForCharacter.keySet();
    }

    public char get(final int index) {
        return characters[index];
    }

    public int indexOf(final char character) {
        return indexForCharacter.get(character);
    }

    public boolean isValid(final String text) {
        boolean valid = text != null;
        for (int i = 0; valid && i < text.length(); i++) {
            if (!indexForCharacter.keySet().contains(text.charAt(i))) {
                valid = false;
            }
        }
        return valid;
    }
}
