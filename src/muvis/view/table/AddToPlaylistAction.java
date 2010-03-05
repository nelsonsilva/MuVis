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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import muvis.Messages;
import muvis.view.controllers.ListViewTableViewController;

/**
 *
 * @author Ricardo
 */
public class AddToPlaylistAction implements ActionListener {

    protected JTable tracksTable;
    protected ListViewTableViewController controller;

    public AddToPlaylistAction(JTable tracksTable, ListViewTableViewController controller) {
        this.tracksTable = tracksTable;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();

            int[] initRows = tracksTable.getSelectedRows();
            final int[] endRows = new int[initRows.length];

            for (int i = 0; i < initRows.length; i++) {
                int rowModel = tracksTable.getRowSorter().convertRowIndexToModel(initRows[i]);
                endRows[i] = (Integer) tracksTable.getValueAt(rowModel, 0);
            }

            if (item.getText().contains(Messages.TRACK_NAME_LABEL)) {
                Executors.newFixedThreadPool(1).execute(new Runnable() {

                    @Override
                    public void run() {
                        //add album to playlist
                        for (int i = 0; i < endRows.length; i++) {
                            controller.addTrackToPlaylist(endRows[i]);
                        }
                    }
                });

            } else if (item.getText().contains(Messages.ALBUM_NAME_LABEL)) {
                Executors.newFixedThreadPool(1).execute(new Runnable() {

                    @Override
                    public void run() {
                        //add album to playlist
                        for (int i = 0; i < endRows.length; i++) {
                            controller.addAlbumToPlaylist(endRows[i]);
                        }
                    }
                });

            } else if (item.getText().contains(Messages.ARTIST_NAME_LABEL)) {
                Executors.newFixedThreadPool(1).execute(new Runnable() {

                    @Override
                    public void run() {
                        //add artist to playlist
                        for (int i = 0; i < endRows.length; i++) {
                            controller.addArtistToPlaylist(endRows[i]);
                        }
                    }
                });
            }
        }
    }
}
