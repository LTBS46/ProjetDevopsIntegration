package fr.project.lib;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.function.BiFunction;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import fr.project.lib.CSVBaseListener;
import fr.project.lib.CSVLexer;
import fr.project.lib.CSVParser;
import fr.project.lib.CSVParser.CsvFileContext;
import fr.project.lib.CSVParser.HdrContext;

import java.util.List;

import javax.xml.crypto.Data;

import java.io.InputStream;
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

    DataFrame(String filename) throws java.io.FileNotFoundException ,java.io.IOException{
        this(new FileInputStream(filename));
    }

    DataFrame(InputStream is) throws java.io.IOException{
        CharStream ais = CharStreams.fromStream(is);
        CSVLexer lexer = new CSVLexer(ais);
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        CSVParser parser = new CSVParser(tokens);
        CsvFileContext tree = parser.csvFile();
        List<String> header = tree.hdr().row().field().stream().map(RuleContext::getText).toList();
        List<List<String>> content = tree.row().stream().map(
            (row) -> row.field().stream().map(RuleContext::getText).toList()
        ).toList();
        int width = header.size();
        int height = content.size();
        init(width, height, InitMode.PutDefault);
        for(int i = 0; i < height; i+=1) {
            col_label[i] = header.get(i);
            List<String> ct = content.get(i);
            for(int j = 0; j < width; j+=1) {
                data[i][j] = ct.get(j);
            }    
        }
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
}
