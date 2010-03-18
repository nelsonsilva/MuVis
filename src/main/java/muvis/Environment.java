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
package muvis;

import muvis.exceptions.CantSavePropertiesFileException;
import muvis.util.Util;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This Singleton class holds the main elements of MuVis application
 * List of elements:
 * -AudioPlayer
 * -Playlist
 * -AudioSnippetPlayerManager
 * -Properties Configuration file
 * -Database Manager
 * -Docking desktop
 * -NBTreeManager
 * @author Ricardo
 */
public class Environment {

    @Autowired private Configuration configuration;
    @Autowired private NBTreeManager nbtreesManager;
    
    private Properties configFile;

   /**
     * @return the configFile
     */
    public Properties getConfigFile() {
        return configFile;
    }


    public Object getProperty(String s) {
        return configuration.getProperty(s);
    }

    public int getInt(String s) {
        return configuration.getInt(s);
    }

    public String getString(String s) {
        return configuration.getString(s);
    }
    
    /**
     * @param aConfigFile the configFile to set
     */
    public void setConfigFile(Properties aConfigFile) {
        configFile = aConfigFile;
    }

    public Environment() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new MemoryCollector(), 300, 300, TimeUnit.SECONDS);
    }

    public void init() {
        initializeDataFolders();
        configFile=new Properties();
        try {
            String dataFolder = configuration.getString("muvis.data_folder");
            String nbtreeMainFolder = configuration.getString("muvis.nbtree_folder");
            String nbtreeFullfolder = dataFolder + Util.getOSEscapeSequence() + nbtreeMainFolder + Util.getOSEscapeSequence();

            nbtreesManager.addNBTree(Elements.TRACKS_NBTREE, new NBTree(Elements.TRACKS_NBTREE, nbtreeFullfolder));
            nbtreesManager.addNBTree(Elements.ALBUMS_NBTREE, new NBTree(Elements.ALBUMS_NBTREE, nbtreeFullfolder));
            nbtreesManager.addNBTree(Elements.ARTISTS_NBTREE, new NBTree(Elements.ARTISTS_NBTREE, nbtreeFullfolder));
        } catch (NBTreeException ex) {
            ex.printStackTrace();
            System.out.println("An error occured when trying to initialize the nbtreemanager!");
        }

        initConfigFile();

    }
    //initializes the data folder, or creates it if necessary
    private void initializeDataFolders() {

        String dataFolder = configuration.getString("muvis.data_folder");
        String nbtreeFolder = configuration.getString("muvis.nbtree_folder");
        String databaseFolder = configuration.getString("muvis.database_folder");

        File file = new File(dataFolder);
        if (!file.isDirectory()) {
            boolean success = (new File(dataFolder)).mkdir();
            if (success) {
                new File(dataFolder + Util.getOSEscapeSequence() + nbtreeFolder).mkdir();
                new File(dataFolder + Util.getOSEscapeSequence() + databaseFolder).mkdir();
            }
        }
    }

    public void saveWorkspace() throws FileNotFoundException {
        nbtreesManager.save();
    }

    public void loadWorkspace() throws FileNotFoundException {
        //nothing to do here
    }



    public String getDataFolderPath(){
        String dataFolder = configuration.getString("muvis.data_folder") + Util.getOSEscapeSequence();
        return dataFolder;
    }

    /**
     * Initializes the configuration file.
     */
    public void initConfigFile() {
        if (!configFileExists()) {
            createConfigFile();
        } else {
            readConfigFile();
        }
    }

    /**
     * Checks if the configuration file exists.
     * @return a boolean that indicates if the file exists
     */
    public boolean configFileExists() {
        String configurationFolder = configuration.getString("muvis.configuration_folder") + Util.getOSEscapeSequence();
        File file = new File(configurationFolder + ".properties");
        if (file.exists() && file.length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    //reads the configuration file
    public void readConfigFile() {
        try {
            String configurationFolder = configuration.getString("muvis.configuration_folder") + Util.getOSEscapeSequence();
            getConfigFile().load(new FileInputStream(new File(configurationFolder + ".properties")));
        } catch (IOException ex) {
            System.out.println("Can't load the config file.");
        }
    }

    //creates the configuration file.
    public void createConfigFile() {
        String configurationFolder = configuration.getString("muvis.configuration_folder") + Util.getOSEscapeSequence();
        File file = new File(configurationFolder + ".properties");
        try {
            if (file.createNewFile()) {
                getConfigFile().load(new FileInputStream(file));
                System.out.println("Properties file was succeful created!");
            }
        } catch (IOException ex1) {
            System.out.println("Cannot create .properties file!");
        }
    }

    //saves the config file under workspace control
    public void saveConfigFile() throws CantSavePropertiesFileException{
        try {
            String configurationFolder = configuration.getString("muvis.configuration_folder") + Util.getOSEscapeSequence();
            FileOutputStream stream = new FileOutputStream(new File(configurationFolder + ".properties"));
            configFile.store(stream, "");
        } catch (FileNotFoundException ex) {
            throw new CantSavePropertiesFileException("Can't save the configuration file!");
        } catch (IOException ex) {
            throw new CantSavePropertiesFileException("Can't save the configuration file!");
        }
    }

    class MemoryCollector implements Runnable{

        @Override
        public void run() {
            System.out.println("GC at Environment running!");
            System.gc();
        }
        
    }
}
