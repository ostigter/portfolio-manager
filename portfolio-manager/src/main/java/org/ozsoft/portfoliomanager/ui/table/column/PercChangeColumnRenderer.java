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
import java.math.BigDecimal;

import org.ozsoft.datatable.DefaultColumnRenderer;
import org.ozsoft.portfoliomanager.ui.UIConstants;

/**
 * Column renderer for percentage change values.
 *
 * @author Oscar Stigter
 */
public class PercChangeColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = -7786489823544289457L;

    private Color textColor;

    @Override
    public String formatValue(Object value) {
        if (value instanceof BigDecimal) {
            BigDecimal percValue = (BigDecimal) value;
            if (percValue.signum() > 0) {
                textColor = UIConstants.DARKER_GREEN;
                return String.format("%+.2f %%", percValue);
            } else if (percValue.signum() < 0) {
                textColor = Color.RED;
                return String.format("%+.2f %%", percValue);
            } else {
                // Empty value when no change.
                textColor = Color.BLACK;
                return "0.00 %";
            }
        } else if (value instanceof Double) {
            double percValue = (double) value;
            if (percValue > 0.0) {
                textColor = UIConstants.DARKER_GREEN;
                return String.format("%+.2f %%", (double) value);
            } else if (percValue < 0.0) {
                textColor = Color.RED;
                return String.format("%+.2f %%", (double) value);
            } else {
                // Empty value when no change.
                textColor = Color.BLACK;
                return "0.00 %";
            }
        } else {
            // Empty value, e.g. in footer row.
            return null;
        }
    }

    @Override
    public Color getForeground() {
        return textColor;
    }
}
