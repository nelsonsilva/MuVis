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
public class MuVisGenreFilterNode extends MuVisFilterNode implements Observer {

    public static String validGenres[] = {
        "African", "Blues", "Classical", "Disco", 
        "Electro", "Funk", "Gospel", "Hip-Hop", "Jazz",
        "Latin", "Metal", "Other", "Pop", "Rap",
        "Reggae", "Rock", "R&B", "Soundtrack", "Soul", "World"
    };

    private String genre;

    /**
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public MuVisGenreFilterNode(){
        super("Genre Filter");
    }

    protected MuVisGenreFilterNode(String filterName,
    					 MuVisFilterNode  	parent,
    					 ProgressStatus status) {
        super(filterName, parent, status);
        Environment.getEnvironmentInstance().getDatabaseManager().registerObserver(this);
    }

    @Override
    protected void buildTree(ProgressStatus status) {

        int k = 0;
        for(String vGenre : validGenres){

            MuVisGenreFilterNode child = new MuVisGenreFilterNode(vGenre, parent, status);
            child.setGenre(vGenre);
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
