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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JFrame;

/**
 * Class that implements the playlist generation view.
 * @author Ricardo
 */
public class GeneratePlaylistView extends GeneratePlaylistViewUI implements ActionListener {

    private boolean includeAllTracks;

    public GeneratePlaylistView(JFrame parent){
        super(parent, true);
        setLocationRelativeTo(parent);
        includeAllTracks = true;

        useAllTracksCheckBox.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED){
                    useAllTracksCheckBox.setSelected(false);
                    numberTracksPlaylistLabel.setEnabled(true);
                    numberTracksToGeneratePlaylistSpinner.setEnabled(true);
                    includeAllTracks = false;
                }
                else {
                    useAllTracksCheckBox.setSelected(true);
                    numberTracksPlaylistLabel.setEnabled(false);
                    numberTracksToGeneratePlaylistSpinner.setEnabled(false);
                    includeAllTracks = true;
                }
            }
        });
        cancelPlaylistGenerationButton.addActionListener(this);
        generatePlaylistButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelPlaylistGenerationButton){
            this.dispose();
        }
        else if (e.getSource() == generatePlaylistButton){
            //randomly generate a playlist
            this.dispose();
        }
    }

    public void addPlaylistGeneratorListener(ActionListener listener){

        generatePlaylistButton.addActionListener(listener);
    }

    /**
     * @return the includeAllTracks
     */
    public boolean isIncludeAllTracks() {
        return includeAllTracks;
    }

    public int getNumTracksInPlaylist(){
        return (Integer)numberTracksToGeneratePlaylistSpinner.getValue();
    }
}
