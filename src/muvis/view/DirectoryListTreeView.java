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
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreeSelectionModel;
import muvis.Environment;
import muvis.audio.AudioMetadataExtractor;
import muvis.audio.MP3AudioMetadataExtractor;
import muvis.audio.playlist.PlaylistItem;
import muvis.exceptions.CannotRetrieveMP3TagException;

/**
 * Filesystem explorer implementation - Allows users to access to their files
 * that are outside the loaded library
 * @author Ricardo
 */
public class DirectoryListTreeView extends DirectoryListViewTreeUI implements Dockable {

    private DockKey key;

    public DirectoryListTreeView() {
        key = new DockKey("Directory List");

        key.setTooltip("Directory List - a view over your file system");
        key.setCloseEnabled(false);
        key.setAutoHideEnabled(true);
        key.setMaximizeEnabled(false);
        key.setAutoHideBorder(DockingConstants.HIDE_LEFT);

        //Where the tree is initialized:
        treeDirectoryList.getSelectionModel().
                setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        final JPopupMenu popup = new JPopupMenu();
        JMenuItem mi;

        mi = new JMenuItem("Preview track");
        popup.add(mi);

        mi = new JMenuItem("Add to Playlist");
        popup.add(mi);
        mi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String pathFile = treeDirectoryList.getSelectionPath().getLastPathComponent().toString();
                    AudioMetadataExtractor extractor = new MP3AudioMetadataExtractor();
                    PlaylistItem item = new PlaylistItem("", pathFile, extractor.getAudioMetadata(pathFile));
                    Environment.getEnvironmentInstance().getAudioPlaylist().appendItem(item);
                } catch (CannotRetrieveMP3TagException ex) {
                    ex.printStackTrace();
                }
            }
        });

        mi = new JMenuItem("Add to MuVis Library");
        popup.add(mi);

        popup.addSeparator();

        mi = new JMenuItem("Close");
        popup.add(mi);
        popup.setOpaque(true);
        popup.setLightWeightPopupEnabled(true);

        treeDirectoryList.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        // thanks to urbanq for the bug fix!
                        int row = treeDirectoryList.getRowForLocation(e.getX(), e.getY());
                        if (row == -1) {
                            return;
                        }
                        treeDirectoryList.setSelectionRow(row);

                        String pathFile = treeDirectoryList.getPathForRow(row).getLastPathComponent().toString();

                        if (pathFile.endsWith(".mp3")) {

                            //add the track to the playlist
                            System.out.println("add the track to the playlist");

                            if (e.isPopupTrigger()) {
                                popup.show((JComponent) e.getSource(),
                                        e.getX(), e.getY());
                            }
                        }
                    }
                });
    }

    @Override
    public DockKey getDockKey() {
        return key;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
