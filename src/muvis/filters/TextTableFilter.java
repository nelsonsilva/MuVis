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

import javax.swing.RowFilter;
import muvis.util.Observable;
import muvis.view.TextFieldListener;
import muvis.view.table.TracksTableModel;

/**
 * Class that holds a RowFilter for the ListView Visualization.
 * @author Ricardo
 */
public class TextTableFilter extends TableFilter{

    private String textToFilter = "";

    @Override
    public RowFilter<TracksTableModel, Object> filter() {
        try {
            return RowFilter.regexFilter(textToFilter,1,2,3);
        } catch (java.util.regex.PatternSyntaxException e) {
            e.printStackTrace();
            return RowFilter.regexFilter("",1,2,3);
        }
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof TextFieldListener){
            textToFilter = (String)arg;
        }
    }

    @Override
    public void reset() {
        textToFilter = "";
    }
}
