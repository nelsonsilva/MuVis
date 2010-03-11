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
import muvis.Environment;
import muvis.view.main.MuVisTreemapNode;

/**
 * Entity responsible for playing previews of select artists in the treemap
 * @author Ricardo
 */
public class PreviewTreemapAction implements ActionListener {

    protected ArrayList<MuVisTreemapNode> selectedNodes;
    protected MuVisTreemapNode nodeUnder;

    public PreviewTreemapAction(ArrayList<MuVisTreemapNode> selectedNodes){
        this.selectedNodes = selectedNodes;
    }

    public void setNodeUnder(MuVisTreemapNode node){
        this.nodeUnder = node;
    }

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
}
