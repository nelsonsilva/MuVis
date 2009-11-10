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

import muvis.audio.AudioMetadata;
import muvis.util.FileUtil;

public class PlaylistItem {

    /**
     * The name of the playlist item (example: filename.mp3)
     */
    protected String name = null;
    /**
     * The string for displaying in screen or anywhere else
     */
    protected String displayName = null;
    /**
     * The location of this item.
     */
    protected String location = null;
    /**
     * Indicates if this item is selected
     */
    protected boolean isSelected = false;
    /**
     * The metadata for this item
     */
    protected AudioMetadata metadata = null;

    /**
     * Contructor for playlist item.
     *
     * @param name     Song name to be displayed
     * @param location File
     * @param metadata   Some metadata properties of this song
     */
    public PlaylistItem(String name, String location, AudioMetadata metadata) {
        this.name = name;
        this.location = location;
        this.metadata = metadata;

        displayName = getFormattedDisplayName();
    }

    /**
     * Returns item name such as (hh:mm:ss) Title - Artist if available.
     *
     * @return
     */
    public String getFormattedName() {
        if (displayName == null) {
            if (metadata.getDuration() > 0) {
                String length = getFormattedLength();
                return "(" + length + ") " + name;
            } else {
                return name;
            }
        } else {
            return displayName;
        }
    }

    public String getArtistTrackName() {

        if (metadata != null) {
            return metadata.getAuthor() + " - " + metadata.getAlbum();
        }
        return "";
    }

    /**
     * Gets the simples name of the item.
     * @return simple name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the full name of the item - location+name
     * @return
     */
    public String getFullName() {
        return location + name;
    }

    /**
     * Gets the location of the item
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns playtime in seconds. If tag info is available then its playtime will be returned.
     *
     * @return playtime
     */
    public long getLength() {
        return metadata.getDuration();
    }

    /**
     * Return audio metadata from this file
     *
     * @return
     */
    public AudioMetadata getAudioMetaData() {
        return metadata;
    }

    /**
     * Selects this item
     * @param mode
     */
    public void setSelected(boolean mode) {
        isSelected = mode;
    }

    /**
     * Checks if this item is selected
     * @return
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Returns item lenght such as hh:mm:ss
     * @return formatted String
     */
    public String getFormattedLength() {
        long time = getLength();    	//time here is milliseconds
        String length = "";
        if (time > -1) {
            int seconds = (int) time; 	//converting the milliseconds to seconds
            int minutes = (int) Math.floor(seconds / 60);
            int hours = (int) Math.floor(minutes / 60);
            minutes = minutes - hours * 60;
            seconds = (int) (seconds - minutes * 60 - hours * 3600);
            // Hours.
            if (hours > 0) {
                length = length + FileUtil.rightPadString(hours + "", '0', 2) + ":";
            }
            length = length + FileUtil.rightPadString(minutes + "", '0', 2) + ":" + FileUtil.rightPadString(seconds + "", '0', 2);
        } else {
            length = "" + time;
        }
        return length;
    }

    /**
     * Returns item name such as (hh:mm:ss) Title - Artist
     *
     * @return formatted String.
     */
    public String getFormattedDisplayName() {
        if (metadata == null) {
            return null;
        } else {
            String length = getFormattedLength();
            if ((metadata.getTitle() != null) && (!metadata.getTitle().equals("")) && (metadata.getAuthor() != null) && (!metadata.getAuthor().equals(""))) {
                if (getLength() > 0) {
                    return ("(" + length + ") " + metadata.getTitle() + " - " + metadata.getAuthor());
                } else {
                    return (metadata.getTitle() + " - " + metadata.getAuthor());
                }
            } else if ((metadata.getTitle() != null) && (!metadata.getTitle().equals(""))) {
                if (getLength() > 0) {
                    return ("(" + length + ") " + metadata.getTitle());
                } else {
                    return (metadata.getTitle());
                }
            } else {
                if (getLength() > 0) {
                    return ("(" + length + ") " + name);
                } else {
                    return (name);
                }
            }
        }
    }

    /**
     * Sets the formatted display name for this item
     * @param fname
     */
    public void setFormattedDisplayName(String fname) {
        displayName = fname;
    }

    /**
     * Return item name such as hh:mm:ss,Title,Artist
     *
     * @return formatted String.
     */
    public String getM3UExtInf() {
        if (metadata == null) {
            return ("--:--" + "," + name);		//unknown music length
        } else {
            if ((metadata.getTitle() != null) && (metadata.getAuthor() != null)) {
                return (getLength() + "," + metadata.getTitle() + " - " + metadata.getAuthor());
            } else if (metadata.getTitle() != null) {
                return (getLength() + "," + metadata.getTitle());
            } else {
                return (metadata.getDuration() + "," + name);
            }
        }
    }
}
