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
package muvis.view.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import muvis.Environment;
import muvis.analyser.DataAnalyser;
import muvis.analyser.processor.ContentProcessor;
import muvis.analyser.processor.ContentUnprocessor;
import muvis.exceptions.CantSavePropertiesFileException;
import muvis.util.Observer;

/**
 * Controller for reloading the MuVis Library
 * @author Ricardo
 */
public class ReloadLibraryController implements ControllerInterface {

    private ArrayList<Observer> observers = new ArrayList<Observer>();

    public void registerLibraryExtractionObserver(Observer obs) {
        observers.add(obs);
    }

    /**
     * Saves the library folders in the config file
     * @param folders
     * @throws muvis.exceptions.CantSavePropertiesFileException
     */
    public void saveLibraryFolders(Object[] folders) throws CantSavePropertiesFileException {

        Properties configFile = Environment.getEnvironmentInstance().getConfigFile();
        if (!configFile.containsKey("folders_number")) {
        }
        configFile.setProperty("folders_number", String.valueOf(folders.length));

        int i = 0;

        for (Object folder : folders) {
            configFile.setProperty("library_folder" + i, folder.toString());
            i++;
        }
        /*try {
            FileOutputStream stream = new FileOutputStream(new File(".properties"));
            configFile.store(stream, "");
        } catch (FileNotFoundException ex) {
            throw new CantSavePropertiesFileException("Can't save the configuration file!");
        } catch (IOException ex) {
            throw new CantSavePropertiesFileException("Can't save the configuration file!");
        }*/
        Environment.getEnvironmentInstance().saveConfigFile();
    }

    public ArrayList<String> getLibraryFolders() {

        ArrayList<String> folders = new ArrayList<String>();
        Properties configFile = Environment.getEnvironmentInstance().getConfigFile();

        int countFolders = 0;
        if (configFile.containsKey("folders_number")) {
            countFolders = Integer.parseInt(configFile.getProperty("folders_number"));
        }

        for (int i = 0; i < countFolders; i++) {
            String folder = configFile.getProperty("library_folder" + i);
            folders.add(folder);
        }

        return folders;
    }

    /**
     * Loads the library processing the respective
     * @param folders
     */
    public void loadProcessLibrary(Object[] folders) {

        final String[] paths = new String[folders.length]; //the total range of folders
        int i = 0;
        for (Object obj : folders) {
            paths[i] = String.valueOf(obj);
            System.out.println(paths[i]);
            i++;
        }

        final ArrayList<String> prevLibraryFolders = getLibraryFolders();  //this will get the folders to load
        ArrayList<String> folderToRemove = getLibraryFolders();
        for (i = 0; i < paths.length; i++) {
            if (prevLibraryFolders.contains(paths[i])) {
                prevLibraryFolders.remove(paths[i]);
                folderToRemove.remove(paths[i]);
            } else {
                prevLibraryFolders.add(paths[i]);
            }
        }

        for (String path : folderToRemove) {
            if (prevLibraryFolders.contains(path)) {
                prevLibraryFolders.remove(path);
            }
        }

        final String[] removePaths = new String[folderToRemove.size()];
        i = 0;
        for (String folder : folderToRemove) {
            removePaths[i] = folder;
            i++;
        }

        //HERE MUST BE REMOVED THE FOLDERS THAT WERE DESELECTED!!!!!
        final ExecutorService tempThreadPool = Executors.newFixedThreadPool(1);

        Executors.newFixedThreadPool(1).execute( new Thread() {

            @Override
            public void run() {

                tempThreadPool.execute(
                        new Thread() {

                            @Override
                            public void run() {
                                DataAnalyser postAnalyser = new DataAnalyser(removePaths);
                                Observer contentObserver = new ContentUnprocessor();
                                postAnalyser.registerObserver(contentObserver);
                                postAnalyser.start();
                            }
                        });

                tempThreadPool.shutdown();

                while (!tempThreadPool.isTerminated()) {
                    try {
                        sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }

                final String[] newPaths = new String[prevLibraryFolders.size()];
                int i = 0;
                for (String folder : prevLibraryFolders) {
                    newPaths[i] = folder;
                    i++;
                }

                Executors.newFixedThreadPool(1).execute(
                        new Thread() {

                            @Override
                            public void run() {

                                DataAnalyser postAnalyser = new DataAnalyser(newPaths);
                                Observer contentObserver = new ContentProcessor();
                                postAnalyser.registerObserver(contentObserver);

                                for (Observer e : observers) {
                                    postAnalyser.registerObserver(e);
                                }

                                postAnalyser.start();
                            }
                        });

            }
        });
    }
}
