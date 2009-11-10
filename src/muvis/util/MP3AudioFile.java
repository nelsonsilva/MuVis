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

package muvis.util;

import java.io.File;

/**
 * Class that abstracts an audio file
 * @author Ricardo
 */
public class MP3AudioFile implements Item {

    private File audioFile;

    public MP3AudioFile(String filename){
        audioFile = new File(filename);
    }

    public MP3AudioFile(File file){
        audioFile = file;
    }

    /**
     * @return the audioFile
     */
    public File getAudioFile() {
        return audioFile;
    }

    /**
     * @param audioFile the audioFile to set
     */
    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }
}
