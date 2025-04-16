package fr.project.demo;
import fr.project.lib.DataFrame;


public class Demo {
     public static void main(String[] args) throws Exception {
        DataFrame df = new DataFrame("demo.csv");
        System.out.println("Shape: " + df.getShape()[0] + " rows, " + df.getShape()[1] + " cols");
        System.out.println("First 5 rows:");
        for (int i = 0; i < 4; i++) {
            System.out.println(df.get(i)); // ou df.get(i, ...) si df.get(i) n'existe pas
        }

        System.out.println("Column subset demo:");
        DataFrame subset =(DataFrame) df.get(0, 1); // si get(int...) existe
        System.out.println(subset.toString());
    }
}
