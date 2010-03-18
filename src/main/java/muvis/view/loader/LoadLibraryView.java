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
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import muvis.Environment;
import muvis.exceptions.CantSavePropertiesFileException;
import muvis.util.Util;
import muvis.view.MuVisAppView;
import muvis.view.controllers.LoadLibraryController;

/**
 * Interface Controller for loading the library
 * @author Ricardo
 */
public class LoadLibraryView extends LoadLibraryViewUI implements ActionListener {

    private JFileChooser browseSystemFile;
    private DefaultListModel libraryListModel;
    public boolean mustloadLibrary;
    protected JFrame parent;
    private LoadLibraryController controller;

    public LoadLibraryView(LoadLibraryController controller) {

        parent = Environment.getEnvironmentInstance().getRootFrame();
        parent.setTitle("Please select your library folders");
        parent.add(this);
        parent.setSize(this.getPreferredSize());
        parent.validate();
        parent.setVisible(true);
        parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //EXIT_ON_CLOSE
        this.controller = controller;
        libraryListModel = new DefaultListModel();
        libraryFoldersList.setModel(libraryListModel);

        removeLibraryFolderButton.addActionListener(this);
        browseFilesystemButton.addActionListener(this);
        loadLibraryButton.addActionListener(this);
        skipLoadingLibraryButton.addActionListener(this);

        browseSystemFile = new JFileChooser();
        mustloadLibrary = false;
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
                            int index = libraryFoldersList.getSelectedIndex(); //get selected index
                            if (index == -1) { //no selection, so insert at beginning
                                index = 0;
                            } else {           //add after the selected item
                                index++;
                            }

                            libraryListModel.insertElementAt(pathName, index);
                            //Select the new item and make it visible.
                            libraryFoldersList.setSelectedIndex(index);
                            libraryFoldersList.ensureIndexIsVisible(index);

                            removeLibraryFolderButton.setEnabled(true);
                            loadLibraryButton.setEnabled(true);
                            mustloadLibrary = true;
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

                if (size != 0) {
                    if (indices[i] == libraryListModel.getSize()) {
                        //removed item in last position
                        indices[i]--;
                    }
                }
            }

            /*
             * Removing the items from the playlist
             */
            for (Object item : itemsToRemove) {
                libraryListModel.removeElement(item);
            }

            if (libraryListModel.getSize() == 0) {
                removeLibraryFolderButton.setEnabled(false);
                loadLibraryButton.setEnabled(false);
                mustloadLibrary = false;
            }

        } else if (event.getSource() == loadLibraryButton) {//Loading library
            if (mustloadLibrary) {

                Object[] folders = libraryListModel.toArray();
                try {
                    controller.saveLibraryFolders(folders);
                    controller.loadProcessLibrary(folders);
                } catch (CantSavePropertiesFileException ex) {
                    Util.displayErrorMessage(Environment.getEnvironmentInstance().getRootFrame(),
                            "Error",
                            "Can't save the properties file!");
                    return;
                }

                JFrame oldFrame = parent;
                oldFrame.dispose();

                parent = buildMainView();
            } else {
                Util.displayInformationMessage(Environment.getEnvironmentInstance().getRootFrame(),
                        "Information",
                        "Please select a folder first");
            }
        } else if (event.getSource() == skipLoadingLibraryButton) {

            Util.displayInformationMessage(Environment.getEnvironmentInstance().getRootFrame(),
                        "Information",
                        "Please load your library later!");
            try {
                //library already loaded
                Environment.getEnvironmentInstance().loadWorkspace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.out.println("Continuing without the loaded configuration");
            }

            buildMainView();
        }
    }

    private MuVisAppView buildMainView() {
        MuVisAppView frameTest = new MuVisAppView();
        frameTest.setSize(1280, 770);
        frameTest.setResizable(true);
        frameTest.validate();
        frameTest.setVisible(true);
        frameTest.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //EXIT_ON_CLOSE
        Environment.getEnvironmentInstance().setRootFrame(frameTest);

        return frameTest;
    }
}
