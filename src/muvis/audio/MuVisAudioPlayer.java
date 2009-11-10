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

package muvis.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import muvis.util.Observable;
import muvis.util.Observer;

/**
 * This class implements the basic mechanism of a simple audio player
 * It has the main functions of play, pause and stop, as functions for controlling the volume
 * and get some information about the player and the track being played
 * @author Ricardo
 * @version 1.0
 */
public class MuVisAudioPlayer implements BasicPlayerListener, Observable {

    /**
     * This enum represents the state of the player or the operation accomplished
     * by the muvis audio player
     */
    public enum Event {
        /*PLAYING*/

        PAUSED, STOPPED, OPENED, VOLUME_CHANGED, NOTHING, SEEKED,
        NEW_TRACK_PLAYING, RESUMED
    }
    /**
     * The current implementation of the player - using Basic Player
     */
    private BasicPlayer player;
    /**
     * The basicplayer controler
     */
    private BasicController control;
    /**
     * This field holds the properties of the file being played
     */
    private Map fileProperties;
    /**
     * This field holds the properties of the stream being played
     */
    private Map playingProperties;
    /**
     * This field holds the bytes that were readed from the current file
     */
    private int bytesReaded;
    /**
     * Boolean that indicates if the player is currently playing
     */
    private boolean isPlaying;
    /**
     * Boolean that indicates if the player is currently paused
     */
    private boolean isPaused;
    /**
     * The current value of the volume.
     * starts the volume at average
     */
    private float volume = 50;
    /**
     * Observers of the MuVisPlayer
     */
    private ArrayList<Observer> observers;
    /**
     * String that represents the file currently being played
     */
    private String filePlayling;
    /**
     * This field is used to be passed to the observers, so they could know
     * what's going on in the player
     */
    private Event event;

    public MuVisAudioPlayer() {
        player = new BasicPlayer();
        control = (BasicController) player;

        //for receiving updates
        player.addBasicPlayerListener(this);
        isPlaying = false;
        isPaused = false;
        filePlayling = "";
        event = Event.NOTHING;

        //creating the list for the observers
        observers = new ArrayList<Observer>();
    }

    /**
     * Method that returns a boolean indicating if the track is being played
     * @return
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Method that returns a boolean indicating if the player is paused
     * @return
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * This method plays the music indicated by the parameter, that should include the absolute path
     * in the filename
     * @param filename - the string that represents the file
     * @throws BasicPlayerException
     */
    public void play(String filename) throws BasicPlayerException {

        if (!isPlaying && !isPaused) {
            control.open(new File(filename));
            // Start playback in a thread.
            control.play();

            filePlayling = filename;

            //setting the volume
            if (volume > 1) {
                volume /= 100;
            }
            control.setGain(volume);
            isPlaying = true;
            isPaused = false;
            event = Event.NEW_TRACK_PLAYING;
            updateObservers();
        } else if (isPaused) {
            resume();
            event = Event.RESUMED;
            updateObservers();
        } else {
            pause();
            event = Event.PAUSED;
            updateObservers();
        }
    }

    public void play(byte[] fileBytes) throws BasicPlayerException {

        if (!isPlaying && !isPaused) {
            control.open(new ByteArrayInputStream(fileBytes));
            // Start playback in a thread.
            control.play();

            //setting the volume
            if (volume > 1) {
                volume /= 100;
            }
            control.setGain(volume);
            isPlaying = true;
            isPaused = false;
            event = Event.NEW_TRACK_PLAYING;
            updateObservers();
        } else if (isPaused) {
            resume();
            event = Event.RESUMED;
            updateObservers();
        } else {
            pause();
            event = Event.PAUSED;
            updateObservers();
        }
    }

    /**
     * Method for pausing the player
     * @throws BasicPlayerException
     */
    public void pause() throws BasicPlayerException {
        if (isPlaying) {
            control.pause();
            isPaused = true;
            isPlaying = false;
            event = Event.PAUSED;
            updateObservers();
        }
    }

    /**
     * Stops the player if it's playing, otherwise don't execute any action
     * @throws BasicPlayerException
     */
    public void stop() throws BasicPlayerException {
        control.stop();
        isPlaying = false;
        isPaused = false;
        filePlayling = "";
        event = Event.STOPPED;
        updateObservers();
    }

    /**
     * Open callback, stream is ready to play.
     *
     * properties map includes audio format dependant features such as
     * bitrate, duration, frequency, channels, number of frames, vbr flag,
     * id3v2/id3v1 (for MP3 only), comments (for Ogg Vorbis), ...
     *
     * @param stream could be File, URL or InputStream
     * @param properties audio stream properties.
     */
    @Override
    public void opened(Object stream, Map properties) {
        // Pay attention to properties. It's useful to get duration,
        // bitrate, channels, even tag such as ID3v2.
        System.out.println("opened : " + properties.toString());

        //for now just saving the properties
        fileProperties = properties;
        event = Event.OPENED;
    }

    /**
     * Progress callback while playing.
     *
     * This method is called severals time per seconds while playing.
     * properties map includes audio format features such as
     * instant bitrate, microseconds position, current frame number, ...
     *
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        bytesReaded = bytesread;
        playingProperties = properties;
    }

    /**
     * Notification callback for basicplayer events such as opened, eom ...
     *
     * @param event
     */
    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        //nothing here to do
        if (event.getCode() == BasicPlayerEvent.STOPPED) {
            isPlaying = false;
            isPaused = false;
            filePlayling = "";
            this.event = Event.STOPPED;
            updateObservers();
        }
    }

    /**
     * A handle to the BasicPlayer, plugins may control the player through
     * the controller (play, stop, ...)
     * @param controller : a handle to the player
     */
    @Override
    public void setController(BasicController controller) {
        control = controller;
    }

    /**
     * This allow the user to skip a part of the music and place it wherever he
     * wants
     * @param millis
     * @throws BasicPlayerException
     */
    public void seek(int millis) throws BasicPlayerException {
        long trackDuration = Long.parseLong(fileProperties.get("duration").toString()) / 1000;
        trackDuration /= 1000;
        long mp3LenghtBytes = Long.parseLong(fileProperties.get("mp3.length.bytes").toString());

        long value = millis * mp3LenghtBytes;
        value = value / trackDuration;

        player.seek(value);
        event = Event.SEEKED;
        updateObservers();
    }

    /**
     * This method retrieves the total time of the track (the length of the track)
     * @return the length of the track
     */
    public int getTrackTotalTime() {
        return (Integer) fileProperties.get("duration");
    }

    /**
     * This method retrieves the position of the track is being played -
     * the time passed since the beginning of the track
     * @return the time position of the track is being played
     */
    public int getPlayerTime() {
        return (Integer) playingProperties.get("mp3.position.byte");
    }

    /**
     * This method sets the actual volume of the player, based on a scale, useful for conversions between
     * different ranges.
     * @param value The new value of the volume
     */
    public void setVolume(float value) throws BasicPlayerException {
        if (player != null) {
            if (player.hasGainControl()) {
                //map the entrance volume to the volume range
                volume = value / 100;
                player.setGain(volume);
                event = Event.VOLUME_CHANGED;
                updateObservers();
            }
        }
    }

    /**
     * This method allows to inspect the value of the volume of the player.
     * If the volume is not available it return Float.MAX_VALUE
     * @return The current value of the volume, or Float.MAX_VALUE if not available
     */
    public float getVolume() {
        if (player != null) {
            if (player.hasGainControl()) {
                //map the entrance volume to the volume range
                volume = player.getGainValue();
            }
        }
        return volume;
    }

    private void resume() throws BasicPlayerException {
        control.resume();
        isPlaying = true;
        isPaused = false;
        event = Event.RESUMED;
        updateObservers();
    }

    /* Methods for the observer pattern */
    /**
     * Register an observer to listen to this player
     * @param obs
     */
    @Override
    public void registerObserver(Observer obs) {
        observers.add(obs);
    }

    /**
     * Unregister observer to stop listening to this player
     * @param obs
     */
    @Override
    public void unregisterObserver(Observer obs) {
        if (observers.contains(obs)) {
            observers.remove(obs);
        }
    }

    /**
     * Notify all the observers that this player has changed
     */
    @Override
    public void updateObservers() {
        for (Observer obs : observers) {
            obs.update(this, event);
        }
    }

    /**
     * Method that returns the string that represents the file being played
     * @return the filePlayling
     */
    public String getFilePlayling() {
        return filePlayling;
    }
}
