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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import muvis.NBTreeManager;
import muvis.Environment;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.SimilarityTableFilter;
import muvis.util.Util;
import muvis.view.controllers.ListViewTableViewController;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapAlbumSimilarityFilter;
import muvis.view.main.filters.TreemapArtistSimilarityFilter;
import muvis.view.main.filters.TreemapTrackSimilarityFilter;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;

/**
 * Simple list visualization of the library.
 * Implemented by a jtable.
 * @author Ricardo
 */
public class ListViewTableView extends ListViewTableUI implements View {

    private MusicLibraryDatabaseManager dbManager;
    private ExecutorService threadPool;
    private TableRowSorter<TracksTableModel> sorter;
    private ListViewTableViewController controller;
    private TracksTableModel model;

    public ListViewTableView(final JFrame parent) {
        dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        controller = new ListViewTableViewController();
        threadPool = Executors.newFixedThreadPool(1);

        model = new TracksTableModel();

        tracksTableView.setModel(model);
        tracksTableView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sorter = new TableRowSorter<TracksTableModel>(model);
        tracksTableView.setRowSorter(sorter);
        tracksTableView.setRowSelectionAllowed(true);
        tracksTableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tracksTableView.setDefaultRenderer(Object.class, new ColorCellRenderer());

        //specific information about the columns
        TableColumn time = tracksTableView.getColumn("Duration");
        time.setCellRenderer(new ColorCellRenderer());
        time.setPreferredWidth(60);
        time.setMaxWidth(60);
        time.setMinWidth(40);

        TableColumn trackNum = tracksTableView.getColumn("Nr.");
        trackNum.setPreferredWidth(40);
        trackNum.setMaxWidth(60);
        trackNum.setCellRenderer(new ColorCellRenderer());

        TableColumn genreCol = tracksTableView.getColumn("Genre");
        genreCol.setPreferredWidth(80);
        genreCol.setMaxWidth(150);

        TableColumn year = tracksTableView.getColumn("Year");
        tracksTableView.removeColumn(year);

        TableColumn beat = tracksTableView.getColumn("Beat");
        tracksTableView.removeColumn(beat);

        TableColumn mood = tracksTableView.getColumn("Mood");
        tracksTableView.removeColumn(mood);

        tracksTableView.addMouseListener(new MouseAdapter() {

            private int lastSelectedRow = 0;

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger() && tracksTableView.isEnabled()) {
                    Point p = new Point(e.getX(), e.getY());
                    int col = tracksTableView.columnAtPoint(p);
                    int row = tracksTableView.rowAtPoint(p);

                    lastSelectedRow = row;

                    // translate table index to model index
                    int mcol = tracksTableView.getColumn(
                            tracksTableView.getColumnName(col)).getModelIndex();

                    if (row >= 0 && row < tracksTableView.getRowCount()) {

                        // create popup menu...
                        JPopupMenu contextMenu = createContextMenu(row,
                                mcol);

                        // Get the ListSelectionModel of the JTable
                        if (tracksTableView.getSelectedRowCount() <= 1) {
                            ListSelectionModel model = tracksTableView.getSelectionModel();

                            // set the selected interval of rows. Using the "rowNumber"
                            // variable for the beginning and end selects only that one row.
                            model.setSelectionInterval(row, row);
                        }

                        // ... and show it
                        if (contextMenu != null && contextMenu.getComponentCount() > 0) {
                            contextMenu.show(tracksTableView, p.x, p.y);
                        }
                    }
                }
            }

            private JPopupMenu createContextMenu(int rowIndex, int columnIndex) {
                JPopupMenu contextMenu = new JPopupMenu();

                JMenuItem previewElementMenu = new JMenuItem();
                JMenuItem findSimilarElementMenu = new JMenuItem();
                JMenuItem findNonSimilarElementMenu = new JMenuItem();
                JMenuItem addElementToPlaylistMenu = new JMenuItem();
                JMenuItem closeMenu = new JMenuItem();

                String colName = tracksTableView.getModel().getColumnName(columnIndex);
                if (colName.equals("Track name")) {
                    if (tracksTableView.getSelectedRowCount() <= 1) {
                        previewElementMenu.setText("Preview Track");
                        findSimilarElementMenu.setText("Find Similar Tracks");
                        findNonSimilarElementMenu.setText("Find Non Similar Tracks");
                        addElementToPlaylistMenu.setText("Add Track to Playlist");
                    } else {
                        previewElementMenu.setText("Preview Tracks");
                        findSimilarElementMenu.setText("Find Similar Tracks");
                        findNonSimilarElementMenu.setText("Find Non Similar Tracks");
                        addElementToPlaylistMenu.setText("Add Tracks to Playlist");
                    }
                } else if (colName.equals("Artist")) {
                    if (tracksTableView.getSelectedRowCount() <= 1) {
                        previewElementMenu.setText("Preview Artist");
                        findSimilarElementMenu.setText("Find Similar Artists");
                        findNonSimilarElementMenu.setText("Find Non Similar Artists");
                        addElementToPlaylistMenu.setText("Add Artist to Playlist");
                    } else {
                        previewElementMenu.setText("Preview Artists");
                        findSimilarElementMenu.setText("Find Similar Artists");
                        findNonSimilarElementMenu.setText("Find Non Similar Artists");
                        addElementToPlaylistMenu.setText("Add Artists to Playlist");
                    }
                } else if (colName.equals("Album")) {
                    if (tracksTableView.getSelectedRowCount() <= 1) {
                        previewElementMenu.setText("Preview Album");
                        findSimilarElementMenu.setText("Find Similar Albums");
                        findNonSimilarElementMenu.setText("Find Non Similar Albums");
                        addElementToPlaylistMenu.setText("Add Album to Playlist");
                    } else {
                        previewElementMenu.setText("Preview Albums");
                        findSimilarElementMenu.setText("Find Similar Albums");
                        findNonSimilarElementMenu.setText("Find Non Similar Albums");
                        addElementToPlaylistMenu.setText("Add Albums to Playlist");
                    }
                } else {
                    return contextMenu;
                }

                closeMenu.setText("Close");

                findNonSimilarElementMenu.addActionListener( new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() instanceof JMenuItem) {
                            JMenuItem item = (JMenuItem) e.getSource();

                            final ArrayList<Integer> tracks = new ArrayList();
                            final int[] trackIds = tracksTableView.getSelectedRows();
                            final NBTreeManager nbtreeManager = Environment.getWorkspaceInstance().getNbtreesManager();

                            if (item.getText().contains("Track")){

                                final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "tracks");
                                ActionListener filter = new ActionListener() {

                                    @Override
                                    @SuppressWarnings("empty-statement")
                                    public void actionPerformed(ActionEvent e) {
                                        for (int i = 0; i < trackIds.length; i++) {
                                            try {
                                                int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                                similarityDialog.dispose();

                                                NBTree trackNBTree = nbtreeManager.getNBTree("tracksNBTree");
                                                double trackKey = dbManager.getTrackKey(trackIds[i]);
                                                if (trackKey < 0){
                                                    Util.displayErrorMessage(parent, "Similarity Filter", "Can't get non-similar tracks!Try later!");
                                                    break;
                                                }
                                                NBPoint trackPoint = trackNBTree.lookupPoint(trackKey);
                                                BTree resultTree = trackNBTree.knnQuery(trackPoint, dbManager.getCountTracks());
                                                TupleBrowser browser = resultTree.browse();
                                                Tuple tuple = new Tuple();
                                                while(browser.getNext(tuple));
                                                for (int j = 0 ; browser.getPrevious(tuple) && j < numSimilarElements ; j++ ) {
                                                    if (tuple.getValue() instanceof NBPoint) {
                                                        NBPoint point = (NBPoint) tuple.getValue();
                                                        int track = dbManager.getTrackId(point.norm());
                                                        tracks.add(track);
                                                    }
                                                }

                                                TreemapTrackSimilarityFilter filter = new TreemapTrackSimilarityFilter(new NoFilter(), tracks);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().filter();

                                            } catch (IOException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (NBTreeException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                };

                                similarityDialog.addFilterListener(filter);
                                similarityDialog.setVisible(true);

                            } else if (item.getText().contains("Album")){

                                final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "albuns");

                                ActionListener filter = new ActionListener() {

                                    @Override
                                    @SuppressWarnings("empty-statement")
                                    public void actionPerformed(ActionEvent e) {

                                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                        similarityDialog.dispose();

                                        NBTree albumsNBTree = nbtreeManager.getNBTree("albumsNBTree");
                                        for (int i = 0; i < trackIds.length; i++) {
                                            try {
                                                double albumKey = dbManager.getAlbumTrackKey(trackIds[i]);
                                                if (albumKey < 0){
                                                    Util.displayErrorMessage(parent, "Similarity Filter", "Can't get non-similar albums!Try later!");
                                                    break;
                                                }
                                                NBPoint albumPoint = albumsNBTree.lookupPoint(albumKey);
                                                BTree resultTree = albumsNBTree.knnQuery(albumPoint, dbManager.getCountAlbums());
                                                TupleBrowser browser = resultTree.browse();
                                                Tuple tuple = new Tuple();
                                                while(browser.getNext(tuple));
                                                for (int j = 0 ; browser.getPrevious(tuple) && j < numSimilarElements ; j++ ) {
                                                    if (tuple.getValue() instanceof NBPoint) {
                                                        NBPoint point = (NBPoint) tuple.getValue();
                                                        int albumId = dbManager.getAlbumId(point.norm());
                                                        ArrayList<Integer> albumTracks = dbManager.getAlbumTracksIds(albumId);
                                                        tracks.addAll(albumTracks);
                                                    }
                                                }

                                                TreemapAlbumSimilarityFilter filter = new TreemapAlbumSimilarityFilter(new NoFilter(), tracks);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().filter();

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

                            } else if (item.getText().contains("Artist")){

                                final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "artists");

                                ActionListener filter = new ActionListener() {

                                    @Override
                                    @SuppressWarnings("empty-statement")
                                    public void actionPerformed(ActionEvent e) {

                                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                        similarityDialog.dispose();

                                        NBTree artistNBTree = nbtreeManager.getNBTree("artistsNBTree");
                                        ArrayList<String> artistNames = new ArrayList<String>();
                                        for (int i = 0; i < trackIds.length; i++) {
                                            try {
                                                double artistKey = dbManager.getArtistTrackKey(trackIds[i]);
                                                if (artistKey < 0){
                                                    Util.displayErrorMessage(parent, "Similarity Filter", "Can't get non-similar artists!Try later!");
                                                    break;
                                                }
                                                NBPoint artistPoint = artistNBTree.lookupPoint(artistKey);
                                                BTree resultTree = artistNBTree.knnQuery(artistPoint, dbManager.getCountArtists());
                                                TupleBrowser browser = resultTree.browse();
                                                Tuple tuple = new Tuple();
                                                while (browser.getNext(tuple));
                                                for (int j = 0; browser.getPrevious(tuple) && j < numSimilarElements ; j++){
                                                    if (tuple.getValue() instanceof NBPoint) {
                                                        NBPoint point = (NBPoint) tuple.getValue();
                                                        String artistName = dbManager.getArtistName(point.norm());
                                                        ArrayList<Integer> artistTracks = dbManager.getArtistTracksIds(artistName);
                                                        tracks.addAll(artistTracks);
                                                        artistNames.add(artistName);
                                                    }
                                                }

                                                TreemapArtistSimilarityFilter filter = new TreemapArtistSimilarityFilter(new NoFilter(), artistNames);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().filter();

                                            } catch (IOException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (NBTreeException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                };

                                similarityDialog.addFilterListener(filter);
                                similarityDialog.setVisible(true);
                            }

                            SimilarityTableFilter filter = new SimilarityTableFilter(tracks);
                            Environment.getWorkspaceInstance().getTableFilterManager().addTableFilter(filter);
                            Environment.getWorkspaceInstance().getTableFilterManager().filter();
                        }
                    }
                });

                findSimilarElementMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        if (e.getSource() instanceof JMenuItem) {
                            JMenuItem item = (JMenuItem) e.getSource();

                            final ArrayList<Integer> tracks = new ArrayList();
                            final int[] trackIds = tracksTableView.getSelectedRows();
                            final NBTreeManager nbtreeManager = Environment.getWorkspaceInstance().getNbtreesManager();
                            if (item.getText().contains("Track")) { //searching for similar tracks

                                final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "tracks");
                                ActionListener filter = new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        for (int i = 0; i < trackIds.length; i++) {
                                            try {
                                                int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                                similarityDialog.dispose();

                                                NBTree trackNBTree = nbtreeManager.getNBTree("tracksNBTree");
                                                double trackKey = dbManager.getTrackKey(trackIds[i]);
                                                if (trackKey < 0){
                                                    Util.displayErrorMessage(parent, "Similarity Filter", "Can't get similar tracks!Try later!");
                                                    break;
                                                }
                                                NBPoint trackPoint = trackNBTree.lookupPoint(trackKey);
                                                BTree resultTree = trackNBTree.knnQuery(trackPoint, numSimilarElements);
                                                TupleBrowser browser = resultTree.browse();
                                                Tuple tuple = new Tuple();
                                                while (browser.getNext(tuple)) {
                                                    if (tuple.getValue() instanceof NBPoint) {
                                                        NBPoint point = (NBPoint) tuple.getValue();
                                                        int track = dbManager.getTrackId(point.norm());
                                                        tracks.add(track);
                                                    }
                                                }

                                                TreemapTrackSimilarityFilter filter = new TreemapTrackSimilarityFilter(new NoFilter(), tracks);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().filter();

                                            } catch (IOException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (NBTreeException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                };

                                similarityDialog.addFilterListener(filter);
                                similarityDialog.setVisible(true);


                            } else if (item.getText().contains("Album")) { //searching for similar albums

                                final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "albuns");

                                ActionListener filter = new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                        similarityDialog.dispose();

                                        NBTree albumsNBTree = nbtreeManager.getNBTree("albumsNBTree");
                                        for (int i = 0; i < trackIds.length; i++) {
                                            try {
                                                double albumKey = dbManager.getAlbumTrackKey(trackIds[i]);
                                                if (albumKey < 0){
                                                    Util.displayErrorMessage(parent, "Similarity Filter", "Can't get similar albums!Try later!");
                                                    break;
                                                }
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

                                                TreemapAlbumSimilarityFilter filter = new TreemapAlbumSimilarityFilter(new NoFilter(), tracks);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().filter();

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

                            } else if (item.getText().contains("Artist")) { //searching for similar artists

                                final SimilarElementsView similarityDialog = new SimilarElementsView(parent, "artists");

                                ActionListener filter = new ActionListener() {

                                    @Override
                                    public void actionPerformed(ActionEvent e) {

                                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                        similarityDialog.dispose();

                                        NBTree artistNBTree = nbtreeManager.getNBTree("artistsNBTree");
                                        ArrayList<String> artistNames = new ArrayList<String>();
                                        for (int i = 0; i < trackIds.length; i++) {
                                            try {
                                                double artistKey = dbManager.getArtistTrackKey(trackIds[i]);
                                                if (artistKey < 0){
                                                    Util.displayErrorMessage(parent, "Similarity Filter", "Can't get similar artists!Try later!");
                                                    break;
                                                }
                                                NBPoint artistPoint = artistNBTree.lookupPoint(artistKey);
                                                BTree resultTree = artistNBTree.knnQuery(artistPoint, numSimilarElements);
                                                TupleBrowser browser = resultTree.browse();
                                                Tuple tuple = new Tuple();
                                                while (browser.getNext(tuple)) {
                                                    if (tuple.getValue() instanceof NBPoint) {
                                                        NBPoint point = (NBPoint) tuple.getValue();
                                                        String artistName = dbManager.getArtistName(point.norm());
                                                        ArrayList<Integer> artistTracks = dbManager.getArtistTracksIds(artistName);
                                                        tracks.addAll(artistTracks);
                                                        artistNames.add(artistName);
                                                    }
                                                }

                                                TreemapArtistSimilarityFilter filter = new TreemapArtistSimilarityFilter(new NoFilter(), artistNames);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().addTreemapFilter(filter);
                                                Environment.getWorkspaceInstance().getTreemapFilterManager().filter();

                                            } catch (IOException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (NBTreeException ex) {
                                                Logger.getLogger(ListViewTableView.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                };

                                similarityDialog.addFilterListener(filter);
                                similarityDialog.setVisible(true);


                            }

                            SimilarityTableFilter filter = new SimilarityTableFilter(tracks);
                            Environment.getWorkspaceInstance().getTableFilterManager().addTableFilter(filter);
                            Environment.getWorkspaceInstance().getTableFilterManager().filter();
                        }
                    }
                });

                addElementToPlaylistMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() instanceof JMenuItem) {
                            JMenuItem item = (JMenuItem) e.getSource();

                            if (item.getText().contains("Track")) {
                                //add a track to playlist
                                threadPool.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        //add album to playlist
                                        int[] rows = tracksTableView.getSelectedRows();
                                        for (int i = 0; i < rows.length; i++) {
                                            int rowModel = tracksTableView.getRowSorter().convertRowIndexToModel(rows[i]);
                                            int id = (Integer)model.getValueAt(rowModel, 0);
                                            controller.addTrackToPlaylist(id, parent);
                                        }
                                    }
                                });

                            } else if (item.getText().contains("Album")) {
                                threadPool.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        //add album to playlist
                                        int[] rows = tracksTableView.getSelectedRows();
                                        for (int i = 0; i < rows.length; i++) {
                                            int rowModel = tracksTableView.getRowSorter().convertRowIndexToModel(rows[i]);
                                            int id = (Integer)model.getValueAt(rowModel, 0);
                                            controller.addAlbumToPlaylist(id, parent);
                                        }
                                    }
                                });

                            } else if (item.getText().contains("Artist")) {
                                threadPool.execute(new Runnable() {

                                    @Override
                                    public void run() {
                                        //add artist to playlist
                                        int[] rows = tracksTableView.getSelectedRows();
                                        for (int i = 0; i < rows.length; i++) {
                                            int rowModel = tracksTableView.getRowSorter().convertRowIndexToModel(rows[i]);
                                            int id = (Integer)model.getValueAt(rowModel, 0);
                                            controller.addArtistToPlaylist(id, parent);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

                previewElementMenu.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() instanceof JMenuItem) {
                            JMenuItem item = (JMenuItem) e.getSource();

                            if (item.getText().contains("Preview Track")) {
                                //preview of a track
                                if (tracksTableView.getSelectedRowCount() <= 1) {
                                    int row = tracksTableView.getRowSorter().convertRowIndexToModel(lastSelectedRow);
                                    int trackId = (Integer)model.getValueAt(row, 0);
                                    Environment.getWorkspaceInstance().getSnippetManager().previewTrack(trackId);
                                } else {
                                    ArrayList<Integer> trackIds = new ArrayList<Integer>(tracksTableView.getSelectedRowCount());
                                    for (int row : tracksTableView.getSelectedRows()) {
                                        int rowModel = tracksTableView.getRowSorter().convertRowIndexToModel(row);
                                        int id = (Integer)model.getValueAt(rowModel, 0);
                                        trackIds.add(id);
                                    }
                                    Environment.getWorkspaceInstance().getSnippetManager().previewTracks(trackIds);
                                }

                            } else if (item.getText().contains("Album")) {
                                //preview of an album
                                if (tracksTableView.getSelectedRowCount() <= 1) {
                                    int row = tracksTableView.getRowSorter().convertRowIndexToModel(lastSelectedRow);
                                    int trackId = (Integer)model.getValueAt(row, 0);
                                    Environment.getWorkspaceInstance().getSnippetManager().previewAlbum(trackId);
                                } else {
                                    ArrayList<Integer> trackIds = new ArrayList<Integer>(tracksTableView.getSelectedRowCount());
                                    for (int row : tracksTableView.getSelectedRows()) {
                                        int rowModel = tracksTableView.getRowSorter().convertRowIndexToModel(row);
                                        int id = (Integer)model.getValueAt(rowModel, 0);
                                        trackIds.add(id);
                                    }
                                    Environment.getWorkspaceInstance().getSnippetManager().previewAlbums(trackIds);
                                }

                            } else if (item.getText().contains("Artist")) {
                                //preview of an artist
                                if (tracksTableView.getSelectedRowCount() <= 1) {
                                    int row = tracksTableView.getRowSorter().convertRowIndexToModel(lastSelectedRow);
                                    int trackId = (Integer)model.getValueAt(row, 0);
                                    Environment.getWorkspaceInstance().getSnippetManager().previewArtist(trackId);
                                } else {
                                    ArrayList<Integer> trackIds = new ArrayList<Integer>(tracksTableView.getSelectedRowCount());
                                    for (int row : tracksTableView.getSelectedRows()) {
                                        int rowModel = tracksTableView.getRowSorter().convertRowIndexToModel(row);
                                        int id = (Integer)model.getValueAt(rowModel, 0);
                                        trackIds.add(id);
                                    }
                                    Environment.getWorkspaceInstance().getSnippetManager().previewArtists(trackIds);
                                }

                            }

                        }
                    }
                });

                contextMenu.add(previewElementMenu);
                contextMenu.add(findSimilarElementMenu);
                contextMenu.add(findNonSimilarElementMenu);
                contextMenu.add(addElementToPlaylistMenu);
                contextMenu.addSeparator();
                contextMenu.add(closeMenu);

                return contextMenu;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
        });
    }

    /**
     * Returns the Sorter of this encapsulated JTable.
     * @return the sorter
     */
    public TableRowSorter<TracksTableModel> getSorter() {
        return sorter;
    }
}