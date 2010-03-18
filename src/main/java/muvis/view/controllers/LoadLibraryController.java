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

import muvis.analyser.DataAnalyser;
import muvis.analyser.processor.ContentProcessor;
import muvis.exceptions.CantSavePropertiesFileException;
import muvis.util.Observer;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Controller responsible for loading the library specified by the user
 * @author Ricardo
 */
public class LoadLibraryController implements ControllerInterface {

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
