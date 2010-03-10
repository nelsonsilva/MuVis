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
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import muvis.Environment;
import muvis.audio.AudioMetadata;
import muvis.audio.playlist.PlaylistItem;
import muvis.database.MusicLibraryDatabaseManager;

/**
 * Controller for the list view interface
 * @author Ricardo
 */
public class ListViewTableViewController implements ControllerInterface {

    private MusicLibraryDatabaseManager dbManager;

    public ListViewTableViewController() {
        dbManager = Environment.getEnvironmentInstance().getDatabaseManager();
    }

    public void addTrackToPlaylist(int trackId) {

        AudioMetadata metadata = dbManager.getTrackMetadata(trackId);
        PlaylistItem pliItem = new PlaylistItem(dbManager.getFilename(trackId), "", metadata);
        Environment.getEnvironmentInstance().getAudioPlaylist().appendItem(pliItem);
    }

    public void addAlbumToPlaylist(int trackId) {
        String album = dbManager.getAlbumName(trackId);
        ArrayList<String> albumTracks = dbManager.getAlbumTracks(album);

        for (String track : albumTracks) {
            AudioMetadata metadata = dbManager.getTrackMetadata(track);
            PlaylistItem pliItem = new PlaylistItem(track, "", metadata);
            Environment.getEnvironmentInstance().getAudioPlaylist().appendItem(pliItem);
        }
    }

    public void addAlbumToPlaylist(String artist, String album) {
        ArrayList<String> albumTracks = dbManager.getAlbumTracks(artist, album);

        for (String track : albumTracks) {
            AudioMetadata metadata = dbManager.getTrackMetadata(track);
            PlaylistItem pliItem = new PlaylistItem(track, "", metadata);
            Environment.getEnvironmentInstance().getAudioPlaylist().appendItem(pliItem);
         }
    }

    public void addArtistToPlaylist(int trackId) {
        String artist = dbManager.getArtistName(trackId);
        ArrayList<String> artistTracks = dbManager.getArtistTracks(artist);

        for (String track : artistTracks) {
            AudioMetadata metadata = dbManager.getTrackMetadata(track);
            PlaylistItem pliItem = new PlaylistItem(track, "", metadata);
            Environment.getEnvironmentInstance().getAudioPlaylist().appendItem(pliItem);
        }
    }
}
