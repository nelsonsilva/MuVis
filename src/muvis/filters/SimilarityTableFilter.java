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

import java.util.ArrayList;
import javax.swing.RowFilter;
import muvis.util.Observable;
import muvis.view.TracksTableModel;

/*
 * Classes for implementing the filters in the list view
 * @author Ricardo
 */
public class SimilarityTableFilter extends TableFilter {
    
    private ArrayList<Integer> similarElements;

    public SimilarityTableFilter(ArrayList<Integer> elements) {
        similarElements = elements;
    }

    @Override
    public RowFilter<TracksTableModel, Object> filter() {
        try {

            RowFilter<TracksTableModel, Object> rf = new RowFilter<TracksTableModel, Object>() {

                @Override
                public boolean include(Entry<? extends TracksTableModel, ? extends Object> entry) {
                    if (entry.getValue(0) != null) {
                        int value = Integer.parseInt(entry.getStringValue(0));

                        if (!similarElements.isEmpty()){
                        if (similarElements.contains(value)) return true;
                            else
                                return false;
                        }
                    }
                    return true;
                }
            };
            return rf;
        } catch (java.util.regex.PatternSyntaxException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void update(Observable obs, Object arg) {
        //nothing to do here
    }

    @Override
    public void reset() {
        similarElements.clear();
    }
}
