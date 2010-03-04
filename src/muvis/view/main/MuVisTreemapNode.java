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

package muvis.view.main;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import muvis.Elements;
import muvis.Environment;
import muvis.database.MusicLibraryDatabaseManager;
import net.bouthier.treemapSwing.TMNode;
import net.bouthier.treemapSwing.TMUpdater;

/**
 * The MuVisTreemapNode implements a Node encapsulating the hierarchy
 * @author Ricardo
 * @version 1.0
 */
public class MuVisTreemapNode implements TMNode {

    protected boolean isLeaf = false;
    protected String artistName = null;       //the artist this node represents
    protected MuVisTreemapNode parent   = null; // the parent
    protected Hashtable  children = null; // the children of this node
    protected TMUpdater  updater  = null; // the updater for this node
    protected boolean isSelected = false;
    protected double key = -1;
    protected boolean isSeed;
    protected double similarity;
    protected double artistKey;

    /* --- Constructor --- */

    /**
     * Constructor.
     *
     * @param file    the File encapsulated in this node
     */
    public MuVisTreemapNode() {
        children = new Hashtable();
        artistName = "Main";

        JFrame frame = new JFrame("Initializing");
        JPanel pane = new JPanel(new BorderLayout());
        frame.setContentPane(pane);

        JLabel infoLabel = new JLabel("Initializing tree...");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pane.add(infoLabel, BorderLayout.NORTH);

        JPanel paneStatus = new JPanel(new FlowLayout());
        JLabel fixedLabel = new JLabel("Reading library : ");
        ProgressStatus progressStatus = new ProgressStatus();
        JLabel statusLabel = progressStatus.getLabel();
        paneStatus.add(fixedLabel);
        paneStatus.add(statusLabel);
        pane.add(paneStatus, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        progressStatus.increment();
        buildTree(progressStatus);

        pane.remove(paneStatus);

        frame.dispose();
    }

    /**
     * Constructor.
     *
     * @param file      the File encapsulated in this node
     * @param parent    the parent of this node
     * @param status    the progress status to update
     */
    protected MuVisTreemapNode(String artistName,
    					 MuVisTreemapNode  	parent,
    					 ProgressStatus status, boolean isLeaf) {
        this.artistName = artistName;
        this.parent = parent;
        children = new Hashtable();
        this.isLeaf = isLeaf;

        if (status != null) {
            status.increment();
        }
    }

    /**
     * Builds the tree hierarchie of a TMFileNode.
     * A status view shows the progression of the activity.
     *
     * @param node      the TMFileNode root of the tree
     * @param status    the progress status to update
     */
    protected void buildTree(ProgressStatus status) {
        if (!isLeaf()) {

            MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();

            ArrayList<String> artistNames = dbManager.getAllArtistNamesAscOrder();
            MuVisTreemapNode others = new MuVisTreemapNode(Elements.OTHERS_NODE, parent, status, true);
            for (String name : artistNames){

                double artistTempKey = dbManager.getArtistKey(name);

                if (dbManager.getArtistNumTracks(name) < 4){
                    MuVisTreemapNode node = new MuVisTreemapNode(name, parent, status, true);
                    others.addChild(node);
                    node.setArtistKey(artistTempKey);
                    continue;
                }
                MuVisTreemapNode node = new MuVisTreemapNode(name, parent, status, true);
                node.setArtistKey(artistTempKey);
                addChild(node);
            }
            addChild(others);
            Elements.othersNode = others;

        }
    }

    /* --- Tree management --- */

    /**
     * Add child to the node.
     *
     * @param child    the TMFileNode to add as a child
     */
    protected void addChild(MuVisTreemapNode child) {
        children.put(child.getName(), child);
    }

    /**
     * Removes a child from the node.
     *
     * @param child    the TMFileChild to remove.
     */
    protected void removeChild(MuVisTreemapNode child) {
        children.remove(child.getName());
    }


    /* --- Accessor --- */

    /**
     * Returns the full name of the file.
     *
     * @return    the full name of the file
     */
    public String getFullName() {
        return getName();
    }

    /**
     * Returns the name of the file.
     *
     * @return    the name of the file
     */
    public String getName() {
        return artistName;
    }

    /**
     * Returns the size of the node.
     * If the node is a file, returns the size of the file.
     * If the node is a folder, returns 0.
     *
     * @return    the size of the node
     */
    public long getSize() {
        return 1;
    }

    /**
     * Returns the last modification date.
     *
     * @return    the last modification date
     */
    public long getDate() {
        return 0;
    }

    /**
     * Returns the node in a String form : return the name.
     *
     * @return    the name of the file
     */
    @Override
    public String toString() {
        return getName();
    }


    /* --- TMNode --- */

    /**
     * Returns the children of this node in an Enumeration.
     * If this node is a file, return a empty Enumeration.
     * Else, return an Enumeration full with TMFileNode.
     *
     * @return    an Enumeration containing childs of this node
     */
    @Override
    public Enumeration children() {
        return children.elements();
    }

    /**
     * Returns true if this node is not an artist.
     */
    @Override
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * Sets the updater for this node.
     *
     * @param updater    the updater for this node
     */
    @Override
    public void setUpdater(TMUpdater updater) {
        this.updater = updater;
    }

    public TMUpdater getUpdater(){
        return updater;
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updater.updateState(this);
    }

    private void setIsLeaf(boolean b) {
        isLeaf = b;
    }

    /**
     * @return the isSeed
     */
    public boolean isSeed() {
        return isSeed;
    }

    /**
     * @param isSeed the isSeed to set
     */
    public void setIsSeed(boolean isSeed) {
        this.isSeed = isSeed;
    }

    /**
     * @return the similarity
     */
    public double getSimilarity() {
        return similarity;
    }

    /**
     * @param similarity the similarity to set
     */
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /**
     * @return the artistKey
     */
    public double getArtistKey() {
        return artistKey;
    }

    /**
     * @param artistKey the artistKey to set
     */
    public void setArtistKey(double artistKey) {
        this.artistKey = artistKey;
    }

    /* --- Inners --- */

    /**
     * The inner class ProgressStatus implements
     * a simple way to update a JLabel to reflect
     * the progress of an activity.
     */
    class ProgressStatus {

        private JLabel label 	= null; // the view : a JLabel
        private int    progress = 0; 	// the model : the progress

        /**
         * Constructor.
         */
        ProgressStatus() {
            label = new JLabel(Integer.toString(progress));
        }

        /**
         * Returns the label.
         *
         * @return    the label
         */
        JLabel getLabel() {
            return label;
        }

        /**
         * Increments the progress.
         */
        void increment() {
            progress++;
            label.setText(Integer.toString(progress));
            label.repaint();
        }
    }
}
