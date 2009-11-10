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
package muvis.view;

import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.swing.table.AbstractTableModel;
import muvis.Workspace;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.database.TableRecord;
import muvis.util.Observable;
import muvis.util.Observer;

import static java.util.concurrent.TimeUnit.*;

/**
 * Class that holds the Model for the list visualization of the library.
 * @author Ricardo
 */
public class TracksTableModel extends AbstractTableModel implements Observer {

    private String[] columnNames = new String[]{"Nr.", "Track name", "Artist",
        "Album", "Duration", "Genre", "Year", "Beat", "Mood"};
    private MusicLibraryDatabaseManager dbManager;
    private Hashtable<Integer, TableRecord> records;
    private ScheduledExecutorService scheduler;
    private boolean fasterMode = true;

    public TracksTableModel() {
        dbManager = Workspace.getWorkspaceInstance().getDatabaseManager();
        int numTracks = dbManager.getCountTracks();
        if (numTracks < 0) {
            numTracks = 0;
        }
        records = new Hashtable<Integer, TableRecord>(numTracks);

        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Get a handle, starting now, with a 10 second delay
        class UpdateTable implements Runnable, Observer {

            TracksTableModel model;
            boolean update = false;

            @Override
            public void run() {
                if (update){
                    fireTableDataChanged();
                    update = false;
                }
            }

            @Override
            public void update(Observable obs, Object arg) {
                if (obs instanceof MusicLibraryDatabaseManager){
                    update = true;
                }
            }
        }

        UpdateTable updateTable = new UpdateTable();
        dbManager.registerObserver(updateTable);

        final ScheduledFuture<?> timeHandle =
                scheduler.scheduleAtFixedRate(updateTable, 10, 90, SECONDS);

        if (fasterMode){
            for(int i = 0; i < numTracks ; i++){
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

        if (!records.containsKey(modRow)){
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

    /**
     * This method is called when the database changes
     */
    @Override
    public void update(Observable obs, Object arg) {
    }
}
