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

package muvis.view.filters;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.bouthier.treemapSwing.TMNode;
import net.bouthier.treemapSwing.TMUpdater;

/**
 * The MuVisTreemapNode implements a Node encapsulating the hierarchy
 * @author Ricardo
 * @version 1.0
 */
public abstract class MuVisFilterNode implements TMNode {

    protected boolean isLeaf = false;
    protected String nodeName = null;       //the artist this node represents
    protected MuVisFilterNode parent   = null; // the parent
    protected Hashtable  children = null; // the children of this node
    protected TMUpdater  updater  = null; // the updater for this node
    protected boolean selected = false;
    private int order;

    /* --- Constructor --- */

    /**
     * Constructor.
     *
     * @param file    the File encapsulated in this node
     */
    public MuVisFilterNode(String filterName) {
        /*this.file = file;
        children = new Hashtable();
        size = getSize();
        date = getDate();
        name = getName();*/
        children = new Hashtable();
        nodeName = filterName;
        order = 0;

        JFrame frame = new JFrame("Initializing");
        JPanel pane = new JPanel(new BorderLayout());
        frame.setContentPane(pane);

        JLabel infoLabel = new JLabel("Initializing tree...");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pane.add(infoLabel, BorderLayout.NORTH);

        JPanel paneStatus = new JPanel(new FlowLayout());
        JLabel fixedLabel = new JLabel("Reading filter : ");
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
        //JLabel threadLabel = new JLabel("Launching checking thread...");

        /*CheckingThread cheackingThread = new CheckingThread();
        cheackingThread.start();*/

        frame.dispose();
    }

    /**
     * Constructor.
     *
     * @param file      the File encapsulated in this node
     * @param parent    the parent of this node
     * @param status    the progress status to update
     */
    protected MuVisFilterNode(String filterName,
    					 MuVisFilterNode  	parent,
    					 ProgressStatus status) {
        //this.file = file;
        this.nodeName = filterName;
        this.parent = parent;
        children = new Hashtable();
        this.isLeaf = true;
        /*size = getSize();
        date = getDate();
        name = getName();*/

        if (status != null) {
            status.increment();
        }
        //buildTree(status);
    }

    /**
     * Builds the tree hierarchie of a TMFileNode.
     * A status view shows the progression of the activity.
     *
     * @param node      the TMFileNode root of the tree
     * @param status    the progress status to update
     */
    protected abstract void buildTree(ProgressStatus status);
    /*{
        if (!isLeaf()) {

            MusicLibraryDatabaseManager dbManager = Workspace.getWorkspaceInstance().getDatabaseManager();

            ArrayList<String> artistNames = dbManager.getAllArtistNamesAscOrder();
            for (String name : artistNames){
                MuVisFilterNode node = new MuVisFilterNode(name, parent, status);
                addChild(node);
            }


        }
    }*/


    /* --- Tree management --- */

    /**
     * Add child to the node.
     *
     * @param child    the TMFileNode to add as a child
     */
    protected void addChild(MuVisFilterNode child) {
        children.put(child.getName(), child);
    }

    /**
     * Removes a child from the node.
     *
     * @param child    the TMFileChild to remove.
     */
    protected void removeChild(MuVisFilterNode child) {
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
        /*try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return getName();
        }*/
    }

    /**
     * Returns the name of the file.
     *
     * @return    the name of the file
     */
    public String getName() {
        //return file.getName();
        return nodeName;
    }

    /**
     * Returns the size of the node.
     * If the node is a file, returns the size of the file.
     * If the node is a folder, returns 0.
     *
     * @return    the size of the node
     */
    public long getSize() {
        //return file.length();
        return 10;
    }

    /**
     * Returns the last modification date.
     *
     * @return    the last modification date
     */
    public long getDate() {
        //return file.lastModified();
        return 0;
    }

    /**
     * Returns the node in a String form : return the name.
     *
     * @return    the name of the file
     */
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
    public Enumeration children() {
        return children.elements();
    }

    /**
     * Returns true if this node is not a directory.
     *
     * @return    <CODE>false</CODE> if this node is a directory;
     *            <CODE>true</CODE> otherwise
     */
    public boolean isLeaf() {
        //return (!file.isDirectory());
        /*if (artistName == null) return false;
        else return false;*/
        return isLeaf;
    }

    /**
     * Sets the updater for this node.
     *
     * @param updater    the updater for this node
     */
    public void setUpdater(TMUpdater updater) {
        this.updater = updater;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        updater.updateState(this);
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }


    /* --- Updates --- */

    /**
     * Checks if something has changed.
     *
     * @return    <CODE>true</CODE> if something has changed;
     *            <CODE>false</CODE> otherwise
     */
    /*protected boolean hasChanged() {
        if (updater != null) {
            if (!file.exists()) {
                if (parent != null) {
                    parent.removeChild(this);
                }
                Runnable doRemoveChild = new Runnable() {
                    public void run() {
                        updater.removeChild(parent, TMFileNode.this);
                    }
                };
                SwingUtilities.invokeLater(doRemoveChild);
                return true;
            }

            if (size != getSize()) {
                size = getSize();
                Runnable doUpdateSize = new Runnable() {
                    public void run() {
                        updater.updateSize(TMFileNode.this);
                    }
                };
                SwingUtilities.invokeLater(doUpdateSize);
                return true;
            }
            if (date != getDate()) {
                date = getDate();
                Runnable doUpdateState = new Runnable() {
                    public void run() {
                        updater.updateState(TMFileNode.this);
                    }
                };
                SwingUtilities.invokeLater(doUpdateState);
                return true;
            }
            if ((name == null) || (!name.equals(getName()))) {
                name = getName();
                Runnable doUpdateState = new Runnable() {
                    public void run() {
                        updater.updateState(TMFileNode.this);
                    }
                };
                SwingUtilities.invokeLater(doUpdateState);
                return true;
            }

            if (!isLeaf()) {
                int childs = file.list().length;
                if (childs > children.size()) {
                    String[] childList = file.list();
                    for (int i = 0; i < childList.length; i++) {
                        if (!children.containsKey(childList[i])) {
                            File f =
                                new File(
                                    file.getPath()
                                        + File.separator
                                        + childList[i]);
                            TMFileNode child = new TMFileNode(f, this, null);
                            addChild(child);

                            class DoAddChild implements Runnable {

                                TMFileNode child = null; // the new child

                                DoAddChild(TMFileNode child) {
                                    this.child = child;
                                }

                                public void run() {
                                    updater.addChild(TMFileNode.this, child);
                                }
                            }

                            Runnable doAddChild = new DoAddChild(child);
                            SwingUtilities.invokeLater(doAddChild);
                            return true;
                        }
                    }
                }

                for (Enumeration e = children(); e.hasMoreElements();) {
                    TMFileNode child = (TMFileNode) e.nextElement();
                    if (child.hasChanged()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/


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

    /**
     * The inner class CheackingThread implements
     * a Thread that checks if files have changed.
     */
    /*class CheckingThread
    	extends Thread {

        public void run() {
            try {
                while (true) {
                    hasChanged();
                    sleep(delay);
                }
            } catch (InterruptedException e) {
            }
        }
    }*/

}
