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
package muvis.view.filters;

import java.awt.Color;
import java.awt.Paint;
import muvis.Workspace;
import muvis.util.Util;
import net.bouthier.treemapSwing.TMExceptionBadTMNodeKind;
import net.bouthier.treemapSwing.TMNode;
import net.bouthier.treemapSwing.TMNodeAdapter;

/**
 *
 * @author Ricardo
 */
public class MuVisDurationFilterDraw extends MuVisFilterDraw {

    /**
     * Returns the tooltip of the node.
     * The nodeAdapter should return an instance of TMFileNode.
     *
     * @param nodeAdapter               we compute the tooltip of this node;
     *                                  should return an instance of TMFileNode
     * @return                          the tooltip of the node
     * @throws TMExceptionBadTMNodeKind If the node does not return an
     *                                  instance of TMFileNode
     */
    public String getTooltip(TMNodeAdapter nodeAdapter)
            throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisFilterNode) {
            MuVisDurationFilterNode fNode = (MuVisDurationFilterNode) node;

            String name = fNode.getName();

            String tooltip = "";

 /*           DurationInspector inspector = fNode.getInspector();
            if (inspector != null){
                inspector.setEnabled(true);
                int value = Workspace.getWorkspaceInstance().getTreemapFilterManager().getFilterManagerClone().getCountFilteredTracks();
                inspector.setEnabled(false);

                tooltip = "<html>" + name + "<p>Tracks available: " + value;
            } else {*/

                int value = Workspace.getWorkspaceInstance().getDatabaseManager().getTracksBetweenTimeRange(fNode.getMinValue(), fNode.getMaxValue());
                tooltip = "<html>" + name + "<p>Tracks available: " + value;
            //}

            return tooltip;

        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

    @Override
    public Paint getFilling(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisFilterNode) {

            MuVisDurationFilterNode fNode = (MuVisDurationFilterNode) node;
            if (fNode.isSelected())
                return new Color(250, 192, 144);
            else {
                int value = Workspace.getWorkspaceInstance().getDatabaseManager().getTracksBetweenTimeRange(fNode.getMinValue(), fNode.getMaxValue());
                return Util.getColor(value);
            }
        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }
}
