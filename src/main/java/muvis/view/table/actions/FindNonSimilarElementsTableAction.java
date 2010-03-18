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
import muvis.Environment;
import muvis.Messages;
import muvis.filters.SimilarityTableFilter;
import muvis.similarity.SimilarityManager;
import muvis.view.SimilarElementsView;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapSimilarityFilter;

/**
 * Find non similar Elements (tracks, albums, artists) common action
 * @author Ricardo
 */
public class FindNonSimilarElementsTableAction implements ActionListener {

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

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), Messages.NUMBER_SIMILAR_TRACKS_LABEL);
                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(SimilarityManager.getSimilarTracks(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.NON_SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);
            } else if (item.getText().contains(Messages.ALBUM_NAME_LABEL)) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), Messages.NUMBER_SIMILAR_ALBUMS_LABEL);

                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(SimilarityManager.getSimilarAlbums(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.NON_SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);

            } else if (item.getText().contains(Messages.ARTIST_NAME_LABEL)) {

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), Messages.FIND_NON_SIMILAR_ARTISTS_LABEL);

                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(SimilarityManager.getSimilarArtists(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.NON_SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);
            }

            TreemapSimilarityFilter similarityFilter = new TreemapSimilarityFilter(new NoFilter(), tracks);
            Environment.getEnvironmentInstance().getTreemapFilterManager().addTreemapFilter(similarityFilter);
            Environment.getEnvironmentInstance().getTreemapFilterManager().filter();

            SimilarityTableFilter tableFilter = new SimilarityTableFilter(tracks);
            Environment.getEnvironmentInstance().getTableFilterManager().addTableFilter(tableFilter);
            Environment.getEnvironmentInstance().getTableFilterManager().filter();
        }
    }
}
