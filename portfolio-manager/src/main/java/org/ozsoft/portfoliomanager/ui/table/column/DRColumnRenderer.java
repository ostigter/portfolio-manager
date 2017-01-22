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

import java.math.BigDecimal;

import org.ozsoft.datatable.DefaultColumnRenderer;

/**
 * Column renderer for the dividend rate (DR) column.
 *
 * @author Oscar Stigter
 */
public class DRColumnRenderer extends DefaultColumnRenderer {

    private static final long serialVersionUID = -8744524110427922656L;

    @Override
    public String formatValue(Object value) {
        if (value instanceof BigDecimal) {
            BigDecimal numericValue = (BigDecimal) value;
            if (numericValue.signum() > 0) {
                return String.format("$ %.2f", numericValue);
            } else {
                // Empty when no dividend.
                return null;
            }
        } else {
            return "<ERROR>";
        }
    }
}
