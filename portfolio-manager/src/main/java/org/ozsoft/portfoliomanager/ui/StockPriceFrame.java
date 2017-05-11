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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.services.UpdateService;
import org.ozsoft.portfoliomanager.util.HttpPageReader;

/**
 * Window showing the current share price and graphs for a specific stock. <br />
 * <br />
 *
 * Uses the Yahoo Finance chart API (delayed).
 *
 * @author Oscar Stigter
 */
public class StockPriceFrame extends JFrame {

    private static final long serialVersionUID = -7868161566551066062L;

    private static final String PRICE_GRAPH_URI = "https://chart.finance.yahoo.com/z?s=%s&t=%s&l=off&p=v";

    private final Stock stock;

    private final HttpPageReader httpPageReader;

    private final UpdateService updateService;

    private final JLabel priceLabel;

    private final ImagePanel fullHistoryGraphPanel;

    private final ImagePanel fiveYearGraphPanel;

    private final ImagePanel oneYearGraphPanel;

    private final ImagePanel sevenDaysGraphPanel;

    /**
     * Private constructor.
     *
     * @param stock
     *            The stock to show the price and graphs for.
     */
    private StockPriceFrame(Stock stock) {
        super(stock.getSymbol() + " - Price");

        this.stock = stock;

        httpPageReader = new HttpPageReader();
        updateService = new UpdateService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        priceLabel = new JLabel();
        priceLabel.setFont(new Font("Proportional", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        getContentPane().add(priceLabel, gbc);

        fullHistoryGraphPanel = new ImagePanel();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(fullHistoryGraphPanel, gbc);

        fiveYearGraphPanel = new ImagePanel();
        gbc.gridx = 1;
        gbc.gridy = 1;
        getContentPane().add(fiveYearGraphPanel, gbc);

        oneYearGraphPanel = new ImagePanel();
        gbc.gridx = 0;
        gbc.gridy = 2;
        getContentPane().add(oneYearGraphPanel, gbc);

        sevenDaysGraphPanel = new ImagePanel();
        gbc.gridx = 1;
        gbc.gridy = 2;
        getContentPane().add(sevenDaysGraphPanel, gbc);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                update();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        getContentPane().add(updateButton, gbc);

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                analyze();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        getContentPane().add(analyzeButton, gbc);
    }

    /**
     * Shows the window for the specified stock.
     *
     * @param stock
     *            The stock.
     */
    public static void show(Stock stock) {
        StockPriceFrame frame = new StockPriceFrame(stock);
        frame.setResizable(false);

        frame.update();
    }

    /**
     * Updates the stock's current price and graphs.
     */
    private void update() {
        String symbol = stock.getSymbol();

        updateService.updatePrice(stock);
        priceLabel.setText(String.format("Current price: $%.2f", stock.getPrice()));

        try {
            fullHistoryGraphPanel.setImage(httpPageReader.downloadFile(String.format(PRICE_GRAPH_URI, symbol, "9y")));
            fiveYearGraphPanel.setImage(httpPageReader.downloadFile(String.format(PRICE_GRAPH_URI, symbol, "1y")));
            oneYearGraphPanel.setImage(httpPageReader.downloadFile(String.format(PRICE_GRAPH_URI, symbol, "10d")));
            sevenDaysGraphPanel.setImage(httpPageReader.downloadFile(String.format(PRICE_GRAPH_URI, symbol, "1d")));

            pack();
            setLocationRelativeTo(null);

            setVisible(true);

            repaint();
            revalidate();

        } catch (IOException e) {
            System.err.format("ERROR: Could not retrieve price graph for %s\n", stock);
            e.printStackTrace(System.err);
        }
    }

    private void analyze() {
        new StockAnalysisFrame(stock);
    }
}
