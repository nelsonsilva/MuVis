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
package muvis.view.table.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import muvis.Messages;
import muvis.MuVisApp;
import muvis.filters.SimilarityTableFilter;
import muvis.filters.TableFilterManager;
import muvis.similarity.SimilarityManager;
import muvis.view.SimilarElementsView;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapFilterManager;
import muvis.view.main.filters.TreemapSimilarityFilter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Find non similar Elements (tracks, albums, artists) common action
 * @author Ricardo
 */
public class FindNonSimilarElementsTableAction implements ActionListener {

    @Autowired protected SimilarityManager similarityManager;
    @Autowired protected TableFilterManager tableFilterManager;
    
    TreemapFilterManager treemapFilterManager;
    protected JTable tracksTable;

    public FindNonSimilarElementsTableAction(JTable tracksTable) {
        this.tracksTable = tracksTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();

            final ArrayList<Integer> tracks = new ArrayList();
            final int[] trackIds = tracksTable.getSelectedRows();

            if (item.getText().contains(Messages.TRACK_NAME_LABEL)) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(MuVisApp.getRootFrame(), Messages.NUMBER_SIMILAR_TRACKS_LABEL);
                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(similarityManager.getSimilarTracks(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.NON_SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);
            } else if (item.getText().contains(Messages.ALBUM_NAME_LABEL)) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(MuVisApp.getRootFrame(), Messages.NUMBER_SIMILAR_ALBUMS_LABEL);

                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(similarityManager.getSimilarAlbums(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.NON_SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);

            } else if (item.getText().contains(Messages.ARTIST_NAME_LABEL)) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(MuVisApp.getRootFrame(), Messages.FIND_NON_SIMILAR_ARTISTS_LABEL);

                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(similarityManager.getSimilarArtists(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.NON_SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);
            }

            TreemapSimilarityFilter similarityFilter = new TreemapSimilarityFilter(new NoFilter(), tracks);
            treemapFilterManager.addTreemapFilter(similarityFilter);
            treemapFilterManager.filter();

            SimilarityTableFilter tableFilter = new SimilarityTableFilter(tracks);
            tableFilterManager.addTableFilter(tableFilter);
            tableFilterManager.filter();
        }
    }
}
