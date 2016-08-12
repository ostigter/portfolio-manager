package org.ozsoft.datatable;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Wrapper around the Swing {@link javax.swing.JTable}, extending it with several features like:
 * <ul>
 * <li>automatic column width resizing</li>
 * <li>footer row</li>
 * <li>column tooltips</li>
 * <li>API for custom column rendering</li>
 * <li>API for adding, modifying and retrieving row and cell values</li>
 * <li>one single API, avoiding separate table model, selection model and cell renderer APIs</li>
 * </ul>
 * 
 * @author Oscar
 */
public class DataTable extends JPanel {

    private static final long serialVersionUID = 3822671047219990819L;

    private final Table mainTable;

    private final Table footerTable;

    /**
     * Constructor.
     */
    public DataTable() {
        mainTable = new Table(false);
        footerTable = new Table(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(mainTable.getTableHeader());
        panel.add(mainTable);
        panel.add(footerTable);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(panel, gbc);
    }

    /**
     * Sets the column definitions.
     * 
     * @param columns
     *            The column definitions.
     */
    public void setColumns(List<Column> columns) {
        mainTable.setColumns(columns);

        List<Column> footerColumns = new ArrayList<Column>();
        for (Column column : columns) {
            footerColumns.add(column.getFooterColumn());
        }
        footerTable.setColumns(footerColumns);
        footerTable.setColumnModel(mainTable.getColumnModel());
    }

    /**
     * Returns the number of columns.
     * 
     * @return The number of columns.
     */
    public int getColumnCount() {
        return mainTable.getColumnCount();
    }

    /**
     * Returns the columns.
     * 
     * @return The columns.
     */
    public Column[] getColumns() {
        return mainTable.getColumns();
    }

    /**
     * Returns the number of rows.
     * 
     * @return The number of rows.
     */
    public int getRowCount() {
        return mainTable.getRowCount();
    }

    /**
     * Returns the rows.
     * 
     * @return The rows.
     */
    public Row[] getRows() {
        return mainTable.getRows();
    }

    /**
     * Returns a cell's value.
     * 
     * @param rowIndex
     *            The row index (0-based).
     * @param columnIndex
     *            The column index (0-based).
     * 
     * @return The cell value.
     */
    public Object getCellValue(int rowIndex, int columnIndex) {
        return mainTable.getCellValue(rowIndex, columnIndex);
    }

    /**
     * Sets a cell's value.
     * 
     * @param rowIndex
     *            The row index (0-based).
     * @param columnIndex
     *            The column index (0-based).
     * @param value
     *            The new cell value.
     */
    public void setCellValue(int rowIndex, int columnIndex, Object value) {
        mainTable.setCellValue(rowIndex, columnIndex, value);
    }

    /**
     * Returns the currently selected row index.
     * 
     * @return The selected row index (0-based).
     */
    public int getSelectedRow() {
        return mainTable.getSelectedRow();
    }

    /**
     * Adds a row with cell values. <br />
     * <br />
     * 
     * <b>NOTE:</b> The number of cell values must be equal to the number of columns! <br />
     * <br />
     * 
     * A <code>null</code> can be used for empty cells.
     * 
     * @param cellValues
     *            The cell values.
     */
    public void addRow(Object... cellValues) {
        mainTable.addRow(cellValues);
    }

    /**
     * Sets the cell values for the footer row. <br />
     * <br />
     * 
     * <b>NOTE:</b> The number of cell values must be equal to the number of columns! <br />
     * <br />
     * 
     * A <code>null</code> can be used for empty cells.
     * 
     * @param cellValues
     *            The cell values.
     */
    public void setFooterRow(Object... cellValues) {
        footerTable.clear();
        footerTable.addRow(cellValues);
    }

    /**
     * Updates the table, refreshing the UI. <br />
     * <br />
     * 
     * To be called when the underlying model has been changed.
     */
    public void update() {
        mainTable.update();
        footerTable.update();
    }

    /**
     * Clears the table by deleting all rows (including the footer row).
     */
    public void clear() {
        mainTable.clear();
        footerTable.clear();
    }

    @Override
    public void setComponentPopupMenu(JPopupMenu menu) {
        mainTable.setComponentPopupMenu(menu);
    }

    @Override
    public void addMouseListener(MouseListener listener) {
        mainTable.addMouseListener(listener);
    }

    /**
     * Inner table class.
     * 
     * @author Oscar Stigter
     */
    private static class Table extends JTable {

        private static final long serialVersionUID = 5019884184139593450L;

        private final boolean isFooter;

        private DataTableModel model;

        private TableRowSorter<TableModel> sorter;

        /**
         * Constructor.
         * 
         * @param isFooter
         *            <code>true</code> if this table is the footer table, otherwise <code>false</code>.
         */
        public Table(boolean isFooter) {
            this.isFooter = isFooter;
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            if (isFooter) {
                setTableHeader(null);
                setRowSelectionAllowed(false);
                setColumnSelectionAllowed(false);
                setFont(new Font(null, Font.BOLD, 12));
            }
        }

        /**
         * Sets the column definitions.
         * 
         * @param columns
         *            The column definitions.
         */
        public void setColumns(List<Column> columns) {
            model = new DataTableModel(columns);
            super.setModel(model);

            if (!isFooter) {
                sorter = new TableRowSorter<TableModel>(model);
                setRowSorter(sorter);
                List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
                sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                sorter.setSortKeys(sortKeys);
                sorter.sort();
            }
        }

        @Override
        public int getColumnCount() {
            return (model != null) ? model.getColumnCount() : 0;
        }

        /**
         * Returns the column definitios.
         * 
         * @return The column definitions.
         */
        public Column[] getColumns() {
            return (model != null) ? model.getColumns() : null;
        }

        @Override
        public int getRowCount() {
            return (model != null) ? model.getRowCount() : 0;
        }

        /**
         * Returns the rows.
         * 
         * @return The rows.
         */
        public Row[] getRows() {
            return (model != null) ? model.getRows() : null;
        }

        /**
         * Returns a cell's value.
         * 
         * @param rowIndex
         *            The row index (0-based).
         * @param columnIndex
         *            The column index (0-based).
         * 
         * @return The cell value.
         */
        public Object getCellValue(int rowIndex, int columnIndex) {
            if (model == null) {
                throw new IllegalStateException("Model not set");
            }

            int modelIndex = sorter.convertRowIndexToModel(rowIndex);
            return model.getValueAt(modelIndex, columnIndex);
        }

        /**
         * Sets a cell's value.
         * 
         * @param rowIndex
         *            The row index (0-based).
         * @param columnIndex
         *            The column index (0-based).
         * @param value
         *            The new cell value.
         */
        public void setCellValue(int rowIndex, int columnIndex, Object value) {
            if (model == null) {
                throw new IllegalStateException("Model not set");
            }

            if (columnIndex < getColumnCount()) {
                if (rowIndex < getRowCount()) {
                    model.getRows()[rowIndex].setCellValue(columnIndex, value);
                } else {
                    throw new IllegalArgumentException("rowIndex out of bounds: " + rowIndex);
                }
            } else {
                throw new IllegalArgumentException("columnIndex out of bounds: " + columnIndex);
            }
        }

        @Override
        public TableCellRenderer getCellRenderer(int rowIndex, int columnIndex) {
            return model.getCellRenderer(rowIndex, columnIndex);
        }

        /**
         * Adds a row with cell values. <br />
         * <br />
         * 
         * <b>NOTE:</b> The number of cell values must be equal to the number of columns! <br />
         * <br />
         * 
         * A <code>null</code> can be used for empty cells.
         * 
         * @param cellValues
         *            The cell values.
         */
        public void addRow(Object... cellValues) {
            if (model == null) {
                throw new IllegalStateException("Model not set");
            }

            model.addRow(cellValues);
        }

        /**
         * Updates the table, refreshing the UI. <br />
         * <br />
         * 
         * To be called when the underlying model has been changed.
         */
        public void update() {
            if (model != null) {
                model.fireTableDataChanged();
                if (!isFooter) {
                    resizeColumns();
                    sorter.sort();
                }
            }
        }

        /**
         * Clears the table by deleting all rows.
         */
        public void clear() {
            if (model != null) {
                model.clear();
            }
        }

        /**
         * Resizes all columns based on the width of the largest cell value.
         */
        private void resizeColumns() {
            int columnCount = getColumnCount();
            int rowCount = getRowCount();
            TableColumnModel columnModel = getColumnModel();
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                TableColumn column = columnModel.getColumn(columnIndex);
                int preferredWidth = column.getMinWidth();
                int maxWidth = column.getMaxWidth();

                // Header value with
                TableCellRenderer cellRenderer = column.getHeaderRenderer();
                if (cellRenderer == null) {
                    cellRenderer = getTableHeader().getDefaultRenderer();
                }
                Object headerValue = column.getHeaderValue();
                Component comp = cellRenderer.getTableCellRendererComponent(this, headerValue, false, false, 0, columnIndex);
                int width = comp.getPreferredSize().width + getIntercellSpacing().width + 15;
                preferredWidth = Math.max(preferredWidth, width);

                // Row values' width
                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                    cellRenderer = getCellRenderer(rowIndex, columnIndex);
                    comp = prepareRenderer(cellRenderer, rowIndex, columnIndex);
                    width = comp.getPreferredSize().width + getIntercellSpacing().width + 15;
                    preferredWidth = Math.max(preferredWidth, width);
                    if (preferredWidth >= maxWidth) {
                        preferredWidth = maxWidth;
                        break;
                    }
                }

                column.setPreferredWidth(preferredWidth);
            }
        }

        /**
         * Data table model.
         * 
         * @author Oscar Stigter
         */
        private static class DataTableModel extends AbstractTableModel {

            private static final long serialVersionUID = -1718244270107253916L;

            private static final TableCellRenderer DEFAULT_CELL_RENDERER = new DefaultColumnRenderer();

            private final List<Column> columns;

            private final List<Row> rows;

            /**
             * Constructor.
             * 
             * @param columns
             *            The column definitions.
             */
            public DataTableModel(List<Column> columns) {
                if (columns == null || columns.isEmpty()) {
                    throw new IllegalArgumentException("Null or empty columns");
                }

                this.columns = columns;

                rows = new ArrayList<Row>();
            }

            @Override
            public int getColumnCount() {
                return columns.size();
            }

            @Override
            public String getColumnName(int columnIndex) {
                if (columnIndex < getColumnCount()) {
                    return columns.get(columnIndex).getName();
                } else {
                    throw new IllegalArgumentException("columnIndex out of bounds: " + columnIndex);
                }
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0) {
                    Object value = getValueAt(0, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    } else {
                        return Object.class;
                    }
                } else {
                    return Object.class;
                }
            }

            @Override
            public int getRowCount() {
                return rows.size();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex < getColumnCount()) {
                    if (rowIndex < getRowCount()) {
                        return rows.get(rowIndex).getCellValue(columnIndex);
                    } else {
                        throw new IllegalArgumentException("rowIndex out of bounds: " + rowIndex);
                    }
                } else {
                    throw new IllegalArgumentException("columnIndex out of bounds: " + columnIndex);
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            /**
             * Returns a cell's {@link TableCellRenderer}.
             * 
             * @param rowIndex
             *            The row index (0-based).
             * @param columnIndex
             *            The column index (0-based).
             * 
             * @return The cell's {@link TableCellRenderer}.
             */
            public TableCellRenderer getCellRenderer(int rowIndex, int columnIndex) {
                if (columnIndex < getColumnCount()) {
                    TableCellRenderer columnRenderer = (TableCellRenderer) columns.get(columnIndex).getRenderer();
                    return (columnRenderer != null) ? columnRenderer : DEFAULT_CELL_RENDERER;
                } else {
                    throw new IndexOutOfBoundsException("columnIndex out of bounds: " + columnIndex);
                }
            }

            /**
             * Adds a row with cell values. <br />
             * <br />
             * 
             * <b>NOTE:</b> The number of cell values must be equal to the number of columns! <br />
             * <br />
             * 
             * A <code>null</code> can be used for empty cells.
             * 
             * @param cellValues
             *            The cell values.
             */
            public void addRow(Object... cellValues) {
                int columnCount = getColumnCount();
                if (cellValues.length != columnCount) {
                    throw new IllegalArgumentException(String.format("Invalid number of columns (expected: %d, actual: %d)", columnCount,
                            cellValues.length));
                }
                Row row = new Row(columnCount);
                row.setCellValues(cellValues);
                rows.add(row);
            }

            /**
             * Returns the column definitions.
             * 
             * @return The column definitions.
             */
            public Column[] getColumns() {
                return columns.toArray(new Column[0]);
            }

            /**
             * Returns the rows.
             * 
             * @return The rows.
             */
            public Row[] getRows() {
                return rows.toArray(new Row[0]);
            }

            /**
             * Clears the model by deleting all rows.
             */
            public void clear() {
                rows.clear();
            }
        }
    }
}
