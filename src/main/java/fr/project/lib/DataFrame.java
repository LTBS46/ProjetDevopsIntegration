package fr.project.lib;

import static java.lang.System.arraycopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import  java.time.LocalDate;

import fr.project.lib.utility.TableInput;

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
        parsers.put(LocalDate.class, arg0 -> LocalDate.parse(arg0.replace('/', '-')));


        type_find.add(Utility::try_parse_int);
        type_find.add(Utility::try_parse_float);
        type_find.add(Utility::try_parse_date);
    }

    DataFrame(String filename) throws IOException {
        this(new FileInputStream(filename));
    }

    DataFrame(InputStream is) throws IOException {
        this(is, InputFormat.CommaSeparatedValues);
    }

    public enum InputFormat {
        CommaSeparatedValues, TabSeparatedValues
    }

    DataFrame(InputStream is, InputFormat _if) throws IOException {
        this(switch (_if) {
            case CommaSeparatedValues -> TableInput.parseCommaSeparatedValues(is);
            case TabSeparatedValues -> TableInput.parseTabSeparatedValues(is);
        });
    }
    
    DataFrame(TableInput ti) {
        int width = ti.w;
        int height = ti.h;
        init(width, height, InitMode.PutDefault);
        col_label = new String[width];
        ti.fill();
        arraycopy(ti.col_label, 0, col_label, 0, width);

        for (int i = 0; i < height; i += 1) {
            String[] act = ti.data[i];
            for (int j = 0; j < width; j += 1) {
                data[i][j] = act[j];
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

    public Iterator<String> iterator() {
        return Arrays.asList(col_label).iterator();// Arrays.asList(col_label).iterator();
    }
}
