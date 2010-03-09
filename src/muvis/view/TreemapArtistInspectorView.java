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

import java.awt.Color;
import muvis.view.table.ColorCellRenderer;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import muvis.Elements;
import muvis.NBTreeManager;
import muvis.Environment;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.SimilarityTableFilter;
import muvis.util.LoweredBevelBorderExtended;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.controllers.ListViewTableViewController;
import muvis.view.main.MuVisTreemapNode;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapFilterManager;
import muvis.view.main.filters.TreemapSimilarityFilter;
import muvis.view.table.JTableMouseAdapter;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;

/**
 *
 * @author Ricardo
 */
public class TreemapArtistInspectorView extends TreemapArtistInspectorViewUI implements ActionListener, Observer {

    private ArtistInspectorTracksTableModel model;
    private TreemapFilterManager filterManager;
    private MusicLibraryDatabaseManager dbManager;
    private ExecutorService threadPool;
    private ListViewTableViewController controller;
    private ArrayList<String> artistAlbums;
    private Hashtable<Integer, ArrayList<String>> pagedAlbuns;
    private int albumsPage;
    private TableRowSorter<ArtistInspectorTracksTableModel> sorter;
    private ArrayList<String> selectedAlbumsToFilter;

    public TreemapArtistInspectorView(final JFrame parent) {
        filterManager = Environment.getEnvironmentInstance().getTreemapFilterManager();
        filterManager.registerObserver(this);
        artistAlbums = new ArrayList<String>();
        pagedAlbuns = new Hashtable<Integer, ArrayList<String>>();
        albumsPage = 1;
        selectedAlbumsToFilter = new ArrayList<String>();

        threadPool = Executors.newFixedThreadPool(1);
        dbManager = Environment.getEnvironmentInstance().getDatabaseManager();

        controller = new ListViewTableViewController();

        tracksTableArtistInspector.getTableHeader().setReorderingAllowed(false);

        model = new ArtistInspectorTracksTableModel(new ArrayList<Integer>());
        tracksTableArtistInspector.setModel(model);
        tracksTableArtistInspector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tracksTableArtistInspector.setRowSelectionAllowed(true);
        sorter = new TableRowSorter<ArtistInspectorTracksTableModel>(model);
        tracksTableArtistInspector.setRowSorter(sorter);
        tracksTableArtistInspector.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tracksTableArtistInspector.setDefaultRenderer(Object.class, new ColorCellRenderer());

        //specific information about the columns
        TableColumn time = tracksTableArtistInspector.getColumn("Duration");
        time.setCellRenderer(new ColorCellRenderer());
        time.setPreferredWidth(60);
        time.setMaxWidth(60);
        time.setMinWidth(40);

        TableColumn trackNum = tracksTableArtistInspector.getColumn("Nr.");
        trackNum.setPreferredWidth(40);
        trackNum.setMaxWidth(60);
        trackNum.setCellRenderer(new ColorCellRenderer());

        TableColumn genreCol = tracksTableArtistInspector.getColumn("Genre");
        genreCol.setPreferredWidth(80);
        genreCol.setMaxWidth(150);

        seeAllArtistsButton.addActionListener(this);

        tracksTableArtistInspector.addMouseListener( new JTableMouseAdapter(controller));

        prevAlbumsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (albumsPage > 0) {
                    albumsPage--;
                }
                updateAlbumsDisplay();
            }
        });

        nextAlbumsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (albumsPage < pagedAlbuns.size()) {
                    albumsPage++;
                }
                updateAlbumsDisplay();
            }
        });

        class AlbumButtonListener implements ActionListener{

            protected JTextArea albumLabel;

            public AlbumButtonListener(JTextArea albumLabel){
                this.albumLabel = albumLabel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                String [] label = albumLabel.getText().split("\n");
                JButton albumButton = (JButton)e.getSource();
                if (albumButton.isSelected()) {
                    if (!selectedAlbumsToFilter.contains(label[0])) {
                        selectedAlbumsToFilter.add(label[0]);
                        filterAlbumsDisplayed();
                    }
                } else {
                    if (selectedAlbumsToFilter.contains(label[0])) {
                        selectedAlbumsToFilter.remove(label[0]);
                        filterAlbumsDisplayed();
                    }
                }
            }
        }

        class AlbumButton extends MouseAdapter {

            private JToggleButton button;
            private JTextArea label;

            public AlbumButton(JToggleButton button, JTextArea label) {
                this.button = button;
                this.label = label;
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger() && button.isEnabled()) {
                    Point p = new Point(e.getX(), e.getY());

                    // create popup menu...
                    JPopupMenu contextMenu = createContextMenu(label.getText());

                    // ... and show it
                    if (contextMenu != null && contextMenu.getComponentCount() > 0) {
                        contextMenu.show(button, p.x, p.y);
                    }
                }
            }

            private JPopupMenu createContextMenu(String albumName) {
                JPopupMenu contextMenu = new JPopupMenu();

                final String album = albumName;

                JMenuItem previewElementMenu = new JMenuItem();
                JMenuItem findSimilarElementMenu = new JMenuItem();
                JMenuItem findNonSimilarElementMenu = new JMenuItem();
                JMenuItem addElementToPlaylistMenu = new JMenuItem();
                JMenuItem closeMenu = new JMenuItem();

                if (selectedAlbumsToFilter.size() > 1) {
                    previewElementMenu.setText("Preview Albums");
                    findSimilarElementMenu.setText("Find Similar Albums");
                    findNonSimilarElementMenu.setText("Find Non Similar Albums");
                    addElementToPlaylistMenu.setText("Add Albums to Playlist");
                } else {
                    previewElementMenu.setText("Preview Album");
                    findSimilarElementMenu.setText("Find Similar Albums");
                    findNonSimilarElementMenu.setText("Find Non Similar Albums");
                    addElementToPlaylistMenu.setText("Add Album to Playlist");
                }

                closeMenu.setText("Close");

                contextMenu.add(previewElementMenu);
                contextMenu.add(findSimilarElementMenu);
                contextMenu.add(findNonSimilarElementMenu);
                contextMenu.add(addElementToPlaylistMenu);
                contextMenu.addSeparator();
                contextMenu.add(closeMenu);

                addElementToPlaylistMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        threadPool.execute(new Runnable() {

                            @Override
                            public void run() {
                                if (selectedAlbumsToFilter.size() > 0) {
                                    for (String albumSelected : selectedAlbumsToFilter) {
                                        String []albumProperties = albumSelected.split("\n");
                                        controller.addAlbumToPlaylist(artistNameLabel.getText(), albumProperties[0]);
                                    }
                                } else {
                                    String []albumProperties = album.split("\n");
                                    controller.addAlbumToPlaylist(artistNameLabel.getText(), albumProperties[0]);
                                }
                            }
                        });
                    }
                });

                previewElementMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (selectedAlbumsToFilter.size() > 1) {
                            ArrayList<Integer> ids = new ArrayList<Integer>(selectedAlbumsToFilter.size());
                            for (String albumN : selectedAlbumsToFilter) {
                                String []albumProperties = albumN.split("\n");
                                int id = Environment.getEnvironmentInstance().getDatabaseManager().getAlbumId(artistNameLabel.getText(), /*albumN*/ albumProperties[0]);
                                List trackIds = Environment.getEnvironmentInstance().getDatabaseManager().getAlbumTracksIds(id);
                                ids.addAll(trackIds);
                            }
                            Environment.getEnvironmentInstance().getSnippetManager().previewAlbums(ids);
                        } else {
                            String []albumProperties = album.split("\n");
                            int id = Environment.getEnvironmentInstance().getDatabaseManager().getAlbumId(artistNameLabel.getText(), /*album*/ albumProperties[0]);
                            List trackIds = Environment.getEnvironmentInstance().getDatabaseManager().getAlbumTracksIds(id);
                            ArrayList<Integer> ids = new ArrayList<Integer>(trackIds);
                            Environment.getEnvironmentInstance().getSnippetManager().previewAlbums(ids);
                        }
                    }
                });

                findSimilarElementMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "albuns");
                        final ArrayList<Integer> tracks = new ArrayList<Integer>();
                        ActionListener filter = new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {

                                int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                similarityDialog.dispose();
                                NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();
                                NBTree albumsNBTree = nbtreeManager.getNBTree("albumsNBTree");


                                if (selectedAlbumsToFilter.size() > 0) {


                                    for (String albumN : selectedAlbumsToFilter) {
                                        try {
                                            String [] albumProperties = albumN.split("\n");
                                            double albumKey = dbManager.getAlbumKey(artistNameLabel.getText(),/* albumN*/albumProperties[0]);//dbManager.getAlbumTrackKey(trackIds[i]);
                                            NBPoint albumPoint = albumsNBTree.lookupPoint(albumKey);
                                            BTree resultTree = albumsNBTree.knnQuery(albumPoint, numSimilarElements);
                                            TupleBrowser browser = resultTree.browse();
                                            Tuple tuple = new Tuple();
                                            while (browser.getNext(tuple)) {
                                                if (tuple.getValue() instanceof NBPoint) {
                                                    NBPoint point = (NBPoint) tuple.getValue();
                                                    int albumId = dbManager.getAlbumId(point.norm());
                                                    ArrayList<Integer> albumTracks = dbManager.getAlbumTracksIds(albumId);
                                                    tracks.addAll(albumTracks);
                                                }
                                            }

                                            TreemapSimilarityFilter filter = new TreemapSimilarityFilter(new NoFilter(), tracks);
                                            Environment.getEnvironmentInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                            Environment.getEnvironmentInstance().getTreemapFilterManager().filter();

                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        } catch (NBTreeException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else {
                                    try {
                                        String []albumProperties = album.split("\n");
                                        double albumKey = dbManager.getAlbumKey(artistNameLabel.getText(), albumProperties[0]);
                                        NBPoint albumPoint = albumsNBTree.lookupPoint(albumKey);
                                        BTree resultTree = albumsNBTree.knnQuery(albumPoint, numSimilarElements);
                                        TupleBrowser browser = resultTree.browse();
                                        Tuple tuple = new Tuple();
                                        while (browser.getNext(tuple)) {
                                            if (tuple.getValue() instanceof NBPoint) {
                                                NBPoint point = (NBPoint) tuple.getValue();
                                                int albumId = dbManager.getAlbumId(point.norm());
                                                ArrayList<Integer> albumTracks = dbManager.getAlbumTracksIds(albumId);
                                                tracks.addAll(albumTracks);
                                            }
                                        }

                                        TreemapSimilarityFilter filter = new TreemapSimilarityFilter(new NoFilter(), tracks);
                                        Environment.getEnvironmentInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                        Environment.getEnvironmentInstance().getTreemapFilterManager().filter();

                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    } catch (NBTreeException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        };
                        similarityDialog.addFilterListener(filter);
                        similarityDialog.setVisible(true);


                        SimilarityTableFilter filterS = new SimilarityTableFilter(tracks);
                        Environment.getEnvironmentInstance().getTableFilterManager().addTableFilter(filterS);
                        Environment.getEnvironmentInstance().getTableFilterManager().filter();

                        closeView();
                    }
                });

                findNonSimilarElementMenu.addActionListener( new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "albums");
                        final ArrayList<Integer> tracks = new ArrayList<Integer>();
                        ActionListener filter = new ActionListener() {

                            @Override
                            @SuppressWarnings("empty-statement")
                            public void actionPerformed(ActionEvent e) {

                                int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                similarityDialog.dispose();
                                NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();
                                NBTree albumsNBTree = nbtreeManager.getNBTree("albumsNBTree");


                                if (selectedAlbumsToFilter.size() > 0) {


                                    for (String albumN : selectedAlbumsToFilter) {
                                        try {
                                            String []albumProperties = albumN.split("\n");
                                            double albumKey = dbManager.getAlbumKey(artistNameLabel.getText(), albumProperties[0]);
                                            NBPoint albumPoint = albumsNBTree.lookupPoint(albumKey);
                                            BTree resultTree = albumsNBTree.knnQuery(albumPoint, dbManager.getCountAlbums());
                                            TupleBrowser browser = resultTree.browse();
                                            Tuple tuple = new Tuple();
                                            while (browser.getNext(tuple));
                                            for (int j = 0; browser.getPrevious(tuple) && j < numSimilarElements ; j++){
                                                if (tuple.getValue() instanceof NBPoint) {
                                                    NBPoint point = (NBPoint) tuple.getValue();
                                                    int albumId = dbManager.getAlbumId(point.norm());
                                                    ArrayList<Integer> albumTracks = dbManager.getAlbumTracksIds(albumId);
                                                    tracks.addAll(albumTracks);
                                                }
                                            }

                                            TreemapSimilarityFilter filter = new TreemapSimilarityFilter(new NoFilter(), tracks);
                                            Environment.getEnvironmentInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                            Environment.getEnvironmentInstance().getTreemapFilterManager().filter();

                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        } catch (NBTreeException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else {
                                    try {
                                        String []albumProperties = album.split("\n");
                                        double albumKey = dbManager.getAlbumKey(artistNameLabel.getText(), albumProperties[0]);
                                        NBPoint albumPoint = albumsNBTree.lookupPoint(albumKey);
                                        BTree resultTree = albumsNBTree.knnQuery(albumPoint, dbManager.getCountAlbums());
                                        TupleBrowser browser = resultTree.browse();
                                        Tuple tuple = new Tuple();
                                        while (browser.getNext(tuple));
                                        for (int j = 0; browser.getPrevious(tuple) && j < numSimilarElements ; j++){
                                            if (tuple.getValue() instanceof NBPoint) {
                                                NBPoint point = (NBPoint) tuple.getValue();
                                                int albumId = dbManager.getAlbumId(point.norm());
                                                ArrayList<Integer> albumTracks = dbManager.getAlbumTracksIds(albumId);
                                                tracks.addAll(albumTracks);
                                            }
                                        }

                                        TreemapSimilarityFilter filter = new TreemapSimilarityFilter(new NoFilter(), tracks);
                                        Environment.getEnvironmentInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                        Environment.getEnvironmentInstance().getTreemapFilterManager().filter();

                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    } catch (NBTreeException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        };
                        similarityDialog.addFilterListener(filter);
                        similarityDialog.setVisible(true);

                        SimilarityTableFilter filterS = new SimilarityTableFilter(tracks);
                        Environment.getEnvironmentInstance().getTableFilterManager().addTableFilter(filterS);
                        Environment.getEnvironmentInstance().getTableFilterManager().filter();

                        closeView();
                    }
                });

                return contextMenu;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }
        }

        albumButton1.addActionListener(new AlbumButtonListener(albumLabel1));
        albumButton2.addActionListener(new AlbumButtonListener(albumLabel2));
        albumButton3.addActionListener(new AlbumButtonListener(albumLabel3));
        albumButton4.addActionListener(new AlbumButtonListener(albumLabel4));

        albumButton1.addMouseListener(new AlbumButton(albumButton1, albumLabel1));
        albumButton2.addMouseListener(new AlbumButton(albumButton2, albumLabel2));
        albumButton3.addMouseListener(new AlbumButton(albumButton3, albumLabel3));
        albumButton4.addMouseListener(new AlbumButton(albumButton4, albumLabel4));
    }

    public void filterAlbumsDisplayed() {
        RowFilter<ArtistInspectorTracksTableModel, Object> albumFilter =
                new RowFilter<ArtistInspectorTracksTableModel, Object>() {

                    @Override
                    public boolean include(Entry<? extends ArtistInspectorTracksTableModel, ? extends Object> entry) {
                        if (entry.getValue(3) != null && selectedAlbumsToFilter.size() > 0) {
                            String albumRow = entry.getStringValue(3);

                            for (String albumFiltered : selectedAlbumsToFilter) {
                                if (artistAlbums.contains(albumFiltered)) {
                                    if (selectedAlbumsToFilter.contains(albumRow)) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        }
                        //the value is not in the range
                        return true;
                    }
                };
        sorter.setRowFilter(albumFilter);
    }

    public void viewArtist(String artistName) {

        artistNameLabel.setText(artistName);

        List tracks;

        if (!artistName.equals(Elements.OTHERS_NODE)){
            tracks = filterManager.getFilteredTracks(artistName);
        } else {

            MuVisTreemapNode fNode = Elements.othersNode;
            tracks = new ArrayList<Integer>();
            for(Enumeration children = fNode.children(); children.hasMoreElements(); ){
                MuVisTreemapNode n = (MuVisTreemapNode)children.nextElement();
                tracks.addAll(filterManager.getFilteredTracks(n.getName()));
            }
        }

        ArrayList<Integer> trackIds = new ArrayList<Integer>(tracks);
        model = new ArtistInspectorTracksTableModel(trackIds);
        tracksTableArtistInspector.setModel(model);
        sorter = new TableRowSorter<ArtistInspectorTracksTableModel>(model);
        tracksTableArtistInspector.setRowSorter(sorter);
        //specific information about the columns
        TableColumn time = tracksTableArtistInspector.getColumn("Duration");
        time.setCellRenderer(new ColorCellRenderer());
        time.setPreferredWidth(60);
        time.setMaxWidth(60);
        time.setMinWidth(40);

        TableColumn trackNum = tracksTableArtistInspector.getColumn("Nr.");
        trackNum.setPreferredWidth(40);
        trackNum.setMaxWidth(60);
        trackNum.setCellRenderer(new ColorCellRenderer());

        TableColumn genreCol = tracksTableArtistInspector.getColumn("Genre");
        genreCol.setPreferredWidth(80);
        genreCol.setMaxWidth(150);

        updateAlbumComponents(artistName);
    }

    private void updateAlbumComponents(String artistName) {

        List albums = filterManager.getFilteredAlbuns(artistName);

        artistAlbums = new ArrayList<String>(albums);
        pagedAlbuns.clear();

        ArrayList<String> pageAlbums = new ArrayList<String>();
        for (int i = 0, id = 0; i < artistAlbums.size(); i += 4, id++) {
            for (int j = i; j < artistAlbums.size() && j < i + 4; j++) {
                pageAlbums.add(artistAlbums.get(j));
            }
            pagedAlbuns.put(id, pageAlbums);
            pageAlbums = new ArrayList<String>();
        }

        if (pagedAlbuns.size() > 1) {
            nextAlbumsButton.setEnabled(true);
        } else {
            nextAlbumsButton.setEnabled(false);
        }

        albumsPage = 0;
        updateAlbumsDisplay();
    }

    private void updateAlbumsDisplay() {

        ArrayList<String> albumsToDisplay = pagedAlbuns.get(albumsPage);

        cleanDisplay();

        if (albumsPage == 0) {
            prevAlbumsButton.setEnabled(false);
        } else if (albumsPage > 0) {
            prevAlbumsButton.setEnabled(true);
        }

        if (albumsPage == (pagedAlbuns.size() - 1)) {
            nextAlbumsButton.setEnabled(false);
        } else if (pagedAlbuns.size() > 1) {
            nextAlbumsButton.setEnabled(true);
        }

        int i = 0;
        if (albumsToDisplay != null) {
            for (i = 0; i < albumsToDisplay.size(); i++) {
                String label = albumsToDisplay.get(i);
                label += "\n" + dbManager.getAlbumYear(artistNameLabel.getText(), albumsToDisplay.get(i));
                String [] albumProperties;
                switch (i) {
                    case 0:
                        albumLabel1.setText(label);
                        albumButton1.setEnabled(true);
                        albumLabel1.setEnabled(true);
                        albumProperties = albumLabel1.getText().split("\n");
                        if (selectedAlbumsToFilter.contains(albumProperties[0])) {
                            albumButton1.setSelected(true);
                        }
                        break;
                    case 1:
                        albumLabel2.setText(label);
                        albumButton2.setEnabled(true);
                        albumLabel2.setEnabled(true);
                        albumProperties = albumLabel2.getText().split("\n");
                        if (selectedAlbumsToFilter.contains(albumProperties[0])) {
                            albumButton2.setSelected(true);
                        }
                        break;
                    case 2:
                        albumLabel3.setText(label);
                        albumButton3.setEnabled(true);
                        albumLabel3.setEnabled(true);
                        albumProperties = albumLabel3.getText().split("\n");
                        if (selectedAlbumsToFilter.contains(albumProperties[0])) {
                            albumButton3.setSelected(true);
                        }
                        break;
                    case 3:
                        albumLabel4.setText(label);
                        albumButton4.setEnabled(true);
                        albumLabel4.setEnabled(true);
                        albumProperties = albumLabel4.getText().split("\n");
                        if (selectedAlbumsToFilter.contains(albumProperties[0])) {
                            albumButton4.setSelected(true);
                        }
                        break;
                }
            }
            filterAlbumsDisplayed();

            TreemapFilterManager fManager = Environment.getEnvironmentInstance().getTreemapFilterManager();
            String label = "Viewing ";
            if (albumsPage > 0){
                label += albumsPage * 4 + 1;
            } else {
                label += 1;
            }
            label += " - " + (albumsPage * 4 + pagedAlbuns.get(albumsPage).size()) + "/";
            label += artistAlbums.size() +" Albums, with " + fManager.getCountFilteredTracks(artistNameLabel.getText());
            label += " tracks";
            albumsInfoLabel.setText(label);
        }
    }

    private void cleanDisplay() {
        //label cleaning
        albumLabel1.setText("Not available");
        albumLabel1.setEnabled(false);
        albumLabel2.setText("Not available");
        albumLabel2.setEnabled(false);
        albumLabel3.setText("Not available");
        albumLabel3.setEnabled(false);
        albumLabel4.setText("Not available");
        albumLabel4.setEnabled(false);

        //button cleaning
        albumButton1.setEnabled(false);
        albumButton2.setEnabled(false);
        albumButton3.setEnabled(false);
        albumButton4.setEnabled(false);
        albumButton1.setSelected(false);
        albumButton2.setSelected(false);
        albumButton3.setSelected(false);
        albumButton4.setSelected(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == seeAllArtistsButton) {

            closeView();
        }
    }

    @Override
    public void update(Observable obs, Object arg) {
        viewArtist(artistNameLabel.getText());
    }

    private void closeView() {

        pagedAlbuns.clear();
        selectedAlbumsToFilter.clear();
        albumsPage = 0;

        MainViewHolder mainViewHolder = (MainViewHolder) Environment.getEnvironmentInstance().getViewManager().getView("mainView");
        mainViewHolder.setView("TreeMapView");
    }
}
