package fr.project.lib;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.function.BiFunction;

import javax.xml.crypto.Data;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataFrame<T extends Object> {
    T[][] data;
    String[] col_label;
    String[] li_label;

    public class Position {
        final String col, line;
        final int x, y;

        Position(String _col, String _line, int _x, int _y) {
            col = _col;
            line = _line;
            x = _x;
            y = _y;
        }
    }

    DataFrame(String filename) {
        this(new FileInputStream(filename), ',', (a,b)->{return(T) a;});
    }

    DataFrame(InputStream is, char delim, BiFunction<String, Position, T> f) {
        Scanner scanner = new Scanner(is);
        String header = scanner.nextLine();
        ArrayList<String> lines = new ArrayList();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        scanner.close();
        String[] headers = header.split(delim);
        height = lines.size();
        width = headers.length;
        init(width, height, InitMode.PutDefault);
        col_label = headers;
        for(int i = 0; i < height; i+=1) {
            String datas = lines.get(i).split(delim);
            for(int j = 0; j < width; j+=1) {
                data[i][j] = f.apply(datas.get(j), new Position());
            }    
        }
    }

    DataFrame(int width, int height) {
        init(width, height, InitMode.PutDefault);
    }

    private enum InitMode { PutBlank, PutDefault }

    private void init(int width, int height, InitMode im) {
        data =(T[][]) new Object[height][width];
        col_label = new String[width];
        li_label = new String[height];
        if(im == null) {} else
        if(im == InitMode.PutBlank) {
            for(int i = 0; i < height; i +=1)
                li_label[i] = "";
            for(int j = 0; j < width; j +=1)
                col_label[j] = "";
        }else if(im == InitMode.PutDefault) {
            for(int i = 0; i < height; i +=1)
                li_label[i] = "" + i;
            for(int j = 0; j < width; j +=1)
                col_label[j] = "" + j;
        } else {
            throw new RuntimeException("");
        }
    }
}
