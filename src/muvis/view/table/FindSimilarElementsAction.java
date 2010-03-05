/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package muvis.view.table;

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
 * Find similar Elements (tracks, albums, artists) common action
 * @author Ricardo
 */
public class FindSimilarElementsAction implements ActionListener {

    protected JTable tracksTable;

    public FindSimilarElementsAction(JTable tracksTable) {
        this.tracksTable = tracksTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();

            final ArrayList<Integer> tracks = new ArrayList();
            final int[] trackIds = tracksTable.getSelectedRows();

            if (item.getText().contains(Messages.TRACK_NAME_LABEL)) { //searching for similar tracks

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), Messages.FIND_SIMILAR_TRACKS_LABEL);
                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(SimilarityManager.getSimilarTracks(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);
            } else if (item.getText().contains(Messages.ALBUM_NAME_LABEL)) { //searching for similar albums

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), Messages.FIND_SIMILAR_ALBUMS_LABEL);

                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(SimilarityManager.getSimilarAlbums(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.SIMILAR));
                    }
                };

                similarityDialog.addFilterListener(filter);
                similarityDialog.setVisible(true);

            } else if (item.getText().contains(Messages.ARTIST_NAME_LABEL)) { //searching for similar artists

                final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), Messages.FIND_SIMILAR_ARTISTS_LABEL);

                ActionListener filter = new ActionListener() {

                    @Override
                    @SuppressWarnings("empty-statement")
                    public void actionPerformed(ActionEvent e) {

                        int numSimilarElements = similarityDialog.getNumberSimilarElements();
                        tracks.addAll(SimilarityManager.getSimilarArtists(trackIds, numSimilarElements, SimilarityManager.SimilarityMode.SIMILAR));
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
