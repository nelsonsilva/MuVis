/*
 * Playlist
 *
 * JavaZOOM : jlgui@javazoom.net
 *            http://www.javazoom.net
 * 
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *   
 *----------------------------------------------------------------------
 * Modified by Ricardo Dias
 */
package muvis.audio.playlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import muvis.util.Observable;

/**
 * Playlist.
 * This interface defines methods that a playlist should implement.<br>
 * A playlist provides a collection of item to play and a cursor to know
 * which item is playing.
 * There are methods to load and save a playlist in format M3U.
 * @author Ricardo
 * @version 1.0
 */
public interface Playlist extends Observable
{
    public enum Event {
        NEW_CURSOR, PLAYLIST_RESIZED, PLAYLIST_LOADED, PLAYLIST_SAVED, GENERAL_MODIFIED,
        PLAYLIST_UPDATED, NOTHING
    }

    // Next methods will be called by the Playlist UI.
    /**
     * Loads playlist.
     */
    public boolean load(String filename, String directory);

    /**
     * Saves playlist.
     */
    public boolean save(String filename, String directory);

    /**
     * Adds item at a given position in the playlist.
     */
    public void addItemAt(PlaylistItem pli, int pos);

    /**
     * Adds the items to the given positions in the playlist
     * @param items
     */
    public void addItemsAt(Map<Integer, PlaylistItem> items);

    /**
     * Searchs and removes item from the playlist.
     */
    public void removeItem(PlaylistItem pli);

    /**
     *  Removes all the items from the playlist
     * @param items
     */
    public void removeItems(ArrayList<PlaylistItem> items);

    /**
     * Removes item at a given position from the playlist.
     */
    public void removeItemAt(int pos);

    /**
     * Removes the items in the specified positions
     * @param positions
     */
    public void removeItemsAt(ArrayList<Integer> positions);

    /**
     * Removes all items in the playlist.
     */
    public void removeAllItems();

    /**
     * Append item at the end of the playlist.
     */
    public void appendItem(PlaylistItem pli);

    /**
     * Appends the items at the end of the playlist
     * @param items
     */
    public void appendItems(ArrayList<PlaylistItem> items);

    /**
     * Sorts items of the playlist.
     */
    public void sortItems(int sortmode);

    /**
     * Returns item at a given position from the playlist.
     */
    public PlaylistItem getItemAt(int pos);

    /**
     * Returns a collection of playlist items.
     */
    public Collection<PlaylistItem> getAllItems();

    /**
     * Returns then number of items in the playlist.
     */
    public int getPlaylistSize();

    // Next methods will be used by the Player
    /**
     * Randomly re-arranges the playlist.
     */
    public void shuffle();

    /**
     * Returns item matching to the cursor.
     */
    public PlaylistItem getCursor();

    /**
     * Moves the cursor at the begining of the Playlist.
     */
    public void begin();

    /**
     * Returns item matching to the cursor.
     */
    public int getSelectedIndex();

    /**
     * Returns index of playlist item.
     */
    public int getIndex(PlaylistItem pli);

    /**
     * Computes cursor position (next).
     */
    public void nextCursor();

    /**
     * Computes cursor position (previous).
     */
    public void previousCursor();

    /**
     * Set the modification flag for the playlist
     */
    boolean setModified(boolean set);

    /**
     * Checks the modification flag
     */
    public boolean isModified();

    /**
     * Sets the playlist cursor
     * @param index
     */
    public void setCursor(int index);
    
    /**
     * Equals to the setCursor, but here the cursor is only updated, not changed
     * @param index
     */
    public void updateCursor(int index);

    /**
     * Sets the current directory of the playlist
     * @param directory
     */
    public void setCurrentDirectory(String directory);
    
    /**
     * Gets the current directory of the playlist
     * @param directory
     * @return
     */
    public String getCurrentDirectory();

    /**
     * Gets the total playing time for the playlist
     * @return the total playing time
     */
    public long getTotalPlayingTime();
    
}
