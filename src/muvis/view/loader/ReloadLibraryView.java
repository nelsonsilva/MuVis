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
package muvis.view.loader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import muvis.Environment;
import muvis.exceptions.CantSavePropertiesFileException;
import muvis.view.controllers.ReloadLibraryController;

/**
 * Reloading library controller
 * @author Ricardo
 */
public class ReloadLibraryView extends LoadLibraryViewUI implements ActionListener {

    private JFileChooser browseSystemFile;
    private JFrame parent;
    private DefaultListModel libraryListModel;
    public boolean mustloadLibrary;
    private ReloadLibraryController controller;
    private ArrayList<String> previousFolders;
    private JFrame newFrame;

    public ReloadLibraryView(JFrame parent, ReloadLibraryController controller) {

        newFrame = new JFrame("Please select your library folders");
        newFrame.add(this);
        this.controller = controller;
        libraryListModel = new DefaultListModel();
        libraryFoldersList.setModel(libraryListModel);

        removeLibraryFolderButton.addActionListener(this);
        browseFilesystemButton.addActionListener(this);
        loadLibraryButton.addActionListener(this);
        skipLoadingLibraryButton.addActionListener(this);

        skipLoadingLibraryButton.setText("Close");

        browseSystemFile = new JFileChooser();
        mustloadLibrary = false;

        previousFolders = controller.getLibraryFolders();
        for (String folder : previousFolders) {
            addNewFolderToView(folder);
        }
        newFrame.pack();
        newFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == browseFilesystemButton) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    browseSystemFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    browseSystemFile.setDialogTitle("Select a library folder");
                    browseSystemFile.setAcceptAllFileFilterUsed(false);
                    int returned = browseSystemFile.showOpenDialog(parent);
                    if (returned == JFileChooser.APPROVE_OPTION) {

                        File file = browseSystemFile.getSelectedFile();
                        String pathName = file.getAbsoluteFile().toString();
                        if (!pathName.equals("")) {
                            addNewFolderToView(pathName);
                            mustloadLibrary = true;
                            loadLibraryButton.setEnabled(true);
                        }
                    }
                }
            });
        } else if (event.getSource() == removeLibraryFolderButton) {

            //We can remove several items
            int[] indices = libraryFoldersList.getSelectedIndices();
            ArrayList<Object> itemsToRemove = new ArrayList<Object>();

            for (int i = 0; i < indices.length; i++) {

                Object playlistItemToRemove = libraryListModel.getElementAt(indices[i]);

                //marking the tracks for removal
                itemsToRemove.add(playlistItemToRemove);

                int size = libraryListModel.getSize();

                if (size == 0) {
                    removeLibraryFolderButton.setEnabled(false);
                    mustloadLibrary = false;
                    if (previousFolders.size() > 0){
                        loadLibraryButton.setEnabled(true);
                    }
                } else { //Select an index.
                    if (indices[i] == libraryListModel.getSize()) {
                        //removed item in last position
                        indices[i]--;
                    }
                }
            }

            boolean shouldLoad = false;
            /*
             * Removing the items from the playlist
             */
            for (Object item : itemsToRemove) {
                libraryListModel.removeElement(item);
                if (previousFolders.contains(item) && !shouldLoad){
                    shouldLoad = true;
                }
            }

            if (libraryListModel.getSize() == 0) {
                removeLibraryFolderButton.setEnabled(false);
                mustloadLibrary = false;
            } else {
                mustloadLibrary = true;
            }

            List<Object> tempFolders = Arrays.asList(libraryListModel.toArray());

            if (shouldLoad){
                loadLibraryButton.setEnabled(true);
            } else if (previousFolders.containsAll(tempFolders)){
                loadLibraryButton.setEnabled(false);
            }

        } else if (event.getSource() == loadLibraryButton) {//Loading library
            if (mustloadLibrary) {
                Object[] folders = libraryListModel.toArray();
                try {
                    controller.loadProcessLibrary(folders);
                    controller.saveLibraryFolders(folders);
                } catch (CantSavePropertiesFileException ex) {
                    JOptionPane.showMessageDialog(this, "Can't save the properties file",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                newFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a folder first",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (event.getSource() == skipLoadingLibraryButton) {
            newFrame.dispose();

            try {
                Environment.getWorkspaceInstance().loadWorkspace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.out.println("Continuing without the loaded configuration");
            }
        }
    }

    private void addNewFolderToView(String folder) {
        int index = libraryFoldersList.getSelectedIndex(); //get selected index
        if (index == -1) {
            //no selection, so insert at beginning
            index = 0;
        } else {
            //add after the selected item
            index++;
        }
        libraryListModel.insertElementAt(folder, index);
        
        //Select the new item and make it visible.
        libraryFoldersList.setSelectedIndex(index);
        libraryFoldersList.ensureIndexIsVisible(index);
        removeLibraryFolderButton.setEnabled(true);
    }
}
