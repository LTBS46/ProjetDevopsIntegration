package fr.project.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    void Testpopclassique(){
        var v = new DataFrame(2, 3);
        v.data[0][0]=1;
        v.data[1][0]=2;
        v.data[2][0]=3;

        v.data[0][1]="un";
        v.data[1][1]="deux";
        v.data[2][1]="trois";

        v.col_label[0]="entier";
        v.col_label[1]="string";

        List<Object> poped=v.pop("entier");
        List<Object> expect= Arrays.asList(1,2,3);
        assertEquals(poped, expect);

        var u = new DataFrame(1, 3);
        u.data[0][0]="un";
        u.data[1][0]="deux";
        u.data[2][0]="trois";

        u.col_label[0]="string";
    }
    
    @Test
    void create_csv() throws IOException{
        var v = new DataFrame(new ByteArrayInputStream(DUMMY_CSV_STRING.getBytes(StandardCharsets.UTF_8)));
        System.out.println(v);        
    }
}
