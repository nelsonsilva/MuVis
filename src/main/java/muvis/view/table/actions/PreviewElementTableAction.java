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
package muvis.view.table.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import muvis.Messages;
import muvis.audio.AudioSnippetPlayerManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Ricardo
 */
public class PreviewElementTableAction implements ActionListener {

    @Autowired private AudioSnippetPlayerManager snippetManager;
    protected JTable tracksTable;

    public PreviewElementTableAction(JTable tracksTable) {
        this.tracksTable = tracksTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();

            if (item.getText().contains(Messages.TRACK_NAME_LABEL)) {
                //preview of a track
                if (tracksTable.getSelectedRowCount() <= 1) {
                    int row = tracksTable.getRowSorter().convertRowIndexToModel(tracksTable.getSelectedRow());
                    int trackId = (Integer) tracksTable.getModel().getValueAt(row, 0);
                    snippetManager.previewTrack(trackId);
                } else {
                    ArrayList<Integer> trackIds = new ArrayList<Integer>(tracksTable.getSelectedRowCount());
                    for (int row : tracksTable.getSelectedRows()) {
                        int rowModel = tracksTable.getRowSorter().convertRowIndexToModel(row);
                        int id = (Integer) tracksTable.getModel().getValueAt(rowModel, 0);
                        trackIds.add(id);
                    }
                    snippetManager.previewTracks(trackIds);
                }

            } else if (item.getText().contains(Messages.ALBUM_NAME_LABEL)) {
                //preview of an album
                if (tracksTable.getSelectedRowCount() <= 1) {
                    int row = tracksTable.getRowSorter().convertRowIndexToModel(tracksTable.getSelectedRow());
                    int trackId = (Integer) tracksTable.getModel().getValueAt(row, 0);
                    snippetManager.previewAlbum(trackId);
                } else {
                    ArrayList<Integer> trackIds = new ArrayList<Integer>(tracksTable.getSelectedRowCount());
                    for (int row : tracksTable.getSelectedRows()) {
                        int rowModel = tracksTable.getRowSorter().convertRowIndexToModel(row);
                        int id = (Integer) tracksTable.getModel().getValueAt(rowModel, 0);
                        trackIds.add(id);
                    }
                    snippetManager.previewAlbums(trackIds);
                }

            } else if (item.getText().contains(Messages.ARTIST_NAME_LABEL)) {
                //preview of an artist
                if (tracksTable.getSelectedRowCount() <= 1) {
                    int row = tracksTable.getRowSorter().convertRowIndexToModel(tracksTable.getSelectedRow());
                    int trackId = (Integer) tracksTable.getModel().getValueAt(row, 0);
                    snippetManager.previewArtist(trackId);
                } else {
                    ArrayList<Integer> trackIds = new ArrayList<Integer>(tracksTable.getSelectedRowCount());
                    for (int row : tracksTable.getSelectedRows()) {
                        int rowModel = tracksTable.getRowSorter().convertRowIndexToModel(row);
                        int id = (Integer) tracksTable.getModel().getValueAt(rowModel, 0);
                        trackIds.add(id);
                    }
                    snippetManager.previewArtists(trackIds);
                }

            }

        }
    }
}


