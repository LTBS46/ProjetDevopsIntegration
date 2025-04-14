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
        return parseField(fc.getText(), ParseFieldStatus.from(fc));
    }

    private static String parseField(TabSeparatedValuesParser.FieldContext fc) {
        return parseField(fc.getText(), ParseFieldStatus.from(fc));
    }

    private enum ParseFieldStatus {
        Text, String;

        private static ParseFieldStatus from(TabSeparatedValuesParser.FieldContext fc) {
            return fc.TEXT() != null ? ParseFieldStatus.Text : (fc.STRING() != null ? ParseFieldStatus.String : null);
        }

        private static ParseFieldStatus from(CommaSeparatedValuesParser.FieldContext fc) {
            return fc.TEXT() != null ? ParseFieldStatus.Text : (fc.STRING() != null ? ParseFieldStatus.String : null);
        }
    }

    private static String parseField(String text, ParseFieldStatus pfs) {
        return pfs == null ? "" : switch (pfs) {
            case Text -> text;
            case String -> String.join("\"", text.substring(1, text.length() - 1).split("\"\""));
        };
    }

    /*    private static <LEXER extends TokenSource, PARSER, FILECONTEXT, ROWCONTEXT, FIELDCONTEXT> TableInput parseSV(
            final InputStream is, final Function<CharStream, LEXER> lx, final Function<TokenStream, PARSER> ps,
            final Function<PARSER, FILECONTEXT> fc, final Function<FILECONTEXT, ROWCONTEXT> hd,
            final ToIntFunction<ROWCONTEXT> rlen,final ToIntFunction<FILECONTEXT> hlen,
            final BiFunction<ROWCONTEXT, Integer, FIELDCONTEXT> ritem, final Function<FIELDCONTEXT, String> pf,
            final BiFunction<FILECONTEXT, Integer, ROWCONTEXT> gl) throws IOException {
        final CharStream ais = fromStream(is);
        final LEXER lexer = lx.apply(ais);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final PARSER parser = ps.apply(tokens);
        final FILECONTEXT tree = fc.apply(parser);
        final ROWCONTEXT header_fields = hd.apply(tree);
        final int width = rlen.applyAsInt(header_fields);
        final int height = hlen.applyAsInt(tree);
        final TableInput rv = new TableInput(width, height);

        for (int i = 0; i < width; i += 1) {
            rv.col_label[i] = pf.apply(ritem.apply(header_fields, i));
        }

        for (int i = 0; i < height; i += 1) {
            final ROWCONTEXT rc = gl.apply(tree, i);
            for (int j = 0; j < width; j += 1) {
                rv.data[i][j] = pf.apply(ritem.apply(rc, j));
            }
        }

        return rv;
    }

    public static TableInput _parseTabSeparatedValues(InputStream is) throws IOException {
        return parseSV(is, TabSeparatedValuesLexer::new, TabSeparatedValuesParser::new,
                TabSeparatedValuesParser::tabSeparatedValuesFile, (f) -> f.hdr().row(), (r) -> r.field().size(),
                (t) -> t.row().size(), (r, i) -> r.field(i), TableInput::parseField, (l, i) -> l.row(i));
    }*/

    public static TableInput parseTabSeparatedValues(InputStream is) throws IOException {
        final CharStream ais = fromStream(is);
        final TabSeparatedValuesLexer lexer = new TabSeparatedValuesLexer(ais);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final TabSeparatedValuesParser parser = new TabSeparatedValuesParser(tokens);
        final TabSeparatedValuesFileContext tree = parser.tabSeparatedValuesFile();
        final TabSeparatedValuesParser.RowContext header_fields = tree.hdr().row();
        final int width = header_fields.field().size();
        final int height = tree.row().size();
        final TableInput rv = new TableInput(width, height);

        for (int i = 0; i < width; i += 1) {
            rv.col_label[i] = parseField(header_fields.field(i));
        }

        for (int i = 0; i < height; i += 1) {
            final TabSeparatedValuesParser.RowContext rc = tree.row(i);
            for (int j = 0; j < width; j += 1) {
                rv.data[i][j] = parseField(rc.field(j));
            }
        }

        return rv;
    }

    public static TableInput parseCommaSeparatedValues(InputStream is) throws IOException {
        final CharStream ais = fromStream(is);
        final CommaSeparatedValuesLexer lexer = new CommaSeparatedValuesLexer(ais);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final CommaSeparatedValuesParser parser = new CommaSeparatedValuesParser(tokens);
        final CommaSeparatedValuesFileContext tree = parser.commaSeparatedValuesFile();
        final CommaSeparatedValuesParser.RowContext header_fields = tree.hdr().row();
        final int width = header_fields.field().size();
        final int height = tree.row().size();
        final TableInput rv = new TableInput(width, height);

        for (int i = 0; i < width; i += 1) {
            rv.col_label[i] = parseField(header_fields.field(i));
        }
        for (int i = 0; i < height; i += 1) {
            final CommaSeparatedValuesParser.RowContext rc = tree.row(i);
            for (int j = 0; j < width; j += 1) {
                rv.data[i][j] = parseField(rc.field(j));
            }
        }

        return rv;
    }
}
