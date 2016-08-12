package org.ozsoft.datatable;

/**
 * Table column definition.
 * 
 * @author Oscar Stigter
 */
public class Column {

    private final String name;

    private final String tooltip;

    private final ColumnRenderer renderer;

    /**
     * Constructor with a {@link DefaultColumnRenderer}.
     * 
     * @param name
     *            Column name.
     * @param tooltip
     *            Tooltip shown when hovering over the colum name.
     */
    public Column(String name, String tooltip) {
        this(name, tooltip, null);
    }

    /**
     * Constructor with a specific {@link ColumnRenderer}.
     * 
     * @param name
     *            Column name.
     * @param tooltip
     *            Tooltip shown when hovering over the column name.
     * @param renderer
     *            Specific {@link ColumnRenderer}.
     */
    public Column(String name, String tooltip, ColumnRenderer renderer) {
        this.name = name;
        this.tooltip = tooltip;
        this.renderer = renderer;
    }

    /**
     * Returns the column name.
     * 
     * @return The column name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the column tooltip.
     * 
     * @return The column tooltip.
     */
    public String getTooltip() {
        return tooltip;
    }

    /*
     * Returns the {@link ColumnRenderer}.
     * 
     * @return The {@link ColumnRenderer}.
     */
    public ColumnRenderer getRenderer() {
        return renderer;
    }

    /**
     * Returns the footer column.
     * 
     * @return The footer column.
     */
    public Column getFooterColumn() {
        Column footerColumn = new Column(name, tooltip, renderer);
        return footerColumn;
    }
}
