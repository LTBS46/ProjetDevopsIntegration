package fr.project.lib;

import static java.util.Arrays.asList;
import static org.antlr.v4.runtime.CharStreams.fromStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import fr.project.lib.CSVParser.CsvFileContext;
import fr.project.lib.CSVParser.FieldContext;
import fr.project.lib.CSVParser.RowContext;

public class DataFrame implements IDataFrame {
    Object[][] data;
    String[] col_label;
    String[] li_label;
    Class<?>[] col_types;

    private static final Map<Class<?>, Function<String, Object>> parsers;

    private static final List<Function<String, Class<?>>> type_find;

    static {
        parsers = new HashMap<>();
        type_find = new ArrayList<>();

        parsers.put(String.class, (s) -> s);
        parsers.put(Integer.class, Integer::parseInt);
        parsers.put(Float.class, Float::parseFloat);

        type_find.add(Utility::try_parse_int);
        type_find.add(Utility::try_parse_float);
    }

    DataFrame(String filename) throws IOException {
        this(new FileInputStream(filename));
    }

    private static String parseCSVField(FieldContext fc) {
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

    DataFrame(InputStream is) throws IOException {
        CharStream ais = fromStream(is);
        CSVLexer lexer = new CSVLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSVParser parser = new CSVParser(tokens);
        CsvFileContext tree = parser.csvFile();
        List<FieldContext> header_fields = tree.hdr().row().field();
        int width = header_fields.size();
        List<String> header = new ArrayList<>();
        for (var fc : header_fields) {
            header.add(parseCSVField(fc));
        }
        List<RowContext> rows_content = tree.row();

        int height = rows_content.size();
        List<List<String>> content = new ArrayList<>(height);

        for (RowContext rc : rows_content) {
            List<String> line_content = new ArrayList<>(width);
            for (var fc : rc.field()) {
                line_content.add(parseCSVField(fc));
            }
            content.add(line_content);
        }
        
        init(width, height, InitMode.PutDefault);
        col_label = header.toArray(String[]::new);
        for (int i = 0; i < height; i += 1) {
            List<String> ct = content.get(i);
            for (int j = 0; j < width; j += 1) {
                data[i][j] = ct.get(j);
            }
        }
        // LINE 1
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

        // LINE 2-N

        for (int i = 0; i < width; i++) {
            Class<?> c = types[i];
            Function<String, Object> col_parse = parsers.get(c);
            for (int j = 0; j < height; j++) {
                data[j][i] = col_parse.apply((String) data[j][i]);
            }
        }
        col_types = types;
    }

    DataFrame(int width, int height) {
        init(width, height, InitMode.PutDefault);
    }

    private enum InitMode {
        PutBlank, PutDefault
    }

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

    @Override
    public boolean getEmpty() {
        return data == null || data.length == 0 || data[0].length == 0;
    }

    @Override
    public int getSize() {
        if (this.getEmpty()) {
            return 0;
        }
        return data.length * data[0].length;
    }

    @Override
    public int[] getShape() {
        if (this.getEmpty()) {
            return new int[] { 0, 0 };
        }
        return new int[] { data.length, data[0].length };
    }

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

        List<Object> colonne = new ArrayList<>();
        int numRows = data.length;
        int numCols = data[0].length;

        for (int i = 0; i < numRows; i++) {
            colonne.add(data[i][temp]);
        }

        Object[][] newData = new Object[numRows][numCols - 1];

        for (int i = 0; i < numRows; i++) {
            int newColIdx = 0;
            for (int j = 0; j < numCols; j++) {
                if (j != temp) {
                    newData[i][newColIdx++] = data[i][j];
                }
            }
        }

        String[] newColLabels = new String[col_label.length - 1];
        int newIdx = 0;
        for (int i = 0; i < col_label.length; i++) {
            if (i != temp) {
                newColLabels[newIdx++] = col_label[i];
            }
        }

        this.data = newData;
        this.col_label = newColLabels;

        return colonne;
    }

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
