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

import java.util.ArrayList;
import java.util.List;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import muvis.Environment;
import muvis.audio.AudioMetadata;
import muvis.audio.MuVisAudioPlayer;
import muvis.audio.playlist.PlaylistItem;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.main.filters.TreemapFilterManager;

/**
 * Music Player Generic Controller - Plays the filtered tracks and next the
 * tracks in the playlist
 * @author Ricardo Dias
 */
public class MusicPlayerGeneralController implements MusicPlayerControllerInterface, Observer {

    private AudioMetadata trackPlaying;
    private int position;
    private MusicLibraryDatabaseManager dbManager;
    private ArrayList<Integer> tracksToPlay;
    private Environment workspace;
    private boolean isPlaying,  updateTracksToPlay;
    private MuVisAudioPlayer audioPlayer;
    private TreemapFilterManager filterManager;
    private boolean enabled;
    private boolean playNext;

    public MusicPlayerGeneralController() {
        trackPlaying = null;
        position = 0;
        workspace = Environment.getWorkspaceInstance();
        dbManager = workspace.getDatabaseManager();
        workspace.getAudioPlayer().registerObserver(this);
        isPlaying = false;
        tracksToPlay = new ArrayList<Integer>();
        updateTracksToPlay = true;
        audioPlayer = workspace.getAudioPlayer();
        playNext = true;

    }

    private void needTrackUpdate() throws BasicPlayerException {
        if (filterManager == null) {
            filterManager = workspace.getTreemapFilterManager();
            filterManager.registerObserver(this);
        }
        if (!isPlaying && updateTracksToPlay) {
            setTracksToPlay();
            updateTracksToPlay = false;
            position = 0; //start from the beginning
        } else if (tracksToPlay == null) {
            setTracksToPlay();
            updateTracksToPlay = false;
        }
    }

    private void setTracksToPlay() {
        List filteredTracks = filterManager.getFilteredTracks();
        tracksToPlay.clear();
        tracksToPlay = new ArrayList<Integer>(filteredTracks);

        //adding the tracks in the playlist
        for (PlaylistItem item : workspace.getAudioPlaylist().getAllItems()){
            int id = dbManager.getTrackId(item.getFullName());
            tracksToPlay.add(id);
        }
    }

    @Override
    public AudioMetadata getTrackPlayingMetadata() {
        try {
            needTrackUpdate();
            trackPlaying = dbManager.getTrackMetadata(tracksToPlay.get(position));
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
            return null;
        }

        return trackPlaying;
    }

    @Override
    public void playNextTrack() {
        if (position < (tracksToPlay.size() - 1)) {
            position++;

        } else {
            position = 0;
        }
        isPlaying = false;
        playNext = true;
        playTrack();
    }

    @Override
    public void playPreviousTrack() {
        if (position > 0) {
            position--;
        } else if (position == 0) {
            position = tracksToPlay.size() - 1;
        }
        isPlaying = false;
        playNext = false;
        playTrack();
    }

    @Override
    public void playTrack() {
        try {
            needTrackUpdate();
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
            }
            if (tracksToPlay.size() > 0) {
                String filename = dbManager.getFilename(tracksToPlay.get(position));
                audioPlayer.play(filename);
            }
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
            if (playNext){
                playNextTrack();
            } else playPreviousTrack();
        }
    }

    @Override
    public void pauseTrack() {
        try {
            audioPlayer.pause();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setPlayerVolume(int value) throws BasicPlayerException {
        audioPlayer.setVolume(value);
    }

    @Override
    public void stopTrack() {
        try {
            isPlaying = false;
            position = 0;
            audioPlayer.stop();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (enabled) {
            if (obs instanceof MuVisAudioPlayer) {
                if (MuVisAudioPlayer.Event.STOPPED.equals(arg) && isPlaying) {
                    playNextTrack();
                } else if (MuVisAudioPlayer.Event.NEW_TRACK_PLAYING.equals(arg)) {
                    isPlaying = true;
                }
            } else if (obs instanceof TreemapFilterManager) {
                updateTracksToPlay = true;
            }
        }
    }

    @Override
    public void setEnable(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isPlaying() {
        return audioPlayer.isPlaying();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
