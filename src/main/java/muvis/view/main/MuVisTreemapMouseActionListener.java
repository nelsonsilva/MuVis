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

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import muvis.Messages;
import muvis.view.MainViewsMouseAdapter;
import muvis.view.main.actions.AddToPlaylistTreemapAction;
import muvis.view.main.actions.FindNonSimilarElementsTreemapAction;
import muvis.view.main.actions.FindSimilarElementsTreemapAction;
import muvis.view.main.actions.PreviewTreemapAction;
import muvis.view.main.actions.SimilarityTreemapAction;
import net.bouthier.treemapSwing.TMView;

/**
 * Main view Treemap mouse listener. Gives support to the main actions such as:
 * adding elements to playlist, using similarity filter or making a preview of
 * the selected elements.
 * @author Ricardo
 */
public class MuVisTreemapMouseActionListener extends MainViewsMouseAdapter {

    protected TMView view;
    protected MuVisTreemapNode nodeUnder;
    protected ArrayList<MuVisTreemapNode> selectedNodes;
    protected PreviewTreemapAction previewAction;
    protected AddToPlaylistTreemapAction addToPlaylistAction;
    protected SimilarityTreemapAction findSimilarAction, findNonSimilarAction;

    public MuVisTreemapMouseActionListener(TMView view, ArrayList<MuVisTreemapNode> selectedNodes) {
        this.view = view;
        this.selectedNodes = selectedNodes;
        previewAction = new PreviewTreemapAction(selectedNodes);
        addToPlaylistAction = new AddToPlaylistTreemapAction(selectedNodes);
        findSimilarAction = new FindSimilarElementsTreemapAction(selectedNodes);
        findNonSimilarAction = new FindNonSimilarElementsTreemapAction(selectedNodes);
    }

    @Override
    protected void assignActionListeners() {
        previewAction.setNodeUnder(nodeUnder);
        addToPlaylistAction.setNodeUnder(nodeUnder);
        findSimilarAction.setNodeUnder(nodeUnder);
        findNonSimilarAction.setNodeUnder(nodeUnder);
        
        previewElementMenu.addActionListener(previewAction);
        addElementToPlaylistMenu.addActionListener(addToPlaylistAction);
        findSimilarElementMenu.addActionListener(findSimilarAction);
        findNonSimilarElementMenu.addActionListener(findNonSimilarAction);
    }

    @Override
    protected void mouseHandler(MouseEvent e) {
        Object node = view.getNodeUnderTheMouse(e);
        if (node != null && e.isPopupTrigger()) {
            MuVisTreemapNode fNode = (MuVisTreemapNode) node;
            nodeUnder = fNode;
            MainViewsMouseAdapter.ElementType type;
            if (selectedNodes.isEmpty() || selectedNodes.size() == 1) {
                type = MainViewsMouseAdapter.ElementType.SIMPLE;
            } else {
                type = MainViewsMouseAdapter.ElementType.MULTIPLE;
            }
            contextMenu = createContextMenu(Messages.COL_ARTIST_NAME_LABEL, type);

            //show the menu
            if (contextMenu != null && contextMenu.getComponentCount() > 0) {
                contextMenu.show(view, e.getX(), e.getY());
            }
        }
    }
}
