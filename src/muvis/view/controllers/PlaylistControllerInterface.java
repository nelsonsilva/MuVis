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
import muvis.audio.playlist.PlaylistItem;

/**
 * Interface for the Playlist Controller
 * @author Ricardo
 */
public interface PlaylistControllerInterface extends ControllerInterface {

    /**
     * Loading a playlist in the model
     * @param playlistname the playlist name
     * @param currentDirectory the directory where the playlist is
     */
    void loadPlaylist(String playlistname, String currentDirectory);

    /**
     * Removing an item from the playlist model.
     * @param item The item to be removed
     */
    void removeTrackFromPlaylist(PlaylistItem item);

    /**
     * Saving a playlist in the model.
     * @param playlistName The playlist name
     * @param currentDirectory The playlist directory folder
     */
    boolean savePlaylist(String playlistName, String currentDirectory);

    /**
     * Removes the speficied tracks from the playlist
     * @param items
     */
    public void removeTracksFromPlaylist(ArrayList<PlaylistItem> items);

}
