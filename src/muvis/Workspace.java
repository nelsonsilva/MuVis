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

import com.vlsolutions.swing.docking.DockingDesktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import muvis.audio.AudioSnippetPlayerManager;
import muvis.audio.MuVisAudioPlayer;
import muvis.audio.playlist.BasePlaylist;
import muvis.audio.playlist.Playlist;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.TableFilterManager;
import muvis.view.ViewManager;
import muvis.view.main.filters.TreemapFilterManager;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;

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
public class Workspace {

    //Fields necessary for the MuVisApp
    private static MuVisAudioPlayer audioPlayer;
    private static AudioSnippetPlayerManager snippetManager;
    private static Playlist userPlaylist;
    private static Properties configFile;
    private static ViewManager viewManager;
    private static NBTreeManager nbtreesManager;
    private static MusicLibraryDatabaseManager databaseManager;
    private static DockingDesktop desk;
    private static TableFilterManager tableFilterManager;
    private static TreemapFilterManager treemapFilterManager;
    /**
     * The only instance of Workspace
     */
    private static Workspace workspace = new Workspace();

    /**
     * @return the treemapFilterManager
     */
    public TreemapFilterManager getTreemapFilterManager() {
        return treemapFilterManager;
    }

    /**
     * @param aTreemapFilterManager the treemapFilterManager to set
     */
    public void setTreemapFilterManager(TreemapFilterManager aTreemapFilterManager) {
        treemapFilterManager = aTreemapFilterManager;
    }

    /**
     * @return the tableFilterManager
     */
    public TableFilterManager getTableFilterManager() {
        return tableFilterManager;
    }

    /**
     * @param aTableFilterManager the tableFilterManager to set
     */
    public void setTableFilterManager(TableFilterManager aTableFilterManager) {
        tableFilterManager = aTableFilterManager;
    }

    /**
     * @return the snippetManager
     */
    public AudioSnippetPlayerManager getSnippetManager() {
        return snippetManager;
    }

    /**
     * @return the desk
     */
    public DockingDesktop getDesk() {
        return desk;
    }

    /**
     * @param aDesk the desk to set
     */
    public void setDesk(DockingDesktop aDesk) {
        desk = aDesk;
    }

    /**
     * @return the nbtreesManager
     */
    public NBTreeManager getNbtreesManager() {
        return nbtreesManager;
    }

    /**
     * @return the databaseManager
     */
    public synchronized MusicLibraryDatabaseManager getDatabaseManager() {
        if (databaseManager == null){
            databaseManager = new MusicLibraryDatabaseManager();
            try {
                databaseManager.connect();
                databaseManager.initDatabase();
            } catch (SQLException ex) {
                System.out.println("Cannot init the database!" + ex.toString());
            }
        }
        return databaseManager;
    }

    /**
     * @return the viewManager
     */
    public ViewManager getViewManager() {
        return viewManager;
    }

    /**
     * @param aViewManager the viewManager to set
     */
    public void setViewManager(ViewManager aViewManager) {
        viewManager = aViewManager;
    }

    /**
     * @return the configFile
     */
    public Properties getConfigFile() {
        return configFile;
    }

    /**
     * @param aConfigFile the configFile to set
     */
    public void setConfigFile(Properties aConfigFile) {
        configFile = aConfigFile;
    }

    private Workspace() {
        //initialize all the elements in the workspace
        audioPlayer = new MuVisAudioPlayer();
        snippetManager = new AudioSnippetPlayerManager(audioPlayer);
        userPlaylist = new BasePlaylist();
        configFile = new Properties();
        viewManager = new ViewManager();
        desk = new DockingDesktop();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new MemoryCollector(), 300, 300, TimeUnit.SECONDS);

        nbtreesManager = new NBTreeManager();
        try {
            nbtreesManager.addNBTree("tracksNBTree", new NBTree("tracksNBTree"));
            nbtreesManager.addNBTree("albumsNBTree", new NBTree("albumsNBTree"));
            nbtreesManager.addNBTree("artistsNBTree", new NBTree("artistsNBTree"));
        } catch (NBTreeException ex) {
            ex.printStackTrace();
            System.out.println("An error occured when trying to initialize the nbtreemanager!");
        }

        initConfigFile();
    }

    public void saveWorkspace() throws FileNotFoundException {
        nbtreesManager.save();
    }

    public void loadWorkspace() throws FileNotFoundException {
        //nothing to do here
    }

    /**
     * This method returns the only instance of Workspace
     * @return workspace The only instance of Workspace
     */
    public static Workspace getWorkspaceInstance() {
        return workspace;
    }

    /**
     * This method returns the MuVisAudioPlayer instance
     * @return audioPlayer the only audioplayer for this application
     */
    public MuVisAudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    /**
     * This method returns the playlist built by the user, so we can add tracks,
     * get track being played, etc.
     * @return userPlaylist The playlist built by the user
     */
    public Playlist getAudioPlaylist() {
        return userPlaylist;
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
        File file = new File(".properties");
        if (file.exists() && file.length() != 0) {
            return true;
        } else {
            return false;
        }
    }

    //reads the configuration file
    private void readConfigFile() {
        try {
            getConfigFile().load(new FileInputStream(new File(".properties")));
        } catch (IOException ex) {
            System.out.println("Can't load the config file.");
        }
    }

    //creates the configuration file.
    private void createConfigFile() {
        File file = new File(".properties");
        try {
            if (file.createNewFile()) {
                getConfigFile().load(new FileInputStream(file));
                System.out.println("Properties file was succeful created!");
            }
        } catch (IOException ex1) {
            System.out.println("Cannot create .properties file!");
        }
    }

    class MemoryCollector implements Runnable{

        @Override
        public void run() {
            System.out.println("GC Workspace running!");
            System.gc();
        }
        
    }
}
