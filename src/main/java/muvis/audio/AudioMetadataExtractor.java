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

/**
 * This interface specifies the method that a class must implement to become an
 * metadata extrator. This way developers can define the method for extracting
 * the metadata from the file.
 * @author Ricardo
 */
public interface AudioMetadataExtractor {

    public AudioMetadata getAudioMetadata(String filename) throws CannotRetrieveMP3TagException;
}
