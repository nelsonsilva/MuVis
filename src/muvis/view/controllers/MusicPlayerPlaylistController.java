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
import muvis.Workspace;
import muvis.audio.AudioMetadata;
import muvis.audio.MuVisAudioPlayer;
import muvis.audio.playlist.Playlist;
import muvis.util.Observable;
import muvis.util.Observer;

/**
 * This class implements a controller for the MuVis music player interface for
 * handling the most common operations, operating under the main playlist
 * @author Ricardo
 */
public class MusicPlayerPlaylistController implements MusicPlayerControllerInterface, Observer {

    private boolean isPlaying;
    private Playlist playlist;
    private MuVisAudioPlayer player;
    private boolean enabled;

    public MusicPlayerPlaylistController() {
        isPlaying = false;
        enabled = false;
        Workspace.getWorkspaceInstance().getAudioPlayer().registerObserver(this);
        playlist = Workspace.getWorkspaceInstance().getAudioPlaylist();
        player = Workspace.getWorkspaceInstance().getAudioPlayer();
    }

    @Override
    public AudioMetadata getTrackPlayingMetadata() {
        return Workspace.getWorkspaceInstance().getAudioPlaylist().getCursor().getAudioMetaData();
    }

    @Override
    public void playNextTrack() {
        playlist.nextCursor();
        isPlaying = false;
        playTrack();
    }

    @Override
    public void playPreviousTrack() {
        playlist.previousCursor();
        isPlaying = false;
        playTrack();
    }

    @Override
    public void playTrack() {
        try {
            int index = playlist.getSelectedIndex();
            if (index < playlist.getPlaylistSize()){
                if (player.isPlaying()){
                    player.stop();
                }
                player.play(playlist.getCursor().getFullName());
            } 
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
            playNextTrack();
        }
    }

    @Override
    public void pauseTrack() {
        try {
            player.pause();
            isPlaying = false;
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setPlayerVolume(int value) throws BasicPlayerException {
        Workspace.getWorkspaceInstance().getAudioPlayer().setVolume(value);
    }

    @Override
    public void stopTrack() {
        try {
            isPlaying = false;
            playlist.begin();
            player.stop();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (enabled){
            if (obs instanceof MuVisAudioPlayer) {
                if (MuVisAudioPlayer.Event.STOPPED.equals(arg) && isPlaying) {
                    playNextTrack();
                }
                else if (MuVisAudioPlayer.Event.NEW_TRACK_PLAYING.equals(arg)){
                    isPlaying = true;
                }
            }
        }
    }

    @Override
    public void setEnable(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
