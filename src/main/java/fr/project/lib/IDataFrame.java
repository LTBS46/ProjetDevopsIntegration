package fr.project.lib;

import java.util.Iterator;
import java.util.List;

/**
 * Interface defining a tabular data structure with labeled columns and rows,
 * providing functionality similar to pandas DataFrame in Python.
 * 
 * <p>The DataFrame stores data in a 2D grid with the following characteristics:
 * <ul>
 *   <li>Columns have names and inferred/declared types</li>
 *   <li>Rows can optionally have labels</li>
 *   <li>Supports heterogeneous data (mixed types across columns)</li>
 *   <li>Provides flexible data access and statistical operations</li>
 * </ul>
 */
public interface IDataFrame extends Iterable<String> {
    
  
    /**
     * Checks if the DataFrame contains no data.
     * @return true if the DataFrame has no rows or columns, false otherwise
     * @throws IllegalStateException if the operation cannot be completed
     */
    boolean getEmpty();

    /**
     * Returns an iterator over the column labels of the DataFrame.
     * @return Iterator of column names in their natural order
     * @throws IllegalStateException if the DataFrame is empty
     */
    @Override
    Iterator<String> iterator();

    /**
     * Gets the total number of data cells in the DataFrame.
     * @return Calculated as rows × columns
     * @throws IllegalStateException if the DataFrame is empty
     */
    int getSize();

    /**
     * Gets the dimensions of the DataFrame.
     * @return 2-element array in format [rowCount, columnCount]
     * @throws IllegalStateException if the DataFrame is empty
     */
    int[] getShape();

    /**
     * Gets the number of dimensions of the DataFrame.
     * @return Always returns 2 (rows and columns) for 2D structure
     */
    int getNDim();

    /**
     * Removes and returns a column from the DataFrame.
     * @param columnName Name of the column to remove
     * @return List containing all values from the removed column in row order
     * @throws IllegalStateException if the DataFrame is empty
     * @throws IllegalArgumentException if the column doesn't exist
     */
    List<Object> pop(String columnName);

    /**
     * Gets a subset DataFrame containing only the specified columns.
     * @param columnNames Names of columns to include in the subset
     * @return New DataFrame containing only the specified columns
     * @throws IllegalStateException if the DataFrame is empty
     * @throws IllegalArgumentException if any column name doesn't exist
     * @throws NullPointerException if columnNames is null
     */
    IDataFrame get(String... columnNames);

    /**
     * Gets a subset DataFrame containing only the specified columns by index.
     * @param columnIndices 0-based indices of columns to include
     * @return New DataFrame containing only the specified columns
     * @throws IllegalStateException if the DataFrame is empty
     * @throws IndexOutOfBoundsException if any index is invalid
     * @throws NullPointerException if columnIndices is null
     */
    IDataFrame get(int... columnIndices);

    /**
     * Flexible element accessor supporting multiple retrieval patterns.
     * 
     * <p><b>Access Patterns:</b>
     * <ul>
     *   <li><b>Single Element:</b> {@code getElem(rowIndex, columnName)}</li>
     *   <li><b>Full Row:</b> {@code getElem(rowIndex, null)} → returns Object[]</li>
     *   <li><b>Full Column:</b> {@code getElem(null, columnName)} → returns Object[]</li>
     * </ul>
     * 
     * @param rowSpec Can be:
     *                <ul>
     *                  <li>Integer (0-based row index)</li>
     *                  <li>String (row label if available)</li>
     *                  <li>null (all rows)</li>
     *                </ul>
     * @param columnSpec Can be:
     *                   <ul>
     *                     <li>Integer (0-based column index)</li>
     *                     <li>String (column name)</li>
     *                     <li>null (all columns)</li>
     *                   </ul>
     * @return Requested data in appropriate format
     * @throws IllegalStateException if DataFrame is empty
     * @throws IllegalArgumentException if:
     *         <ul>
     *           <li>Both specifiers are null</li>
     *           <li>Invalid specifier types</li>
     *           <li>Label not found</li>
     *         </ul>
     * @throws IndexOutOfBoundsException if numeric indices are invalid
     */
    Object getElem(Object rowSpec, Object columnSpec);

    /**
     * Calculates the arithmetic mean of a numeric column.
     * @param columnName Name of the column to calculate
     * @return Mean value as float
     * @throws IllegalStateException if DataFrame is empty
     * @throws IllegalArgumentException if:
     *         <ul>
     *           <li>Column doesn't exist</li>
     *           <li>Column is not numeric (Integer/Float)</li>
     *           <li>Column contains only null values</li>
     *         </ul>
     */
    float Mean(String columnName);

    /**
     * Finds the maximum value in a numeric column.
     * @param columnName Name of the column to analyze
     * @return Maximum value as float
     * @throws IllegalStateException if DataFrame is empty
     * @throws IllegalArgumentException if:
     *         <ul>
     *           <li>Column doesn't exist</li>
     *           <li>Column is not numeric (Integer/Float)</li>
     *           <li>Column contains only null values</li>
     *         </ul>
     */
    float Max(String columnName);

    /**
     * Finds the minimum value in a numeric column.
     * @param columnName Name of the column to analyze
     * @return Minimum value as float
     * @throws IllegalStateException if DataFrame is empty
     * @throws IllegalArgumentException if:
     *         <ul>
     *           <li>Column doesn't exist</li>
     *           <li>Column is not numeric (Integer/Float)</li>
     *           <li>Column contains only null values</li>
     *         </ul>
     */
    float Min(String columnName);

    /**
     * Generates CSV representation of the DataFrame.
     * @return String containing CSV data with:
     *         <ul>
     *           <li>First line: column headers</li>
     *           <li>Subsequent lines: data rows</li>
     *           <li>Proper escaping of special characters</li>
     *         </ul>
     * @throws IllegalStateException if DataFrame is empty
     */
    String toCSV();
}