// This file is part of the 'portfolio-manager' (Portfolio Manager)
// project, an open source stock portfolio manager application
// written in Java.
//
// Copyright 2015 Oscar Stigter
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.portfoliomanager.ui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.ozsoft.datatable.Column;
import org.ozsoft.datatable.ColumnRenderer;
import org.ozsoft.datatable.DataTable;
import org.ozsoft.datatable.DefaultColumnRenderer;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Portfolio;
import org.ozsoft.portfoliomanager.domain.Position;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.ui.Dialog;
import org.ozsoft.portfoliomanager.ui.EditStockDialog;
import org.ozsoft.portfoliomanager.ui.MainFrame;
import org.ozsoft.portfoliomanager.ui.StockPriceFrame;
import org.ozsoft.portfoliomanager.ui.table.column.CRColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.DGColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.MoneyColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.PercChangeColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.PercentageColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.ResultColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.YDGColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.YieldColumnRenderer;
import org.ozsoft.portfoliomanager.util.MathUtils;

/**
 * Table with the stocks that are currently owned or have been at some point in time, i.e. all open and closed positions. <br />
 * <br />
 *
 * Upon each refresh, the positions and portfolio statistics are generated based on the transactions.
 *
 * @author Oscar Stigter
 */
public class OwnedTable extends DataTable {

    private static final long serialVersionUID = -7051733783546691662L;

    private final Configuration config = Configuration.getInstance();

    protected final MainFrame mainFrame;

    protected final EditStockDialog editStockDialog;

    /**
     * Constructor.
     *
     * @param mainFrame
     *            The application's main window.
     */
    public OwnedTable(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        initColumns();

        initContextMenu();

        // Double-click stock to edit it
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewStockPrice();
                }
            }
        });

        editStockDialog = new EditStockDialog(mainFrame);
    }

    /**
     * Initializes the table columns.
     */
    protected void initColumns() {
        boolean roundTotals = Configuration.getInstance().isRoundTotals();

        ColumnRenderer numberColumnRenderer = new DefaultColumnRenderer(SwingConstants.RIGHT);
        ColumnRenderer centeredColumnRenderer = new DefaultColumnRenderer(SwingConstants.CENTER);
        ColumnRenderer percChangeColumnRenderer = new PercChangeColumnRenderer();
        ColumnRenderer yieldColumnRenderer = new YieldColumnRenderer();
        ColumnRenderer divGrowthColumnRenderer = new DGColumnRenderer();
        ColumnRenderer yearsColumnRenderer = new YDGColumnRenderer();
        ColumnRenderer ratingColumnRenderer = new CRColumnRenderer();
        ColumnRenderer smallMoneyColumnRenderer = new MoneyColumnRenderer(2);
        ColumnRenderer totalMoneyColumnRenderer = new MoneyColumnRenderer(roundTotals ? 0 : 2);
        ColumnRenderer resultColumnRenderer = new ResultColumnRenderer(roundTotals ? 0 : 2);
        ColumnRenderer percColumnRenderer = new PercentageColumnRenderer();

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("Stock name", "Stock name"));
        columns.add(new Column("Symbol", "Ticker symbol", centeredColumnRenderer));
        columns.add(new Column("Price", "Current stock price", smallMoneyColumnRenderer));
        columns.add(new Column("Change", "Change in stock price since last closing", percChangeColumnRenderer));
        columns.add(new Column("Yield", "Current dividend yield", yieldColumnRenderer));
        columns.add(new Column("DGR", "5-year annualized dividend growth rate", divGrowthColumnRenderer));
        columns.add(new Column("YDG", "Consecutive years of dividend growth", yearsColumnRenderer));
        columns.add(new Column("CR", "Current credit rating", ratingColumnRenderer));
        columns.add(new Column("Shares", "Current number of shares owned", numberColumnRenderer));
        columns.add(new Column("Cost", "Current cost basis", totalMoneyColumnRenderer));
        columns.add(new Column("CPS", "Current cost basis per share", smallMoneyColumnRenderer));
        columns.add(new Column("Value", "Current market value", totalMoneyColumnRenderer));
        columns.add(new Column("Weight", "Relative weight in portfolio based on cost", percColumnRenderer));
        columns.add(new Column("Result", "Current result (value minus cost)", resultColumnRenderer));
        columns.add(new Column("Result %", "Current result as percentage of cost", percChangeColumnRenderer));
        columns.add(new Column("AI", "Annual income based on current yield", totalMoneyColumnRenderer));
        columns.add(new Column("YOC", "Annual yield on cost", percColumnRenderer));
        columns.add(new Column("TI", "Overall total income received", resultColumnRenderer));
        columns.add(new Column("RR", "Total realized result from sales", resultColumnRenderer));
        columns.add(new Column("TR", "Total return (result plus income)", resultColumnRenderer));
        columns.add(new Column("Notes", "Notes about this stock"));

        setColumns(columns);
    }

    /**
     * Initializes the table's context menu.
     */
    protected void initContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("View stock price...");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStockPrice();
            }
        });
        contextMenu.add(menuItem);

        contextMenu.addSeparator();

        menuItem = new JMenuItem("Edit stock...");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editStock();
            }
        });
        contextMenu.add(menuItem);

        setComponentPopupMenu(contextMenu);
    }

    @Override
    public final void update() {
        clear();

        boolean showClosedPositions = config.getShowClosedPositions();

        // Populate table with portfolio positions (owned stocks).
        Portfolio portfolio = config.getPortfolio();
        BigDecimal currentPortfolioCost = portfolio.getCurrentCost();
        for (Position p : portfolio.getPositions()) {
            if (p.getNoOfShares().intValue() > 0 || showClosedPositions) {
                Stock s = p.getStock();
                BigDecimal weight = MathUtils.perc(p.getCurrentCost(), currentPortfolioCost);
                addRow(s.getName(), s.getSymbol(), s.getPrice(), s.getChangePerc(), s.getYield(), s.getDivGrowth().doubleValue(),
                        s.getYearsDivGrowth(), s.getCreditRating(), p.getNoOfShares().intValue(), p.getCurrentCost(), p.getCostPerShare(),
                        p.getCurrentValue(), weight, p.getCurrentResult(), p.getCurrentResultPercentage(), p.getAnnualIncome(), p.getYieldOnCost(),
                        p.getTotalIncome(), p.getRealizedResult(), p.getTotalReturn(), "  " + s.getComment());
            }
        }

        // Populate footer row with totals.
        setFooterRow(null, null, null, null, null, null, null, null, null, currentPortfolioCost, null, portfolio.getCurrentValue(), null,
                portfolio.getCurrentResult(), portfolio.getCurrentResultPercentage(), portfolio.getAnnualIncome(), portfolio.getYieldOnCost(),
                portfolio.getTotalIncome(), portfolio.getRealizedResult(), portfolio.getTotalReturn(), null);

        super.update();

        // Force table's footer row to repaint (appearently needed because of a Swing bug)
        repaint();
        revalidate();
    }

    /**
     * Handles the 'Show stock price...' menu item.
     */
    private void viewStockPrice() {
        Stock stock = getSelectedStock();
        if (stock != null) {
            StockPriceFrame.show(stock);
        }
    }

    /**
     * Handles the 'Edit stock...' menu item.
     */
    private void editStock() {
        Stock stock = getSelectedStock();
        if (stock != null) {
            if (editStockDialog.show(stock) == Dialog.OK) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                });
            }
        }
    }

    /**
     * Returns the currently selected stock.
     *
     * @return The selected stock, or {@code null} if no stock is selected.
     */
    private Stock getSelectedStock() {
        Stock stock = null;

        int rowIndex = getSelectedRow();
        if (rowIndex >= 0) {
            String symbol = (String) getCellValue(rowIndex, 1);
            stock = config.getStock(symbol);
        }

        return stock;
    }
}
