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

import muvis.exceptions.CantRetrieveMP3TagException;
import muvis.database.MusicLibraryDatabaseManager;
import java.io.File;
import muvis.Workspace;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

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
    private MusicLibraryDatabaseManager dbManager;

    public MP3AudioMetadataExtractor() {

        dbManager = Workspace.getWorkspaceInstance().getDatabaseManager();
    }

    /**
     * This method retrieves the metadata of the desired track.
     * @param file the filename of the track we want to retrieve
     * @return
     */
    @Override
    public AudioMetadata getAudioMetadata(String filename) throws CantRetrieveMP3TagException {

        AudioMetadata metadata = new AudioMetadata();
        File sourceFile = new File(filename);

        MP3File mp3file;
        try {
            mp3file = (MP3File) AudioFileIO.read(sourceFile);
            MP3AudioHeader audioHeader = (MP3AudioHeader) mp3file.getAudioHeader();
            String artist = validatedArtist(mp3file.getID3v1Tag().getFirstArtist());

            metadata.setAuthor(artist);
            metadata.setAlbum(validatedAlbum(artist, mp3file.getID3v1Tag().getFirstAlbum()));
            metadata.setTitle(validateTitle(mp3file.getID3v1Tag().getFirstTitle()));
            metadata.setDuration(audioHeader.getTrackLength());
            metadata.setBitrate(audioHeader.getBitRate());
            metadata.setYear(validatedYear(mp3file.getID3v1Tag().getFirstYear()));
            metadata.setGenre(validatedGenre(mp3file.getID3v1Tag().getFirstGenre()));

            metadata.setTrackNumber(0);     //for debugging purposes

        } catch (Exception ex) {
            throw new CantRetrieveMP3TagException("Can't retrieve tag from MP3 file.\nPossible reason: " + ex);
        }

        return metadata;
    }

    private String validateTitle(String firstTitle) {

        if (firstTitle.equals("")){
            return "Unknown";
        }
        else return firstTitle;
    }

    private String validatedAlbum(String artist, String album) {

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
