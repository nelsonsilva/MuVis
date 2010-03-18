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
 * Track Similarity Filter - Class that implements the similarity filter for
 * tracks, by using the treemap
 * @author Ricardo
 */
public class TreemapSimilarityFilter extends FilterDecorator {

    private ArrayList<Integer> trackIds;

    public TreemapSimilarityFilter(TreemapFilter filter, ArrayList<Integer> trackIds) {
        super(filter);
        this.trackIds = trackIds;
    }

    @Override
    protected String getQuery(String artistName) {
        boolean firstTime = true;

        if (!trackIds.isEmpty()) {
            query = "(";
            for (int trackId : trackIds) {
                if (firstTime) {
                    query = query + " (id=" + trackId + ")";
                    firstTime = false;
                } else {
                    query = query + " OR (id=" + trackId + ")";
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
        trackIds.clear();
    }
}

