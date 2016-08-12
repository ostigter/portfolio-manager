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

import javax.swing.SwingUtilities;

import org.ozsoft.datatable.DefaultColumnRenderer;
import org.ozsoft.portfoliomanager.domain.CreditRating;
import org.ozsoft.portfoliomanager.ui.UIConstants;

/**
 * Credit rating column renderer.
 *
 * @author Oscar Stigter
 */
public class CRColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = 4924544217201385488L;

    private Color backgroundColor;

    /**
     * Constructor.
     */
    public CRColumnRenderer() {
        setHorizontalAlignment(SwingUtilities.CENTER);
    }

    @Override
    public String formatValue(Object value) {
        if (value instanceof CreditRating) {
            if (value == CreditRating.NA) {
                backgroundColor = Color.LIGHT_GRAY;
            } else {
                int ordinalValue = ((CreditRating) value).ordinal();
                if (ordinalValue <= CreditRating.A_MINUS.ordinal()) {
                    backgroundColor = UIConstants.DARK_GREEN;
                } else if (ordinalValue <= CreditRating.BBB_MINUS.ordinal()) {
                    backgroundColor = Color.GREEN;
                } else if (ordinalValue <= CreditRating.B_MINUS.ordinal()) {
                    backgroundColor = Color.YELLOW;
                } else {
                    backgroundColor = Color.ORANGE;
                }
            }
            return ((CreditRating) value).getText();
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
