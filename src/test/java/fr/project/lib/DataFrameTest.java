package fr.project.lib;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.project.lib.DataFrame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DataFrameTest {
    public static final String DUMMY_CSV_STRING = """
"REVIEW_DATE","AUTHOR","ISBN","DISCOUNTED_PRICE"
"1985/01/21","Douglas Adams",0345391802,5.95
"1990/01/12","Douglas Hofstadter",0465026567,9.95
"1998/07/15","Timothy ""The Parser"" Campbell",0968411304,18.99
"1999/12/03","Richard Friedman",0060630353,5.95
"2001/09/19","Karen Armstrong",0345384563,9.95
"2002/06/23","David Jones",0198504691,9.95
"2002/06/23","Julian Jaynes",0618057072,12.50
"2003/09/30","Scott Adams",0740721909,4.95
"2004/10/04","Benjamin Radcliff",0804818088,4.95
"2004/10/04","Randel Helms",0879755725,4.50
""";

    @Test
    void create() {
        var v = new DataFrame(1, 2);
    }

    @Test
    void create_csv() throws IOException{
        var v = new DataFrame(new ByteArrayInputStream(DUMMY_CSV_STRING.getBytes(StandardCharsets.UTF_8)));
        System.out.println(v);
        
    }
}
