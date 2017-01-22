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

package org.ozsoft.portfoliomanager.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.UtilDateModel;
import org.ozsoft.portfoliomanager.domain.Configuration;
import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.domain.Transaction;
import org.ozsoft.portfoliomanager.domain.TransactionType;

/**
 * Dialog for creating or editing a transaction.
 *
 * @author Oscar Stigter
 */
public class EditTransactionDialog extends Dialog {

    private JDatePicker datePicker;

    private JComboBox<String> symbolComboBox;

    private JTextField nameText;

    private JComboBox<TransactionType> typeComboBox;

    private JTextField sharesText;

    private JTextField priceText;

    private JTextField costsText;

    private JButton okButton;

    private JButton cancelButton;

    private Transaction transaction;

    public EditTransactionDialog(JFrame owner) {
        super(owner);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    protected void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel("Date:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 10, 0, 5);
        dialog.add(label, gbc);

        datePicker = new JDateComponentFactory(UtilDateModel.class, new DateComponentFormatter(), null).createJDatePicker();
        ((Component) datePicker).setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 5, 0, 5);
        dialog.add((JComponent) datePicker, gbc);

        label = new JLabel("Symbol:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 10, 0, 5);
        dialog.add(label, gbc);

        symbolComboBox = new JComboBox<String>();
        symbolComboBox.setPreferredSize(new Dimension(75, 20));
        symbolComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stock stock = null;
                String symbol = (String) symbolComboBox.getSelectedItem();
                if (symbol != null && symbol.length() > 0) {
                    stock = Configuration.getInstance().getStock(symbol);
                }
                if (stock != null) {
                    nameText.setText(stock.getName());
                } else {
                    nameText.setText("");
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 0, 10);
        dialog.add(symbolComboBox, gbc);

        label = new JLabel("Stock name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 10, 0, 5);
        dialog.add(label, gbc);

        nameText = new JTextField();
        nameText.setEditable(false);
        nameText.setPreferredSize(new Dimension(200, 20));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 0, 10);
        dialog.add(nameText, gbc);

        label = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 10, 0, 5);
        dialog.add(label, gbc);

        typeComboBox = new JComboBox<TransactionType>();
        typeComboBox.addItem(TransactionType.BUY);
        typeComboBox.addItem(TransactionType.SELL);
        typeComboBox.addItem(TransactionType.DIVIDEND);
        typeComboBox.setPreferredSize(new Dimension(75, 20));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 0, 10);
        dialog.add(typeComboBox, gbc);

        label = new JLabel("Shares:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 10, 0, 5);
        dialog.add(label, gbc);

        sharesText = new JTextField();
        sharesText.setPreferredSize(new Dimension(75, 20));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 0, 10);
        dialog.add(sharesText, gbc);

        label = new JLabel("Price:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 10, 0, 5);
        dialog.add(label, gbc);

        priceText = new JTextField();
        priceText.setPreferredSize(new Dimension(75, 20));
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 0, 10);
        dialog.add(priceText, gbc);

        label = new JLabel("Costs:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 10, 0, 5);
        dialog.add(label, gbc);

        costsText = new JTextField();
        costsText.setPreferredSize(new Dimension(75, 20));
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 0, 10);
        dialog.add(costsText, gbc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());

        okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(75, 20));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        buttonPanel.add(okButton, gbc);

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(75, 20));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        buttonPanel.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        dialog.add(buttonPanel, gbc);
    }

    @Override
    public int show() {
        return show(null);
    }

    public int show(Transaction transaction) {
        this.transaction = transaction;

        // Populate combobox wih all stock symbols, sorted alphabetically.
        Set<String> symbols = new TreeSet<String>();
        for (Stock stock : Configuration.getInstance().getStocks()) {
            symbols.add(stock.getSymbol());
        }
        symbolComboBox.removeAllItems();
        symbolComboBox.addItem("");
        for (String symbol : symbols) {
            symbolComboBox.addItem(symbol);
        }

        if (transaction == null) {
            dialog.setTitle("Add Transaction");
            ((UtilDateModel) datePicker.getModel()).setValue(new Date());
            symbolComboBox.setSelectedItem("");
            // nameText.setText("");
            typeComboBox.setSelectedItem(TransactionType.DIVIDEND);
            sharesText.setText("");
            priceText.setText("");
            costsText.setText("");
        } else {
            dialog.setTitle("Edit Transaction");
            ((UtilDateModel) datePicker.getModel()).setValue(new Date(transaction.getDate()));
            symbolComboBox.setSelectedItem(transaction.getSymbol());
            String symbol = transaction.getSymbol();
            Stock stock = Configuration.getInstance().getStock(symbol);
            String name = (stock != null) ? stock.getName() : "";
            nameText.setText(name);
            typeComboBox.setSelectedItem(transaction.getType());
            sharesText.setText(String.valueOf(transaction.getNoOfShares()));
            priceText.setText(String.format("%.4f", transaction.getPrice()));
            costsText.setText(String.format("%.2f", transaction.getCost()));
        }

        okButton.requestFocus();

        return super.show();
    }

    private void apply() {
        if (transaction == null) {
            transaction = new Transaction();
        }

        Date date = (Date) datePicker.getModel().getValue();
        if (date == null) {
            JOptionPane.showMessageDialog(null, "No date selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String symbol = (String) symbolComboBox.getSelectedItem();
        if (symbol == null || symbol.length() == 0) {
            JOptionPane.showMessageDialog(null, "No stock symbol selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TransactionType type = (TransactionType) typeComboBox.getSelectedItem();

        int noOfShares = -1;
        try {
            noOfShares = Integer.parseInt(sharesText.getText());
        } catch (NumberFormatException e) {
            // Handled by range check below.
        }
        if (noOfShares < 1) {
            JOptionPane.showMessageDialog(null, "Invalid number of shares (must be at least 1).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal price = BigDecimal.ZERO;
        try {
            price = new BigDecimal(priceText.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            // Handled by range check below.
        }
        if (price.signum() <= 0) {
            JOptionPane.showMessageDialog(null, "Invalid price (must be greater than $0.00).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal cost = BigDecimal.ZERO;
        try {
            cost = new BigDecimal(costsText.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            // Handled by range check below.
        }
        if (cost.signum() < 0) {
            JOptionPane.showMessageDialog(null, "Invalid costs (must be equal to or greater than $0).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        transaction.setSymbol(symbol);
        transaction.setDate(date.getTime());
        transaction.setType(type);
        transaction.setNoOfShares(new BigDecimal(noOfShares));
        transaction.setPrice(price);
        transaction.setCost(cost);

        ok();
    }
}
