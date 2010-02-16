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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import muvis.exceptions.CannotRetrieveMP3TagException;
import java.io.ByteArrayOutputStream;
import muvis.Workspace;

/**
 * Class that generates a snippet of an mp3 file.
 * This snippet is generated randomly, but always have 7-9s.
 * @author Ricardo
 */
public class MP3AudioSnippetExtractor {

    //private AudioMetadataExtractor for retrieving the bitrate of a music
    private static AudioMetadataExtractor extractor = new MP3AudioMetadataExtractor();

    //converts the bitrate for a useful represent in calculus
    private static int getBitrate(String originalBitrate) {
        int bitrate = 0;

        String bitrateStr = "";

        for (int i = 0; i < originalBitrate.length(); i++) {
            if (Character.isDigit(originalBitrate.charAt(i))) {
                bitrateStr += originalBitrate.charAt(i);
            }
        }

        bitrate = Integer.parseInt(bitrateStr);

        return bitrate;
    }

    public static byte[] extractAudioSnippet(String filename) {

        try {
            AudioMetadata metadata = Workspace.getWorkspaceInstance().getDatabaseManager().getTrackMetadata(filename);
            if (metadata == null) {
                metadata = extractor.getAudioMetadata(filename);
            }

            int aproxBitrate = getBitrate(metadata.getBitrate());

            File inputFile = new File(filename);
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            int maxSize = (int) (12 * (aproxBitrate * 1024)); // desired_seconds*(song's
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(maxSize);

            double cutStartPoint = 0;

            if (bufferedInputStream != null) {
                byte[] buffer = new byte[4 * 1024];

                int data = 0;
                int totDataRead = 0;
                int readedData = 0;

                cutStartPoint = inputFile.length() * (1f / 4f);

                //just read until start cutting
                while (data != -1 && readedData < cutStartPoint && data < inputFile.length()) {
                    data = bufferedInputStream.read(buffer);
                    readedData += data;
                }

                //now just cut until the defined end position
                while (data != -1 && totDataRead < maxSize && readedData < inputFile.length()) {
                    outputStream.write(buffer, 0, data);
                    data = bufferedInputStream.read(buffer);
                    totDataRead += data * 8;
                    readedData += data * 8;
                }
                byte[] snippetBytes = outputStream.toByteArray();

                outputStream.flush();
                outputStream.close();

                return snippetBytes;
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (CannotRetrieveMP3TagException ex) {
            ex.printStackTrace();
        }
        //must be careful with this non value here
        return null;
    }
}
