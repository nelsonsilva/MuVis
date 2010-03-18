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

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import muvis.util.Observable;
import muvis.util.Observer;
import net.bouthier.treemapSwing.TMAction;
import net.bouthier.treemapSwing.TMView;

/**
 *
 * @author Ricardo
 */
public class MuVisFilterAction extends TMAction implements Observable {

    private ArrayList<Observer> observers;
    private ExecutorService threadPool;
    private Observable observable;
    private Event event;
    private MuVisFilterNode lastSelectedNode;

    /**
     * @return the lastSelectedNode
     */
    public MuVisFilterNode getLastSelectedNode() {
        return lastSelectedNode;
    }

    public enum Event {
        NODE_SELECTED_WITH_CTRL, NODE_SELECTED_WITHOUT_CTRL , NODE_UNSELECTED, NO_ACTION
    }

    public MuVisFilterAction(TMView view){
       super(view);
       observers = new ArrayList<Observer>();
       threadPool = Executors.newFixedThreadPool(5);
       observable = this;
       event = Event.NO_ACTION;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        Object node = view.getNodeUnderTheMouse(e);
        if (node != null){
            MuVisFilterNode fNode = (MuVisFilterNode)node;

            if (e.isShiftDown() && e.isControlDown()){
                view.zoom(e.getX(), e.getY());
                return;
            } else if (e.isShiftDown()){
                view.unzoom();
            }

            if (fNode.isSelected()){
                fNode.setSelected(false);
                event = Event.NODE_UNSELECTED;
                updateObservers();
            }
            else{
                fNode.setSelected(true);
                if (e.isControlDown()){
                    event = Event.NODE_SELECTED_WITH_CTRL;
                }
                else {
                    event = Event.NODE_SELECTED_WITHOUT_CTRL;
                }
                updateObservers();
            }
            lastSelectedNode = fNode;
        }
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
        for(final Observer obs : observers){
            threadPool.execute( new Runnable() {
                @Override
                public void run() {
                    obs.update(observable, event);
                }
            });
        }
    }
}
