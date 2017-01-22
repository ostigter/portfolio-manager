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
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ozsoft.portfoliomanager.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.datatable.DataTable;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.services.AnalyzeService;
import org.ozsoft.portfoliomanager.services.UpdateService;
import org.ozsoft.portfoliomanager.ui.table.BenchTable;
import org.ozsoft.portfoliomanager.ui.table.GoalTable;
import org.ozsoft.portfoliomanager.ui.table.StockTable;
import org.ozsoft.portfoliomanager.ui.table.WatchTable;

/**
 * The application's main window.
 *
 * @author Oscar Stigter
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 410441330732167672L;

    private static final String TITLE = "Portfolio Manager";

    private static final int DEFAULT_WIDTH = 950;

    private static final int DEFAULT_HEIGHT = 600;

    private static final Logger LOGGER = LogManager.getLogger(MainFrame.class);

    private final Configuration config;

    private final UpdateService updateService = new UpdateService();

    private final AnalyzeService analyzeService = new AnalyzeService();

    private JTabbedPane tabbedPane;

    private OwnedPanel ownedPanel;

    private DataTable goalTable;

    private DataTable watchTable;

    private DataTable benchTable;

    private DataTable allTable;

    private JLabel statusLabel;

    private EditStockDialog addStockDialog;

    private MessageDialog messageDialog;

    /**
     * Constructor.
     */
    public MainFrame() {
        config = Configuration.getInstance();

        initUI();

        updateTables();

        setVisible(true);
    }

    /**
     * Initializes the UI.
     */
    private void initUI() {
        setTitle(TITLE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        getContentPane().setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setBorder(UIConstants.SPACER_BORDER);
        toolBar.setFloatable(false);

        JButton button = new JButton("Update All");
        button.setToolTipText("Update all stock data");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAllStockData();
            }
        });
        toolBar.add(button);

        button = new JButton("Analyze All");
        button.setToolTipText("Analyze all stocks");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeAllStocks();
            }
        });
        toolBar.add(button);

        button = new JButton("Add Stock");
        button.setToolTipText("Add a new stock to the watch list");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStock();
            }
        });
        toolBar.add(button);

        getContentPane().add(toolBar, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(UIConstants.SPACER_BORDER);

        ownedPanel = new OwnedPanel(this);
        tabbedPane.add("Owned", ownedPanel);

        goalTable = new GoalTable(this);
        tabbedPane.add("Goal", new JScrollPane(goalTable));

        watchTable = new WatchTable(this);
        tabbedPane.add("Watch", new JScrollPane(watchTable));

        benchTable = new BenchTable(this);
        tabbedPane.add("Bench", new JScrollPane(benchTable));

        allTable = new StockTable(this);
        tabbedPane.add("All", new JScrollPane(allTable));

        tabbedPane.setToolTipTextAt(0, "Stocks currently or once owned");
        tabbedPane.setToolTipTextAt(1, "Favorite stocks to be owned sooner or later");
        tabbedPane.setToolTipTextAt(2, "Watch list of potential stocks to own");
        tabbedPane.setToolTipTextAt(3, "Stocks currently on the bench (disqualified)");
        tabbedPane.setToolTipTextAt(4, "All stocks being tracked");

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        statusLabel = new JLabel();
        statusLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        getContentPane().add(statusLabel, BorderLayout.SOUTH);

        addStockDialog = new EditStockDialog(this);

        messageDialog = new MessageDialog(this);

        int noOfStocks = config.getStocks().size();
        if (noOfStocks > 0) {
            setStatus(String.format("Tracking %d stocks.", noOfStocks));
        } else {
            setStatus("No stocks defined.");
        }

        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Updates all stock tables (UI refresh).
     */
    public void updateTables() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ownedPanel.update();
                goalTable.update();
                watchTable.update();
                benchTable.update();
                allTable.update();
            }
        });
    }

    /**
     * Updates the Owned panel (UI refresh).
     */
    public void updateOwnedPanel() {
        ownedPanel.update();
    }

    public void showMessageDialog(String message) {
        messageDialog.show(message);
    }

    public void closeMessageDialog() {
        messageDialog.close();
    }

    public void setStatus(String status) {
        if (status == null || status.isEmpty()) {
            statusLabel.setText(" ");
        } else {
            statusLabel.setText(status);
        }
    }

    /**
     * Updates all stock data.
     *
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    private void updateAllStockData() {
        setStatus("Updating stock data...");
        showMessageDialog("Updating all stock data, please wait...");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int updatedStocks = updateService.updateAllStockData();
                updateTables();
                closeMessageDialog();
                setStatus(String.format("%d stocks updated.", updatedStocks));
            }
        });
    }

    /**
     * Analyzes all stocks.
     */
    private void analyzeAllStocks() {
        if (JOptionPane.showConfirmDialog(this, "Analyze all stocks now? (This may take several minutes.)", "Analyze All Stocks",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            setStatus("Analyzing stocks...");
            showMessageDialog("Analyzing all stocks, please wait...");
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String resultMessage = analyzeService.analyzeAllStocks();
                    closeMessageDialog();
                    setStatus(resultMessage);
                }
            });
        }
    }

    /**
     * Handles a click of the 'Add Stock' button. <br />
     * <br />
     *
     * The new stock will be placed to the 'watch' list.
     */
    private void addStock() {
        if (addStockDialog.show() == Dialog.OK) {
            config.addStock(addStockDialog.getStock());
            updateTables();
            tabbedPane.setSelectedIndex(2); // Jump to 'Watch' tab
        }
    }

    /**
     * Closes the application.
     */
    private void close() {
        Configuration.save();
        LOGGER.info("Application closed");
    }
}
