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
import muvis.analyser.DataAnalyser;
import muvis.analyser.processor.ContentProcessor;
import muvis.analyser.processor.ContentUnprocessor;
import muvis.exceptions.CantSavePropertiesFileException;
import muvis.util.Observer;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Controller for reloading the MuVis Library
 * @author Ricardo
 */
public class ReloadLibraryController implements ControllerInterface, ApplicationContextAware {

    @Autowired
    PropertiesConfiguration configuration;
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

        if (!configuration.containsKey("folders_number")) {
        }
        configuration.setProperty("folders_number", String.valueOf(folders.length));

        int i = 0;

        for (Object folder : folders) {
            configuration.setProperty("library_folder" + i, folder.toString());
            i++;
        }
        try {
            configuration.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public ArrayList<String> getLibraryFolders() {

        ArrayList<String> folders = new ArrayList<String>();

        int countFolders = 0;
        if (configuration.containsKey("folders_number")) {
            countFolders = configuration.getInt("folders_number");
        }

        for (int i = 0; i < countFolders; i++) {
            String folder = configuration.getString("library_folder" + i);
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
                                Observer contentObserver = (Observer) context.getBean("contentUnprocessor");
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
                                Observer contentObserver = (Observer) context.getBean("contentProcessor");
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

    private ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }
}
