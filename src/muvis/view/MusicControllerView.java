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

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import muvis.util.Observable;
import muvis.view.controllers.MusicPlayerPlaylistController;
import muvis.Environment;
import muvis.audio.AudioMetadata;
import muvis.audio.MuVisAudioPlayer;
import muvis.audio.playlist.Playlist;
import muvis.util.JImagePanel;
import muvis.util.Observer;
import muvis.util.Util;
import muvis.view.controllers.MusicPlayerControllerInterface;
import muvis.view.controllers.MusicPlayerFilterController;
import muvis.view.controllers.MusicPlayerGeneralController;

/**
 * This class implements the ControllerView for the musicplayer, handling the
 * interface functions and methods.
 * @author Ricardo
 */
public class MusicControllerView extends MusicControllerViewUI
        implements Dockable, ActionListener, ChangeListener, Observer {

    /**
     * @return the playingType
     */
    public PlayingType getPlayingType() {
        return playingType;
    }

    public void playNextTrack() {
        getMusicPlayerActiveController().playNextTrack();
    }

    public void playPreviousTrack() {
        getMusicPlayerActiveController().playPreviousTrack();
    }

    public void stopPlayer() {
        getMusicPlayerActiveController().stopTrack();
        displayInfoPanel("Artist & album - not available", "Track name - not available", "Year - not available");
    }

    public void playTrack() {
        if (getMusicPlayerActiveController().isPlaying()) {
            getMusicPlayerActiveController().pauseTrack();
        } else {
            getMusicPlayerActiveController().playTrack();
        }
    }

    public void setPlayingType(PlayingType playingType) {
        if (PlayingType.FILTER_MODE.equals(playingType)){
            filteredTracksRadioButton.doClick();
        } else if (PlayingType.PLAYLIST_MODE.equals(playingType)) {
            playlistRadioButton.doClick();
        } else if (PlayingType.GENERAL_MODE.equals(playingType)){
            allTracksRadioButton.doClick();
        }
    }

    public enum PlayingType {

        PLAYLIST_MODE, FILTER_MODE, GENERAL_MODE, INDIVIDUAL_TRACK
    }

    //dockable key
    private DockKey key;
    //Controller for the various playing modes available
    private MusicPlayerControllerInterface playlistModeController;
    private MusicPlayerControllerInterface filteredTracksModeController;
    private MusicPlayerControllerInterface allTracksModeController;
    //The active controller for this player
    private MusicPlayerControllerInterface activeController;
    //Parent JFrame
    //Timer for updating the musicTimelineSlider
    private Timer musicTimelineTimer;
    private int timelineSliderValue = 0;
    private PlayingType playingType;

    public MusicControllerView(JFrame parent) {

        //initializing fields
        this.playlistModeController = new MusicPlayerPlaylistController();
        this.filteredTracksModeController = new MusicPlayerFilterController();
        this.allTracksModeController = new MusicPlayerGeneralController();
        this.activeController = playlistModeController;
        activeController.setEnable(true);

        playingType = PlayingType.PLAYLIST_MODE;

        filteredTracksRadioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    activeController.stopTrack();
                } catch (Exception ex){}
                setMusicPlayerActiveController(filteredTracksModeController);
                playingType = PlayingType.FILTER_MODE;
            }
        });
        
        playlistRadioButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    activeController.stopTrack();
                } catch (Exception ex){}
                setMusicPlayerActiveController(playlistModeController);
                playingType = PlayingType.PLAYLIST_MODE;
            }
        });
        
        allTracksRadioButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    activeController.stopTrack();
                } catch (Exception ex){}
                setMusicPlayerActiveController(allTracksModeController);
                playingType = PlayingType.GENERAL_MODE;
            }
        });

        albumCoverPanel.addMouseListener( new MouseListener() {

            JFrame albumCoverPreview;

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2){
                    BufferedImage image = ((JImagePanel)e.getSource()).getImage();

                    if (albumCoverPreview == null || !albumCoverPreview.isVisible()){
                        albumCoverPreview = new JFrame("Album Cover");
                        JImagePanel albumPreviewPanel = new JImagePanel(image, 0, 0,image.getWidth(), image.getHeight());
                        albumCoverPreview.add(albumPreviewPanel);
                        albumCoverPreview.setSize( new Dimension(image.getWidth(), image.getHeight()));
                        albumCoverPreview.setVisible(true);
                    }
                }
            }

            private void displayAlbumCoverPreview(BufferedImage image){
                
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        //register objects and listeners
        Environment.getWorkspaceInstance().getAudioPlayer().registerObserver(this);
        Environment.getWorkspaceInstance().getAudioPlaylist().registerObserver(this);
        playTrackButton.addActionListener(this);
        previousTrackButton.addActionListener(this);
        nextTrackButton.addActionListener(this);
        stopPlayerButton.addActionListener(this);
        volumeSlider.addChangeListener(this);

        //musicTimelineSlider.setMajorTickSpacing(60);
        //musicTimelineSlider.setMinorTickSpacing(1);
        musicTimelineSlider.setPaintTicks(true);
        //musicTimelineSlider.setPaintLabels(true);

        musicTimelineSlider.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    timelineSliderValue = musicTimelineSlider.getValue();
                    Environment.getWorkspaceInstance().getAudioPlayer().seek(timelineSliderValue);
                    musicTimelineTimer.restart();
                } catch (BasicPlayerException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        //setting the volume
        volumeSlider.setValue((int) Environment.getWorkspaceInstance().getAudioPlayer().getVolume());

        //Setting the properties for the timelineslider timer
        int delay = 1000; //milliseconds
        ActionListener taskPerformer = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                int value = musicTimelineSlider.getValue();
                if (value == 0) {
                    value = timelineSliderValue;
                }
                musicTimelineSlider.setValue(value + 1);
            }
        };
        musicTimelineTimer = new Timer(delay, taskPerformer);

        //initializing the dockable properties
        key = new DockKey("Music Player");
        key.setTooltip("Music Player Controls and playing information");
        key.setCloseEnabled(false);
        key.setMaximizeEnabled(false);
        key.setAutoHideBorder(DockingConstants.HIDE_BOTTOM);
        key.setFloatEnabled(true);
        key.setAutoHideEnabled(true);

        playlistRadioButton.setSelected(true);
    }

    @Override
    public DockKey getDockKey() {
        return key;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * Triggers an action in the objects registered in this handler
     * @param event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == playTrackButton) {
            playTrack();
        } else if (event.getSource() == nextTrackButton) {
            playNextTrack();
        } else if (event.getSource() == previousTrackButton) {
            playPreviousTrack();
        } else if (event.getSource() == stopPlayerButton) {
            stopPlayer();
        }
    }

    /**
     * Generic method for displaying information in the info panel
     * @param artistAlbum
     * @param trackName
     * @param trackYear
     */
    private void displayInfoPanel(String artistAlbum, String trackName, String trackYear) {
        artistAlbumInfoLabel.setText(artistAlbum);
        trackNameLabel.setText(trackName);
        trackyearLabel.setText(trackYear);
    }

    /*updates the info panel with the track being played - uses the controller
     * to get the information
     */
    private void updateInfoPanel() {

        AudioMetadata metadata = getMusicPlayerActiveController().getTrackPlayingMetadata();
        displayInfoPanel(metadata.getAuthor() + "-" + metadata.getAlbum(),
                metadata.getTitle(), metadata.getYear());
    }

    //updates the timelinesliderparameters
    private void updateTimelineParameters() {

        AudioMetadata metadata = getMusicPlayerActiveController().getTrackPlayingMetadata();

        int length = (int) metadata.getDuration();

        musicTimelineSlider.setMinimum(0);
        musicTimelineSlider.setMaximum(length);

        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(0), new JLabel("0"));
        labelTable.put(new Integer(length), new JLabel(Util.secondsToTimeDisplay(length)));
        musicTimelineSlider.setLabelTable(labelTable);

        key.setName("Music Player - Playing: " + metadata.getTitle() + " from " + metadata.getAuthor());
    }

    /**
     * Handling changes in some elements
     * @param e
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == volumeSlider) {
            try {
                //setting the volume to a new value
                getMusicPlayerActiveController().setPlayerVolume(volumeSlider.getValue());
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method is the implementation of the observer pattern, for handling some
     * changes in the underlying model
     * @param obs
     */
    @Override
    public void update(Observable obs, Object arg) {
        MuVisAudioPlayer player = Environment.getWorkspaceInstance().getAudioPlayer();
        if (obs instanceof MuVisAudioPlayer) {
            if (MuVisAudioPlayer.Event.RESUMED.equals(arg) && player.isPlaying()) {
                //start the timer
                musicTimelineTimer.start();
                playTrackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Pause16.gif"))); // NOI18N
            } else if (MuVisAudioPlayer.Event.NEW_TRACK_PLAYING.equals(arg)) {
                //start the time but...
                musicTimelineTimer.start();
                //set the timelineslider because a new song is playing
                updateTimelineParameters();
                updateInfoPanel();
                musicTimelineSlider.setValue(0);
                
                playTrackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Pause16.gif"))); // NOI18N
                key.setNotification(true);
            } else if (MuVisAudioPlayer.Event.STOPPED.equals(arg)) {
                //stop the timer because the player is either stop or paused
                musicTimelineTimer.stop();
                musicTimelineSlider.setValue(0);
                playTrackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play16.gif"))); // NOI18N
                displayInfoPanel("Artist & album - not available",
                    "Track name - not available", "Year - not available");
                key.setName("Music Player");
            } else if (MuVisAudioPlayer.Event.PAUSED.equals(arg)) {
                musicTimelineTimer.stop();
                playTrackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play16.gif"))); // NOI18N
            } else if (MuVisAudioPlayer.Event.VOLUME_CHANGED.equals(arg)) {
                //volumeSlider.setValue((int) player.getVolume());
                //this must be mapped to the interface space
            }
        } else if (obs instanceof Playlist) {
            if (Playlist.Event.NEW_CURSOR.equals(arg)) {
                //a new cursor is available - a new item is going to be played perhaps
                //set the vars for accomplishing this
                updateInfoPanel();
                updateTimelineParameters();
            }
        }
    }

    public void setMusicPlayerFilterController(MusicPlayerControllerInterface filterController){
        filteredTracksModeController = filterController;
    }

    public MusicPlayerControllerInterface getMusicPlayerFilterController(){
        return filteredTracksModeController;
    }

    public void setMusicPlayerGeneralController(MusicPlayerControllerInterface generalController){
        allTracksModeController = generalController;
    }

    public MusicPlayerControllerInterface getMusicPlayerGeneralController(){
        return allTracksModeController;
    }

    public void setMusicPlayerPlaylistController(MusicPlayerControllerInterface playlistModeController) {
        this.playlistModeController = playlistModeController;
    }

    public MusicPlayerControllerInterface getMusicPlayerPlaylistController() {
        return playlistModeController;
    }

    /**
     * @return the activeController
     */
    public MusicPlayerControllerInterface getMusicPlayerActiveController() {
        return activeController;
    }

    /**
     * @param activeController the activeController to set
     */
    public void setMusicPlayerActiveController(MusicPlayerControllerInterface activeController) {
        this.activeController.setEnable(false); //disable the previous controller
        this.activeController = activeController;
        this.activeController.setEnable(true);
    }
}
