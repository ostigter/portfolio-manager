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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.io.IOUtils;

/**
 * Panel with an bitmap image. <br />
 * <br />
 *
 * The panel will automatically resize to fit the image.
 *
 * @author Oscar Stigter
 */
public class ImagePanel extends JPanel {

    private static final long serialVersionUID = -7868161566551066062L;

    private BufferedImage image;

    /**
     * Constructor.
     *
     * @param is
     *            The image as an {@code InputStream}.
     *
     * @throws IOException
     *             If the image is not specified ({@code null}) or could not be constructed.
     */
    public void setImage(InputStream is) throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("Null input stream");
        }

        try {
            image = ImageIO.read(is);
            if (image != null) {
                setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            } else {
                setPreferredSize(new Dimension(100, 100));
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
}
