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
package muvis.view.main.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import muvis.Environment;
import muvis.Messages;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.SimilarityTableFilter;
import muvis.similarity.SimilarityManager;
import muvis.similarity.SimilarityManager.SimilarityMode;
import muvis.view.SimilarElementsView;
import muvis.view.main.MuVisTreemapNode;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapSimilarityFilter;

/**
 * Generic action for similarity action on the treemap.
 * Childs only need to call super with the respective SimilarityMode
 * @author Ricardo
 */
public abstract class SimilarityTreemapAction implements ActionListener {

    protected ArrayList<MuVisTreemapNode> selectedNodes;
    protected MuVisTreemapNode nodeUnder;
    protected SimilarityMode similarityMode;

    public SimilarityTreemapAction(ArrayList<MuVisTreemapNode> selectedNodes, SimilarityMode mode) {
        this.selectedNodes = selectedNodes;
        this.similarityMode = mode;
    }

    public void setNodeUnder(MuVisTreemapNode node){
        this.nodeUnder = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) {

            final SimilarElementsView similarityDialog = new SimilarElementsView(Environment.getEnvironmentInstance().getRootFrame(), Messages.FIND_SIMILAR_ARTISTS_LABEL);

            final ArrayList<Integer> tracks = new ArrayList<Integer>();

            ActionListener filter = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();
                    int numSimilarElements = similarityDialog.getNumberSimilarElements();
                    similarityDialog.dispose();

                    if (!selectedNodes.contains(nodeUnder)) {
                        selectedNodes.add(nodeUnder);
                    }

                    ArrayList<String> artistNames = new ArrayList<String>();
                    for (MuVisTreemapNode sNode : selectedNodes) {
                        String artist = sNode.getName();
                        double artistKey = dbManager.getArtistKey(artist);
                        if (artistKey < 0) {
                            //this artist is a dead end, because no similarity information
                            //was found
                            continue;
                        } else {
                            artistNames.add(artist);
                        }
                    }

                    SimilarityManager.getSimilarArtists(artistNames, numSimilarElements, similarityMode);

                    TreemapSimilarityFilter filterTreemap = new TreemapSimilarityFilter(new NoFilter(), tracks);
                    Environment.getEnvironmentInstance().getTreemapFilterManager().addTreemapFilter(filterTreemap);
                    Environment.getEnvironmentInstance().getTreemapFilterManager().filter();

                    SimilarityTableFilter filterTable = new SimilarityTableFilter(tracks);
                    Environment.getEnvironmentInstance().getTableFilterManager().addTableFilter(filterTable);
                    Environment.getEnvironmentInstance().getTableFilterManager().filter();

                    selectedNodes.remove(nodeUnder);
                }
            };

            similarityDialog.addFilterListener(filter);
            similarityDialog.setVisible(true);
        }
    }
}
