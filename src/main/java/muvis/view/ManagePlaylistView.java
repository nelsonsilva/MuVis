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

import muvis.view.table.ColorCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import muvis.audio.playlist.BasePlaylist;
import muvis.audio.playlist.Playlist;
import muvis.audio.playlist.PlaylistItem;
import muvis.util.Util;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * View of the Manage Playlist operation
 * @author Ricardo Dias
 */
public class ManagePlaylistView extends ManagePlaylistViewUI implements ActionListener {

    //file choosers for saving and loading playlist
    private JFileChooser loadPlaylistChooser,  savePlaylistChooser;
    private JFrame parent;


    public void setParent(JFrame parent){
        this.parent=parent;
        setLocationRelativeTo(parent);
    }

    public ManagePlaylistView() {
        super(true);
    }

    @Override
    public void init(){
        super.init();
        playlistListViewTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        playlistListViewTable.setDefaultRenderer(Object.class, new ColorCellRenderer());

        //specific information about the columns
        TableColumn time = playlistListViewTable.getColumn("Duration");
        time.setPreferredWidth(60);
        time.setMaxWidth(60);
        time.setMinWidth(40);

        TableColumn trackNum = playlistListViewTable.getColumn("Nr.");
        trackNum.setPreferredWidth(40);
        trackNum.setMaxWidth(60);

        TableColumn genreCol = playlistListViewTable.getColumn("Genre");
        genreCol.setPreferredWidth(80);
        genreCol.setMaxWidth(150);

        loadPlaylistChooser = new JFileChooser(new File(""));
        savePlaylistChooser = new JFileChooser(new File(""));

        //add the action listeners for the buttons
        remTracksButton.addActionListener(this);
        appendPlaylistButton.addActionListener(this);
        savePlaylistButton.addActionListener(this);
        shufflePlaylistButton.addActionListener(this);
        discardChangesButton.addActionListener(this);
        closeSaveChangesButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == remTracksButton) {
            int[] selectedRows = playlistListViewTable.getSelectedRows();
            if (selectedRows.length == 1) {
                ((ManagePlaylistTableModel) playlistListViewTable.getModel()).removeRow(selectedRows[0]);
            } else {
                ((ManagePlaylistTableModel) playlistListViewTable.getModel()).removeRows(selectedRows);
            }
        } else if (e.getSource() == appendPlaylistButton) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    loadPlaylistChooser.setDialogTitle("Select a playlist file");
                    FileNameExtensionFilter fFilter = new FileNameExtensionFilter("M3U playlist", "m3u");
                    loadPlaylistChooser.addChoosableFileFilter(fFilter);
                    int returned = loadPlaylistChooser.showOpenDialog(parent);
                    if (returned == JFileChooser.APPROVE_OPTION) {
                        File file = loadPlaylistChooser.getSelectedFile();

                        ((ManagePlaylistTableModel) playlistListViewTable.getModel()).appendPlaylist(file.getName().toString(), loadPlaylistChooser.getCurrentDirectory().toString() + Util.getOSEscapeSequence());
                    }
                }
            });
        } else if (e.getSource() == savePlaylistButton) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    savePlaylistChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    savePlaylistChooser.setDialogTitle("Save your playlist");
                    FileNameExtensionFilter fFilter = new FileNameExtensionFilter("M3U playlist", "m3u");
                    savePlaylistChooser.addChoosableFileFilter(fFilter);
                    savePlaylistChooser.setAcceptAllFileFilterUsed(false);
                    int returned = savePlaylistChooser.showSaveDialog(parent);
                    if (returned == JFileChooser.APPROVE_OPTION) {
                        File file = savePlaylistChooser.getSelectedFile();
                        String playlistName = file.getName();

                        boolean saved = ((ManagePlaylistTableModel) playlistListViewTable.getModel()).savePlaylist(playlistName,
                                savePlaylistChooser.getCurrentDirectory().toString() + Util.getOSEscapeSequence());
                        if (saved) {
                            JOptionPane.showMessageDialog(parent, "Playlist succefuly saved!",
                                    "Save Playlist", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(parent, "Can't save the playlist!Please try later!",
                                    "Save Playlist", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
        } else if (e.getSource() == shufflePlaylistButton) {
            ((ManagePlaylistTableModel) playlistListViewTable.getModel()).shufflePlaylist();
        } else if (e.getSource() == discardChangesButton) {
            this.dispose();
        } else if (e.getSource() == closeSaveChangesButton) {
            //updating the playlist with the modified ording and tracks
            ((ManagePlaylistTableModel) playlistListViewTable.getModel()).updatePlaylist();
            this.dispose();
        }
    }
}

/**
 * Table model for the Manage Playlist Table Model
 * @author Ricardo Dias
 */
class ManagePlaylistTableModel extends DefaultTableModel implements RowSorterListener {

    private BasePlaylist playlist;
    private String[] columnNames = new String[]{"Nr.", "Track name", "Artist",
            "Album", "Duration", "Genre"};
    private Playlist managePlaylist,  originalPlaylist;

    public ManagePlaylistTableModel() {

    }

    @Autowired 
    public void setPlayList(BasePlaylist playlist){
        managePlaylist = new BasePlaylist();

        for (PlaylistItem item : playlist.getAllItems()) {
            managePlaylist.appendItem(item);
        }
    }
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return getManagePlaylist().getItemAt(row).getAudioMetaData().getTrackNumber();
        } else if (column == 1) {
            return getManagePlaylist().getItemAt(row).getAudioMetaData().getTitle();
        } else if (column == 2) {
            return getManagePlaylist().getItemAt(row).getAudioMetaData().getAuthor();
        } else if (column == 3) {
            return getManagePlaylist().getItemAt(row).getAudioMetaData().getAlbum();
        } else if (column == 4) {
            return getManagePlaylist().getItemAt(row).getAudioMetaData().getDuration();
        } else if (column == 5) {
            return getManagePlaylist().getItemAt(row).getAudioMetaData().getGenre();
        }
        return new Object();
    }

    @Override
    public int getRowCount() {
        if (getManagePlaylist() != null) {
            return getManagePlaylist().getPlaylistSize();
        } else {
            return 0;
        }
    }

    @Override
    public void removeRow(int row) {
        getManagePlaylist().removeItemAt(row);
        fireTableDataChanged();
    }

    public void removeRows(int[] rows) {

        ArrayList<PlaylistItem> removeItems = new ArrayList<PlaylistItem>();
        for (int row : rows) {
            removeItems.add(getManagePlaylist().getItemAt(row));
        }

        for (PlaylistItem it : removeItems) {
            getManagePlaylist().removeItem(it);
        }
        fireTableDataChanged();
    }

    public void updatePlaylist() {

        PlaylistItem cursorItem = originalPlaylist.getCursor();
        originalPlaylist.removeAllItems();

        for (PlaylistItem it : getManagePlaylist().getAllItems()) {
            originalPlaylist.appendItem(it);
        }

        int index = originalPlaylist.getIndex(cursorItem);
        if (index != -1){
            originalPlaylist.updateCursor(index);
        }
    }

    public void shufflePlaylist() {
        getManagePlaylist().shuffle();
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void moveRow(int start, int end, int to) {
        //under specification
    }

    /**
     * @return the managePlaylist
     */
    private Playlist getManagePlaylist() {
        return managePlaylist;
    }

    public boolean savePlaylist(String filename, String directory) {
        return managePlaylist.save(filename, directory);
    }

    public void appendPlaylist(String filename, String directory) {
        managePlaylist.load(filename, directory);
        fireTableDataChanged();
    }

    @Override
    public void sorterChanged(RowSorterEvent e) {
        if (e.getType() == RowSorterEvent.Type.SORTED) {
            //must update the underlying model so that the changes may be reflected
            Playlist tempPlaylist = new BasePlaylist();

            int viewRowsCount = e.getSource().getViewRowCount();
            for (int i = viewRowsCount - 1; i >= 0; i--) {
                int modelRowTransf = e.getSource().convertRowIndexToModel(i);
                tempPlaylist.appendItem(managePlaylist.getItemAt(modelRowTransf));
            }
            managePlaylist.removeAllItems();

            for (PlaylistItem item : tempPlaylist.getAllItems()) {
                managePlaylist.appendItem(item);
            }
        }
    }
}