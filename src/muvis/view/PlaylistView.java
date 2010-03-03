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

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingConstants;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import muvis.Elements;
import muvis.Environment;
import muvis.audio.playlist.Playlist;
import muvis.audio.playlist.PlaylistItem;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.util.Util;
import muvis.view.controllers.PlaylistControllerInterface;

/**
 * This class is the Interface View for the Playlist
 * The handlers for the swing interface are specified here, but the interaction
 * is made through a controller.
 * @author Ricardo
 */
public class PlaylistView extends PlaylistViewUI implements Dockable, ActionListener, Observer {

    //Controller for the interface
    private PlaylistControllerInterface playlistController;
    
    //Model for the list of tracks
    private DefaultListModel playlistListModel;

    //DocKey for this panel to be dockable
    private DockKey key;

    //file choosers for saving and loading playlist
    private JFileChooser loadPlaylistChooser,  savePlaylistChooser;

    //Parent JFrame
    private JFrame parent;

    public PlaylistView(JFrame parent, PlaylistControllerInterface controller) {

        playlistController = controller;
        this.parent = parent;
        loadPlaylistChooser = new JFileChooser(new File(""));
        savePlaylistChooser = new JFileChooser(new File(""));

        loadPlaylistButton.addActionListener(this);
        savePlaylistButton.addActionListener(this);
        remTrackButton.addActionListener(this);
        managePlaylistButton.addActionListener(this);
        Environment.getWorkspaceInstance().getAudioPlaylist().registerObserver(this);

        initPlaylistList();
        initDockKey();
    }

    /**
     * This is the method for handling the actions in the buttons
     * Each button have a specific method for handling the input
     * @param event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == remTrackButton) {
            remPlaylistItemsAction();
        } else if (event.getSource() == savePlaylistButton) {
            savePlaylistAction();
        } else if (event.getSource() == loadPlaylistButton) {
            loadPlaylistAction();
        } else if (event.getSource() == managePlaylistButton) {
            ManagePlaylistView managePlaylist = new ManagePlaylistView(parent);
            managePlaylist.setVisible(true);
        }
    }

    /**
     * Initializes and sets the properties of this dockable panel
     */
    private void initDockKey() {
        key = new DockKey("Playlist");
        key.setTooltip("Playlist List");
        key.setCloseEnabled(false);
        key.setAutoHideEnabled(true);
        key.setMaximizeEnabled(false);
        key.setAutoHideBorder(DockingConstants.HIDE_RIGHT);
    }

    /**
     * Initializes all the properties related to the playlist visualization.
     */
    private void initPlaylistList() {
        playlistListModel = new DefaultListModel();
        listTracks.setModel(playlistListModel);
        listTracks.setCellRenderer(new PlaylistListCellRenderer());

        MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Environment workspace = Environment.getWorkspaceInstance();

                    MusicControllerView musicPlayerControllerView = (MusicControllerView) workspace.getViewManager().getView(Elements.MUSIC_PLAYER_VIEW);
                    musicPlayerControllerView.setPlayingType(MusicControllerView.PlayingType.PLAYLIST_MODE);

                    //sets the cursor to the selected track
                    int index = listTracks.locationToIndex(e.getPoint());
                    PlaylistItem item = (PlaylistItem)playlistListModel.getElementAt(index);

                    int itemIndex = workspace.getAudioPlaylist().getIndex(item);
                    workspace.getAudioPlaylist().updateCursor(itemIndex);

                    musicPlayerControllerView.playTrack();
                }
            }
        };
        listTracks.addMouseListener(mouseListener);
    }

    /**
     * Action for loading the playlist: uses the controller associated to this view
     */
    private void loadPlaylistAction(){
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                loadPlaylistChooser.setDialogTitle("Select a playlist file");
                FileNameExtensionFilter fFilter = new FileNameExtensionFilter("M3U playlist", "m3u");
                loadPlaylistChooser.addChoosableFileFilter(fFilter);
                int returned = loadPlaylistChooser.showOpenDialog(parent);
                if (returned == JFileChooser.APPROVE_OPTION) {
                    File file = loadPlaylistChooser.getSelectedFile();
                    playlistController.loadPlaylist(file.getName().toString(), loadPlaylistChooser.getCurrentDirectory().toString());
                    updateListTracksDisplay();
                    remTrackButton.setEnabled(true);
                }
            }
        });
    }

    /**
     * Action for removing items from the playlist: uses the controller
     */
    private void remPlaylistItemsAction() {
        //We can remove several playlist items
        int[] indices = listTracks.getSelectedIndices();
        ArrayList<PlaylistItem> itemsToRemove = new ArrayList<PlaylistItem>();
        for (int i = 0; i < indices.length; i++) {
            PlaylistItem playlistItemToRemove = (PlaylistItem) playlistListModel.getElementAt(indices[i]);
            //marking the tracks for removal
            itemsToRemove.add(playlistItemToRemove);
            int size = playlistListModel.getSize();
            if (size == 0) {
                remTrackButton.setEnabled(false);
            } else {
                //Select an index.
                if (indices[i] == playlistListModel.getSize()) {
                    //removed item in last position
                    indices[i]--;
                }
            }
        }
        /*
         * Removing the items from the playlist
         */
        playlistController.removeTracksFromPlaylist(itemsToRemove);
        for (PlaylistItem it : itemsToRemove){
            playlistListModel.removeElement(it);
        }
    }

    /**
     * Action for saving the current playlist: uses the controller
     */
    private void savePlaylistAction(){
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
                    boolean saved = playlistController.savePlaylist(playlistName, savePlaylistChooser.getCurrentDirectory().toString());
                    if (saved){
                        JOptionPane.showMessageDialog(parent, "Playlist succefuly saved!",
                            "Save Playlist", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(parent, "Can't save the playlist!Please try later!",
                            "Save Playlist", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void updateListTracksDisplay() {
        Environment workspace = Environment.getWorkspaceInstance();
        Playlist playlist = workspace.getAudioPlaylist();
        PlaylistItem prevSelectedItem = (PlaylistItem)listTracks.getSelectedValue();
        
        playlistListModel.clear();
        int index = listTracks.getSelectedIndex(); //get selected index
        int prevSelectedItemIndex = 0;

        for (PlaylistItem item : playlist.getAllItems()) {

            if (index == -1) { //no selection, so insert at beginning
                index = 0;
            } else {           //add after the selected item
                index++;
            }

            playlistListModel.insertElementAt(item, index);
            //try to select the previous element
            if (prevSelectedItem != null && prevSelectedItem.equals(item)){
                prevSelectedItemIndex = index;
            }
        }

        //Select the new item and make it visible.
        //if previous selected item is still there, then it will continue selected
        listTracks.setSelectedIndex(prevSelectedItemIndex);
        listTracks.ensureIndexIsVisible(prevSelectedItemIndex);

        long playlistDuration = playlist.getTotalPlayingTime();
        String playlistDurationStr = Util.secondsToTimeDisplay(playlistDuration);
        totalTracksInfoLabel.setText("Total: " + playlistDurationStr + "s, in "
                + playlist.getPlaylistSize() + "tracks.");
    }

    private void updateListTracksDisplayNewCursor() {
        Environment workspace = Environment.getWorkspaceInstance();
        Playlist playlist = workspace.getAudioPlaylist();

        PlaylistItem newCursor = playlist.getCursor();
        int index = 0;

        for (; index < playlistListModel.getSize() ; index++) {

            if (playlistListModel.get(index).equals(newCursor)){
                break;
            }
        }

        //Select the new item and make it visible.
        //if previous selected item is still there, then it will continue selected
        listTracks.setSelectedIndex(index);
        listTracks.ensureIndexIsVisible(index);
    }

    @Override
    public DockKey getDockKey() {
        return key;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * This updates the selected item in the playlist list view
     * @param obs
     */
    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof Playlist){
            Playlist playlist = (Playlist)obs;
            if (Playlist.Event.NEW_CURSOR.equals(arg) || Playlist.Event.PLAYLIST_UPDATED.equals(arg)){
                
                updateListTracksDisplayNewCursor();
            }
            else if (Playlist.Event.PLAYLIST_RESIZED.equals(arg)){
                updateListTracksDisplay();
                getDockKey().setNotification(true);
            }
            else updateListTracksDisplay();
            if (playlist.getPlaylistSize() > 0)
                remTrackButton.setEnabled(true);
            else remTrackButton.setEnabled(false);
        }
    }
}
/**
 * This class implements the CellRenderer for the playlist JList.
 * The only modification from the default cell renderer is the string that
 * is shown: replaced by PlaylistItem.getFormattedDisplayName()
 * @author Ricardo
 */
class PlaylistListCellRenderer extends JLabel implements ListCellRenderer {

    /*
     * This is the only method defined by ListCellRenderer.
     */
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value, // value to display
            int index, // cell index
            boolean isSelected, // is the cell selected
            boolean cellHasFocus) // the list and the cell have the focus
    {
        /*
         * this is the most important modification to the renderer, we
         * replace the text from the jlabel with the value we want.
         */
        String s = ((PlaylistItem) value).getFormattedDisplayName();
        setText(s);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);
        return this;
    }
}

