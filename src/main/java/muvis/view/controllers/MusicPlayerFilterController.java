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
import muvis.audio.AudioMetadata;
import muvis.audio.MuVisAudioPlayer;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.main.filters.TreemapFilterManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Music player Controller - Controller for playing the filtered tracks by the user
 * @author Ricardo Dias
 */
public class MusicPlayerFilterController implements MusicPlayerControllerInterface, Observer {

    private AudioMetadata trackPlaying;
    private int position;
    private MusicLibraryDatabaseManager dbManager;
    private ArrayList<Integer> tracksToPlay;
    private boolean isPlaying,  updateTracksToPlay;

    @Autowired private MuVisAudioPlayer audioPlayer;
    @Autowired private TreemapFilterManager filterManager;
    private boolean enabled;
    private boolean playNext;

    public MusicPlayerFilterController() {
        trackPlaying = null;
        position = 0;
        isPlaying = false;
        tracksToPlay = new ArrayList<Integer>();
        updateTracksToPlay = true;
        playNext = true;

    }

    @Autowired
    public void setAudioPlayer(MuVisAudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
         audioPlayer.registerObserver(this);
    }

    private void needTrackUpdate() throws BasicPlayerException {
        filterManager.registerObserver(this);
        
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
        try {
            needTrackUpdate();
        } catch (BasicPlayerException ex) {
            System.out.println("Cannot update the tracks!");
        }
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
        try {
            needTrackUpdate();
        } catch (BasicPlayerException ex) {
            System.out.println("Cannot update the tracks!");
        }
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
        } catch (BasicPlayerException ex) {
            System.out.println("Cannot update the tracks!");
        }
        try {
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
            playNext = false;
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
            }
        }
        if (obs instanceof TreemapFilterManager) {
            updateTracksToPlay = true;
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
