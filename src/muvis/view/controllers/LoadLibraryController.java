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

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;
import muvis.Environment;
import muvis.analyser.DataAnalyser;
import muvis.analyser.processor.ContentProcessor;
import muvis.exceptions.CantSavePropertiesFileException;
import muvis.util.Observer;

/**
 * Controller responsible for loading the library specified by the user
 * @author Ricardo
 */
public class LoadLibraryController implements ControllerInterface {

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
        Environment.getEnvironmentInstance().saveConfigFile();
    }

    /**
     * Loads the library processing the respective
     * @param folders
     */
    public void loadProcessLibrary(Object[] folders) {

        final String[] paths = new String[folders.length];
        int i = 0;
        for (Object obj : folders) {
            paths[i] = String.valueOf(obj);
            System.out.println(paths[i]);
            i++;
        }

        Executors.newFixedThreadPool(1).execute(
                new Thread() {

                    @Override
                    public void run() {

                        DataAnalyser postAnalyser = new DataAnalyser(paths);
                        Observer contentObserver = new ContentProcessor();
                        postAnalyser.registerObserver(contentObserver);

                        for (Observer e : observers) {
                            postAnalyser.registerObserver(e);
                        }

                        postAnalyser.start();
                    }
                });
    }
}
