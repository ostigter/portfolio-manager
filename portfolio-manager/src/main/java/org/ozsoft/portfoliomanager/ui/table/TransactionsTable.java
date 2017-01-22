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
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import org.ozsoft.datatable.Column;
import org.ozsoft.datatable.ColumnRenderer;
import org.ozsoft.datatable.DataTable;
import org.ozsoft.datatable.DefaultColumnRenderer;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.domain.Transaction;
import org.ozsoft.portfoliomanager.domain.TransactionType;
import org.ozsoft.portfoliomanager.ui.Dialog;
import org.ozsoft.portfoliomanager.ui.EditTransactionDialog;
import org.ozsoft.portfoliomanager.ui.MainFrame;
import org.ozsoft.portfoliomanager.ui.table.column.DateColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.MoneyColumnRenderer;
import org.ozsoft.portfoliomanager.ui.table.column.SharesColumnRenderer;

/**
 * Table with the transactions.
 *
 * @author Oscar Stigter
 */
public class TransactionsTable extends DataTable {

    private static final long serialVersionUID = -6959086848794121532L;

    private final Configuration config = Configuration.getInstance();

    protected final MainFrame mainFrame;

    private final EditTransactionDialog editTransactionDialog;

    /**
     * Constructor.
     *
     * @param mainFrame
     *            The application's main window.
     */
    public TransactionsTable(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        editTransactionDialog = new EditTransactionDialog(mainFrame);

        initColumns();

        initContextMenu();

        // Double-click transaction to edit it
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editTransaction();
                }
            }
        });
    }

    /**
     * Initializes the table columns
     */
    protected void initColumns() {
        boolean roundTotals = Configuration.getInstance().isRoundTotals();

        ColumnRenderer idColumnRenderer = new DefaultColumnRenderer(SwingConstants.RIGHT);
        ColumnRenderer centerColumnRenderer = new DefaultColumnRenderer(SwingConstants.CENTER);
        ColumnRenderer dateColumnRenderer = new DateColumnRenderer();
        ColumnRenderer sharesColumnRenderer = new SharesColumnRenderer();
        ColumnRenderer priceColumnRenderer = new MoneyColumnRenderer(4);
        ColumnRenderer costColumnRenderer = new MoneyColumnRenderer(2);
        ColumnRenderer totalColumnRenderer = new MoneyColumnRenderer(roundTotals ? 0 : 2);

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("ID", "Transaction ID", idColumnRenderer));
        columns.add(new Column("Date", "Transaction date", dateColumnRenderer));
        columns.add(new Column("Stock name", "Stock name"));
        columns.add(new Column("Symbol", "Stock ticker sybol", centerColumnRenderer));
        columns.add(new Column("Type", "Transaction type", centerColumnRenderer));
        columns.add(new Column("Shares", "Number of shares", sharesColumnRenderer));
        columns.add(new Column("Price", "Price per share", priceColumnRenderer));
        columns.add(new Column("Costs", "Transaction costs, incl. broker fees, valuta costs, etc.", costColumnRenderer));
        columns.add(new Column("Total", "Total transaction value", totalColumnRenderer));
        setColumns(columns);
    }

    /**
     * Initializes the table's context menu.
     */
    protected void initContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("Add transaction...");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction();
            }
        });
        contextMenu.add(menuItem);

        menuItem = new JMenuItem("Edit transaction...");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editTransaction();
            }
        });
        contextMenu.add(menuItem);

        menuItem = new JMenuItem("Delete transaction");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTransaction();
            }
        });
        contextMenu.add(menuItem);

        setComponentPopupMenu(contextMenu);
    }

    @Override
    public final void update() {
        clear();
        for (Transaction t : config.getTransactions()) {
            String symbol = t.getSymbol();
            Stock stock = config.getStock(symbol);
            String stockName = (stock != null) ? stock.getName() : "<ERROR: Stock deleted>";
            TransactionType type = t.getType();
            BigDecimal noOfShares = (type == TransactionType.SELL) ? t.getNoOfShares().multiply(new BigDecimal("-1")) : t.getNoOfShares();
            BigDecimal price = t.getPrice();
            BigDecimal cost = t.getCost();
            BigDecimal total = noOfShares.multiply(price);
            if (type == TransactionType.BUY) {
                total = total.add(cost);
            } else {
                total = total.subtract(cost, MathContext.DECIMAL64);
            }

            addRow(t.getId(), t.getDate(), stockName, symbol, type, noOfShares.intValue(), price, cost, total);
        }
        super.update();
    }

    /**
     * Handles a click of the 'Add Transaction...' button.
     */
    public void addTransaction() {
        if (editTransactionDialog.show() == Dialog.OK) {
            config.addTransaction(editTransactionDialog.getTransaction());
            update();
            mainFrame.updateOwnedPanel();
        }
    }

    /**
     * Handles the 'Edit transaction...' menu item.
     */
    private void editTransaction() {
        Transaction transaction = getSelectedTransaction();
        if (transaction != null) {
            if (editTransactionDialog.show(transaction) == Dialog.OK) {
                update();
                mainFrame.updateOwnedPanel();
            }
        }
    }

    /**
     * Handles the 'Delete transaction' menu item.
     */
    private void deleteTransaction() {
        Transaction transaction = getSelectedTransaction();
        if (transaction != null) {
            if (JOptionPane.showConfirmDialog(null, "Permanently delete transaction?", "Warning", JOptionPane.WARNING_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                config.deleteTransaction(transaction);
                update();
                mainFrame.updateOwnedPanel();
            }
        }
    }

    /**
     * Returns the currently selected transaction.
     *
     * @return The selected transaction, or {@code null} if no transaction is selected.
     */
    private Transaction getSelectedTransaction() {
        Transaction transaction = null;

        int rowIndex = getSelectedRow();
        if (rowIndex >= 0) {
            int id = (int) getCellValue(rowIndex, 0);
            for (Transaction tx : config.getTransactions()) {
                if (tx.getId() == id) {
                    transaction = tx;
                    break;
                }
            }
        }

        return transaction;
    }
}
