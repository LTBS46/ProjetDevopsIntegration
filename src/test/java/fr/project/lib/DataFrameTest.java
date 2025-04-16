package fr.project.lib;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class DataFrameTest {
    private IDataFrame df;
    private static final Path RESOURCES_DIR = Paths.get("src/test/resources");
    private static final String[] TEST_CSVS = {
        "normal_data.csv",
        "missing_values.csv",
        "mixed_types.csv",
        "stress_test_random.csv"
    };

    @BeforeEach
    void initDataFrame() throws IOException {
        df = new DataFrame(RESOURCES_DIR.resolve(TEST_CSVS[0]).toString());
    }

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
            LocalDate.class, String.class, Integer.class, Float.class
    };
    private final static Class<?>[] DUMMY2_TYPES = new Class<?>[] {
            Integer.class, Integer.class
    };

    @Test
    void create_csv() throws IOException {
        DataFrame v;
        try (FileInputStream f = new FileInputStream("src/test/resources/DUMMY1.csv")) {
            v = new DataFrame(f, DataFrame.InputFormat.CommaSeparatedValues);
        }
        assertArrayEquals(v.col_types, DUMMY1_TYPES);
        assertEquals(v.getSize(), 40);
    }

    @Test
    void create_tsv() throws IOException {
        DataFrame v;
        try (FileInputStream f = new FileInputStream("src/test/resources/DUMMY2.tsv")) {
            v = new DataFrame(f, DataFrame.InputFormat.TabSeparatedValues);
        }
        assertArrayEquals(v.col_types, DUMMY2_TYPES);
        assertEquals(v.getSize(), 10);
    }


    @Test
    void testOverrideing() {
        for (Method m : Arrays.stream(DataFrame.class.getMethods()).toList()) {
            if (m.getDeclaringClass() == IDataFrame.class) {
                System.out.println("Attention : " + m.getName()
                        + " has not been implemented it will trigger an error in the future");
                System.out.println();
            }
        }
    }



      /* --------------------------
        Core Functionality Tests
       -------------------------- */
    @Test
    void testStructure() {
        assertAll(
            () -> assertFalse(df.getEmpty()),
            () -> assertArrayEquals(new int[]{4, 5}, df.getShape()), // 4 rows in normal_data.csv
            () -> assertEquals(20, df.getSize()) // 4 rows * 5 cols
        );
    }

    @Test 
    void testTypeDetection() {
        assertAll(
            () -> assertEquals(Integer.class, df.getElem(0, "age").getClass()),
            () -> assertEquals(Float.class, df.getElem(0, "salary").getClass()),
            () -> assertEquals(Boolean.class, df.getElem(0, "active").getClass())
        );
    }

    /* --------------------------
        Edge Case Tests
       -------------------------- */
    @Test
    void testMissingValues() throws IOException {
        IDataFrame edgeDf = new DataFrame(RESOURCES_DIR.resolve(TEST_CSVS[0]).toString());
        
        assertAll(
            () -> assertNull(edgeDf.getElem(0, "stock")), // Empty number
            () -> assertNull(edgeDf.getElem(1, "price")),  // Empty number
            () -> assertEquals("", edgeDf.getElem(2, "product")) // Empty string
        );
    }

    @Test
    void testMixedTypes() throws IOException {
        assertThrows(NumberFormatException.class,()-> new DataFrame(RESOURCES_DIR.resolve(TEST_CSVS[2]).toString()));
        /*IDataFrame mixedDf = 
        
        assertAll(
            () -> assertEquals(Integer.class, mixedDf.getElem(1, "value").getClass()), // -3 as Integer
            () -> assertEquals(Float.class, mixedDf.getElem(0, "value").getClass()),   // 15.5 as Float
            () -> assertEquals(String.class, mixedDf.getElem(3, "value").getClass())   // "test" as String
        );*/
    }

    /* --------------------------
        Performance Tests
       -------------------------- */ 
    @Test
    @Timeout(2) // 2 second timeout for large file
    void testLargeDataset() throws IOException {
        IDataFrame largeDf = new DataFrame(RESOURCES_DIR.resolve(TEST_CSVS[3]).toString());
        assertTrue(largeDf.getShape()[0] >= 1000); // Verify scale
    }

    /* --------------------------
        Data Operations
       -------------------------- */
    @Test
    void testColumnSubset() {
        IDataFrame subset = df.get("name", "age");
        assertEquals(2, subset.getShape()[1]); // 2 columns
    }

    @Test
    void testRowSubset() {
        Object[] row = (Object[]) df.getElem(2, null); // 3rd row
        assertEquals((Integer)3, row[0]); // Verify ID
    }

    @Test
    void testPopColumn() {
        int originalCols = df.getShape()[1];
        List<Object> ages = df.pop("age");
        
        assertAll(
            () -> assertEquals(4, ages.size()), // 4 rows
            () -> assertEquals(originalCols - 1, df.getShape()[1]) // Columns reduced
        );
    }

    /* --------------------------
        File Format Tests
       -------------------------- */
    @Test
    void testTabDelimited() throws IOException {
        // Create temporary TSV file
        Path tsvFile = RESOURCES_DIR.resolve("test.tab");
        Files.writeString(tsvFile, "col1\tcol2\nval1\tval2");
        
        try {
            DataFrame tsvDf = new DataFrame(
                new FileInputStream(tsvFile.toFile()),
                DataFrame.InputFormat.TabSeparatedValues
            );
            assertEquals("val2", tsvDf.getElem(0, "col2"));
        } finally {
            Files.deleteIfExists(tsvFile);
        }
    }
}

