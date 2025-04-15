package fr.project.lib.utility;

import static java.util.Arrays.asList;
import static org.antlr.v4.runtime.CharStreams.fromStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;

import fr.project.lib.CommaSeparatedValuesLexer;
import fr.project.lib.CommaSeparatedValuesParser;
import fr.project.lib.CommaSeparatedValuesParser.CommaSeparatedValuesFileContext;
import fr.project.lib.TabSeparatedValuesLexer;
import fr.project.lib.TabSeparatedValuesParser;
import fr.project.lib.TabSeparatedValuesParser.TabSeparatedValuesFileContext;

/**
 * A utility class for parsing tabular data from CSV/TSV files into a structured format.
 * Uses ANTLR parsers for robust parsing of delimited text files.
 */
public final class TableInput {
    /** The parsed data as a 2D String array [rows][columns] */
    public final String[][] data;
    
    /** Array of column labels */
    public final String[] col_label;
    
    /** Width (number of columns) of the table */
    public final int w;
    
    /** Height (number of rows) of the table */
    public final int h;

    /**
     * Constructs a new TableInput with specified dimensions
     * @param width Number of columns
     * @param height Number of rows
     */
    TableInput(int width, int height) {
        col_label = new String[width];
        data = new String[height][width];
        w = width;
        h = height;
    }

    /**
     * Fills empty cells in the table with default values:
     * - Empty column labels become "colN"
     * - Empty data cells become ""
     */
    public void fill() {
        for (int i = 0; i < w; i += 1) {
            if (col_label[i] == null)
                col_label[i] = "col" + i;
            for (int j = 0; j < h; j += 1) {
                if (data[j][i] == null) {
                    data[j][i] = "";
                }
            }
        }
    }

    /**
     * Parses a CSV field context into a String value
     * @param fc The ANTLR parser context for the field
     * @return The parsed String value
     */
    private static String parseField(CommaSeparatedValuesParser.FieldContext fc) {
        return parseField(fc.getText(), ParseFieldStatus.from(fc));
    }

    /**
     * Parses a TSV field context into a String value
     * @param fc The ANTLR parser context for the field
     * @return The parsed String value
     */
    private static String parseField(TabSeparatedValuesParser.FieldContext fc) {
        return parseField(fc.getText(), ParseFieldStatus.from(fc));
    }

    /**
     * Enum representing the different types of fields that can be parsed
     */
    private enum ParseFieldStatus {
        /** Unquoted text field */ 
        Text, 
        /** Quoted string field */
        String;

        /**
         * Determines field type from TSV parser context
         */
        private static ParseFieldStatus from(TabSeparatedValuesParser.FieldContext fc) {
            return fc.TEXT() != null ? ParseFieldStatus.Text : 
                  (fc.STRING() != null ? ParseFieldStatus.String : null);
        }

        /**
         * Determines field type from CSV parser context
         */
        private static ParseFieldStatus from(CommaSeparatedValuesParser.FieldContext fc) {
            return fc.TEXT() != null ? ParseFieldStatus.Text : 
                  (fc.STRING() != null ? ParseFieldStatus.String : null);
        }
    }

    /**
     * Parses a field's raw text according to its type
     * @param text The raw text content of the field
     * @param pfs The field's parse status (quoted/unquoted)
     * @return The processed String value
     */
    private static String parseField(String text, ParseFieldStatus pfs) {
        return pfs == null ? "" : switch (pfs) {
            case Text -> text;  // Unquoted text used as-is
            case String -> String.join("\"", text.substring(1, text.length() - 1).split("\"\""));
                // Quoted strings have outer quotes removed and escaped quotes un-doubled
        };
    }

    /**
     * Parses tab-separated values from an input stream
     * @param is The input stream containing TSV data
     * @return TableInput containing parsed data
     * @throws IOException If there's an error reading the stream
     */
    public static TableInput parseTabSeparatedValues(InputStream is) throws IOException {
        // Set up ANTLR parsing pipeline
        final CharStream ais = fromStream(is);
        final TabSeparatedValuesLexer lexer = new TabSeparatedValuesLexer(ais);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final TabSeparatedValuesParser parser = new TabSeparatedValuesParser(tokens);
        
        // Parse the complete file
        final TabSeparatedValuesFileContext tree = parser.tabSeparatedValuesFile();
        final TabSeparatedValuesParser.RowContext header_fields = tree.hdr().row();
        
        // Determine table dimensions
        final int width = header_fields.field().size();
        final int height = tree.row().size();
        final TableInput rv = new TableInput(width, height);

        // Parse column headers
        for (int i = 0; i < width; i += 1) {
            rv.col_label[i] = parseField(header_fields.field(i));
        }

        // Parse data rows
        for (int i = 0; i < height; i += 1) {
            final TabSeparatedValuesParser.RowContext rc = tree.row(i);
            for (int j = 0; j < width; j += 1) {
                rv.data[i][j] = parseField(rc.field(j));
            }
        }

        return rv;
    }

    /**
     * Parses comma-separated values from an input stream
     * @param is The input stream containing CSV data
     * @return TableInput containing parsed data
     * @throws IOException If there's an error reading the stream
     */
    public static TableInput parseCommaSeparatedValues(InputStream is) throws IOException {
        // Set up ANTLR parsing pipeline
        final CharStream ais = fromStream(is);
        final CommaSeparatedValuesLexer lexer = new CommaSeparatedValuesLexer(ais);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final CommaSeparatedValuesParser parser = new CommaSeparatedValuesParser(tokens);
        
        // Parse the complete file
        final CommaSeparatedValuesFileContext tree = parser.commaSeparatedValuesFile();
        final CommaSeparatedValuesParser.RowContext header_fields = tree.hdr().row();
        
        // Determine table dimensions
        final int width = header_fields.field().size();
        final int height = tree.row().size();
        final TableInput rv = new TableInput(width, height);

        // Parse column headers
        for (int i = 0; i < width; i += 1) {
            rv.col_label[i] = parseField(header_fields.field(i));
        }
        
        // Parse data rows
        for (int i = 0; i < height; i += 1) {
            final CommaSeparatedValuesParser.RowContext rc = tree.row(i);
            for (int j = 0; j < width; j += 1) {
                rv.data[i][j] = parseField(rc.field(j));
            }
        }

        return rv;
    }
}