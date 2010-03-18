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
package muvis.view.main.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import javax.swing.JMenuItem;
import muvis.Messages;
import muvis.audio.AudioMetadata;
import muvis.audio.playlist.BasePlaylist;
import muvis.audio.playlist.PlaylistItem;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.view.main.MuVisTreemapNode;
import muvis.view.main.filters.TreemapFilterManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Action for adding tracks, albums or artists to the current playlist.
 * @author Ricardo
 */
public class AddToPlaylistTreemapAction implements ActionListener {

    @Autowired MusicLibraryDatabaseManager dbManager;
    @Autowired TreemapFilterManager filterManager;
    @Autowired
    BasePlaylist playlist;
    protected ArrayList<MuVisTreemapNode> selectedNodes;
    protected MuVisTreemapNode nodeUnder;

    public AddToPlaylistTreemapAction(ArrayList<MuVisTreemapNode> selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public void setNodeUnder(MuVisTreemapNode node) {
        this.nodeUnder = node;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) e.getSource();

            if (item.getText().contains(Messages.ARTIST_NAME_LABEL)) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {

                    @Override
                    public void run() {
                        //add artist to playlist


                        List artistTracks = new ArrayList();
                        if (!selectedNodes.contains(nodeUnder)) {
                            selectedNodes.add(nodeUnder);
                        }

                        for (MuVisTreemapNode sNode : selectedNodes) {
                            artistTracks.addAll(filterManager.getFilteredTracks(sNode.getName()));
                        }

                        for (Object trackObject : artistTracks) {
                            int trackId = (Integer) trackObject;
                            String track = dbManager.getFilename(trackId);
                            AudioMetadata metadata = dbManager.getTrackMetadata(trackId);
                            PlaylistItem pliItem = new PlaylistItem(track, Messages.EMPTY_STRING, metadata);
                            playlist.appendItem(pliItem);
                        }
                        selectedNodes.remove(nodeUnder);
                    }
                });
            }
        }
    }
}
