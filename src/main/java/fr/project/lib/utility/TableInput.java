package fr.project.lib.utility;

import static java.util.Arrays.asList;
import static org.antlr.v4.runtime.CharStreams.fromStream;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import fr.project.lib.CommaSeparatedValuesLexer;
import fr.project.lib.CommaSeparatedValuesParser;
import fr.project.lib.CommaSeparatedValuesParser.CommaSeparatedValuesFileContext;
import fr.project.lib.TabSeparatedValuesLexer;
import fr.project.lib.TabSeparatedValuesParser;
import fr.project.lib.TabSeparatedValuesParser.TabSeparatedValuesFileContext;

// intermediary class used by CSV
public final class TableInput {

    public final String[][] data;
    public final String[] col_label;
    public final int w, h;

    TableInput(int width, int height) {
        col_label = new String[width];
        data = new String[height][width];
        w = width;
        h = height;
    }

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

    private static String parseField(CommaSeparatedValuesParser.FieldContext fc) {
        if (fc.STRING() == null) {
            return fc.getText();
        } else if (fc.TEXT() == null) {
            String val = fc.getText();
            String chopped = val.substring(1, val.length() - 1);
            String[] content = chopped.split("\"\"");
            return String.join("\"", asList(content));
        } else {
            return "";
        }
    }

    private static String parseField(TabSeparatedValuesParser.FieldContext fc) {
        if (fc.STRING() == null) {
            return fc.getText();
        } else if (fc.TEXT() == null) {
            String val = fc.getText();
            String chopped = val.substring(1, val.length() - 1);
            String[] content = chopped.split("\"\"");
            return String.join("\"", asList(content));
        } else {
            return "";
        }
    }

    public static TableInput parseTabSeparatedValues(InputStream is) throws IOException {
        CharStream ais = fromStream(is);
        TabSeparatedValuesLexer lexer = new TabSeparatedValuesLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TabSeparatedValuesParser parser = new TabSeparatedValuesParser(tokens);
        TabSeparatedValuesFileContext tree = parser.tabSeparatedValuesFile();
        var header_fields = tree.hdr().row();
        int width = header_fields.field().size();
        int height = tree.row().size();

        TableInput rv = new TableInput(width, height);

        for (int i = 0; i < width; i += 1) {
            rv.col_label[i] = parseField(header_fields.field(i));
        }

        for (int i = 0; i < height; i += 1) {
            TabSeparatedValuesParser.RowContext rc = tree.row(i);
            for (int j = 0; j < width; j += 1) {
                rv.data[i][j] = parseField(rc.field(j));
            }
        }

        return rv;
    }

    public static TableInput parseCommaSeparatedValues(InputStream is) throws IOException {
        CharStream ais = fromStream(is);
        CommaSeparatedValuesLexer lexer = new CommaSeparatedValuesLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CommaSeparatedValuesParser parser = new CommaSeparatedValuesParser(tokens);
        CommaSeparatedValuesFileContext tree = parser.commaSeparatedValuesFile();
        var header_fields = tree.hdr().row();
        int width = header_fields.field().size();
        int height = tree.row().size();

        TableInput rv = new TableInput(width, height);

        for (int i = 0; i < width; i += 1) {
            rv.col_label[i] = parseField(header_fields.field(i));
        }
        for (int i = 0; i < height; i += 1) {
            CommaSeparatedValuesParser.RowContext rc = tree.row(i);
            for (int j = 0; j < width; j += 1) {
                rv.data[i][j] = parseField(rc.field(j));
            }
        }

        return rv;
    }
}
