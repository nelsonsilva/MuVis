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

import java.util.List;
import muvis.util.Observable;

/**
 * Decorator pattern element, that allows filters composition
 * @author Ricardo
 */
public class CompositeFilter extends FilterDecorator {

    public CompositeFilter(TreemapFilter filter){
        super(filter);
    }

    @Override
    public int getCountFilteredTracks(String artistName) {
        return parentFilter.getCountFilteredTracks(artistName);
    }

    @Override
    public int getCountFilteredAlbuns(String artistName) {
        return parentFilter.getCountFilteredAlbuns(artistName);
    }

    @Override
    public List getFilteredTracks(String artistName) {
        return parentFilter.getFilteredTracks(artistName);
    }

    @Override
    public List getFilteredAlbuns(String artistName) {
        return parentFilter.getFilteredAlbuns(artistName);
    }

    @Override
    public void reset() {
        parentFilter.reset();
    }

    @Override
    protected String getQuery(String artistName) {
        return parentFilter.getQuery(artistName);
    }

    @Override
    public void update(Observable obs, Object arg) {
        parentFilter.update(obs, arg);
    }

}
