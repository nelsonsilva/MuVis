/*
* MuVis Database Schema.
*
* The GPLv3 licence :
* -----------------
* Copyright (c) 2009 Ricardo Dias

* This file is part of MuVis.

* MuVis is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* MuVis is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with MuVis.  If not, see <http://www.gnu.org/licenses/>.
*/

CREATE TABLE artists_table (
	id INTEGER IDENTITY PRIMARY KEY,
	artist_name LONGVARCHAR,
	key DOUBLE,
	UNIQUE(id)
);

CREATE TABLE albums_table (
	id INTEGER IDENTITY PRIMARY KEY,
	album_name LONGVARCHAR,
	artist_id INTEGER,
	key DOUBLE,
	UNIQUE(id),
	FOREIGN KEY (artist_id) REFERENCES artists_table(id) ON DELETE CASCADE
);

CREATE TABLE tracks_table (
	id INTEGER IDENTITY PRIMARY KEY,
	filename LONGVARCHAR,
	artist_id INTEGER,
	album_id INTEGER,
	key DOUBLE,
	FOREIGN KEY (artist_id) REFERENCES artists_table(id) ON DELETE CASCADE,
	FOREIGN KEY (album_id) REFERENCES albums_table(id) ON DELETE CASCADE
);

CREATE TABLE information_tracks_table (
	id INTEGER,
	track_title LONGVARCHAR,
	artist_name LONGVARCHAR,
	album_name LONGVARCHAR,
	duration BIGINT,
	genre LONGVARCHAR,
	year LONGVARCHAR,
	beat LONGVARCHAR,
	mood LONGVARCHAR,
	FOREIGN KEY (id) REFERENCES tracks_table(id) ON DELETE CASCADE
);
