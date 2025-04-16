package fr.project.lib;
import  java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;


public abstract class Utility {
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
    static Class<LocalDate> try_parse_date(String a) {
        try{
            LocalDate.parse(a.replace('/', '-'));
            return LocalDate.class;
        }  catch(DateTimeParseException ne){
            return null;
        } 
    }

    static Class<Boolean> try_parse_bool(String a) {
        return switch(a) {
            case "true", "false", "True", "False" -> Boolean.class;
            default -> null;
        };
    }
}
