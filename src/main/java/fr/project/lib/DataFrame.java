package fr.project.lib;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import fr.project.lib.CSVBaseListener;
import fr.project.lib.CSVLexer;
import fr.project.lib.CSVParser;
import fr.project.lib.CSVParser.CsvFileContext;
import fr.project.lib.CSVParser.HdrContext;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.xml.crypto.Data;

import java.io.InputStream;
import java.sql.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataFrame implements IDataFrame {
    Object[][] data;
    String[] col_label;
    String[] li_label;

    private static final Map<Class<?>, Function<String, Object>> parsers;

    private static final List<Function<String, Class<?>>> type_find;

    static {
        parsers = new HashMap();
        type_find = new ArrayList<>();

        parsers.put(String.class, (s)->s);
        parsers.put(Integer.class, (s)->Integer.parseInt(s));
        parsers.put(Float.class, (s)->Float.parseFloat(s));

        type_find.add((a)->{
            try {
                Integer.parseInt(a);
                return Integer.class;
            } catch(NumberFormatException ne){
                return null;
            }
        });

        type_find.add((a)->{
            try {
                Float.parseFloat(a);
                return Float.class;
            } catch(NumberFormatException ne){
                return null;
            }
        });
    }

    DataFrame(String filename) throws java.io.FileNotFoundException ,java.io.IOException{
        this(new FileInputStream(filename));
    }

    private static String parseCSVField(CSVParser.FieldContext fc) {
        if(fc.STRING() == null) {
            return fc.getText();
        } else if (fc.TEXT() == null) {
            String val = fc.getText();
            String chopped = val.substring(1, val.length() - 1);
            String []content = chopped.split("\"\"");
            return String.join("\"", Arrays.asList(content));
        } else {
            return "";
        }
    }

    DataFrame(InputStream is) throws java.io.IOException{
        CharStream ais = CharStreams.fromStream(is);
        CSVLexer lexer = new CSVLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        CSVParser parser = new CSVParser(tokens);
        CsvFileContext tree = parser.csvFile();
        List<String> header = tree.hdr().row().field().stream().map(DataFrame::parseCSVField).toList();
        List<List<String>> content = tree.row().stream().map(
            (row) -> row.field().stream().map(DataFrame::parseCSVField).toList()
        ).toList();
        int width = header.size();
        int height = content.size();
        init(width, height, InitMode.PutDefault);
        col_label = header.toArray(String[]::new);
        for(int i = 0; i < height; i+=1) {
            List<String> ct = content.get(i);
            for(int j = 0; j < width; j+=1) {
                data[i][j] = ct.get(j);
            }    
        }
        // LINE 1
        Class<?>[] types = new Class[width];
        for(int j = 0; j < width; j+=1) {
            Class<?> tt = null;
            for(Function<String, Class<?>> fsc : type_find) {
                tt = fsc.apply((String) data[0][j]);
                if(tt != null) {
                    break;
                }
            }
            
            types[j] = tt != null ? tt : String.class;
        }    
    
        // LINE 2-N

        for(int i = 0; i < width; i++) {
            Class<?> c = types[i];
            Function<String, Object> col_parse = parsers.get(c);
            for(int j = 0; j < height; j++) {
                data[j][i] = col_parse.apply((String)data[j][i]);
            }
        }
        System.out.println(Arrays.toString(types));
    }

    DataFrame(int width, int height) {
        init(width, height, InitMode.PutDefault);
    }

    private enum InitMode { PutBlank, PutDefault }

    private void init(int width, int height, InitMode im) {
        data = new Object[height][width];
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
        @SuppressWarnings("unchecked")
        Object[][] newData =  new Object[numRows][numCols - 1];
    
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
        StringBuilder sb = new StringBuilder();
        sb.append("\t");
        for(String hd : col_label) {
            sb.append("\t");
            sb.append(hd);
        }
        sb.append("\n");
        for(int i = 0; i < data.length; i++) {
            sb.append(li_label[i]);
            for (int j = 0; j < data[i].length; j+= 1) {
                sb.append("\t");
                sb.append(data[i][j].toString());
            }
            sb.append("\n");
        }
        return sb.toString();

      }
}

