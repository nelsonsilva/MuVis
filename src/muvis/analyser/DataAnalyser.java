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

package muvis.analyser;

import java.util.ArrayList;
import muvis.analyser.loader.Loader;
import muvis.analyser.loader.MusicLoader;
import muvis.util.Observable;
import muvis.util.Observer;

/**
 * Class that holds the processing of the data
 * @author Ricardo
 */
public class DataAnalyser extends Thread implements Observable {

    private ArrayList<Observer> observers;
	private Loader loader;
    private String[] paths;

	public DataAnalyser(String[]paths){

        this.paths = paths;
		loader = new MusicLoader();
        observers = new ArrayList<Observer>();
	}

    @Override
    public void run(){
        analyse();
    }

    public void setPaths(String[]paths){
        this.paths = paths;
    }

    private void analyse(){

        loader.load(paths);
    }

    @Override
    public void registerObserver(Observer obs) {
        observers.add(obs);

        Observable observable = (Observable)loader;
		observable.registerObserver(obs);
    }

    @Override
    public void unregisterObserver(Observer obs) {
        observers.remove(obs);
        Observable observable = (Observable)loader;
        observable.unregisterObserver(obs);
    }

    @Override
    public void updateObservers() {
        for(Observer obs: observers){
            obs.update(this, null);
        }
    }
}

