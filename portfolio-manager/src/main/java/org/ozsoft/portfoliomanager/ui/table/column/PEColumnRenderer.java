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

package org.ozsoft.portfoliomanager.ui.table.column;

import java.awt.Color;

import org.ozsoft.datatable.DefaultColumnRenderer;

/**
 * Column renderer for the P/E ratio column.
 *
 * @author Oscar Stigter
 */
public class PEColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = 4924544217201385488L;

    private Color backgroundColor;

    @Override
    public String formatValue(Object value) {
        if (value instanceof Double) {
            double numericValue = (double) value;
            if (numericValue <= 0.0) {
                backgroundColor = Color.ORANGE;
                return "N/A";
            } else {
                if (numericValue > 20.0) {
                    backgroundColor = Color.ORANGE;
                } else if (numericValue > 15.0) {
                    backgroundColor = Color.YELLOW;
                } else {
                    backgroundColor = Color.GREEN;
                }
                return String.format("%.1f", numericValue);
            }
        } else {
            backgroundColor = Color.WHITE;
            return null;
        }
    }

    @Override
    public Color getBackground() {
        return backgroundColor;
    }
}
