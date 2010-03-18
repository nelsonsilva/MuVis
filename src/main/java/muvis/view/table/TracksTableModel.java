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
package muvis.view.table;

import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.table.AbstractTableModel;
import muvis.Messages;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.database.TableRecord;
import muvis.util.Observable;
import muvis.util.Observer;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.concurrent.TimeUnit.*;

/**
 * Class that holds the Model for the list visualization of the library.
 * @author Ricardo
 */
public class TracksTableModel extends AbstractTableModel {

    protected String[] columnNames = new String[]{
        Messages.COL_TRACK_NUMBER_LABEL,
        Messages.COL_TRACK_NAME_LABEL,
        Messages.COL_ARTIST_NAME_LABEL,
        Messages.COL_ALBUM_NAME_LABE,
        Messages.COL_TRACK_DURATION_LABEL,
        Messages.COL_TRACK_GENRE_LABEL,
        Messages.COL_TRACK_YEAR_LABEL,
        Messages.COL_TRACK_BEAT_LABEL,
        Messages.COL_TRACK_MOOD_LABEL };

    @Autowired
    protected MusicLibraryDatabaseManager dbManager;
    protected Hashtable<Integer, TableRecord> records;
    protected ScheduledExecutorService scheduler;
    protected boolean fasterMode = true;

    public void init() {
        int numTracks = dbManager.getCountTracks();
        if (numTracks < 0) {
            numTracks = 0;
        }
        records = new Hashtable<Integer, TableRecord>(numTracks);

        UpdateTable updateTable = new UpdateTable(this);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(updateTable, 10, 10, SECONDS);

        if (fasterMode) {
            for (int i = 0; i < numTracks; i++) {
                getValueAt(i, 1);
            }
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;

    }

    @Override
    public int getRowCount() {
        int rowCount = dbManager.getCountTracks();
        if (rowCount < 0) {
            return 0;
        }
        return rowCount;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        int modRow = row, modCol = col;
        if (row == 0) {
            modRow++;
        } else {
            modRow = modRow + 1;
        }
        if (col == 0) {
            modCol++;
        } else {
            modCol = modCol + 1;
        }

        if (!records.containsKey(modRow)) {
            TableRecord rec = dbManager.getRowAt(modRow);
            records.put(modRow, rec);
        }
        return records.get(modRow).getValueColumn(modCol);

    }

    @Override
    public Class getColumnClass(int c) {

        if (c == 0) {
            return Integer.class;
        } else if (c == 1) {
            return String.class;
        } else if (c == 2) {
            return String.class;
        } else if (c == 3) {
            return String.class;
        } else if (c == 4) {
            return Long.class;
        } else if (c == 5) {
            return String.class;
        } else {
            return String.class;
        }
    }
}

// Get a handle, starting now, with a 10 second delay
class UpdateTable implements Runnable, Observer {

    TracksTableModel model;
    boolean update = false;

    public UpdateTable(TracksTableModel model) {
        this.model = model;
        model.dbManager.registerObserver(this);
    }

    @Override
    public void run() {
        if (update) {
            model.fireTableDataChanged();
            update = false;
        }
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof MusicLibraryDatabaseManager) {
            update = true;
        }
    }
}
