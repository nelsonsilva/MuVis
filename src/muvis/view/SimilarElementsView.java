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

package muvis.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

/**
 * Class that implements the playlist generation view.
 * @author Ricardo
 */
public class SimilarElementsView extends SimilarElementsViewUI implements ActionListener {

    public SimilarElementsView(JFrame parent, String elementDescription){
        super(parent, true);
        setLocationRelativeTo(parent);

        numberSimilarLabel.setText("Number of similar " + elementDescription + ":");

        cancelFilterButton.addActionListener(this);
        filterLibraryButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelFilterButton){
            this.dispose();
        }
        else if (e.getSource() == filterLibraryButton){
            //randomly generate a playlist
            this.dispose();
        }
    }

    public int getNumberSimilarElements(){
        return (Integer)numberSimilarElementsSpinner.getValue();
    }

    public void addFilterListener(ActionListener buttonListener){
        filterLibraryButton.addActionListener(buttonListener);
    }
}

