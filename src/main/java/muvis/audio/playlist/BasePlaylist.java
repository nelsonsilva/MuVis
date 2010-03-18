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

package muvis.audio.playlist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import muvis.audio.AudioMetadata;
import muvis.audio.AudioMetadataExtractor;
import muvis.audio.MP3AudioMetadataExtractor;
import muvis.util.Observer;
import muvis.util.Util;

public class BasePlaylist implements Playlist {

    /**
     * Vector that holds the playlist items
     */
    protected Vector<PlaylistItem> playlist;
    /**
     * Vector that holds the observers of this playlist
     */
    protected Vector<Observer> observers;
    /**
     * Index that indicates the currently selected item
     */
    protected int cursor;

    /**
     * Item associated with the current cursor (for internal purposes)
     */
    protected PlaylistItem cursorItem;

    /**
     * Indicates if the playlist has changed: new items added
     */
    protected boolean isModified;
    /**
     * String that represents the current directory of this playlist
     */
    protected String currentDirectory;
    /**
     * This event is used to update the observers
     */
    private Event event;

    /**
     * Constructs an object of type BasePlaylist
     * @param base The PApplet necessary for this playlist to be able to load metadata from the music file items
     */
    public BasePlaylist() {
        playlist = new Vector<PlaylistItem>();
        observers = new Vector<Observer>();
        currentDirectory = "";
        cursor = 0;
        cursorItem = null;
        event = Event.NOTHING;
    }

    /**
     * Sets the Current Directory of the Playlist to the parameter
     * @param directory
     */
    @Override
    public void setCurrentDirectory(String directory) {
        currentDirectory = directory;
    }

    /**
     * Gets the Current Directory of the Playlist
     * @return currentDirectory
     */
    @Override
    public String getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Add a playlist item to a specific location in the playlist internal
     * structure.
     * @param pli
     * @param pos
     */
    @Override
    public void addItemAt(PlaylistItem pli, int pos) {
        //The item can only be added if that index is valid
        if (pos >= 0 && pos < playlist.size()) {
            playlist.add(pos, pli);
            isModified = true;

            recalculateCursor();

            event = Event.PLAYLIST_RESIZED;
            updateObservers();
        }
    }

    @Override
    public void addItemsAt(Map<Integer, PlaylistItem> items) {
        for (Map.Entry<Integer, PlaylistItem> it : items.entrySet()){
            if (it.getKey() >= 0 && it.getKey() < playlist.size()) {
                playlist.add(it.getKey(), it.getValue());
            }
        }
        isModified = true;

        recalculateCursor();

        event = Event.PLAYLIST_RESIZED;
        updateObservers();
    }

    @Override
    public void removeItemsAt(ArrayList<Integer> positions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Append a new item to the playlist
     * @param pli
     */
    @Override
    public void appendItem(PlaylistItem pli) {
        playlist.add(pli);
        isModified = true;
        event = Event.PLAYLIST_RESIZED;
        updateObservers();
    }

    /**
     * Appends the specified items to the playlist
     * @param items
     */
    @Override
    public void appendItems(ArrayList<PlaylistItem> items) {
        for (PlaylistItem it : items){
            playlist.add((it));
        }
        isModified = true;
        event = Event.PLAYLIST_RESIZED;
        updateObservers();
    }

    /**
     * Restarts the cursor of the playlist
     */
    @Override
    public void begin() {
        cursor = 0;
        if (playlist.size() > 0){
            cursorItem = playlist.firstElement();
        } else cursorItem = null;
        event = Event.NEW_CURSOR;
        updateObservers();
    }

    /**
     * Gets all the items in this playlist
     * @return
     */
    @Override
    public Collection<PlaylistItem> getAllItems() {
        return playlist;
    }

    /**
     * Gets the currently selected playlist item
     * @return
     */
    @Override
    public PlaylistItem getCursor() {
        return playlist.get(cursor);
    }

    /**
     * This specific method returns the index of pli, but if the playlist doesn't have this item, returns -1
     */
    @Override
    public int getIndex(PlaylistItem pli) {

        if (playlist.contains(pli)) {
            return playlist.indexOf(pli);
        } else {
            return -1;
        }
    }

    /**
     * Returns null if there isn't an item at position pos
     */
    @Override
    public PlaylistItem getItemAt(int pos) {
        if (pos >= 0 && pos < playlist.size()) {
            return playlist.elementAt(pos);
        } else {
            return null;
        }
    }

    /**
     * Gets playlist size
     * @return
     */
    @Override
    public int getPlaylistSize() {
        return playlist.size();
    }

    /**
     * Gets the selected index
     * @return
     */
    @Override
    public int getSelectedIndex() {
        return cursor;
    }

    /**
     * Indicates if the playlist has changed
     * @return
     */
    @Override
    public boolean isModified() {
        return isModified;
    }

    /**
     * Loads playlist as M3U format.
     * @return true if the playlist was correctly loaded or false otherwise
     */
    @Override
    public boolean load(String filename, String directory) {
        setModified(true);
        boolean loaded = false;
        if ((filename != null) && (filename.toLowerCase().endsWith(".m3u"))) {
            loaded = loadM3U(filename, directory);
        } else {
            System.out.println("Can't load the playlist");
        }
        if (loaded) {
            event = Event.PLAYLIST_LOADED;
            updateObservers();
        }
        return loaded;
    }

    /**
     * Load playlist from M3U format.
     *
     * @param filename
     * @return
     */
    protected boolean loadM3U(String filename, String directory) {
        AudioMetadataExtractor metatadaExtractor = new MP3AudioMetadataExtractor();
        boolean loaded = false;
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(directory + filename));

            String line = null;
            String songName = null;
            String songFile = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (line.startsWith("#")) {
                    if (line.toUpperCase().startsWith("#EXTINF"));
                } else {
                    songFile = line;
                    if (songName == null) {
                        songName = songFile;
                    }
                    PlaylistItem pli = null;
                    // File.
                    //Try relative path
                    File f = new File(directory + songName);
                    if (f.exists()) {

                        AudioMetadata meta = metatadaExtractor.getAudioMetadata(directory + songName);

                        pli = new PlaylistItem(songName, directory, meta);
                    } else {
                        // Try absolute path
                        f = new File(songName);
                        if (f.exists()) {

                            AudioMetadata meta = metatadaExtractor.getAudioMetadata(songName);
                            String path = f.getParent() + Util.getOSEscapeSequence();
                            CharSequence pathSeq = path.subSequence(0, path.length());
                            songName = songName.replace(pathSeq, "");

                            pli = new PlaylistItem(songName,
                                    path, meta);
                        }
                    }
                    if (pli != null) {
                        this.appendItem(pli);
                    }
                    songFile = null;
                    songName = null;

                }
                loaded = true;
            }
        } catch (Exception e) {
            // this here should give user a nice message
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ioe) {
                System.out.println("Can't close .m3u playlist\n" + ioe.toString());
            }
        }
        return loaded;
    }

    /**
     * Next cursor
     */
    @Override
    public void nextCursor() {
        cursor++;
        if (cursor >= playlist.size()) {
            cursor = 0;
        }

        if (playlist.size() > 0){
            cursorItem = playlist.get(cursor);
        } else cursorItem = null;
        
        event = Event.NEW_CURSOR;
        updateObservers();
    }

    @Override
    public void previousCursor() {
        cursor--;
        if (cursor < 0) {
            cursor = playlist.size() - 1;
        }

        if (playlist.size() > 0){
            cursorItem = playlist.get(cursor);
        } else cursorItem = null;

        event = Event.NEW_CURSOR;
        updateObservers();
    }

    @Override
    public void removeAllItems() {
        playlist.clear();
        event = Event.PLAYLIST_RESIZED;
        updateObservers();
        cursor = 0;
        cursorItem = null;
    }

    @Override
    public void removeItem(PlaylistItem pli) {
        if (playlist.contains(pli)) {		//only removes the item if it effectively exists
            playlist.remove(pli);

            recalculateCursor();

            event = Event.PLAYLIST_RESIZED;
            updateObservers();
        }
    }

    private void recalculateCursor(){

        boolean found = false;
        for (PlaylistItem it : playlist){
            if(cursorItem != null && it.equals(cursorItem)){
                found = true;
                cursor = getIndex(cursorItem);

                break;
            }
        }
        if (!found){
            cursor = 0;
            if (playlist.size() > 0){
                cursorItem = getItemAt(cursor);
            } else cursorItem = null;
        }
    }

    @Override
    public void removeItems(ArrayList<PlaylistItem> items) {
        for (PlaylistItem it : items){
            if (playlist.contains(it)){
                playlist.remove(it);
            }
        }

        recalculateCursor();

        event = Event.PLAYLIST_RESIZED;
        updateObservers();
    }

    @Override
    public void removeItemAt(int pos) {
        if (pos >= 0 && pos < playlist.size()) {		//checks if the position is valid
            playlist.remove(pos);

            recalculateCursor();

            event = Event.PLAYLIST_RESIZED;
            updateObservers();
        }
    }

    /**
     * Saves playlist in M3U format.
     * For each entry in the playlist, this method saves the full location of the file.
     * This depends on the plataform used to save the playlist.
     */
    @Override
    public boolean save(String filename, String directory) {
        if (playlist != null) {
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(directory + filename + ".m3u"));
                bw.write("#Playlist created by MuVis: because music visualization matters!");
                bw.newLine();
                bw.write("#EXTM3U");
                bw.newLine();
                Iterator<PlaylistItem> it = playlist.iterator();
                while (it.hasNext()) {
                    bw.write("#EXTINF:");
                    PlaylistItem pli = (PlaylistItem) it.next();
                    int seconds = (int) Math.floor((pli.getLength() * 0.001));
                    bw.write(seconds + "," + pli.getArtistTrackName());
                    bw.newLine();
                    bw.write(pli.getLocation() + pli.getName());
                    bw.newLine();
                }
                event = Event.PLAYLIST_SAVED;
                updateObservers();
                return true;
            } catch (IOException e) {
                System.out.println("Can't save playlist" + e.toString());
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException ioe) {
                    System.out.println("Can't close playlist." + ioe.toString());
                }
            }
        }
        return false;
    }

    @Override
    public void setCursor(int index) {
        //The cursor must be clamped to a valid position
        if (index < 0 || index >= playlist.size()) {
            cursor = 0;
            if (playlist.size() > 0){
                cursorItem = playlist.firstElement();
            } else cursorItem = null;
        } else {
            cursor = index;
            cursorItem = playlist.get(index);
        }

        event = Event.NEW_CURSOR;
        updateObservers();
    }

    @Override
    public void updateCursor(int index) {
        //The cursor must be clamped to a valid position
        if (index < 0 || index >= playlist.size()) {
            cursor = 0;
            if (playlist.size() > 0){
                cursorItem = playlist.firstElement();
            } else cursorItem = null;
        } else {
            cursor = index;
            cursorItem = playlist.get(index);
        }

        event = Event.PLAYLIST_UPDATED;
        updateObservers();
    }

    @Override
    public boolean setModified(boolean set) {
        isModified = set;
        return set;
    }

    @Override
    /**
     * Method that shuffles the playlist
     */
    public void shuffle() {
        PlaylistItemComparator comparator = new PlaylistItemComparator(0);
        Collections.sort(playlist, comparator);
        event = Event.GENERAL_MODIFIED;
        updateObservers();
    }

    @Override
    /**
     * Method for sorting the playlist, acording to some sortmodes
     */
    public void sortItems(int sortmode) {
        PlaylistItemComparator comparator = new PlaylistItemComparator(sortmode);
        Collections.sort(playlist, comparator);
        event = Event.GENERAL_MODIFIED;
        updateObservers();
    }

    @Override
    public void registerObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public void unregisterObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void updateObservers() {
        for (Observer o : observers) {
            o.update(this, event);
        }
    }

    @Override
    public long getTotalPlayingTime() {
        long totalTime = 0; //seconds

        for (PlaylistItem item : playlist) {
            totalTime += item.getLength();
        }
        return totalTime;
    }
}
/**
 * Simple playlist comparator just for shuffle and simple ordering
 * Implements Comparator<PlaylistItem> because it comparates playlist items
 * @author Ricardo
 */
class PlaylistItemComparator implements Comparator<PlaylistItem> {

    private int sortmode;
    private Random rand;

    @Override
    public int compare(PlaylistItem pli1, PlaylistItem pli2) {

        int result = 0;
        if (sortmode == -1) {
            result = pli1.getName().compareTo(pli2.getName());
        } else if (sortmode == 0) {
            result = rand.nextInt(2);
            if (result > 1) //if we have a value higher than 1, then use inverse order
            {
                result = -1;
            }
        } else {
            result = pli1.getName().compareTo(pli2.getName());
            if (result == 1) {
                result = 0;
            } else if (result == -1) {
                result = 1;
            }
        }

        return result;
    }

    /**
     * Valid sortmodes: -1, 0, 1
     * -1: descendente
     * 0: random
     * 1: ascendente
     * @param sortmode
     */
    public PlaylistItemComparator(int sortmode) {
        if (sortmode > 1 || sortmode < -1) {
            this.sortmode = 0;	//random by default
        } else {
            this.sortmode = sortmode;
        }

        rand = new Random();
    }
}
