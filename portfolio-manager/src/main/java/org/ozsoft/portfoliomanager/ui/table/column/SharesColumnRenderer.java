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

/**
 * Column renderer for the Number Of Shares (Shares) column.
 *
 * @author Oscar Stigter
 */
public class SharesColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = -4207280209997046453L;

    private Color textColor;

    /**
     * Constructor.
     */
    public SharesColumnRenderer() {
        super(SwingConstants.RIGHT);
    }

    @Override
    public String formatValue(Object value) {
        if (value instanceof Integer) {
            int intValue = (int) value;
            if (intValue >= 0) {
                textColor = Color.BLACK;
            } else {
                textColor = Color.RED;
            }
            return String.valueOf(intValue);
        } else {
            return null;
        }
    }

    @Override
    public Color getForeground() {
        return textColor;
    }
}
