/*
 * The GPLv3 licence :
 * -----------------
 * Copyright (c) 2009 Ricardo Dias
 *
 * This file is part of MuVis.
 *
 * MuVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MuVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MuVis.  If not, see <http://www.gnu.org/licenses/>.
 */
package muvis.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Class that holds a specific JPanel for drawing images. Has three methods that
 * complement each other.
 * @author Ricardo
 */
public class JImagePanel extends JPanel {

    private BufferedImage image;
    private int x, y, width, height;

    /**
     * Constructs an JImagePanel. This Panel allows to draw an image on it,
     * and if necessary resize himself and the image.
     * @param image The already loaded image
     * @param x X-Coordinate do start drawing
     * @param y Y-Coordinate do start drawing
     * @param width Width of the Panel. Used to resize the image if requested.
     * @param height Height of the Panel. Used to resize the image if requested.
     */
    public JImagePanel(BufferedImage image, int x, int y, int width, int height) {
        super();
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs an JImagePanel but only having the imagePath. Image loading is
     * accomplished the JImagePanel.
     * @param imagePath The Image path
     * @param x
     * @param y
     * @param width
     * @param height
     * @throws IOException
     */
    public JImagePanel(String imagePath, int x, int y, int width, int height) throws IOException {
        this(ImageIO.read(new File(imagePath)), x, y, width, height);
    }

    /**
     * Constructs an JImagePanel but only having the image URL. Image loading is
     * accomplished the JImagePanel.
     * @param imageURL
     * @param x
     * @param y
     * @param width
     * @param height
     * @throws IOException
     */
    public JImagePanel(URL imageURL, int x, int y, int width, int height) throws IOException {
        this(ImageIO.read(imageURL), x, y, width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //resize the image if necessary
        g.drawImage(image,
                x, y, width, height, /* dst rectangle */
                x, y, image.getWidth(), image.getHeight(), /* src area of image */
                null);
    }

    /**
     * Method that returns the contained image width
     * @return
     */
    public int getImageWidth(){
        return image.getWidth();
    }

    /**
     * Method that returns the contained image height
     * @return
     */
    public int getImageHeight(){
        return image.getHeight();
    }

    /**
     * Method that returns the contained image
     * @return
     */
    public BufferedImage getImage(){
        return image;
    }

    public void setImage(BufferedImage image){
        this.image = image;
    }
}
