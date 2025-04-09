package fr.project.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DataFrameTest {
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

        List<Integer> poped=v.pop("entier");
        List<Integer> expect= Arrays.asList(1,2,3);
        assertEquals(poped, expect);

        var u = new DataFrame(1, 3);
        u.data[0][0]="un";
        u.data[1][0]="deux";
        u.data[2][0]="trois";

        u.col_label[0]="string";
    }
}
