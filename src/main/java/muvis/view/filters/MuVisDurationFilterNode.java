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

import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapFilterManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Ricardo
 */
public class MuVisDurationFilterNode extends MuVisFilterNode implements Observer {

    protected int minValue = 0;
    protected int maxValue = 0;

    public static TreemapFilterManager treemapFilterManager;

    @Autowired
    public void setTreemapFilterManager(TreemapFilterManager manager){
      MuVisDurationFilterNode.treemapFilterManager=manager;
    }
        /**
     * @return the value
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * @param value the value to set
     */
    public void setMinValue(int value) {
        this.minValue = value;
    }

        /**
     * @return the value
     */
    public int getMaxValue() {
        return maxValue;
    }
    /**
     * @param value the value to set
     */
    public void setMaxValue(int value) {
        this.maxValue = value;
    }

    public MuVisDurationFilterNode(){
        super("Duration Filter");
        super.isLeaf = false;
    }

    protected MuVisDurationFilterNode(String filterName,
    					 MuVisFilterNode  	parent,
    					 ProgressStatus status) {
        super(filterName, parent, status);
        dbManager.registerObserver(this);
        treemapFilterManager.registerObserver(this);
        super.isLeaf = true;

    }

    @Override
    protected void buildTree(ProgressStatus status) {

        String name = "";
        name = "<2min";
        MuVisDurationFilterNode child = new MuVisDurationFilterNode(name, parent, status);
        child.setMinValue(0);
        child.setMaxValue(120);
        child.setOrder(5);
        addChild(child);

        name = "2-3min";
        child = new MuVisDurationFilterNode(name, parent, status);
        child.setMinValue(120);
        child.setMaxValue(180);
        child.setOrder(4);
        addChild(child);

        name = "3-4min";
        child = new MuVisDurationFilterNode(name, parent, status);
        child.setMinValue(180);
        child.setMaxValue(240);
        child.setOrder(3);
        addChild(child);

        name = "4-5min";
        child = new MuVisDurationFilterNode(name, parent, status);
        child.setMinValue(240);
        child.setMaxValue(300);
        child.setOrder(2);
        addChild(child);

        name = ">5min";
        child = new MuVisDurationFilterNode(name, parent, status);
        child.setMinValue(300);
        child.setMaxValue(Integer.MAX_VALUE);
        child.setOrder(1);
        addChild(child);
    }

    /**
     * This method is called when the database changes
     */
    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof MusicLibraryDatabaseManager) {
            updater.updateSize(this);
        } else if (obs instanceof TreemapFilterManager){
            updater.updateSize(this);
            updater.updateState(this);
        }
    }

}