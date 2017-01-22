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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

/**
 * Message dialog for temporarily showing a single-line message (e.g. status/progress).
 *
 * @author Oscar Stigter
 */
public class MessageDialog extends JDialog {

    private static final long serialVersionUID = -70419363927208237L;

    private final Frame parent;

    private final JLabel messageLabel;

    /**
     * Constructor.
     *
     * @param parent
     *            The application's main window.
     */
    public MessageDialog(Frame parent) {
        super(parent, false);

        this.parent = parent;

        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(Color.DARK_GRAY));
        messageLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(messageLabel, gbc);
        add(panel, gbc);

        setSize(400, 100);
    }

    /**
     * Shows the dialog with the specified message.
     *
     * @param message
     *            The message.
     */
    public void show(String message) {
        if (!isVisible()) {
            messageLabel.setText(message);
            setLocationRelativeTo(parent);
            setVisible(true);
        }
    }

    /**
     * Closes the dialog.
     */
    public void close() {
        if (isVisible()) {
            setVisible(false);
        }
    }
}
