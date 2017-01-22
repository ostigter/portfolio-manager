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
import org.ozsoft.portfoliomanager.ui.UIConstants;

/**
 * Column renderer for the consecutive number of years of dividend growth.
 *
 * @author Oscar Stigter
 */
public class YDGColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = -8644113732788006823L;

    private Color backgroundColor;

    @Override
    public String formatValue(Object value) {
        if (value instanceof Integer) {
            int numericValue = (int) value;
            if (numericValue < 1) {
                backgroundColor = Color.ORANGE;
                return "0";
            } else {
                if (numericValue >= 25) {
                    backgroundColor = UIConstants.DARK_GREEN;
                } else if (numericValue >= 10.0) {
                    backgroundColor = Color.GREEN;
                } else {
                    backgroundColor = Color.YELLOW;
                }
                return String.valueOf(numericValue);
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
