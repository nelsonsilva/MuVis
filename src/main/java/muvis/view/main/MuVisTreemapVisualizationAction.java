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
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import muvis.Elements;
import muvis.Environment;
import muvis.view.MainViewHolder;
import muvis.view.TreemapArtistInspectorView;
import net.bouthier.treemapSwing.TMAction;
import net.bouthier.treemapSwing.TMView;

/**
 * Treemap UI interaction handler.
 * Mouse listener is delegated to the MuVisTreemapMouseActionListener entity.
 * @author Ricardo
 */
public class MuVisTreemapVisualizationAction extends TMAction {

    private MainViewHolder mainViewHolder;
    private TreemapArtistInspectorView artistInspector;
    private ArrayList<MuVisTreemapNode> selectedNodes;
    private MuVisTreemapMouseActionListener mouseListener;

    public MuVisTreemapVisualizationAction(TMView view, JFrame frame) {
        super(view);
        selectedNodes = new ArrayList<MuVisTreemapNode>();
        mouseListener = new MuVisTreemapMouseActionListener(view, selectedNodes);
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
                mainViewHolder.setView(Elements.ARTIST_INSPECTOR_VIEW);
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
        mouseListener.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseListener.mouseReleased(e);
    }   
}