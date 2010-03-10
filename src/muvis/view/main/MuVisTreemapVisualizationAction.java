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
 
package muvis.view.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import muvis.Elements;
import muvis.NBTreeManager;
import muvis.Environment;
import muvis.Messages;
import muvis.audio.AudioMetadata;
import muvis.audio.playlist.PlaylistItem;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.SimilarityTableFilter;
import muvis.util.Util;
import muvis.view.MainViewHolder;
import muvis.view.SimilarElementsView;
import muvis.view.TreemapArtistInspectorView;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapFilterManager;
import muvis.view.main.filters.TreemapSimilarityFilter;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;
import net.bouthier.treemapSwing.TMAction;
import net.bouthier.treemapSwing.TMView;

/**
 * Treemap UI interaction handler
 * @author Ricardo
 */
public class MuVisTreemapVisualizationAction extends TMAction {

    private ExecutorService threadPool;
    private MuVisTreemapNode nodeUnder;
    private JFrame parentFrame;
    private MainViewHolder mainViewHolder;
    private TreemapArtistInspectorView artistInspector;
    private ArrayList<MuVisTreemapNode> selectedNodes;

    public MuVisTreemapVisualizationAction(TMView view, JFrame frame) {
        super(view);
        threadPool = Executors.newFixedThreadPool(1);
        parentFrame = frame;
        selectedNodes = new ArrayList<MuVisTreemapNode>();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        Object node = view.getNodeUnderTheMouse(e);
        if (node != null) {
            if (e.isControlDown() && e.isShiftDown()) {
                view.zoom(e.getX(), e.getY());
            } else  if (e.isShiftDown()) {
                view.unzoom();
            }

            MuVisTreemapNode fNode = (MuVisTreemapNode) node;
            if (e.getClickCount() == 2) {
                if (artistInspector == null) {
                    mainViewHolder = (MainViewHolder) Environment.getEnvironmentInstance().getViewManager().getView(Elements.MAIN_VIEW);
                    artistInspector = (TreemapArtistInspectorView) mainViewHolder.getView(Elements.ARTIST_INSPECTOR_VIEW);
                }

                for (MuVisTreemapNode sNode : selectedNodes) {
                    sNode.setSelected(false);
                }
                selectedNodes.clear();
                //must view the artist inspector
                artistInspector.viewArtist(fNode.getName());
                mainViewHolder.setView("artistInspector");
            }

            if (!SwingUtilities.isRightMouseButton(e) && e.getClickCount() < 2){
            if (e.isControlDown()) {
                if (fNode.isSelected()) {
                    fNode.setSelected(false);
                    selectedNodes.remove(fNode);
                } else {
                    fNode.setSelected(true);
                    selectedNodes.add(fNode);
                    }
                } else {

                    if (fNode.isSelected()) {
                        fNode.setSelected(false);
                        selectedNodes.remove(fNode);

                    } else {

                        for (MuVisTreemapNode sNode : selectedNodes) {
                            sNode.setSelected(false);
                        }
                        selectedNodes.clear();

                        fNode.setSelected(true);
                        selectedNodes.add(fNode);
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    public void showPopup(MouseEvent e) {
        Object node = view.getNodeUnderTheMouse(e);
        if (node != null && e.isPopupTrigger()) {
            MuVisTreemapNode fNode = (MuVisTreemapNode) node;
            nodeUnder = fNode;
            JPopupMenu contextMenu = createContextMenu();

            //show the menu
            if (contextMenu != null && contextMenu.getComponentCount() > 0) {
                contextMenu.show(view, e.getX(), e.getY());
            }
        }
    }

    private JPopupMenu createContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem previewElementMenu = new JMenuItem();
        JMenuItem findSimilarElementMenu = new JMenuItem();
        JMenuItem findNonSimilarElementMenu = new JMenuItem();
        JMenuItem addElementToPlaylistMenu = new JMenuItem();
        JMenuItem closeMenu = new JMenuItem();

        if (selectedNodes.isEmpty() || selectedNodes.size() == 1) {
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
        closeMenu.setText("Close");

        findNonSimilarElementMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JMenuItem) {
                    JMenuItem item = (JMenuItem) e.getSource();

                    final ArrayList<Integer> tracks = new ArrayList();
                    final NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();

                    if (item.getText().contains("Artist")) {

                        final SimilarElementsView similarityDialog = new SimilarElementsView(parentFrame, Messages.FIND_NON_SIMILAR_ARTISTS_LABEL);

                        ActionListener filter = new ActionListener() {

                            @Override
                            @SuppressWarnings("empty-statement")
                            public void actionPerformed(ActionEvent e) {

                                int numSimilarElements = similarityDialog.getNumberSimilarElements();
                                similarityDialog.dispose();
                                NBTree artistsNBTree = nbtreeManager.getNBTree("artistsNBTree");
                                MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();
                                ArrayList<String> artistNames = new ArrayList<String>();
                                if (!selectedNodes.contains(nodeUnder)) {
                                    selectedNodes.add(nodeUnder);
                                }

                                try {
                                    for (MuVisTreemapNode sNode : selectedNodes) {
                                        double artistKey = dbManager.getArtistKey(sNode.getName());
                                        if (artistKey < 0){
                                            Util.displayErrorMessage(parentFrame, "Similarity Filter", "Can't get non-similar artists!Try later!");
                                                break;
                                        }
                                        NBPoint artistPoint = artistsNBTree.lookupPoint(artistKey);
                                        BTree resultTree = artistsNBTree.knnQuery(artistPoint, dbManager.getCountArtists());
                                        TupleBrowser browser = resultTree.browse();
                                        Tuple tuple = new Tuple();
                                        while (browser.getNext(tuple));
                                        for (int j = 0; browser.getPrevious(tuple) && j < numSimilarElements; j++) {
                                            if (tuple.getValue() instanceof NBPoint) {
                                                NBPoint point = (NBPoint) tuple.getValue();
                                                String artist = dbManager.getArtistName(point.norm());
                                                artistNames.add(artist);
                                            }
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

                                selectedNodes.remove(nodeUnder);
                            }
                        };

                        similarityDialog.addFilterListener(filter);
                        similarityDialog.setVisible(true);

                    }

                    SimilarityTableFilter filter = new SimilarityTableFilter(tracks);
                    Environment.getEnvironmentInstance().getTableFilterManager().addTableFilter(filter);
                    Environment.getEnvironmentInstance().getTableFilterManager().filter();
                }
            }
        });

        addElementToPlaylistMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JMenuItem) {
                    JMenuItem item = (JMenuItem) e.getSource();

                    if (item.getText().contains("Artist")) {
                        threadPool.execute(new Runnable() {

                            @Override
                            public void run() {
                                //add artist to playlist
                                MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();
                                TreemapFilterManager filterManager = Environment.getEnvironmentInstance().getTreemapFilterManager();

                                List artistTracks = new ArrayList();
                                if (!selectedNodes.contains(nodeUnder)) {
                                    selectedNodes.add(nodeUnder);
                                }

                                for (MuVisTreemapNode sNode : selectedNodes) {
                                    artistTracks.addAll(filterManager.getFilteredTracks(sNode.getName()));
                                }

                                for (Object trackObject : artistTracks) {
                                    int trackId = (Integer) trackObject;
                                    String track = dbManager.getFilename(trackId);
                                    AudioMetadata metadata = dbManager.getTrackMetadata(trackId);
                                    PlaylistItem pliItem = new PlaylistItem(track, "", metadata);
                                    Environment.getEnvironmentInstance().getAudioPlaylist().appendItem(pliItem);
                                }
                                selectedNodes.remove(nodeUnder);
                            }
                        });
                    }
                }
            }
        });

        previewElementMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> artistsToPreview = new ArrayList<String>();

                if (!selectedNodes.contains(nodeUnder)) {
                    selectedNodes.add(nodeUnder);
                }

                for (MuVisTreemapNode sNode : selectedNodes) {
                    artistsToPreview.add(sNode.getName());
                }
                Environment.getEnvironmentInstance().getSnippetManager().previewArtists(artistsToPreview, true);
                selectedNodes.remove(nodeUnder);
            }
        });

        findSimilarElementMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() instanceof JMenuItem) {

                    final SimilarElementsView similarityDialog = new SimilarElementsView(parentFrame, Messages.FIND_SIMILAR_ARTISTS_LABEL);

                    final NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();
                    final MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();
                    final ArrayList<Integer> tracks = new ArrayList<Integer>();

                    ActionListener filter = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            int numSimilarElements = similarityDialog.getNumberSimilarElements();
                            similarityDialog.dispose();

                            NBTree artistNBTree = nbtreeManager.getNBTree("artistsNBTree");
                            ArrayList<String> artistNames = new ArrayList<String>();
                            if (!selectedNodes.contains(nodeUnder)) {
                                selectedNodes.add(nodeUnder);
                            }

                            try {
                                for (MuVisTreemapNode sNode : selectedNodes) {
                                    String artist = sNode.getName();
                                    double artistKey = dbManager.getArtistKey(artist);
                                    if (artistKey < 0){
                                        Util.displayErrorMessage(parentFrame, "Similarity Filter", "Can't get similar artists!Try later!");
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
                                }

                                TreemapSimilarityFilter filterTreemap = new TreemapSimilarityFilter(new NoFilter(), tracks);
                                Environment.getEnvironmentInstance().getTreemapFilterManager().addTreemapFilter(filterTreemap);
                                Environment.getEnvironmentInstance().getTreemapFilterManager().filter();

                                SimilarityTableFilter filterTable = new SimilarityTableFilter(tracks);
                                Environment.getEnvironmentInstance().getTableFilterManager().addTableFilter(filterTable);
                                Environment.getEnvironmentInstance().getTableFilterManager().filter();

                            } catch (IOException ex) {
                            } catch (NBTreeException ex) {
                            }

                            selectedNodes.remove(nodeUnder);
                        }
                    };

                    similarityDialog.addFilterListener(filter);
                    similarityDialog.setVisible(true);
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
}