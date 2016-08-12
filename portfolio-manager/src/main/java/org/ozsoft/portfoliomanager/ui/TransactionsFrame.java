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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ozsoft.portfoliomanager.ui.table.TransactionsTable;

/**
 * Modal window to view and edit portfolio transactions.
 *
 * @author Oscar Stigter
 */
public class TransactionsFrame extends JDialog {

    private static final long serialVersionUID = -4942077142767084610L;

    private final TransactionsTable transactionsTable;

    /**
     * Constructor.
     *
     * @param mainFrame
     *            The application's main window.
     */
    public TransactionsFrame(MainFrame mainFrame) {
        super(mainFrame, "Transactions", true);

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton button = new JButton("Add");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTransaction();
            }
        });
        buttonPanel.add(button);

        add(buttonPanel, BorderLayout.NORTH);

        transactionsTable = new TransactionsTable(mainFrame);
        add(new JScrollPane(transactionsTable), BorderLayout.CENTER);

        transactionsTable.update();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (0.6 * screenSize.getWidth());
        int height = (int) (0.85 * screenSize.getHeight());
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(mainFrame);
    }

    /**
     * Handles a click of the 'Add Transaction' button.
     */
    private void addTransaction() {
        transactionsTable.addTransaction();
    }
}
