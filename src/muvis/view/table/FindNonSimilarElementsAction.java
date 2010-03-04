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

package muvis.view.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import muvis.Environment;
import muvis.Messages;
import muvis.NBTreeManager;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.SimilarityTableFilter;
import muvis.util.Util;
import muvis.view.SimilarElementsView;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapAlbumSimilarityFilter;
import muvis.view.main.filters.TreemapArtistSimilarityFilter;
import muvis.view.main.filters.TreemapTrackSimilarityFilter;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;

/**
 * Find non similar Elements (tracks, albums, artists) common action
 * @author Ricardo
 */
public class FindNonSimilarElementsAction implements ActionListener {

    protected JTable tracksTable;

    public FindNonSimilarElementsAction(JTable tracksTable) {
        this.tracksTable = tracksTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();
            final MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();

            final ArrayList<Integer> tracks = new ArrayList();
            final int[] trackIds = tracksTable.getSelectedRows();
            final NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();

            if (item.getText().contains(Messages.TRACK_NAME_LABEL)) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), "tracks");
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
                                if (trackKey < 0) {
                                    Util.displayErrorMessage(Environment.getEnvironmentInstance().getRootFrame(), "Similarity Filter", "Can't get non-similar tracks!Try later!");
                                    break;
                                }
                                NBPoint trackPoint = trackNBTree.lookupPoint(trackKey);
                                BTree resultTree = trackNBTree.knnQuery(trackPoint, dbManager.getCountTracks());
                                TupleBrowser browser = resultTree.browse();
                                Tuple tuple = new Tuple();
                                while (browser.getNext(tuple));
                                for (int j = 0; browser.getPrevious(tuple) && j < numSimilarElements; j++) {
                                    if (tuple.getValue() instanceof NBPoint) {
                                        NBPoint point = (NBPoint) tuple.getValue();
                                        int track = dbManager.getTrackId(point.norm());
                                        tracks.add(track);
                                    }
                                }

                                TreemapTrackSimilarityFilter filter = new TreemapTrackSimilarityFilter(new NoFilter(), tracks);
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
            } else if (item.getText().contains("Album")) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), "albuns");

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
                                if (albumKey < 0) {
                                    //Util.displayErrorMessage(parent, "Similarity Filter", "Can't get non-similar albums!Try later!");
                                    break;
                                }
                                NBPoint albumPoint = albumsNBTree.lookupPoint(albumKey);
                                BTree resultTree = albumsNBTree.knnQuery(albumPoint, dbManager.getCountAlbums());
                                TupleBrowser browser = resultTree.browse();
                                Tuple tuple = new Tuple();
                                while (browser.getNext(tuple));
                                for (int j = 0; browser.getPrevious(tuple) && j < numSimilarElements; j++) {
                                    if (tuple.getValue() instanceof NBPoint) {
                                        NBPoint point = (NBPoint) tuple.getValue();
                                        int albumId = dbManager.getAlbumId(point.norm());
                                        ArrayList<Integer> albumTracks = dbManager.getAlbumTracksIds(albumId);
                                        tracks.addAll(albumTracks);
                                    }
                                }

                                TreemapAlbumSimilarityFilter filter = new TreemapAlbumSimilarityFilter(new NoFilter(), tracks);
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

            } else if (item.getText().contains("Artist")) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), "artists");

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
                                if (artistKey < 0) {
                                    Util.displayErrorMessage(Environment.getEnvironmentInstance().getRootFrame(), "Similarity Filter", "Can't get non-similar artists!Try later!");
                                    break;
                                }
                                NBPoint artistPoint = artistNBTree.lookupPoint(artistKey);
                                BTree resultTree = artistNBTree.knnQuery(artistPoint, dbManager.getCountArtists());
                                TupleBrowser browser = resultTree.browse();
                                Tuple tuple = new Tuple();
                                while (browser.getNext(tuple));
                                for (int j = 0; browser.getPrevious(tuple) && j < numSimilarElements; j++) {
                                    if (tuple.getValue() instanceof NBPoint) {
                                        NBPoint point = (NBPoint) tuple.getValue();
                                        String artistName = dbManager.getArtistName(point.norm());
                                        ArrayList<Integer> artistTracks = dbManager.getArtistTracksIds(artistName);
                                        tracks.addAll(artistTracks);
                                        artistNames.add(artistName);
                                    }
                                }

                                TreemapArtistSimilarityFilter filter = new TreemapArtistSimilarityFilter(new NoFilter(), artistNames);
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
            }

            SimilarityTableFilter filter = new SimilarityTableFilter(tracks);
            Environment.getEnvironmentInstance().getTableFilterManager().addTableFilter(filter);
            Environment.getEnvironmentInstance().getTableFilterManager().filter();
        }
    }
}

/*
findNonSimilarElementMenu.addActionListener(new ActionListener() {

@Override
public void actionPerformed(ActionEvent e) {


}
});
 *
 *
 */
