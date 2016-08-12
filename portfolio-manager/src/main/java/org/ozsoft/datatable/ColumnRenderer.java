package org.ozsoft.datatable;

/**
 * Column renderer.
 * 
 * @author Oscar Stigter
 */
public interface ColumnRenderer {

    /**
     * Sets whether this renderer is for the footer row.
     * 
     * @param isFooter
     *            <code>true</code> if for the footer row, otherwise <code>false</code>.
     */
    void setFooter(boolean isFooter);

    /**
     * Sets the decimal precision for numberic cell values.
     * 
     * @param decimalPrecision
     *            The decimal precision.
     */
    void setDecimalPrecision(int decimalPrecision);

    /**
     * Sets the horizontal aligment for the cell text.
     * 
     * @param horizontalAlignment
     *            The horizontal alignment as {@see SwingConstants}.
     */
    void setHorizontalAlignment(int horizontalAlignment);

    /**
     * Returns the formatted <code>String</code> as the rendered cell value.
     * 
     * @param value
     *            The cell value from the table model.
     * 
     * @return The rendered cell value.
     */
    String formatValue(Object value);
}
