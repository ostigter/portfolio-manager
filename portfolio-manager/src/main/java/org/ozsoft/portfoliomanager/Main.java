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

package org.ozsoft.portfoliomanager;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ozsoft.portfoliomanager.ui.MainFrame;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.debug("Starting application");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
                LOGGER.info("Application started");
            }
        });

//		Stock stock = Configuration.getInstance().getStock("AAPL");
//		new UpdateService().updatePrice(stock);

//		StockAnalysis analysis = new AnalyzeService().analyzeStock(stock);
//		System.out.format("Stock: %s\n", stock);
//		System.out.format("Credit rating: %s\n", stock.getCreditRating().getText());
//		System.out.format("Years DGR: %d\n", stock.getYearsDivGrowth());
//		System.out.format("10-yr CAGR: %,.2f %%\n", analysis.getCagr10yr());
//		System.out.format(" 5-yr CAGR: %,.2f %%\n", analysis.getCagr5yr());
//		System.out.format("Current yield: %,.2f %%\n", stock.getYield());
//		System.out.format("5-yr DGR: %,.2f %%\n", stock.getDivGrowth());
    }
}
