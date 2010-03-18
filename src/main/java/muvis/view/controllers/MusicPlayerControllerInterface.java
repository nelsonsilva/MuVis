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

package muvis.view.controllers;

import javazoom.jlgui.basicplayer.BasicPlayerException;
import muvis.audio.AudioMetadata;

/**
 * Interface for the muvis player controller UI.
 * Every controller must implement this interface in order to interacte correctly
 * with the UI.
 * @author Ricardo
 */
public interface MusicPlayerControllerInterface {

    public AudioMetadata getTrackPlayingMetadata();

    public void pauseTrack();

    public void playNextTrack();

    public void playPreviousTrack();

    public void playTrack();

    public void setPlayerVolume(int value) throws BasicPlayerException;

    public void stopTrack();

    public void setEnable(boolean enabled);

    public boolean isEnabled();

    public boolean isPlaying();

}
