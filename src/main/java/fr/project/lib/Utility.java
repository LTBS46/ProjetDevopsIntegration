package fr.project.lib;

public class Utility {
    private Utility() {
    }

    static Class<Float> try_parse_float(String a) {
        try {
            Float.parseFloat(a);
            return Float.class;
        } catch (NumberFormatException ne) {
            return null;
        }
    }

    static Class<Integer> try_parse_int(String a) {
        try {
            Integer.parseInt(a);
            return Integer.class;
        } catch (NumberFormatException ne) {
            return null;
        }
    }
}
