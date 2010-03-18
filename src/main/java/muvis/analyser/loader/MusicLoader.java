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

package muvis.analyser.loader;

import java.util.ArrayList;
import muvis.util.MP3AudioFile;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.util.Util;

/**
 * Implements the Music Loader for the MuVis player.
 * Scans the directories and processes it.
 * @author Ricardo
 */
public class MusicLoader implements Loader, Observable {

    protected ArrayList<Observer> processors;
    private ArrayList<MP3AudioFile> allFiles;

    public MusicLoader() {
        processors = new ArrayList<Observer>();
    }

    @Override
    public void load(String path) {

        allFiles = Util.listFilesRecursive(path);

        updateObservers();
    }

    @Override
    public void load(String[] paths) {

        allFiles = new ArrayList<MP3AudioFile>();
        ArrayList<MP3AudioFile> files = new ArrayList<MP3AudioFile>();

        for (String path : paths) {
            files = Util.listFilesRecursive(path);
            allFiles.addAll(files);
        }
        updateObservers();
    }

    /**
     * Code for implementing the Observable interface
     * Methods: registerObserver
     * 			unregisterObserver
     * 			updateObservers
     */
    @Override
    public void registerObserver(Observer obs) {

        processors.add(obs);
    }

    @Override
    public void unregisterObserver(Observer obs) {

        int observerPosition = processors.indexOf(obs);

        if (observerPosition >= 0) {
            processors.remove(observerPosition);
        }
    }

    @Override
    public void updateObservers() {
        for (Observer obs : processors) {
            obs.update(this, allFiles);
        }
    }
}
