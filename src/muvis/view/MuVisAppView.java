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

package muvis.view;

import muvis.view.table.ListViewTableView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import com.vlsolutions.swing.docking.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import muvis.Elements;
import muvis.Environment;
import muvis.Messages;
import muvis.audio.AudioMetadata;
import muvis.audio.playlist.PlaylistItem;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.view.actions.AboutMuVisAction;
import muvis.view.actions.MuVisExitAction;
import muvis.view.controllers.PlaylistController;
import muvis.view.controllers.PlaylistControllerInterface;
import muvis.view.controllers.ReloadLibraryController;
import muvis.view.loader.ReloadLibraryView;
import muvis.view.main.filters.TreemapFilterManager;
import muvis.view.tray.MuVisTrayView;
import org.xml.sax.SAXException;

public class MuVisAppView extends JFrame {

    private static final long serialVersionUID = -8910566176114738718L;
    //Views
    private MusicControllerView musicPlayerView;
    private DirectoryListTreeView filesystemView;
    private PlaylistView playlistView;
    private ListViewTableView tracksViewTable;
    private TreemapView treemapView;
    private MainViewHolder mainView;

    //Controllers
    private PlaylistControllerInterface playlistController;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private JFrame frame;
    private ExecutorService threadPool;

    public MuVisAppView() {

        this.frame = this;
        this.setTitle(Messages.MUVIS_QUOTE);

        threadPool = Executors.newFixedThreadPool(1);
        new MuVisTrayView();

        //init controllers
        playlistController = new PlaylistController();

        //init views
        musicPlayerView = new MusicControllerView(this);
        filesystemView = new DirectoryListTreeView();
        playlistView = new PlaylistView(this, playlistController);
        tracksViewTable = new ListViewTableView(this);
        treemapView = new TreemapView(this);
        mainView = new MainViewHolder(this);
        mainView.addView(Elements.LIST_VIEW, tracksViewTable);
        mainView.addView(Elements.TREEMAP_VIEW, treemapView);
        mainView.setView(Elements.LIST_VIEW);
        mainView.initializeFilters();

        ViewManager viewManager = Environment.getEnvironmentInstance().getViewManager();
        viewManager.addView(Elements.MUSIC_PLAYER_VIEW, musicPlayerView);
        viewManager.addView(Elements.FILE_SYSTEM_VIEW, filesystemView);
        viewManager.addView(Elements.PLAYLIST_VIEW, playlistView);
        viewManager.addView(Elements.LIST_VIEW, tracksViewTable);
        viewManager.addView(Elements.TREEMAP_VIEW, treemapView);
        viewManager.addView(Elements.MAIN_VIEW, mainView);
        viewManager.addView(Elements.MUVIS_APP_VIEW, this);
        TreemapArtistInspectorView artistInspectorView = new TreemapArtistInspectorView(frame);
        viewManager.addView(Elements.ARTIST_INSPECTOR_VIEW, artistInspectorView);
        mainView.addView(Elements.ARTIST_INSPECTOR_VIEW, artistInspectorView);

        final DockingDesktop desk = Environment.getEnvironmentInstance().getDesk();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().add(desk); // desk becomes the only one component

        try {
            // desk becomes the only one component
            loadDocking();

        } catch (FileNotFoundException ex) {
            //this is the first time loading the application
            desk.addDockable(mainView);
            desk.split(mainView, musicPlayerView, DockingConstants.SPLIT_BOTTOM);
            desk.setDockableHeight(musicPlayerView, 0.18f);
            desk.split(mainView, filesystemView, DockingConstants.SPLIT_LEFT);
            desk.setDockableWidth(filesystemView, 0.15f);
            desk.split(mainView, playlistView, DockingConstants.SPLIT_RIGHT);
            desk.setDockableWidth(playlistView, 0.2f);

            //desk.setAutoHide(filesystemView, true);
            //desk.setAutoHide(playlistView, true);
            //desk.setAutoHide(musicPlayerView, true);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        setJMenuBar(menuBar);

        ActionListener exitMenuListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    try {
                        saveDocking();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.out.println("Couldn't save the desk disposition!");
                    }
                    //saving the state of the application
                    Environment.getEnvironmentInstance().saveWorkspace();
                    //Exiting the application
                    System.exit(0);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        };

        class MuVisAppWindowAdapter extends WindowAdapter{

            MuVisExitAction exitAction;

            MuVisAppWindowAdapter(){
                exitAction = new MuVisExitAction();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                exitAction.actionPerformed(null);
            }
        }

        //create custom close operation
        addWindowListener(new MuVisAppWindowAdapter());

        ActionListener saveMenuOptionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                JFileChooser fc = new JFileChooser(new File("C:\\"));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setDialogTitle("Save your workspace");
                FileFilter fFilter = new javax.swing.filechooser.FileNameExtensionFilter("Results file", "out");
                fc.addChoosableFileFilter(fFilter);
                fc.setAcceptAllFileFilterUsed(false);
                int returned = fc.showSaveDialog(frame);
                if (returned == JFileChooser.APPROVE_OPTION) {

                    File saveFile = fc.getSelectedFile();
                    try {
                        saveDocking(saveFile.getAbsolutePath());
                    } catch (IOException ex) {
                        System.out.println("Couldn't save the configuration.");
                    }


                }
            }
        };

        ActionListener loadMenuOptionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                JFileChooser fc = new JFileChooser(new File(""));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Save your workspace");
                FileFilter fFilter = new javax.swing.filechooser.FileNameExtensionFilter("Workspace file", "xml");
                fc.addChoosableFileFilter(fFilter);
                fc.setAcceptAllFileFilterUsed(false);
                int returned = fc.showOpenDialog(frame);
                if (returned == JFileChooser.APPROVE_OPTION) {

                    File openFile = fc.getSelectedFile();
                    try {
                        loadDocking(openFile.getAbsolutePath());
                    } catch (FileNotFoundException ex) {
                        try {
                            //try to load the default configuration file
                            loadDocking();
                        } catch (FileNotFoundException ex1) {
                            ex1.printStackTrace();
                        } catch (IOException ex1) {
                            ex1.printStackTrace();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        ActionListener generatePlaylist = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                final GeneratePlaylistView generatePlaylist = new GeneratePlaylistView(frame);

                ActionListener listener = new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        threadPool.execute(new Runnable() {

                            @Override
                            public void run() {
                                TreemapFilterManager filterManager = Environment.getEnvironmentInstance().getTreemapFilterManager();
                                MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();

                                List availableTracks = filterManager.getFilteredTracks();
                                List tracks = new ArrayList();

                                if (!generatePlaylist.isIncludeAllTracks()) {
                                    int numTracks = generatePlaylist.getNumTracksInPlaylist();
                                    Random rnd = new Random();
                                    for (int i = 0; i < numTracks; i++) {
                                        tracks.add(availableTracks.get(rnd.nextInt(availableTracks.size() - 1)));
                                    }
                                } else {
                                    tracks = availableTracks;
                                }

                                for (Object trackObject : tracks) {
                                    int trackId = (Integer) trackObject;
                                    String track = dbManager.getFilename(trackId);
                                    AudioMetadata metadata = dbManager.getTrackMetadata(trackId);
                                    PlaylistItem pliItem = new PlaylistItem(track, "", metadata);
                                    Environment.getEnvironmentInstance().getAudioPlaylist().appendItem(pliItem);
                                }
                            }
                        });

                    }
                };

                generatePlaylist.addPlaylistGeneratorListener(listener);
                generatePlaylist.setVisible(true);
            }
        };

        menuItem = new JMenuItem("Save workspace");
        menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/playlist/save-element.png")));
        menu.add(menuItem);
        menuItem.addActionListener(saveMenuOptionListener);

        menuItem = new JMenuItem("Load workspace");
        menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/playlist/document-open.png")));
        menu.add(menuItem);
        menuItem.addActionListener(loadMenuOptionListener);

        menuItem = new JMenuItem("Reload Library");
        menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menus/reload.png")));
        menu.add(menuItem);
        menuItem.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ReloadLibraryView reloadLibrary = new ReloadLibraryView(frame, new ReloadLibraryController());
                reloadLibrary.setVisible(true);
            }
        });

        //a group of JMenuItems
        menuItem = new JMenuItem("Exit", KeyEvent.VK_F4);
        menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menus/system-exit.png")));
        menu.add(menuItem);
        menuItem.addActionListener(exitMenuListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        menu = new JMenu("Playlist");
        menuBar.add(menu);

        menuItem = new JMenuItem("Generate Playlist");
        menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menus/generate-playlist.png")));
        menu.add(menuItem);
        menuItem.addActionListener(generatePlaylist);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));

        menu = new JMenu("View");
        menuBar.add(menu);

        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Treemap View");
        rbMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menus/treemap-view.png")));
        rbMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        rbMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainView.setView(Elements.TREEMAP_VIEW);
            }
        });
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("List View");
        rbMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menus/table-view.png")));
        rbMenuItem.setSelected(true);
        rbMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        rbMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mainView.setView(Elements.LIST_VIEW);
            }
        });
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        menu = new JMenu("Help");
        menuItem = new JMenuItem("About");
        menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/menus/help.png")));
        menuItem.addActionListener( new AboutMuVisAction());
        menu.add(menuItem);
        menuBar.add(menu);
    }

    /**
     * Saves the current dock configuration.
     * @throws java.io.IOException
     */
    public void saveDocking() throws IOException {
        saveDocking(Environment.getEnvironmentInstance().getDataFolderPath() + "desk.xml");
    }

    /**
     * Saves the current dock configuration, to a specified file.
     * @param filename
     * @throws java.io.IOException
     */
    public void saveDocking(String filename) throws IOException {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
            DockingDesktop desk = Environment.getEnvironmentInstance().getDesk();
            desk.writeXML(out);
            out.close(); // stream isn't closed in case you'd like to save something else after
            System.out.println("File saved!");
        } catch (IOException ioe) {
            // process exception here
            System.err.println("Couldn't save the file!");
            throw ioe;
        }
    }

    /**
     * Loads the dock configuration, if one exists
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void loadDocking() throws FileNotFoundException, IOException {
        loadDocking(Environment.getEnvironmentInstance().getDataFolderPath() + "desk.xml");
    }

    /**
     * Loads the dock configuration from a specified file, if one exists
     * @param filename
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void loadDocking(String filename) throws FileNotFoundException, IOException {
        try {
            // first : declare the dockables to the desktop (they will be in the "closed" dockable state).
            DockingDesktop desk = Environment.getEnvironmentInstance().getDesk();
            desk.registerDockable(mainView);
            desk.registerDockable(musicPlayerView);
            desk.registerDockable(filesystemView);
            desk.registerDockable(playlistView);

            BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
            // then, load the workspace
            desk.readXML(in);

            in.close(); // stream isn't closed

            System.out.println("File opened!");
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
    }

    
}