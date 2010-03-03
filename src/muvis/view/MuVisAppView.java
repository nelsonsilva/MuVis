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
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import muvis.Elements;
import muvis.Environment;
import muvis.audio.AudioMetadata;
import muvis.audio.MuVisAudioPlayer;
import muvis.audio.playlist.PlaylistItem;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.controllers.PlaylistController;
import muvis.view.controllers.PlaylistControllerInterface;
import muvis.view.controllers.ReloadLibraryController;
import muvis.view.loader.ReloadLibraryView;
import muvis.view.main.filters.TreemapFilterManager;
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
        this.setTitle("MuVis - Because Music Visualization matters");

        threadPool = Executors.newFixedThreadPool(1);
        new SystemTrayView();

        //init controllers
        playlistController = new PlaylistController();

        //init views
        musicPlayerView = new MusicControllerView(this);
        filesystemView = new DirectoryListTreeView();
        playlistView = new PlaylistView(this, playlistController);
        tracksViewTable = new ListViewTableView(this);
        treemapView = new TreemapView(this);
        mainView = new MainViewHolder(this);
        mainView.addView("ListView", tracksViewTable);
        mainView.addView("TreeMapView", treemapView);
        mainView.setView("ListView");
        mainView.initializeFilters();

        ViewManager viewManager = Environment.getWorkspaceInstance().getViewManager();
        viewManager.addView(Elements.MUSIC_PLAYER_VIEW, musicPlayerView);
        viewManager.addView(Elements.FILE_SYSTEM_VIEW, filesystemView);
        viewManager.addView(Elements.PLAYLIST_VIEW, playlistView);
        viewManager.addView(Elements.LIST_VIEW, tracksViewTable);
        viewManager.addView(Elements.TREEMAP_VIEW, treemapView);
        viewManager.addView(Elements.MAIN_VIEW, mainView);
        TreemapArtistInspectorView artistInspectorView = new TreemapArtistInspectorView(frame);
        viewManager.addView(Elements.ARTIST_INSPECTOR_VIEW, artistInspectorView);
        mainView.addView(Elements.ARTIST_INSPECTOR_VIEW, artistInspectorView);

        final DockingDesktop desk = Environment.getWorkspaceInstance().getDesk();

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

        //final JFrame frame = this;

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
                    Environment.getWorkspaceInstance().saveWorkspace();
                    //Exiting the application
                    System.exit(0);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        };

        //create custom close operation
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exitProcedure();
            }

            private void exitProcedure() {
                //Exiting the application
                closeApplication();
            }
        });
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

                JFileChooser fc = new JFileChooser(new File("C:\\"));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setDialogTitle("Save your workspace");
                FileFilter fFilter = new javax.swing.filechooser.FileNameExtensionFilter("Results file", "out");
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
                                TreemapFilterManager filterManager = Environment.getWorkspaceInstance().getTreemapFilterManager();
                                MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();

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
                                    Environment.getWorkspaceInstance().getAudioPlaylist().appendItem(pliItem);
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
        menu.add(menuItem);
        menuItem.addActionListener(saveMenuOptionListener);

        menuItem = new JMenuItem("Load workspace");
        menu.add(menuItem);
        menuItem.addActionListener(loadMenuOptionListener);

        menuItem = new JMenuItem("Reload Library");
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
        menu.add(menuItem);
        menuItem.addActionListener(exitMenuListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        menu = new JMenu("Playlist");
        menuBar.add(menu);

        menuItem = new JMenuItem("Generate Playlist");
        menu.add(menuItem);
        menuItem.addActionListener(generatePlaylist);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));

        menu = new JMenu("View");
        menuBar.add(menu);

        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Treemap View");
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
        menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/About16.gif"))); // NOI18N
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "MuVis - Because Music Visualization matters\nv0.1 @2009\n Created by Ricardo Dias";
                JOptionPane.showMessageDialog(frame, message,
                        "About MuVis", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
    }

    protected void closeApplication() {
        try {
            //saving the state of the application
            Environment.getWorkspaceInstance().saveWorkspace();
            saveDocking();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Couldn't save the desk disposition!");
        }
        //Exiting the application
        System.exit(0);
    }

    /**
     * Saves the current dock configuration.
     * @throws java.io.IOException
     */
    public void saveDocking() throws IOException {
        saveDocking(Environment.getWorkspaceInstance().getDataFolderPath() + "desk.xml");
    }

    /**
     * Saves the current dock configuration, to a specified file.
     * @param filename
     * @throws java.io.IOException
     */
    public void saveDocking(String filename) throws IOException {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));
            DockingDesktop desk = Environment.getWorkspaceInstance().getDesk();
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
        loadDocking(Environment.getWorkspaceInstance().getDataFolderPath() + "desk.xml");
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
            DockingDesktop desk = Environment.getWorkspaceInstance().getDesk();
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

    class SystemTrayView {

        public SystemTrayView() {

            //Check the SystemTray is supported
            if (!SystemTray.isSupported()) {
                System.out.println("SystemTray is not supported");
                return;
            }
            final PopupMenu popup = new PopupMenu();
            final TrayIcon trayIcon =
                    new TrayIcon(createImage("/images/logo.png", "MuVis - Because Music Visualization matters"));
            final SystemTray tray = SystemTray.getSystemTray();

            // Create a pop-up menu components
            MenuItem aboutItem = new MenuItem("About");
            MenuItem displayMenu = new MenuItem("Hide");
            MenuItem playItem = new MenuItem("Play");
            MenuItem stopItem = new MenuItem("Stop");
            MenuItem nextTrackItem = new MenuItem("Next track");
            MenuItem prevTrackItem = new MenuItem("Prev track");
            MenuItem exitItem = new MenuItem("Exit");

            exitItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    closeApplication();
                }
            });

            class PlayPauseListener implements ActionListener, Observer {

                MusicControllerView controller;
                MenuItem item;

                PlayPauseListener(MenuItem item){
                    
                    Environment.getWorkspaceInstance().getAudioPlayer().registerObserver(this);
                    this.item = item;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (controller == null){
                        controller = (MusicControllerView) Environment.getWorkspaceInstance().getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);
                    }
                    controller.playTrack();
                }

                @Override
                public void update(Observable obs, Object arg) {
                    if (obs instanceof MuVisAudioPlayer){
                        MuVisAudioPlayer player = (MuVisAudioPlayer)obs;

                        if (player.isPlaying()){
                            item.setLabel("Pause");
                        } else item.setLabel("Play");
                    }
                }
            }

            playItem.addActionListener(new PlayPauseListener(playItem));

            stopItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    MusicControllerView controller =
                            (MusicControllerView) Environment.getWorkspaceInstance().getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);

                    controller.stopPlayer();
                }
            });

            prevTrackItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    MusicControllerView controller =
                            (MusicControllerView) Environment.getWorkspaceInstance().getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);

                    controller.playPreviousTrack();
                }
            });

            nextTrackItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    MusicControllerView controller =
                            (MusicControllerView) Environment.getWorkspaceInstance().getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);

                    controller.playNextTrack();
                }
            });

            aboutItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = "MuVis - Because Music Visualization matters\nv1.0 @2009\n Created by Ricardo Dias";
                    JOptionPane.showMessageDialog(frame, message,
                            "About MuVis", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            displayMenu.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    boolean visibility = frame.isVisible();
                    visibility = !visibility;

                    if (e.getSource() instanceof MenuItem) {
                        MenuItem item = (MenuItem) e.getSource();
                        if (visibility) {
                            item.setLabel("Hide");
                        } else {
                            item.setLabel("Show");
                        }
                    }
                    frame.setVisible(visibility);
                }
            });

            //Add components to pop-up menu
            popup.add(aboutItem);
            popup.add(displayMenu);
            popup.addSeparator();
            popup.add(playItem);
            popup.add(stopItem);
            popup.add(nextTrackItem);
            popup.add(prevTrackItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }

        }

        //Obtain the image URL
        protected Image createImage(String path, String description) {
            URL imageURL = SystemTrayView.class.getResource(path);

            if (imageURL == null) {
                System.err.println("Resource not found: " + path);
                return null;
            } else {
                return (new ImageIcon(imageURL, description)).getImage();
            }
        }
    }
}
