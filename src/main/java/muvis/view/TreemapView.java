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

import javax.swing.JFrame;
import muvis.Environment;
import muvis.view.main.MuVisComputeTrackSize;
import muvis.view.main.MuVisNodeDraw;
import muvis.view.main.MuVisTreemapNode;
import muvis.view.main.MuVisTreemapVisualizationAction;
import muvis.view.main.TMAlgorithmAscOrder;
import muvis.view.main.TMAlgorithmDescOrder;
import muvis.view.main.filters.TreemapFilterManager;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapBeatFilter;
import muvis.view.main.filters.TreemapDurationFilter;
import muvis.view.main.filters.TreemapGenreFilter;
import muvis.view.main.filters.TreemapMoodFilter;
import muvis.view.main.filters.TreemapTextFilter;
import muvis.view.main.filters.TreemapYearFilter;
import net.bouthier.treemapSwing.TMAlgorithmSquarified;
import net.bouthier.treemapSwing.TMView;
import net.bouthier.treemapSwing.TreeMap;

/**
 * Class that holds the main visualization the treemap visualization
 * @author Ricardo
 */
public class TreemapView extends TreemapViewUI implements View {

    private JFrame parent;
    private MuVisTreemapNode root = null; // the root of the tree
    private TreeMap treeMap = null; // the treemap builded
    private TMView currentView = null;

    public TreemapView(JFrame parent) {
        this.parent = parent;

        root = new MuVisTreemapNode();
        TreemapFilterManager filterManager = new TreemapFilterManager(root);
        Environment.getEnvironmentInstance().setTreemapFilterManager(filterManager);

        //treemap filters
        TreemapDurationFilter treemapDurationFilter =
                new TreemapDurationFilter(new NoFilter());
        TreemapYearFilter treemapYearFilter = new TreemapYearFilter(new NoFilter());
        TreemapGenreFilter treemapGenreFilter = new TreemapGenreFilter(new NoFilter());
        TreemapBeatFilter treemapBeatFilter = new TreemapBeatFilter(new NoFilter());
        TreemapMoodFilter treemapMoodFilter = new TreemapMoodFilter(new NoFilter());
        TreemapTextFilter treemapTextFilter = new TreemapTextFilter(new NoFilter());

        filterManager.addTreemapFilter(treemapDurationFilter);
        filterManager.addTreemapFilter(treemapYearFilter);
        filterManager.addTreemapFilter(treemapGenreFilter);
        filterManager.addTreemapFilter(treemapBeatFilter);
        filterManager.addTreemapFilter(treemapMoodFilter);
        filterManager.addTreemapFilter(treemapTextFilter);

        treeMap = new TreeMap(root);
        TMView view = buildNewView();
        currentView = view;

        add(view, "TreeMap");

    }

    public TreeMap getTreeMapRepresentation() {
        return treeMap;
    }

    public TMView getCurrentView() {
        return currentView;
    }

    private TMView buildNewView() {
        MuVisComputeTrackSize fSize = new MuVisComputeTrackSize();
        MuVisNodeDraw fDraw = new MuVisNodeDraw();
        TMView view = treeMap.getView(fSize, fDraw);
        //view.addAlgorithm(new TMAlgorithmSimilarityOrder(), "SimilarityAlgorithm");
        view.addAlgorithm(new TMAlgorithmSquarified(), "SimilarityAlgorithm");
        view.addAlgorithm(new TMAlgorithmAscOrder(), "AscOrderAlgorithm");
        view.addAlgorithm(new TMAlgorithmDescOrder(), "DescOrderAlgorithm");
        view.setAlgorithm("SimilarityAlgorithm");
        view.setAction(new MuVisTreemapVisualizationAction(view, parent));
        return view;
    }
}
