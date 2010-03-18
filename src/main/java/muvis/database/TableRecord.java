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

package muvis.database;

/**
 * Class that represents a row of the information table
 * @author Ricardo
 */
public class TableRecord {

    private int nr;
    private String trackName, artistName, albumName;
    private Long duration;
    private String trackGenre, trackYear, trackBeat, trackMood;

    public void setValueColumn(int col, Object value){
        if (value == null){
            return;
        }
        switch (col) {
            case 1:  nr = (Integer)value; break;
            case 2:  trackName = value.toString(); break;
            case 3:  artistName = value.toString(); break;
            case 4:  albumName = value.toString(); break;
            case 5:  duration = (Long)value; break;
            case 6:  trackGenre = value.toString(); break;
            case 7:  trackYear = value.toString(); break;
            case 8:  trackBeat = value.toString(); break;
            case 9:  trackMood = value.toString(); break;
        }
    }

    public Object getValueColumn(int col){
        switch (col) {
            case 1:  return nr;
            case 2:  return trackName;
            case 3:  return artistName;
            case 4:  return albumName;
            case 5:  return duration;
            case 6:  return trackGenre;
            case 7:  return trackYear;
            case 8:  return trackBeat;
            case 9:  return trackMood;
            default: return new Object();
        }
    }

}
