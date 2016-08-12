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

package org.ozsoft.portfoliomanager.ui;

import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Defines UI-specific constants.
 *
 * @author Oscar Stigter
 */
public interface UIConstants {

    /** Dark green color. */
    Color DARK_GREEN = new Color(0, 160, 0);

    /** Darker green color. */
    Color DARKER_GREEN = new Color(0, 0x80, 0);

    /** Spacer border. */
    Border SPACER_BORDER = new EmptyBorder(10, 10, 10, 10);
}
