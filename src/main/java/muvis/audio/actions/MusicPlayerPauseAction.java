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
package muvis.audio.actions;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import muvis.Elements;
import muvis.Environment;
import muvis.Messages;
import muvis.audio.MuVisAudioPlayer;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.MusicControllerView;

/**
 *
 * @author Ricardo
 */
public class MusicPlayerPauseAction implements ActionListener, Observer {

    protected MusicControllerView controller;
    protected MenuItem item;

    public MusicPlayerPauseAction(MenuItem item) {

        Environment.getEnvironmentInstance().getAudioPlayer().registerObserver(this);
        this.item = item;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller == null) {
            controller = (MusicControllerView) Environment.getEnvironmentInstance().getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);
        }
        controller.playTrack();
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof MuVisAudioPlayer) {
            MuVisAudioPlayer player = (MuVisAudioPlayer) obs;

            if (player.isPlaying()) {
                item.setLabel(Messages.SYSTEM_TRAY_PAUSE_MENU_ITEM);
            } else {
                item.setLabel(Messages.SYSTEM_TRAY_PLAY_MENU_ITEM);
            }
        }
    }
}
