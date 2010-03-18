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
package muvis.view.main.filters;

import muvis.util.Observable;
import muvis.view.filters.MuVisFilterAction;
import muvis.view.filters.MuVisFilterNode;
import muvis.view.filters.MuVisMoodFilterNode;

/**
 * This class implements a beat filter for the treemap.
 * @author Ricardo
 */
public class TreemapMoodFilter extends FilterDecorator {


    @Override
    protected String getQuery(String artistName) {
        boolean firstTime = true;

        if (!selectedNodes.isEmpty()) {

            query = "(";

            for (MuVisFilterNode node : selectedNodes.values()) {
                MuVisMoodFilterNode fNode = (MuVisMoodFilterNode) node;

                String mood = fNode.getMood();

                if (firstTime) {
                    query = query + " (mood='" + mood  + "')";
                    firstTime = false;
                } else {
                    query = query + " OR (mood='" + mood  + "')";
                }
            }

            query = query + ")";
            query = query + " AND " + parentFilter.getQuery(artistName);
        } else {
            query = parentFilter.getQuery(artistName);
        }

        return query;
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof MuVisFilterAction) {

            MuVisFilterAction action = (MuVisFilterAction) obs;
            MuVisFilterNode node = action.getLastSelectedNode();
            if (MuVisFilterAction.Event.NODE_SELECTED_WITHOUT_CTRL.equals(arg)) {
                if (node instanceof MuVisMoodFilterNode) {
                    for (MuVisFilterNode n : selectedNodes.values()) {
                        n.setSelected(false);
                    }
                    selectedNodes.clear();
                    selectedNodes.put(node.getName(), node);
                }
            } else if (MuVisFilterAction.Event.NODE_SELECTED_WITH_CTRL.equals(arg)) {
                if (node instanceof MuVisMoodFilterNode) {
                    selectedNodes.put(node.getName(), node);
                }
            } else if (MuVisFilterAction.Event.NODE_UNSELECTED.equals(arg)) {
                if (node instanceof MuVisMoodFilterNode) {
                    selectedNodes.remove(node.getName());
                }
            }
        }
    }
}
