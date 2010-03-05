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

import java.awt.Point;
/*import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;*/
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/*import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;*/
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
/*import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import muvis.NBTreeManager;
import muvis.Environment;*/
import muvis.Messages;
import muvis.view.View;
import muvis.view.controllers.ListViewTableViewController;
/*import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.SimilarityTableFilter;
import muvis.util.Util;
import muvis.view.controllers.ListViewTableViewController;
import muvis.view.main.filters.NoFilter;
import muvis.view.main.filters.TreemapAlbumSimilarityFilter;
import muvis.view.main.filters.TreemapArtistSimilarityFilter;
import muvis.view.main.filters.TreemapTrackSimilarityFilter;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;*/

/**
 * Simple list visualization of the library.
 * Implemented by a jtable.
 * @author Ricardo
 */
public class ListViewTableView extends ListViewTableUI implements View {

    //private MusicLibraryDatabaseManager dbManager;
    //private ExecutorService threadPool;
    private TableRowSorter<TracksTableModel> sorter;
    private ListViewTableViewController controller;
    private TracksTableModel model;

    public ListViewTableView(final JFrame parent) {
        //dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        controller = new ListViewTableViewController();
        //threadPool = Executors.newFixedThreadPool(1);

        model = new TracksTableModel();

        tracksTableView.setModel(model);
        tracksTableView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sorter = new TableRowSorter<TracksTableModel>(model);
        tracksTableView.setRowSorter(sorter);
        tracksTableView.setRowSelectionAllowed(true);
        tracksTableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tracksTableView.setDefaultRenderer(Object.class, new ColorCellRenderer());

        //specific information about the columns
        TableColumn time = tracksTableView.getColumn(Messages.COL_TRACK_DURATION_LABEL);
        time.setCellRenderer(new ColorCellRenderer());
        time.setPreferredWidth(60);
        time.setMaxWidth(60);
        time.setMinWidth(40);

        TableColumn trackNum = tracksTableView.getColumn(Messages.COL_TRACK_NUMBER_LABEL);
        trackNum.setPreferredWidth(40);
        trackNum.setMaxWidth(60);
        trackNum.setCellRenderer(new ColorCellRenderer());

        TableColumn genreCol = tracksTableView.getColumn(Messages.COL_TRACK_GENRE_LABEL);
        genreCol.setPreferredWidth(80);
        genreCol.setMaxWidth(150);

        TableColumn year = tracksTableView.getColumn(Messages.COL_TRACK_YEAR_LABEL);
        tracksTableView.removeColumn(year);

        TableColumn beat = tracksTableView.getColumn(Messages.COL_TRACK_BEAT_LABEL);
        tracksTableView.removeColumn(beat);

        TableColumn mood = tracksTableView.getColumn(Messages.COL_TRACK_MOOD_LABEL);
        tracksTableView.removeColumn(mood);

        //tracksTableView.addMouseListener();
        tracksTableView.addMouseListener(new JTableMouseAdapter(controller));
    }

    /**
     * Returns the Sorter of this encapsulated JTable.
     * @return the sorter
     */
    public TableRowSorter<TracksTableModel> getSorter() {
        return sorter;
    }
}
