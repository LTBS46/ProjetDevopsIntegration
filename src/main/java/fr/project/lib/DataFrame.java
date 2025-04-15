package fr.project.lib;

import static java.lang.System.arraycopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import fr.project.lib.utility.TableInput;

/**
 * A Java implementation of a DataFrame similar to pandas' DataFrame.
 * Stores tabular data with labeled columns and rows, supporting various data operations.
 */
public class DataFrame implements IDataFrame {
    // Core data storage as a 2D array [rows][columns]
    Object[][] data;
    
    // Column names array
    String[] col_label;
    
    // Row labels array
    String[] li_label;
    
    // Data types for each column
    Class<?>[] col_types;

    // Static map of parsers for converting strings to specific types
    private static final Map<Class<?>, Function<String, Object>> parsers;

    // Static list of type detection functions
    private static final List<Function<String, Class<?>>> type_find;

    // Static initialization block for parsers and type detectors
    static {
        parsers = new HashMap<>();
        type_find = new ArrayList<>();

        // Register basic type parsers
        parsers.put(String.class, (s) -> s);
        parsers.put(Integer.class, Integer::parseInt);
        parsers.put(Float.class, Float::parseFloat);

        // Register type detection functions (order matters - most specific first)
        type_find.add(Utility::try_parse_int);
        type_find.add(Utility::try_parse_float);
    }

    /**
     * Constructs DataFrame from a file
     * @param filename Path to input file
     * @throws IOException If file reading fails
     */
    DataFrame(String filename) throws IOException {
        this(new FileInputStream(filename));
    }

    /**
     * Constructs DataFrame from an input stream (CSV format by default)
     * @param is Input stream containing data
     * @throws IOException If stream reading fails
     */
    DataFrame(InputStream is) throws IOException {
        this(is, InputFormat.CommaSeparatedValues);
    }

    /**
     * Supported input file formats
     */
    public enum InputFormat {
        CommaSeparatedValues, TabSeparatedValues
    }

    /**
     * Constructs DataFrame from input stream with specified format
     * @param is Input stream containing data
     * @param _if Format of the input data
     * @throws IOException If stream reading fails
     */
    DataFrame(InputStream is, InputFormat _if) throws IOException {
        this(switch (_if) {
            case CommaSeparatedValues -> TableInput.parseCommaSeparatedValues(is);
            case TabSeparatedValues -> TableInput.parseTabSeparatedValues(is);
        });
    }
    
    /**
     * Constructs DataFrame from TableInput object
     * @param ti Pre-parsed table input structure
     */
    DataFrame(TableInput ti) {
        int width = ti.w;
        int height = ti.h;
        init(width, height, InitMode.PutDefault);
        col_label = new String[width];
        ti.fill();
        arraycopy(ti.col_label, 0, col_label, 0, width);

        // Copy raw string data
        for (int i = 0; i < height; i += 1) {
            String[] act = ti.data[i];
            for (int j = 0; j < width; j += 1) {
                data[i][j] = act[j];
            }
        }
        
        // Detect column types using first row
        Class<?>[] types = new Class[width];
        for (int j = 0; j < width; j += 1) {
            Class<?> tt = null;
            for (Function<String, Class<?>> fsc : type_find) {
                tt = fsc.apply((String) data[0][j]);
                if (tt != null) {
                    break;
                }
            }
            types[j] = tt != null ? tt : String.class;
        }

        // Convert all data to proper types
        for (int i = 0; i < width; i++) {
            Class<?> c = types[i];
            Function<String, Object> col_parse = parsers.get(c);
            for (int j = 0; j < height; j++) {
                data[j][i] = col_parse.apply((String) data[j][i]);
            }
        }
        col_types = types;
    }

    /**
     * Creates empty DataFrame with specified dimensions
     * @param width Number of columns
     * @param height Number of rows
     */
    DataFrame(int width, int height) {
        init(width, height, InitMode.PutDefault);
    }

    /**
     * Initialization modes for new DataFrames
     */
    private enum InitMode {
        PutBlank, PutDefault
    }

    /**
     * Initializes DataFrame storage with specified mode
     * @param width Number of columns
     * @param height Number of rows
     * @param im Initialization mode (blank or default labels)
     */
    private void init(int width, int height, InitMode im) {
        data = new Object[height][width];
        col_label = new String[width];
        li_label = new String[height];
        if (im == null) {
        } else if (im == InitMode.PutBlank) {
            for (int i = 0; i < height; i += 1)
                li_label[i] = "";
            for (int j = 0; j < width; j += 1)
                col_label[j] = "";
        } else if (im == InitMode.PutDefault) {
            for (int i = 0; i < height; i += 1)
                li_label[i] = "" + i;
            for (int j = 0; j < width; j += 1)
                col_label[j] = "" + j;
        } else {
            throw new RuntimeException("");
        }
    }

    /**
     * Checks if DataFrame is empty
     * @return true if DataFrame contains no data
     */
    @Override
    public boolean getEmpty() {
        return data == null || data.length == 0 || data[0].length == 0;
    }

    /**
     * Gets total number of cells in DataFrame
     * @return Count of all data cells
     */
    @Override
    public int getSize() {
        if (this.getEmpty()) {
            return 0;
        }
        return data.length * data[0].length;
    }

    /**
     * Gets dimensions of DataFrame [rows, columns]
     * @return 2-element array with row and column counts
     */
    @Override
    public int[] getShape() {
        if (this.getEmpty()) {
            return new int[] { 0, 0 };
        }
        return new int[] { data.length, data[0].length };
    }

    /**
     * Removes and returns a column from DataFrame
     * @param s Name of column to remove
     * @return List of values from the removed column
     * @throws IllegalArgumentException if column not found or DataFrame is empty
     */
    @Override
    public List<Object> pop(String s) {
        if (this.getEmpty()) {
            throw new IllegalArgumentException("DataFrame is empty");
        }

        int temp = -1;
        for (int i = 0; i < col_label.length; i++) {
            if (col_label[i].equals(s)) {
                temp = i;
                break;
            }
        }

        if (temp == -1) {
            throw new IllegalArgumentException("Column '" + s + "' not found");
        }

        // Extract column values
        List<Object> colonne = new ArrayList<>();
        int numRows = data.length;
        int numCols = data[0].length;

        for (int i = 0; i < numRows; i++) {
            colonne.add(data[i][temp]);
        }

        // Create new data structure without the column
        Object[][] newData = new Object[numRows][numCols - 1];
        for (int i = 0; i < numRows; i++) {
            int newColIdx = 0;
            for (int j = 0; j < numCols; j++) {
                if (j != temp) {
                    newData[i][newColIdx++] = data[i][j];
                }
            }
        }

        // Update column labels
        String[] newColLabels = new String[col_label.length - 1];
        int newIdx = 0;
        for (int i = 0; i < col_label.length; i++) {
            if (i != temp) {
                newColLabels[newIdx++] = col_label[i];
            }
        }

        // Modify current DataFrame
        this.data = newData;
        this.col_label = newColLabels;

        return colonne;
    }

    /**
     * Gets subset DataFrame containing specified columns (by name)
     * @param cols Names of columns to include
     * @return New DataFrame with only specified columns
     * @throws IllegalArgumentException if any column not found
     */
    @Override
    public IDataFrame get(String... cols) {
        return getByName(cols);
    }

    /**
     * Gets subset DataFrame containing specified columns (by index)
     * @param colIndices Indices of columns to include
     * @return New DataFrame with only specified columns
     * @throws IndexOutOfBoundsException if any index is invalid
     */
    public IDataFrame get(int... colIndices) {
        return getByIndices(colIndices);
    }

    /**
     * Internal implementation of get() for column names
     */
    private IDataFrame getByName(String[] cols){
        if (this.getEmpty()) {
            throw new IllegalStateException("DataFrame is empty");
        }
        
        if (cols == null || cols.length == 0) {
            throw new IllegalArgumentException("At least one column must be specified");
        }
    
        // Resolve and validate column indices
        int[] colIndices = new int[cols.length];
        for (int i = 0; i < cols.length; i++) {
            colIndices[i] = findColumnIndex(cols[i]);
            if (colIndices[i] == -1) {
                throw new IllegalArgumentException("Column '" + cols[i] + "' not found");
            }
        }
    
        return createColumnSubset(colIndices);
    }

    /**
     * Internal implementation of get() for column indices
     */
    private IDataFrame getByIndices(int[] colIndices) {
        if (this.getEmpty()) {
            throw new IllegalStateException("DataFrame is empty");
        }
        
        if (colIndices == null || colIndices.length == 0) {
            throw new IllegalArgumentException("At least one column must be specified");
        }
    
        // Validate all column indices
        for (int i = 0; i < colIndices.length; i++) {
            if (colIndices[i] < 0 || colIndices[i] >= col_label.length) {
                throw new IndexOutOfBoundsException(
                    String.format("Column index %d out of bounds [0-%d]", 
                        colIndices[i], col_label.length - 1));
            }
        }
    
        return createColumnSubset(colIndices);
    }
        
    /**
     * Finds index of column by name
     * @param colName Name of column to find
     * @return Column index or -1 if not found
     */
    private int findColumnIndex(String colName) {
        for (int i = 0; i < col_label.length; i++) {
            if (col_label[i].equals(colName)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Creates new DataFrame with subset of columns
     * @param colIndices Indices of columns to include
     * @return New DataFrame with specified columns
     */
    private IDataFrame createColumnSubset(int[] colIndices) {
        DataFrame subset = new DataFrame(colIndices.length, data.length);
        
        // Copy column labels
        for (int j = 0; j < colIndices.length; j++) {
            subset.col_label[j] = this.col_label[colIndices[j]];
        }
        
        // Copy row labels
        System.arraycopy(this.li_label, 0, subset.li_label, 0, data.length);
        
        // Copy data cells
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < colIndices.length; j++) {
                subset.data[i][j] = this.data[i][colIndices[j]];
            }
        }
        
        // Copy column type information
        subset.col_types = new Class<?>[colIndices.length];
        for (int j = 0; j < colIndices.length; j++) {
            subset.col_types[j] = this.col_types[colIndices[j]];
        }
        
        return subset;
    }

    /**
     * Generates string representation of DataFrame
     * @return Formatted table showing data with labels
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("\t");
        for (String hd : col_label) {
            sb.append("\t").append(hd);
        }
        sb.append("\n");
        for (int i = 0; i < data.length; i++) {
            sb.append(li_label[i]);
            for (int j = 0; j < data[i].length; j += 1) {
                sb.append("\t").append(data[i][j].toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}