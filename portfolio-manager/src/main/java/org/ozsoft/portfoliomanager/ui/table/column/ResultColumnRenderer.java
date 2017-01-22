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
import java.math.MathContext;

import org.ozsoft.datatable.DefaultColumnRenderer;
import org.ozsoft.portfoliomanager.ui.UIConstants;

/**
 * Column renderer for monetary result (profit/loss) values.
 *
 * @author Oscar Stigter
 */
public class ResultColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = -2184616889371886011L;

    private static final int DEFAULT_DECIMAL_PRECISION = 2;

    private final String positiveFormat;

    private final String negativeFormat;

    private Color textColor;

    public ResultColumnRenderer() {
        this(DEFAULT_DECIMAL_PRECISION);
    }

    public ResultColumnRenderer(int decimalPrecision) {
        if (decimalPrecision < 0) {
            throw new IllegalArgumentException("Invalid decimalPrecision; must be equal to 0 or greater");
        }
        positiveFormat = String.format("$ %%,.%df", decimalPrecision);
        negativeFormat = String.format("($ %%,.%df)", decimalPrecision);
    }

    @Override
    public String formatValue(Object value) {
        if (value instanceof BigDecimal) {
            BigDecimal numericValue = (BigDecimal) value;
            if (numericValue.signum() > 0) {
                textColor = UIConstants.DARKER_GREEN;
                return String.format(positiveFormat, numericValue);
            } else if (numericValue.signum() < 0) {
                textColor = Color.RED;
                return String.format(negativeFormat, numericValue.abs(MathContext.DECIMAL64));
            } else {
                // Empty value on no change.
                return null;
            }
        } else if (value instanceof Double) {
            double numericValue = (double) value;
            if (numericValue > 0.0) {
                textColor = UIConstants.DARKER_GREEN;
                return String.format(positiveFormat, numericValue);
            } else if (numericValue < 0.0) {
                textColor = Color.RED;
                return String.format(negativeFormat, Math.abs(numericValue));
            } else {
                // Empty value on no change.
                return null;
            }
        } else {
            textColor = Color.RED;
            return "<ERROR>";
        }
    }

    @Override
    public Color getForeground() {
        return textColor;
    }
}
