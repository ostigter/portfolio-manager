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

package org.ozsoft.portfoliomanager.ui.table;

import java.util.Set;
import java.util.TreeSet;

import org.ozsoft.portfoliomanager.domain.Stock;
import org.ozsoft.portfoliomanager.domain.StockLevel;
import org.ozsoft.portfoliomanager.ui.MainFrame;

/**
 * Table with the stocks that have been promoted to the 'Goal' list.
 *
 * @author Oscar Stigter
 */
public class GoalTable extends StockTable {

    private static final long serialVersionUID = 1406716019847307872L;

    public GoalTable(MainFrame mainFrame) {
        super(mainFrame);
    }

    @Override
    protected Set<Stock> getStocks() {
        Set<Stock> stocks = new TreeSet<Stock>();
        for (Stock stock : getConfig().getStocks()) {
            StockLevel level = stock.getLevel();
            if (level == StockLevel.GOAL || level == StockLevel.OWNED) {
                stocks.add(stock);
            }
        }
        return stocks;
    }
}
