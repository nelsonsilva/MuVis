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

/**
 * Class that encapsulates the textual metadata extracted from the mp3file
 * @author Ricardo
 */
public class AudioMetadata {

    /*
     * Metadata Fields
     */

    private long duration;
    private String title;
    private String author;
    private String album;
    private int trackNumber;
    private String year;
    private String bitrate;
    private String genre;

    /**
     * Method that returns the length of the track.
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Method for retrieving the title of the track.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method that returns the author of the track.
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Retrieves the album of the track.
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Method that returns the number of this track.
     * @return the trackNumber
     */
    public int getTrackNumber() {
        return trackNumber;
    }

    /**
     * Method that returns the year of the track.
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * Method that returns the (average) bitrate of the track.
     * @return the bitrate
     */
    public String getBitrate() {
        return bitrate;
    }

    /**
     * Method that returns the genre of the track.
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    @Override
    public String toString(){
        return "{" + "duration: " + duration + ", title: " + title + ", author: " + author + 
                ", album: " + album + ", track number: " + trackNumber +
                ", year: " + year + ", bitrate: " + bitrate + ", genre: " + genre
                + "}";
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @param album the album to set
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * @param trackNumber the trackNumber to set
     */
    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @param bitrate the bitrate to set
     */
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /*
     * Methods that returns the fields of the metadata in a array format.
     * Fields available:
     * 1. Duration
     * 2. Title
     * 3. Author
     * 4. Album
     * 5. Track Number
     * 6. Year
     * 7. Bitrate
     * 8. Genre
     */
    public Object[] toArray(){

        Object [] metadataFields = new Object[8];
        metadataFields[0] = duration;
        metadataFields[1] = title;
        metadataFields[2] = author;
        metadataFields[3] = album;
        metadataFields[4] = trackNumber;
        metadataFields[5] = year;
        metadataFields[6] = bitrate;
        metadataFields[7] = genre;

        return metadataFields;
    }
}
