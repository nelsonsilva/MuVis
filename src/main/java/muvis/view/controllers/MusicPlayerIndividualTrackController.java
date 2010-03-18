/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package muvis.view.controllers;

import javazoom.jlgui.basicplayer.BasicPlayerException;
import muvis.Elements;
import muvis.audio.AudioMetadata;
import muvis.audio.MuVisAudioPlayer;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Observable;
import muvis.util.Observer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Ricardo
 */
public class MusicPlayerIndividualTrackController implements MusicPlayerControllerInterface, Observer {

    private MuVisAudioPlayer player;
    @Autowired private MusicLibraryDatabaseManager dbManager;
    private int trackId;
    private boolean isPlaying;
    private boolean enabled;

    public MusicPlayerIndividualTrackController(){
        isPlaying = false;
        enabled = false;
    }

    @Autowired 
    public void setPlayer(MuVisAudioPlayer player) {
        this.player = player;
         player.registerObserver(this);
    }

    public void setTrackId(int trackId){
        this.trackId = trackId;
    }

    @Override
    public AudioMetadata getTrackPlayingMetadata() {
        return dbManager.getTrackMetadata(trackId);
    }

    @Override
    public void playNextTrack() {
        //no operation
    }

    @Override
    public void playPreviousTrack() {
        //no operation
    }

    @Override
    public void playTrack() {

        try {
            if (player.isPlaying()){
                player.stop();
            }
            player.play(dbManager.getFilename(trackId));
            isPlaying = true;
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
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
        player.setVolume(value);
    }

    @Override
    public void stopTrack() {
        try {
            isPlaying = false;
            player.stop();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setEnable(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (enabled){
            if (obs instanceof MuVisAudioPlayer) {
                if (MuVisAudioPlayer.Event.STOPPED.equals(arg)) {
                }
            }
        }
    }

}
