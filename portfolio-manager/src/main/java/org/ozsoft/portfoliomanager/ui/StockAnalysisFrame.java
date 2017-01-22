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

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.domain.StockAnalysis;
import org.ozsoft.portfoliomanager.services.AnalyzeService;

/**
 * Modal window to view portfolio statistics.
 *
 * @author Oscar Stigter
 */
public class StockAnalysisFrame extends JFrame {

    private static final long serialVersionUID = -1275875522615811609L;

    private final Stock stock;

    private final AnalyzeService analyzeService;

    private final JTextArea textArea;

    /**
     * Constructor.
     *
     * @param stock
     *            Stock to analyze.
     */
    public StockAnalysisFrame(Stock stock) {
        super(stock.getSymbol() + " - Analysis");

        this.stock = stock;

        analyzeService = new AnalyzeService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        setSize(400, 350);
        setResizable(false);
        setLocationRelativeTo(null);

        showAnalysis();

        setVisible(true);
    }

    /**
     * Shows the stock's anlysis.
     */
    private void showAnalysis() {
        MessageDialog messageDialog = new MessageDialog(this);
        messageDialog.show("Analyzing stock...");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StockAnalysis analysis = analyzeService.analyzeStock(stock);

                StringBuilder sb = new StringBuilder(String.format("\n  Analysis for %s:\n\n", analysis.getStock()));
                sb.append(String.format("  10-year return:     %,.2f %% CAGR\n", analysis.getCagr10yr()));
                sb.append(String.format("   5-year return:     %,.2f %% CAGR\n", analysis.getCagr5yr()));
                sb.append(String.format("   1-year return:     %,.2f %%\n\n", analysis.getChange1yr()));
                sb.append(String.format("  10-year volatility: %,.2f %%\n\n", analysis.getVolatility()));
                sb.append(String.format("  52-week high:       $ %,.2f\n", analysis.getHigh52wk()));
                sb.append(String.format("  52-week low:        $ %,.2f\n", analysis.getLow52wk()));
                sb.append(String.format("  Current price:      $ %,.2f\n", analysis.getCurrentPrice()));
                sb.append(String.format("  5-year discount:    %.2f %%\n", analysis.getDiscount5yr()));
                sb.append(String.format("  1-year discount:    %.2f %%\n\n", analysis.getDiscount1yr()));
                sb.append(String.format("  Score:              %.2f\n", analysis.getScore()));
                textArea.append(sb.toString());

                messageDialog.close();
            }
        });
    }
}
