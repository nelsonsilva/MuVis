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

import muvis.exceptions.CannotRetrieveMP3TagException;
import java.io.File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * Implementation of the AudioMetadataExtractor for extracting Mp3 tags from files.
 * Uses JaudioTagger to extract tags from mp3 files.
 * @author Ricardo
 */
public class MP3AudioMetadataExtractor implements AudioMetadataExtractor {

    private static String validGenres[] = {
        "Jazz", "Gospel", "Blues", "Metal",
        "Rock", "Pop", "Disco", "Funk", "R&B",
        "Rap", "Hip-Hop", "Electro", "Latin",
        "Classical", "Soundtrack", "World",
        "Reggae", "Soul", "African", "Other"
    };
    
    /**
     * This method retrieves the metadata of the desired track.
     * @param file the filename of the track we want to retrieve
     * @return
     */
    @Override
    public AudioMetadata getAudioMetadata(String filename) throws CannotRetrieveMP3TagException {

        AudioMetadata metadata = new AudioMetadata();
        File sourceFile = new File(filename);

        AudioFile mp3file;
        String artist, album, title, bitrate, year, genre;
        int duration, trackNumber;
        try {
            mp3file = AudioFileIO.read(sourceFile);
            Tag tag = mp3file.getTag();
            AudioHeader audioHeader = mp3file.getAudioHeader();

            artist = validatedArtist(tag.getFirst(FieldKey.ARTIST));
            album = validatedAlbum(tag.getFirst(FieldKey.ALBUM));
            title = validateTitle(tag.getFirst(FieldKey.TITLE));
            duration = audioHeader.getTrackLength();
            bitrate = audioHeader.getBitRate();
            year = validatedYear(tag.getFirst(FieldKey.YEAR));
            genre = validatedGenre(tag.getFirst(FieldKey.GENRE));
            trackNumber = 0;

            metadata.setAuthor(artist);
            metadata.setAlbum(album);
            metadata.setTitle(title);
            metadata.setDuration(duration);
            metadata.setBitrate(bitrate);
            metadata.setYear(year);
            metadata.setGenre(genre);
            metadata.setTrackNumber(trackNumber);
            metadata.setTrackNumber(trackNumber);

        } catch (Exception ex) {
            //Could not retrieve MP3 Tags
            throw new CannotRetrieveMP3TagException("Can't retrieve tag from MP3 file.\nPossible reason: " + ex, filename);
        }

        return metadata;
    }

    private String validateTitle(String firstTitle) {

        if (firstTitle.equals("")){
            return "Unknown";
        }
        else return firstTitle;
    }

    private String validatedAlbum(String album) {

        String albumTest = album.toLowerCase();
        if (albumTest.contains("unknown") || albumTest.contains("desconhecido") || albumTest.contains("sconosciuto") || albumTest.contains("desconocido") || albumTest.equals("")) {
            return "Unknown";
        }

        return album;
    }

    private String validatedArtist(String artist) {

        String artistTest = artist.toLowerCase();
        if (artistTest.contains("unknown") || artistTest.contains("desconhecido") || artistTest.contains("sconosciuto")
                || artistTest.contains("desconocido") || artistTest.equals("")) {
            return "Unknow";
        }

        return artist;
    }

    private String validatedGenre(String genre) {

        String originalGenre;
        String validatedGenre = "";
        String testGenre = genre.toLowerCase();

        for (String validGenre : validGenres) {

            originalGenre = validGenre;
            validGenre = validGenre.toLowerCase();
            if (testGenre.equals(validGenre) || validGenre.contains(testGenre) ||
                    testGenre.contains(validGenre)) {
                validatedGenre = originalGenre;
                break;      //found the apropriate genre
            } else {
                validatedGenre = "Other";
            }
        }

        return validatedGenre;

    }

    //if a year is not valid it returns a string with INTEGER.MIN_VALUE
    private String validatedYear(String year) {

        String validatedYear = "";

        try {
            //test if the string year can be converted to an integer
            Integer.parseInt(year);
            validatedYear = year;
        } catch (NumberFormatException e) {
            validatedYear += Integer.MIN_VALUE;
        }

        return validatedYear;
    }
}
