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
import org.ozsoft.portfoliomanager.ui.UIConstants;

/**
 * Column renderer for the (income) yield column.
 *
 * @author Oscar Stigter
 */
public class YieldColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = 7036417211003108327L;

    private Color backgroundColor;

    @Override
    public String formatValue(Object value) {
        if (value instanceof Double) {
            double percValue = (double) value;
            if (percValue >= 6.0) {
                // Very high yield
                backgroundColor = UIConstants.DARK_GREEN;
            } else if (percValue >= 3.5) {
                // High yield
                backgroundColor = Color.GREEN;
            } else if (percValue >= 2.5) {
                // Good yield
                backgroundColor = Color.WHITE;
            } else {
                // Poor yield
                backgroundColor = Color.YELLOW;
            }
            return String.format("%.2f %%", (double) value);
        } else {
            // No value, e.g. in footer row.
            backgroundColor = Color.WHITE;
            return null;
        }
    }

    @Override
    public Color getBackground() {
        return backgroundColor;
    }
}
