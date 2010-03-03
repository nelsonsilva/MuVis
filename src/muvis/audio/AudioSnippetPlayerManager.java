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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import muvis.Elements;
import muvis.Environment;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.MusicControllerView;
import muvis.view.controllers.MusicPlayerControllerInterface;

/**
 * Class that implements an audio player for playing snippets
 * @author Ricardo
 */
public class AudioSnippetPlayerManager {

    private TracksPreviewer tracksPreviewer;
    private ExecutorService threadPool;

    public AudioSnippetPlayerManager(MuVisAudioPlayer snippetPlayer) {
        tracksPreviewer = new TracksPreviewer(snippetPlayer);
        threadPool = Executors.newFixedThreadPool(1);
    }

    public void previewArtists(ArrayList<String> artistsToPreview, boolean b) {

        int maxTracks = 5;
        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();

        ArrayList<String> tracks = new ArrayList<String>(maxTracks * artistsToPreview.size());
        ArrayList<String> allTracks = new ArrayList<String>();

        for (String artist : artistsToPreview) {

            allTracks.addAll(dbManager.getArtistTracks(artist));

            int inc = allTracks.size() / maxTracks;
            if (inc == 0) {
                inc = 1;
            }
            for (int i = 0; i < allTracks.size(); i += inc) {
                tracks.add(allTracks.get(i));
            }
            allTracks.clear();
        }

        tracksPreviewer.setTracks(tracks);
        threadPool.execute(tracksPreviewer);
    }

    public void previewTrack(int trackId) {

        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        String filename = dbManager.getFilename(trackId);

        ArrayList<String> tracks = new ArrayList<String>();
        tracks.add(filename);

        tracksPreviewer.setTracks(tracks);
        threadPool.execute(tracksPreviewer);
    }

    public void previewTracks(ArrayList<Integer> trackIds) {

        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();

        ArrayList<String> tracks = new ArrayList<String>();
        for (int trackId : trackIds) {
            tracks.add(dbManager.getFilename(trackId));
        }
        tracksPreviewer.setTracks(tracks);
        threadPool.execute(tracksPreviewer);
    }

    public void previewArtist(String artistName) {

        int maxTracks = 5;
        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();

        ArrayList<String> tracks = new ArrayList<String>(maxTracks);
        ArrayList<String> allTracks = dbManager.getArtistTracks(artistName);

        int inc = allTracks.size() / maxTracks;
        if (inc == 0) {
            inc = 1;
        }
        for (int i = 0; i < allTracks.size(); i += inc) {
            tracks.add(allTracks.get(i));
        }

        tracksPreviewer.setTracks(tracks);
        threadPool.execute(tracksPreviewer);
    }

    public void previewArtist(int trackId) {

        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        String artistName = dbManager.getArtistName(trackId);
        previewArtist(artistName);
    }

    public void previewArtists(ArrayList<Integer> tracksId) {

        int maxTracks = 5;
        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();

        ArrayList<String> tracks = new ArrayList<String>(maxTracks * tracksId.size());
        for (int trackId : tracksId) {
            String artistName = dbManager.getArtistName(trackId);
            ArrayList<String> artistTracks = dbManager.getArtistTracks(artistName);

            int inc = artistTracks.size() / maxTracks;
            if (inc == 0) {
                inc = 1;
            }
            for (int i = 0; i < artistTracks.size(); i += inc) {
                tracks.add(artistTracks.get(i));
            }
        }

        tracksPreviewer.setTracks(tracks);
        threadPool.execute(tracksPreviewer);
    }

    public void previewAlbum(int trackId) {

        int maxTracks = 3;
        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();

        String albumName = dbManager.getAlbumName(trackId);

        ArrayList<String> albumTracks = dbManager.getAlbumTracks(albumName);
        int inc = albumTracks.size() / maxTracks;
        if (inc == 0) {
            inc = 1;
        }

        ArrayList<String> tracks = new ArrayList<String>();
        for (int i = 0; i < albumTracks.size(); i += inc) {
            tracks.add(albumTracks.get(i));
        }

        tracksPreviewer.setTracks(tracks);
        threadPool.execute(tracksPreviewer);
    }

    public void previewAlbums(ArrayList<Integer> trackIds) {

        int maxTracks = 3;
        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();

        ArrayList<String> tracks = new ArrayList<String>(maxTracks * trackIds.size());
        for (int trackId : trackIds) {

            String albumName = dbManager.getAlbumName(trackId);
            ArrayList<String> albumTracks = dbManager.getAlbumTracks(albumName);
            int inc = albumTracks.size() / maxTracks;
            if (inc == 0) {
                inc = 1;
            }

            for (int i = 0; i < albumTracks.size(); i += inc) {
                tracks.add(albumTracks.get(i));
            }
        }
        tracksPreviewer.setTracks(tracks);
        threadPool.execute(tracksPreviewer);
    }
}

class TracksPreviewer implements Runnable, Observer, MusicPlayerControllerInterface {

    private MuVisAudioPlayer snippetPlayer;
    private ArrayList<String> tracks;
    private Iterator<String> it;
    private String trackPlaying;
    private boolean isPreviewing;
    private boolean enabled;
    private MusicPlayerControllerInterface filterController,  generalController,  playlistController;
    private MusicPlayerControllerInterface activeController;

    public TracksPreviewer(MuVisAudioPlayer player) {
        this.snippetPlayer = player;
        player.registerObserver(this);
        isPreviewing = false;
        trackPlaying = "";
        enabled = false;
    }

    public void setTracks(ArrayList<String> tracks) {
        this.tracks = tracks;
        it = this.tracks.iterator();
    }

    private void setMusicPlayerSnippetController() {
        if (filterController == null) {
            MusicControllerView view =
                    (MusicControllerView) Environment.getWorkspaceInstance().
                    getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);

            //save the previously music controllers
            filterController = view.getMusicPlayerFilterController();
            generalController = view.getMusicPlayerGeneralController();
            playlistController = view.getMusicPlayerPlaylistController();

            //saves the current active controller
            activeController = view.getMusicPlayerActiveController();

            filterController.setEnable(false);
            generalController.setEnable(false);
            playlistController.setEnable(false);

            enabled = true;

            view.setMusicPlayerFilterController(this);
            view.setMusicPlayerGeneralController(this);
            view.setMusicPlayerPlaylistController(this);
            view.setMusicPlayerActiveController(this);
        }
    }

    private void resetMusicPlayerSnippetController() {

        if (filterController != null) {

            MusicControllerView view =
                    (MusicControllerView) Environment.getWorkspaceInstance().
                    getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);

            MusicControllerView.PlayingType type = view.getPlayingType();
            if (type.equals(MusicControllerView.PlayingType.FILTER_MODE)) {
                view.setMusicPlayerActiveController(filterController);
            } else if (type.equals(MusicControllerView.PlayingType.GENERAL_MODE)) {
                view.setMusicPlayerActiveController(generalController);
            } else if (type.equals(MusicControllerView.PlayingType.PLAYLIST_MODE)) {
                view.setMusicPlayerActiveController(playlistController);
            }
            view.setMusicPlayerFilterController(filterController);
            view.setMusicPlayerGeneralController(generalController);
            view.setMusicPlayerPlaylistController(playlistController);

            filterController.setEnable(false);
            generalController.setEnable(false);
            playlistController.setEnable(false);

            if (filterController.equals(activeController)){
                filterController.setEnable(true);
            } else if (playlistController.equals(activeController)){
                playlistController.setEnable(true);
            } else if (generalController.equals(activeController)){
                generalController.setEnable(true);
            }

            if (filterController.isPlaying()){
                filterController.stopTrack();
            } else if (generalController.isPlaying()){
                generalController.stopTrack();
            } else if (playlistController.isPlaying()){
                playlistController.stopTrack();
            }

            enabled = false;

            filterController = null;
            generalController = null;
            playlistController = null;
        }
    }

    @Override
    public void run() {
        setMusicPlayerSnippetController();
        isPreviewing = false;
        if (snippetPlayer.isPlaying()) {
            try {
                //ignore the update request for the stop
                snippetPlayer.stop();
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        }
        preview();
    }

    private void preview() {
        if (it != null && it.hasNext()) {
            String nextTrack = it.next();
            trackPlaying = nextTrack;
            enabled = true;
            previewTrack(nextTrack);
        } else {
            isPreviewing = false;
            enabled = false;
            resetMusicPlayerSnippetController();
        }
    }

    private void previewTrack(String filename) {

        try {
            byte[] snippet = MP3AudioSnippetExtractor.extractAudioSnippet(filename);
            snippetPlayer.play(snippet);
            isPreviewing = true;
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        } catch (Exception e){
            System.out.println("Cannot play the snippet, from file: " + filename);
        }
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (enabled) {
            if (obs instanceof MuVisAudioPlayer) {
                if (MuVisAudioPlayer.Event.STOPPED.equals(arg) && isPreviewing) {
                    if (isPreviewing) {
                        preview();
                    } else {
                        isPreviewing = true;
                    }
                }
            }
        }
    }

    @Override
    public AudioMetadata getTrackPlayingMetadata() {
        AudioMetadata metadata = Environment.getWorkspaceInstance().getDatabaseManager().getTrackMetadata(trackPlaying);
        return metadata;
    }

    @Override
    public void pauseTrack() {
        //not implemented
    }

    @Override
    public void playNextTrack() {
        //not implemented
    }

    @Override
    public void playPreviousTrack() {
        //not implemented
    }

    @Override
    public void playTrack() {
        //not implemented
    }

    @Override
    public void setPlayerVolume(int value) throws BasicPlayerException {
        snippetPlayer.setVolume(value);
    }

    @Override
    public void stopTrack() {
        try {
            isPreviewing = false;
            snippetPlayer.stop();
            resetMusicPlayerSnippetController();
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setEnable(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isPlaying() {
        if (enabled) {
            return snippetPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}