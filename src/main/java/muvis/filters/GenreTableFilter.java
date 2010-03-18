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

package muvis.filters;

import java.util.Hashtable;
import javax.swing.RowFilter;
import muvis.util.Observable;
import muvis.view.table.TracksTableModel;
import muvis.view.filters.MuVisFilterAction;
import muvis.view.filters.MuVisFilterNode;
import muvis.view.filters.MuVisGenreFilterNode;

/*
 * Classes for implementing the filters in the list view
 * @author Ricardo
 */
public class GenreTableFilter extends TableFilter {

    private Hashtable<String, MuVisFilterNode> selectedNodes;

    public GenreTableFilter() {
        selectedNodes = new Hashtable<String, MuVisFilterNode>();
    }

    @Override
    public RowFilter<TracksTableModel, Object> filter() {
        try {

            RowFilter<TracksTableModel, Object> rf = new RowFilter<TracksTableModel, Object>() {

                @Override
                public boolean include(Entry<? extends TracksTableModel, ? extends Object> entry) {
                    if (entry.getValue(5) != null) {
                        String rowGenre = entry.getStringValue(5);

                        if (!selectedNodes.isEmpty()){
                            for (MuVisFilterNode node : selectedNodes.values()){
                                MuVisGenreFilterNode fNode = (MuVisGenreFilterNode)node;

                                if (fNode.getGenre().equals(rowGenre)){
                                    return true;
                                }
                            }
                            return false;
                        }
                        else return true;
                    }
                    //the value is not in the range
                    return true;
                }
            };
            //sorter.setRowFilter(rf);
            return rf;
        } catch (java.util.regex.PatternSyntaxException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof MuVisFilterAction) {

            MuVisFilterAction action = (MuVisFilterAction) obs;
            MuVisFilterNode node = action.getLastSelectedNode();
            if (MuVisFilterAction.Event.NODE_SELECTED_WITH_CTRL.equals(arg)) {
                if (node instanceof MuVisGenreFilterNode)
                    selectedNodes.put(node.getName(), node);
            } else if (MuVisFilterAction.Event.NODE_SELECTED_WITHOUT_CTRL.equals(arg)){
                if (node instanceof MuVisGenreFilterNode){
                    for (MuVisFilterNode n : selectedNodes.values()){
                        n.setSelected(false);
                    }
                    selectedNodes.clear();
                    selectedNodes.put(node.getName(), node);
                }
            } else if (MuVisFilterAction.Event.NODE_UNSELECTED.equals(arg)) {
                if (node instanceof MuVisGenreFilterNode)
                    selectedNodes.remove(node.getName());
            }
        }
    }

    @Override
    public void reset() {
        for (MuVisFilterNode node : selectedNodes.values()){
            node.setSelected(false);
        }
        selectedNodes.clear();
    }
}
