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

import java.util.ArrayList;
import muvis.util.Observable;

/**
 * Artist Similarity Filter
 * @author Ricardo
 */
public class TreemapArtistSimilarityFilter extends FilterDecorator {

    private ArrayList<String> artists;

    public TreemapArtistSimilarityFilter(TreemapFilter filter, ArrayList<String> artistNames) {
        super(filter);
        artists = artistNames;
    }

    @Override
    protected String getQuery(String artistName) {
        boolean firstTime = true;

        if (!artists.isEmpty()) {
            query = "(";
            for (String artist : artists) {
                if (firstTime) {
                    query = query + " (artist_name='" + artist + "')";
                    firstTime = false;
                } else {
                    query = query + " OR (artist_name='" + artist + "')";
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
        //nothing to update here
    }

    @Override
    public void reset(){
        artists.clear();
    }
}

