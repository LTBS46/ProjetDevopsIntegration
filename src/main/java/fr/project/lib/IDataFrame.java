package fr.project.lib;

import java.util.Iterator;
import java.util.List;

public interface IDataFrame<T extends Object> extends Iterable<String> {
    class NotImplementedYet extends RuntimeException {}

    default boolean getEmpty() { 
        throw new NotImplementedYet();
     }

    @Override
    default Iterator<String> iterator() {
        throw new NotImplementedYet();
    }

    default int getSize() {
        throw new NotImplementedYet();   
    }

    default int[] getShape() {
        throw new NotImplementedYet();   
    }

    default List<T> pop(String s){
        throw new NotImplementedYet();   
    }

    default IDataFrame<T> get(String ... cols){
        throw new NotImplementedYet();   
    }
    
    default int getNDim() {
        throw new NotImplementedYet();
    }
}
