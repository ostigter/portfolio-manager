package org.ozsoft.datatable;

/**
 * Table row. <br />
 * <br />
 * 
 * Always holds the exact same number of cell values as the number of columns. <br />
 * <br />
 * 
 * Empty cells have a <code>null</code> value.
 * 
 * @author Oscar Stigter
 */
public class Row {

    private Object[] cellValues;

    /**
     * Constructor.
     * 
     * @param columnCount
     *            The number of columns.
     */
    /* package */Row(int columnCount) {
        cellValues = new Object[columnCount];
    }

    /**
     * Returns the cell values.
     * 
     * @return The cell values.
     */
    public Object[] getCellValues() {
        return cellValues;
    }

    /**
     * Sets the cell values.
     * 
     * @param cellValues
     *            The cell values.
     */
    /* package */void setCellValues(Object... cellValues) {
        this.cellValues = cellValues;
    }

    /**
     * Returns a cell's value.
     * 
     * @param columnIndex
     *            The column index (0-based).
     * 
     * @return The cell value.
     */
    public Object getCellValue(int columnIndex) {
        if (columnIndex < cellValues.length) {
            return cellValues[columnIndex];
        } else {
            throw new IllegalArgumentException("Invalid columnIndex: " + columnIndex);
        }
    }

    /**
     * Sets a cell's value.
     * 
     * @param columnIndex
     *            The column index (0-based).
     * @param cellValue
     *            The new cell value.
     */
    public void setCellValue(int columnIndex, Object cellValue) {
        if (columnIndex < cellValues.length) {
            cellValues[columnIndex] = cellValue;
        } else {
            throw new IllegalArgumentException("Invalid columnIndex: " + columnIndex);
        }
    }
}
