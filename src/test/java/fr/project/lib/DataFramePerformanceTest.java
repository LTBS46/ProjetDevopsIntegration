package fr.project.lib;
import fr.project.lib.DataFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataFramePerformanceTest {
    
    static DataFrame structuredDf;
    static DataFrame randomDf;
    private static final Path RESOURCES_DIR = Paths.get("src/test/resources");
    private static final String[] TEST_CSVS = {"stress_test.csv", "stress_test_random.csv" };
    @BeforeAll
    static void loadDataFrames() throws IOException {
        
        structuredDf = new DataFrame(RESOURCES_DIR.resolve(TEST_CSVS[0]).toString());
        randomDf = new DataFrame(RESOURCES_DIR.resolve(TEST_CSVS[1]).toString());
    }

    @Test
    void testLargeDataShape() {
        int[] shape1 = structuredDf.getShape();
        int[] shape2 = randomDf.getShape();

        assertTrue(shape1[0] > 9000);
        assertTrue(shape2[0] > 9000);
        assertEquals(1, shape1[1]);
        assertTrue(shape2[1] > 2);
    }

    @Test
    void testRandomAccessPerformance() {
        int rowCount = structuredDf.getShape()[0];
        int colCount = structuredDf.getShape()[1];

        long start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            int row = (int) (Math.random() % rowCount);
            int col = (int) (Math.random() % colCount);
            Object val = structuredDf.get(row, col);
            assertNotNull(val);
        }
        long duration = System.nanoTime() - start;
        System.out.printf("Random access of 100 elements took %.2f ms%n", duration / 1e6);
    }

    // @Test
    // void testPopColumnStress() {
    //     int originalCols = randomDf.getShape()[1];
    //     int originalRows = randomDf.getShape()[0];

    //     Object[] popped = randomDf.pop("ge");
    //     assertEquals(originalRows, popped.length);
    //     assertEquals(originalCols - 1, randomDf.getShape()[1]);
    // }

    @Test
    void testToStringOnLargeDataFrame() {
        String result = structuredDf.toString();
        assertNotNull(result);
        assertTrue(result.length() > 500); // Should show part of large data
    }

    @Test
    void testColumnSubsetPerformance() {
        int colCount = randomDf.getShape()[1];
        assertTrue(colCount >= 3, "Expected at least 3 columns");

        long start = System.nanoTime();
        IDataFrame subset = randomDf.get(0, 1, 2);
        long duration = System.nanoTime() - start;

        assertNotNull(subset);
        assertEquals(randomDf.getShape()[0], subset.getShape()[0]);
        assertEquals(3, subset.getShape()[1]);
        System.out.printf("Subsetting 3 columns took %.2f ms%n", duration / 1e6);
    }

    @Test
    void testLoadPerformance() throws IOException {
        long start = System.currentTimeMillis();
        DataFrame df = new DataFrame(new FileInputStream(RESOURCES_DIR.resolve(TEST_CSVS[0]).toString()));
        long duration = System.currentTimeMillis() - start;
        System.out.printf("Loading stress_test.csv took %d ms%n", duration);
        assertTrue(df.getShape()[0] > 1000);
    }
}
