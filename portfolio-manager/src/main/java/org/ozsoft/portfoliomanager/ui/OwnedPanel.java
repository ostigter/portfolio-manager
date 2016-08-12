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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.services.UpdateService;
import org.ozsoft.portfoliomanager.ui.table.OwnedTable;

/**
 * Panel with the portfolio of owned stocks and transactions.
 *
 * @author Oscar Stigter.
 */
public class OwnedPanel extends JPanel {

    private static final long serialVersionUID = -6819380411937781494L;

    private final MainFrame mainFrame;

    private final JCheckBox showClosedPositionsCheck;

    private final JCheckBox deductIncomeTaxCheck;

    private final OwnedTable ownedTable;

    private final TransactionsFrame transactionsFrame;

    private final Configuration config = Configuration.getInstance();

    private final UpdateService updateService = new UpdateService();

    /**
     * Constructor.
     *
     * @param mainFrame
     *            The application's main window.
     */
    public OwnedPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        transactionsFrame = new TransactionsFrame(mainFrame);

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton button = new JButton("Update");
        button.setToolTipText("Update the currently visible stocks in the portfolio");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStockPrices();
            }
        });
        buttonPanel.add(button);

        button = new JButton("Transactions");
        button.setToolTipText("View or edit the portfolio transactions");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transactionsFrame.setVisible(true);
            }
        });
        buttonPanel.add(button);

        button = new JButton("Statistics");
        button.setToolTipText("Shows portfolio statistics like costbase, income and total return over time");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StatisticsFrame(mainFrame);
            }
        });
        buttonPanel.add(button);

        showClosedPositionsCheck = new JCheckBox("Show closed positions");
        showClosedPositionsCheck.setToolTipText("Toggles showing only open positions or also the closed positions");
        showClosedPositionsCheck.setSelected(config.getShowClosedPositions());
        showClosedPositionsCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleShowClosedPositions();
            }
        });
        buttonPanel.add(showClosedPositionsCheck);

        deductIncomeTaxCheck = new JCheckBox("Deduct dividend tax");
        deductIncomeTaxCheck.setToolTipText("Toggles the automatic deduction of the dividend tax (view only)");
        deductIncomeTaxCheck.setSelected(config.isDeductIncomeTax());
        deductIncomeTaxCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDeductIncomeTax();
            }
        });
        buttonPanel.add(deductIncomeTaxCheck);

        add(buttonPanel, BorderLayout.NORTH);

        ownedTable = new OwnedTable(mainFrame);
        add(new JScrollPane(ownedTable), BorderLayout.CENTER);
    }

    /**
     * Updates the panel.
     */
    public void update() {
        ownedTable.update();
    }

    /**
     * Updates the prices of the owned stocks (open positions).
     */
    private void updateStockPrices() {
        mainFrame.setStatus("Updating stock prices...");
        mainFrame.showMessageDialog("Updating stock prices, please wait...");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int updatedStocks = updateService.updatePrices(config.getOwnedStocks());
                mainFrame.updateTables();
                mainFrame.closeMessageDialog();
                mainFrame.setStatus(String.format("%d stock prices updated.", updatedStocks));
            }
        });
    }

    /**
     * Handles the toggling whether to show or hide closed positions.
     */
    private void toggleShowClosedPositions() {
        config.setShowClosedPositions(showClosedPositionsCheck.isSelected());
        update();
    }

    /**
     * Handles the toggling whether to automatically deduct income tax.
     */
    private void toggleDeductIncomeTax() {
        config.setDeductIncomeTax(deductIncomeTaxCheck.isSelected());
        update();
    }
}
