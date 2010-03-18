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

import muvis.view.table.actions.FindSimilarElementsTableAction;
import muvis.view.table.actions.FindNonSimilarElementsTableAction;
import muvis.view.table.actions.PreviewElementTableAction;
import muvis.view.table.actions.AddToPlaylistTableAction;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import muvis.Elements;
import muvis.Environment;
import muvis.view.MainViewsMouseAdapter;
import muvis.view.MusicControllerView;
import muvis.view.ViewManager;
import muvis.view.controllers.ListViewTableViewController;
import muvis.view.controllers.MusicPlayerIndividualTrackController;

/**
 *
 * @author Ricardo
 */
public class JTableMouseAdapter extends MainViewsMouseAdapter {

    protected ListViewTableViewController controller;
    protected JTable tracksTable;
    protected ActionListener previewAction, addToPlaylistAction, findSimilarAction, findNonSimilarAction;

    public JTableMouseAdapter(JTable tracksTable, ListViewTableViewController controller) {
        this.controller = controller;
        this.tracksTable = tracksTable;
        previewAction = new PreviewElementTableAction(tracksTable);
        addToPlaylistAction = new AddToPlaylistTableAction(tracksTable, controller);
        findSimilarAction = new FindSimilarElementsTableAction(tracksTable);
        findNonSimilarAction = new FindNonSimilarElementsTableAction(tracksTable);
    }

    @Override
    protected void assignActionListeners() {
        previewElementMenu.addActionListener(previewAction);
        addElementToPlaylistMenu.addActionListener(addToPlaylistAction);
        findSimilarElementMenu.addActionListener(findSimilarAction);
        findNonSimilarElementMenu.addActionListener(findNonSimilarAction);
    }

    @Override
    protected void mouseHandler(MouseEvent e) {

        if (e.isPopupTrigger() && tracksTable.isEnabled()) {
            Point p = new Point(e.getX(), e.getY());
            int col = tracksTable.columnAtPoint(p);
            int row = tracksTable.rowAtPoint(p);

            // translate table index to model index
            int mcol = tracksTable.getColumn(
                    tracksTable.getColumnName(col)).getModelIndex();
            String colName = tracksTable.getModel().getColumnName(mcol);

            if (row >= 0 && row < tracksTable.getRowCount()) {

                // create popup menu...
                contextMenu = createContextMenu(colName,
                        ((tracksTable.getSelectedRowCount() <= 1) ? MainViewsMouseAdapter.ElementType.SIMPLE
                        : MainViewsMouseAdapter.ElementType.MULTIPLE));

                // Get the ListSelectionModel of the JTable
                if (tracksTable.getSelectedRowCount() <= 1) {
                    ListSelectionModel model = tracksTable.getSelectionModel();

                    // set the selected interval of rows. Using the "rowNumber"
                    // variable for the beginning and end selects only that one row.
                    model.setSelectionInterval(row, row);
                }

                // ... and show it
                if (contextMenu != null && contextMenu.getComponentCount() > 0) {
                    contextMenu.show(tracksTable, p.x, p.y);
                }
            }
        } else if (e.getClickCount() == 2) {
            //ask controller to play song
            int row = tracksTable.getRowSorter().convertRowIndexToModel(tracksTable.getSelectedRow());
            int trackId = (Integer) tracksTable.getModel().getValueAt(row, 0);
            ViewManager vm = Environment.getEnvironmentInstance().getViewManager();

            MusicControllerView mpController = (MusicControllerView) vm.getView(Elements.MUSIC_PLAYER_VIEW);
            ((MusicPlayerIndividualTrackController) mpController.getMusicPlayerIndividualController()).setTrackId(trackId);
            mpController.setPlayingType(MusicControllerView.PlayingType.INDIVIDUAL_TRACK);
            mpController.playTrack();
        }
    }
}