package fr.project.lib;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.xml.crypto.Data;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataFrame<T extends Object> implements IDataFrame<T> {
    T[][] data;
    String[] col_label;
    String[] li_label;

    public class Position {
        final String col, line;
        final int x, y;

        Position(int _x, int _y) {
            col = col_label[_x];
            line = li_label[_y];
            x = _x;
            y = _y;
        }
    }

    DataFrame(String filename) throws java.io.FileNotFoundException {
        this(new FileInputStream(filename), ',', (a,b)->{return(T) a;});
    }

    DataFrame(InputStream is, char _delim, BiFunction<String, Position, T> f) {
        String delim = new String(new char[] {_delim});
        Scanner scanner = new Scanner(is);
        String header = scanner.nextLine();
        ArrayList<String> lines = new ArrayList();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        scanner.close();
        String[] headers = header.split(delim);
        int height = lines.size();
        int width = headers.length;
        init(width, height, InitMode.PutDefault);
        col_label = headers;
        for(int i = 0; i < height; i+=1) {
            String []datas = lines.get(i).split(delim);
            for(int j = 0; j < width; j+=1) {
                data[i][j] = f.apply(datas[j], new Position(i, j));
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

    @Override
    public boolean getEmpty() { 
        return data == null || data.length==0 || data[0].length==0;
    }

    @Override
    public int getSize() {
        if(this.getEmpty()){
            return 0;
        }
        return data.length * data[0].length;
    }
    @Override
    public int[] getShape() {
        if(this.getEmpty()){
            return new int []{0,0};
        }
        return new int [] {data.length ,data[0].length};
    }
    @Override
    public List<T> pop(String s) {
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
    
        List<T> colonne = new ArrayList<>();
        int numRows = data.length;
        int numCols = data[0].length;
    
    
        for (int i = 0; i < numRows; i++) {
            colonne.add(data[i][temp]);
        }
        @SuppressWarnings("unchecked")
        T[][] newData = (T[][]) new Object[numRows][numCols - 1];
    
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
}

