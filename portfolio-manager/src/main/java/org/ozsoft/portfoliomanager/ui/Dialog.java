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

import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Abstract base class for dialogs.
 *
 * @author Oscar Stigter
 */
public abstract class Dialog {

    public static final int OK = 0;

    public static final int CANCEL = 1;

    private final JFrame owner;

    protected final JDialog dialog;

    private int result;

    public Dialog(JFrame owner) {
        this.owner = owner;

        dialog = new JDialog(owner, true);
        dialog.setResizable(false);
        dialog.setLayout(new GridBagLayout());

        initUI();
    }

    public int show() {
        result = CANCEL;
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        // Block until user has clicked a button.

        return result;
    }

    protected abstract void initUI();

    protected final void ok() {
        close(OK);
    }

    protected final void cancel() {
        close(CANCEL);
    }

    private void close(int result) {
        this.result = result;
        dialog.setVisible(false);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(owner, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
