package fr.project.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class DataFrameTest {

    @Test
    void Testpopclassique() {
        var v = new DataFrame(2, 3);
        v.data[0][0] = 1;
        v.data[1][0] = 2;
        v.data[2][0] = 3;

        v.data[0][1] = "un";
        v.data[1][1] = "deux";
        v.data[2][1] = "trois";

        v.col_label[0] = "entier";
        v.col_label[1] = "string";

        List<Object> poped = v.pop("entier");
        List<Object> expect = asList(1, 2, 3);
        assertEquals(poped, expect);

        var u = new DataFrame(1, 3);
        u.data[0][0] = "un";
        u.data[1][0] = "deux";
        u.data[2][0] = "trois";

        u.col_label[0] = "string";
    }

    private final static Class<?>[] DUMMY1_TYPES = new Class<?>[] {
            String.class, String.class, Integer.class, Float.class
    };

    @Test
    void create_csv() throws IOException {
        DataFrame v;
        try (FileInputStream f = new FileInputStream("src/test/resources/DUMMY1.csv")) {
            v = new DataFrame(f);
        }
        assertArrayEquals(v.col_types, DUMMY1_TYPES);
        assertEquals(v.getSize(), 40);
    }
}
