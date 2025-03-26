package fr.project.lib;

import java.util.function.BiFunction;
import java.io.InputStream;

public class DataFrame<T> {
    T[][] data;
    String[] col_label;
    String[] li_label;

    DataFrame(InputStream is, char delim, BiFunction<String, String, T> f) {

    }
    DataFrame(int width, int height) {

    }




    private void Init(int width, int height) {

    }
}
