package fr.project.lib;

import java.util.Iterator;
import java.util.List;
/**
 * Interface defining the core functionality of a DataFrame structure.
 * Represents a tabular data structure with labeled columns and rows,
 * similar to pandas DataFrame in Python.
 */
public interface IDataFrame extends Iterable<String> {
    
    /**
     * Exception thrown when a method has not been implemented yet.
     * Used for default interface methods that must be overridden.
     */
    class NotImplementedYet extends RuntimeException {}
    
    /**
     * Checks if the DataFrame contains no data.
     * @return true if the DataFrame is empty (no rows or columns), false otherwise
     * @throws NotImplementedYet if not implemented by concrete class
     */
    default boolean getEmpty() { 
        throw new NotImplementedYet();
    }

    /**
     * Returns an iterator over the column labels of the DataFrame.
     * @return Iterator of column names
     * @throws NotImplementedYet if not implemented by concrete class
     */
    @Override
    default Iterator<String> iterator() {
        throw new NotImplementedYet();
    }

    /**
     * Gets the total number of data cells in the DataFrame (rows × columns).
     * @return Total count of data cells
     * @throws NotImplementedYet if not implemented by concrete class
     */
    default int getSize() {
        throw new NotImplementedYet();   
    }

    /**
     * Gets the dimensions of the DataFrame as [rows, columns].
     * @return 2-element array containing row count and column count
     * @throws NotImplementedYet if not implemented by concrete class
     */
    default int[] getShape() {
        throw new NotImplementedYet();   
    }

    /**
     * Removes and returns a column from the DataFrame.
     * @param s Name of the column to remove
     * @return List containing all values from the removed column
     * @throws NotImplementedYet if not implemented by concrete class
     * @throws IllegalArgumentException if column not found or DataFrame is empty
     */
    default List<Object> pop(String s) {
        throw new NotImplementedYet();   
    }

    /**
     * Gets a subset DataFrame containing only the specified columns.
     * @param cols Names of columns to include in the subset
     * @return New DataFrame containing only the specified columns
     * @throws NotImplementedYet if not implemented by concrete class
     * @throws IllegalArgumentException if any column name is not found
     */
    default IDataFrame get(String... cols) {
        throw new NotImplementedYet();   
    }
    
    /**
     * Gets the number of dimensions of the DataFrame (always 2 for 2D structure).
     * @return Number of dimensions (2 for rows and columns)
     * @throws NotImplementedYet if not implemented by concrete class
     */
    default int getNDim() {
        throw new NotImplementedYet();
    }
}