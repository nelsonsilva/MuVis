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
import java.util.Enumeration;
import java.util.List;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.main.MuVisTreemapNode;

/**
 * Treemap Filter Manager - Class that implements a manager for filtering in
 * the treemap
 * @author Ricardo
 */
public class TreemapFilterManager extends TreemapFilter implements Observer, Observable {

    private ArrayList<TreemapFilter> filters;
    private ArrayList<Observer> observers;
    private MuVisTreemapNode root;
    private TreemapFilter filterRoot;
    private boolean needComposition;

    public TreemapFilterManager(MuVisTreemapNode treemapRoot) {
        filters = new ArrayList<TreemapFilter>();
        observers = new ArrayList<Observer>();
        root = treemapRoot;
        needComposition = true;
    }

    public void addTreemapFilter(TreemapFilter filter) {
        needComposition = true;
        filters.add(filter);
        composeFilters();
    }

    public synchronized int getCountFilteredAlbums() {

        int value = 0;

        boolean firstTime = true;
        TreemapFilter filter = null;

        if (!filters.isEmpty()) {
            filter = filters.get(0);
            FilterDecorator fd = (FilterDecorator) filter;
            fd.setParentFilter(new EmptyFilter());
        }

        for (TreemapFilter f : filters) {
            if (firstTime) {
                firstTime = false;
                continue;
            }

            if (f instanceof FilterDecorator) {

                FilterDecorator fd = (FilterDecorator) f;
                fd.setParentFilter(filter);
                filter = f;
            }
        }

        value = filter.getCountFilteredAlbuns("");

        if (!filters.isEmpty()) {
            filter = filters.get(0);
            FilterDecorator fd = (FilterDecorator) filter;
            fd.setParentFilter(new NoFilter());
        }

        return value;
    }

    public synchronized int getCountFilteredTracks() {

        int value = 0;

        boolean firstTime = true;
        TreemapFilter filter = null;

        if (!filters.isEmpty()) {
            filter = filters.get(0);
            FilterDecorator fd = (FilterDecorator) filter;
            fd.setParentFilter(new EmptyFilter());
        }

        for (TreemapFilter f : filters) {
            if (firstTime) {
                firstTime = false;
                continue;
            }

            if (f instanceof FilterDecorator) {

                FilterDecorator fd = (FilterDecorator) f;
                fd.setParentFilter(filter);
                filter = f;
            }
        }

        value = filter.getCountFilteredTracks("");

        if (!filters.isEmpty()) {
            filter = filters.get(0);
            FilterDecorator fd = (FilterDecorator) filter;
            fd.setParentFilter(new NoFilter());
        }

        return value;
    }

    public synchronized List getFilteredTracks() {

        List filteredTracks = null;

        boolean firstTime = true;
        TreemapFilter filter = null;

        if (!filters.isEmpty()) {
            filter = filters.get(0);
            FilterDecorator fd = (FilterDecorator) filter;
            fd.setParentFilter(new EmptyFilter());
        }

        for (TreemapFilter f : filters) {
            if (firstTime) {
                firstTime = false;
                continue;
            }

            if (f instanceof FilterDecorator) {

                FilterDecorator fd = (FilterDecorator) f;
                fd.setParentFilter(filter);
                filter = f;
            }
        }

        filteredTracks = filter.getFilteredTracks("");

        if (!filters.isEmpty()) {
            filter = filters.get(0);
            FilterDecorator fd = (FilterDecorator) filter;
            fd.setParentFilter(new NoFilter());
        }

        return filteredTracks;
    }

    public synchronized void removeTableFilter(TreemapFilter filter) {
        needComposition = true;
        filters.remove(filter);
        composeFilters();
    }
    
    private void updateChildrenNodes(){
        for (Enumeration e = root.children(); e.hasMoreElements();) {
            MuVisTreemapNode n = (MuVisTreemapNode) e.nextElement();
            n.getUpdater().updateSize(n);
            n.getUpdater().updateState(n);
        }
    }

    public void filter(){
        updateChildrenNodes();
        updateObservers();
    }

    @Override
    public void reset() {
        for(TreemapFilter filter : filters){
            filter.reset();
        }
        updateChildrenNodes();
        updateObservers();
    }

    @Override
    public void update(Observable obs, Object arg) {
        for (Observer o : filters) {
            o.update(obs, arg);
        }
        updateChildrenNodes();
        updateObservers();
    }

    private void composeFilters() {
        boolean firstTime = true;
        TreemapFilter filter = null;

        if (!filters.isEmpty()) {
            filter = filters.get(0);
        }

        for (TreemapFilter f : filters) {
            if (firstTime) {
                firstTime = false;
                continue;
            }

            if (f instanceof FilterDecorator) {

                FilterDecorator fd = (FilterDecorator) f;
                fd.setParentFilter(filter);
                filter = f;
            }
        }

        needComposition = false;
        filterRoot = filter;
    }

    @Override
    public synchronized int getCountFilteredTracks(String artistName) {
        if (needComposition) {
            composeFilters();
        }
        return filterRoot.getCountFilteredTracks(artistName);
    }

    @Override
    public synchronized int getCountFilteredAlbuns(String artistName) {
        if (needComposition) {
            composeFilters();
        }
        return filterRoot.getCountFilteredAlbuns(artistName);
    }

    @Override
    public synchronized List getFilteredTracks(String artistName) {
        if (needComposition) {
            composeFilters();
        } 
        return filterRoot.getFilteredTracks(artistName);
    }

    @Override
    public synchronized List getFilteredAlbuns(String artistName) {
        if (needComposition) {
            composeFilters();
        }
        return filterRoot.getFilteredAlbuns(artistName);
    }

    @Override
    protected String getQuery(String artistName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public void unregisterObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void updateObservers() {
        for (Observer o : observers){
            o.update(this, null);
        }
    }
}
