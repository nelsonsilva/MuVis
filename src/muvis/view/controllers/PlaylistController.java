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
import muvis.Workspace;
import muvis.audio.playlist.Playlist;
import muvis.audio.playlist.PlaylistItem;
import muvis.util.Util;

/**
 * This class implements the Controller Interface.
 * It's the interface between the view and model to do operations in the
 * model.
 * Reads from the model are made directly or from a proxy.
 * @author Ricardo
 */
public class PlaylistController implements PlaylistControllerInterface {

    /**
     * Loading a playlist in the model
     * @param playlistname the playlist name
     * @param currentDirectory the directory where the playlist is
     */
    @Override
    public void loadPlaylist(String playlistname, String currentDirectory){
        Workspace workspace = Workspace.getWorkspaceInstance();
        workspace.getAudioPlaylist().removeAllItems();
        workspace.getAudioPlaylist().load(playlistname, currentDirectory + Util.getOSEscapeSequence());
    }

    /**
     * Saving a playlist in the model.
     * @param playlistName The playlist name
     * @param currentDirectory The playlist directory folder
     */
    @Override
    public boolean savePlaylist(String playlistName, String currentDirectory){
        Workspace workspace = Workspace.getWorkspaceInstance();
        return workspace.getAudioPlaylist().save(playlistName, currentDirectory + Util.getOSEscapeSequence());
    }

    /**
     * Removing an item from the playlist model.
     * @param item The item to be removed
     */
    @Override
    public void removeTrackFromPlaylist(PlaylistItem item){
        Workspace workspace = Workspace.getWorkspaceInstance();
        workspace.getAudioPlaylist().removeItem(item);
    }

    @Override
    public void removeTracksFromPlaylist(ArrayList<PlaylistItem> items) {
        Playlist playlist = Workspace.getWorkspaceInstance().getAudioPlaylist();
        playlist.removeItems(items);
    }
}
