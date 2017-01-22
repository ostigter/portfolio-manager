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

package org.ozsoft.portfoliomanager.ui.table.column;

import java.awt.Color;
import org.ozsoft.datatable.DefaultColumnRenderer;

/**
 * Column renderer for the Target Price Index (TPI) column.
 *
 * @author Oscar Stigter
 */
public class TPIColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = 7036417211003108327L;

    private Color backgroundColor;

    @Override
    public String formatValue(Object value) {
        if (value instanceof Double) {
            double indexValue = (double) value;
            if (indexValue == 0.0) {
                backgroundColor = null;
                return null;
            } else {
                if (indexValue >= 100.0) {
                    // Target price reached
                    backgroundColor = Color.GREEN;
                } else if (indexValue >= 95.0) {
                    // Approaching target price (within 5 %)
                    backgroundColor = Color.YELLOW;
                } else {
                    // Far from target price
                    backgroundColor = null;
                }
                return String.format("%.1f", indexValue);
            }
        } else {
            backgroundColor = null;
            return null;
        }
    }

    @Override
    public Color getBackground() {
        if (backgroundColor != null) {
            return backgroundColor;
        } else {
            return super.getBackground();
        }
    }
}
