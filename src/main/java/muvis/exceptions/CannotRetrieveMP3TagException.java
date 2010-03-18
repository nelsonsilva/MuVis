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

package muvis.exceptions;

/**
 * Generic exception for MuVis audio retrieving
 * @author Ricardo
 */
public class CannotRetrieveMP3TagException extends Exception {

    String mp3File;

    public CannotRetrieveMP3TagException(String message, String file) {
        super(message + "\nFile:" + file);
        mp3File = file;
    }

    public String getFile(){
        return mp3File;
    }

}
