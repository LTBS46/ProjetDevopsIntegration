package fr.project.lib;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class Utility {
    private Utility() {
    }

    static Class<Float> try_parse_float(String a) {
        try {
            parseFloat(a);
            return Float.class;
        } catch (NumberFormatException ne) {
            return null;
        }
    }

    static Class<Integer> try_parse_int(String a) {
        try {
            parseInt(a);
            return Integer.class;
        } catch (NumberFormatException ne) {
            return null;
        }
    }
}
