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

import javax.swing.JFrame;
import net.bouthier.treemapSwing.TMView;
import net.bouthier.treemapSwing.TreeMap;

/**
 *
 * @author Ricardo
 */
public class Test {

    private static MuVisFilterNode root 	  = null; // the root of the demo tree
    private static TreeMap 	  treeMap = null; // the treemap builded

    public static void main(String[]args){

        //root = new MuVisDurationFilterNode();
        root = new MuVisYearFilterNode();
        treeMap = new TreeMap(root);
        TMView view = buildNewView();
    }

    static TMView buildNewView() {
        /*MuVisDurationFilterSize fSize = new MuVisDurationFilterSize();
        MuVisFilterDraw fDraw = new MuVisFilterDraw();*/

        MuVisYearFilterSize fSize = new MuVisYearFilterSize();
        MuVisFilterDraw fDraw = new MuVisFilterDraw();

        TMView view = treeMap.getView(fSize, fDraw);
        view.setAction(new MuVisFilterAction(view));
        view.setAlgorithm(TMView.SQUARIFIED);
        JFrame viewFrame = new JFrame(root.getName());
        viewFrame.setContentPane(view);
        viewFrame.pack();
        viewFrame.setVisible(true);
        return view;
    }
}
