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
import muvis.view.TextFieldListener;

/**
 * This class implements a text filter for the treemap.
 * @author Ricardo
 */
public class TreemapTextFilter extends FilterDecorator {

    private String textField;

    public TreemapTextFilter() {
        textField = "";
    }

    @Override
    protected String getQuery(String artistName) {
        if (!textField.equals("")){

            query = "(";
            query = query + "(track_title LIKE '%" + textField + "%')";
            query = query + " OR (artist_name LIKE '%" + textField + "%')";
            query = query + " OR (album_name LIKE '%" + textField + "%')";
            query = query + ")";
            query = query + " AND " + parentFilter.getQuery(artistName);
        } else {
            query = parentFilter.getQuery(artistName);
        }

        return query;
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof TextFieldListener){
            textField = (String)arg;
        }
    }

    @Override
    public void reset(){
        textField = "";
    }
}
