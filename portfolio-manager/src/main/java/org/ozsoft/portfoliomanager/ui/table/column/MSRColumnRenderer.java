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

import javax.swing.SwingConstants;

import org.ozsoft.datatable.DefaultColumnRenderer;
import org.ozsoft.portfoliomanager.ui.UIConstants;

/**
 * Column renderer for Morningstar value rating values (1 to 5 stars).
 *
 * @author Oscar Stigter
 */
public class MSRColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = 9128778326338428542L;

    private Color backgroundColor;

    public MSRColumnRenderer() {
        super(SwingConstants.CENTER);
    }

    @Override
    public String formatValue(Object value) {
        if (value instanceof Integer) {
            int starRating = (int) value;
            if (starRating < 1) {
                backgroundColor = Color.LIGHT_GRAY;
                return "N/R";
            } else {
                if (starRating <= 1) {
                    backgroundColor = Color.ORANGE;
                } else if (starRating == 2) {
                    backgroundColor = Color.YELLOW;
                } else if (starRating == 3) {
                    backgroundColor = Color.WHITE;
                } else if (starRating == 4) {
                    backgroundColor = Color.GREEN;
                } else {
                    backgroundColor = UIConstants.DARK_GREEN;
                }
            }
            return "*****".substring(0, starRating);
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
