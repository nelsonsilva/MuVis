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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class holds some common functions that are used across the project
 * @author Ricardo
 * @version 1.0
 */
public class Util {

    public static String[] protocols = {"http:", "file:", "ftp:", "https:", "ftps:", "jar:"};
    public static String[] mood = {"Calm", "Energetic", "Dark", "Positive"};
    public static String[] beat = {"Slow", "Moderate", "Fast", "Very fast"};
    private static Util instance = null;

    /**
     * Returns Util instance.
     */
    public synchronized static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {

        BufferedImage resizedImage = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * Checks if the given string matches some of the protocols suported by this method.
     * @param input
     * @return
     */
    public static boolean startWithProtocol(String input) {
        boolean ret = false;
        if (input != null) {
            input = input.toLowerCase();
            for (int i = 0; i < protocols.length; i++) {
                if (input.startsWith(protocols[i])) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Method that returns a string that represents the OS escape sequence
     * @return the OS escape sequence
     */
    public static String getOSEscapeSequence() {
        // TO DO - modify this method to use System.getProperty("file.separator")
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            return "\\";
        } else {
            return "//";
        }
    }

    public static void displayErrorMessage(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void displayInformationMessage(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Converts a double array descriptor to a string descriptor
     * @param descriptor
     * @return
     */
    public String doubleArrayToString(double[] descriptor, char separator) {

        String descriptorStr = "";

        for (int i = 0; i < descriptor.length; i++) {
            descriptorStr += descriptor[i];
            if (i != (descriptor.length - 1)) {
                descriptorStr += separator;
            }
        }

        return descriptorStr;
    }

    /**
     * Converts a string descriptor to a double array descriptor
     * @param descriptor
     * @return
     */
    public double[] stringToDoubleArray(String descriptor) {

        String[] descriptors = descriptor.split(",");

        double[] result = new double[descriptors.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(descriptors[i]);
        }

        return result;
    }

    /**
     * Converts a long into a string that represents the music time.
     * Format: (hh:mm:ss)
     * @param value
     * @return
     */
    public static String secondsToTimeDisplay(Object value) {

        String timeDisplay = "";

        if (value instanceof Long) {
            long valueLong = (Long) value;
            long hours = valueLong / 3600;
            long minutes = (valueLong - (hours * 3600)) / 60;
            long seconds = (valueLong - (hours * 3600) - (minutes * 60));

            if (hours != 0) {
                if (hours < 10) {
                    timeDisplay += "0";
                }
                timeDisplay += hours + ":";
            }
            if (minutes < 10) {
                timeDisplay += "0";
            }
            timeDisplay += minutes + ":";

            if (seconds < 10) {
                timeDisplay += "0" + seconds;
            } else {
                timeDisplay += seconds;
            }
        }

        return timeDisplay;

    }

    public static Color getColor(int value) {
        Color color1 = new Color(231, 239, 249);
        Color color2 = new Color(197, 217, 241);
        Color color3 = new Color(141, 180, 227);
        Color color4 = new Color(83, 142, 213);
        Color color5 = new Color(23, 55, 93);

        if (value <= 100) {
            return color1;
        } else if (value > 100 && value <= 300) {
            return color2;
        } else if (value > 300 && value <= 600) {
            return color3;
        } else if (value > 600 && value <= 1000) {
            return color4;
        } else if (value > 1000) {
            return color5;
        } else {
            return Color.white;
        }
    }

    public static Color getGenreColor(String genre) {

        Color jazz = new Color(255, 128, 223);
        Color gospel = new Color(193, 5, 255);
        Color blues = new Color(159, 128, 255);
        Color metal = new Color(102, 51, 255);
        Color rock = new Color(0, 0, 204);
        Color pop = new Color(128, 191, 255);
        Color disco = new Color(128, 255, 255);
        Color funk = new Color(128, 255, 128);
        Color rb = new Color(223, 128, 255);
        Color rap = new Color(204, 0, 153);
        Color hphp = new Color(255, 10, 194);
        Color electro = new Color(0, 51, 204);
        Color latin = new Color(255, 159, 128);
        Color classical = new Color(255, 233, 128);
        Color soundtrack = new Color(228, 255, 122);
        Color world = new Color(223, 255, 128);
        Color reggae = new Color(113, 255, 66);
        Color soul = new Color(68, 255, 5);
        Color african = new Color(163, 255, 71);
        Color other = new Color(255, 61, 61);

        if (genre.equals("Jazz")) {
            return jazz;
        } else if (genre.equals("Gospel")) {
            return gospel;
        } else if (genre.equals("Blues")) {
            return blues;
        } else if (genre.equals("Metal")) {
            return metal;
        } else if (genre.equals("Rock")) {
            return rock;
        } else if (genre.equals("Pop")) {
            return pop;
        } else if (genre.equals("Disco")) {
            return disco;
        } else if (genre.equals("Funk")) {
            return funk;
        } else if (genre.equals("R&B")) {
            return rb;
        } else if (genre.equals("Rap")) {
            return rap;
        } else if (genre.equals("Hip-Hop")) {
            return hphp;
        } else if (genre.equals("Electro")) {
            return electro;
        } else if (genre.equals("Latin")) {
            return latin;
        } else if (genre.equals("Classical")) {
            return classical;
        } else if (genre.equals("Soundtrack")) {
            return soundtrack;
        } else if (genre.equals("World")) {
            return world;
        } else if (genre.equals("Reggae")) {
            return reggae;
        } else if (genre.equals("Sould")) {
            return soul;
        } else if (genre.equals("African")) {
            return african;
        } else {
            return other;
        }
    }

    // Function to get a list of all files in a directory and all subdirectories
    public static ArrayList<MP3AudioFile> listFilesRecursive(String dir) {
        ArrayList<MP3AudioFile> fileList = new ArrayList<MP3AudioFile>();
        recurseDir(fileList, dir, null);
        return fileList;
    }

    // Recursive function to traverse sub-directories
    private static void recurseDir(ArrayList<MP3AudioFile> a, String dir, File parent) {
        File file = new File(dir);
        if (file.isDirectory()) {
            // If you want to include directories in the list
            //a.add(new Node(file,parent));
            File[] subfiles = file.listFiles();
            Arrays.sort(subfiles);
            for (int i = 0; i < subfiles.length; i++) {

                if (subfiles[i].getAbsolutePath().equals(".") || subfiles[i].getAbsolutePath().equals("..")) {
                    continue;
                }
                //also skip non mp3 files - m3u files will be handled later
                String possibleFile = subfiles[i].getAbsolutePath();
                if (Pattern.matches("(.+)\\.(.+)", possibleFile)) {
                    if (!Pattern.matches("(.+)\\.(mp3)", possibleFile)) {
                        continue;
                    }
                }
                // Call this function on all files in this directory
                recurseDir(a, subfiles[i].getAbsolutePath(), file);
            }
        } else {
            a.add(new MP3AudioFile(file));
        }
    }
}
