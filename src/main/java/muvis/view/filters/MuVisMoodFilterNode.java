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
import muvis.util.Util;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Ricardo
 */
public class MuVisMoodFilterNode extends MuVisFilterNode implements Observer {

    private String mood;

    /**
     * @return the genre
     */
    public String getMood() {
        return mood;
    }

    /**
     * @param mood the mood to set
     */
    public void setMood(String mood) {
        this.mood = mood;
    }

    public MuVisMoodFilterNode(){
        super("Mood Filter");
    }

    protected MuVisMoodFilterNode(String filterName,
    					 MuVisFilterNode  	parent,
    					 ProgressStatus status) {
        super(filterName, parent, status);
        dbManager.registerObserver(this);
    }

    @Override
    protected void buildTree(ProgressStatus status) {

        int k = 0;
        for(String vMood : Util.mood){

            MuVisMoodFilterNode child = new MuVisMoodFilterNode(vMood, parent, status);
            child.setMood(vMood);
            child.setOrder(k++);
            addChild(child);
        }
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
