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

package muvis.view.filters;

import muvis.Environment;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Observable;
import muvis.util.Observer;

/**
 *
 * @author Ricardo
 */
public class MuVisYearFilterNode extends MuVisFilterNode implements Observer {

    protected int startYear = 0;
    protected int yearRange = 0;

        /**
     * @return the value
     */
    public int getYear() {
        return startYear;
    }

    /**
     * @param value the value to set
     */
    public void setYear(int value) {
        this.startYear = value;
    }

    public MuVisYearFilterNode(){
        super("Year Filter");
    }

    protected MuVisYearFilterNode(String filterName,
    					 MuVisFilterNode  	parent,
    					 ProgressStatus status) {
        super(filterName, parent, status);
        Environment.getWorkspaceInstance().getDatabaseManager().registerObserver(this);
    }

    @Override
    protected void buildTree(ProgressStatus status) {

        String name = "";
        int k = 0;

        //last decade
        for (int i = 2000; i < 2012; i++, k++){
            name = i + "";
            MuVisYearFilterNode child = new MuVisYearFilterNode(name, parent, status);
            child.setYear(i);
            child.setYearRange(0);
            child.setOrder(k);
            addChild(child);
        }

        String years [] = {"90's","80's", "70's", "60's", "50's", "40's"};
        k = -1;
        for (int i = 1990, j = 0; i > 1930; i -= 10, j++, k--){
            name = years[j];
            MuVisYearFilterNode child = new MuVisYearFilterNode(name, parent, status);
            child.setYear(i);
            child.setYearRange(10);
            child.setOrder(k);
            addChild(child);
        }

        //<1940
        name = "<1940";
        MuVisYearFilterNode child = new MuVisYearFilterNode(name, parent, status);
        child.setYear(0);
        child.setYearRange(1940);
        child.setOrder(--k);
        addChild(child);

        name = "Others";
        child = new MuVisYearFilterNode(name, parent, status);
        child.setYear(Integer.MIN_VALUE);
        child.setYearRange(0);
        child.setOrder(--k);
        addChild(child);
    }

    /**
     * @return the yearRange
     */
    public int getYearRange() {
        return yearRange;
    }

    /**
     * @param yearRange the yearRange to set
     */
    public void setYearRange(int yearRange) {
        this.yearRange = yearRange;
    }

    /**
     * This method is called when the database changes
     */
    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof MusicLibraryDatabaseManager) {
            updater.updateSize(this);
        }
    }

}
