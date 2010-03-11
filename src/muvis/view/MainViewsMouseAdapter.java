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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import muvis.Messages;
import muvis.util.EmptyActionListener;

/**
 *
 * @author Ricardo
 */
public abstract class MainViewsMouseAdapter extends MouseAdapter {

    protected JPopupMenu contextMenu;
    protected JMenuItem previewElementMenu;
    protected JMenuItem findSimilarElementMenu;
    protected JMenuItem findNonSimilarElementMenu;
    protected JMenuItem addElementToPlaylistMenu;
    protected JMenuItem closeMenu;

    public enum ElementType {

        SIMPLE, MULTIPLE
    }

    protected void mouseHandler(MouseEvent e) {
        contextMenu = createContextMenu(null, ElementType.SIMPLE);
        contextMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseHandler(e);
    }

    protected void assignActionListeners() {
        previewElementMenu.addActionListener(new EmptyActionListener());
        addElementToPlaylistMenu.addActionListener(new EmptyActionListener());
        findSimilarElementMenu.addActionListener(new EmptyActionListener());
        findNonSimilarElementMenu.addActionListener(new EmptyActionListener());
    }

    protected JPopupMenu createContextMenu(String elementName, ElementType type) {

        //creating the JPopupMenu and the menu items
        contextMenu = new JPopupMenu();
        previewElementMenu = new JMenuItem();
        findSimilarElementMenu = new JMenuItem();
        findNonSimilarElementMenu = new JMenuItem();
        addElementToPlaylistMenu = new JMenuItem();
        closeMenu = new JMenuItem();

        //setting the labels for the menu items
        if (elementName.equals(Messages.COL_TRACK_NAME_LABEL)) {
            findSimilarElementMenu.setText(Messages.FIND_SIMILAR_TRACKS_LABEL);
            findNonSimilarElementMenu.setText(Messages.FIND_NON_SIMILAR_TRACKS_LABEL);

            if (type.equals(ElementType.SIMPLE)) {
                previewElementMenu.setText(Messages.PREVIEW_TRACK_LABEL);
                addElementToPlaylistMenu.setText(Messages.ADD_TRACK_PLAYLIST_LABEL);
            } else {
                previewElementMenu.setText(Messages.PREVIEW_SEVERAL_TRACKS_LABEL);
                addElementToPlaylistMenu.setText(Messages.ADD_SEVERAL_TRACKS_PLAYLIST_LABEL);
            }
        } else if (elementName.equals(Messages.COL_ALBUM_NAME_LABE)) {
            findSimilarElementMenu.setText(Messages.FIND_SIMILAR_ALBUMS_LABEL);
            findNonSimilarElementMenu.setText(Messages.FIND_NON_SIMILAR_ALBUMS_LABEL);

            if (type.equals(ElementType.SIMPLE)) {
                previewElementMenu.setText(Messages.PREVIEW_ALBUM_LABEL);
                addElementToPlaylistMenu.setText(Messages.ADD_ALBUM_PLAYLIST_LABEL);
            } else {
                previewElementMenu.setText(Messages.PREVIEW_SEVERAL_ALBUMS_LABEL);
                addElementToPlaylistMenu.setText(Messages.ADD_SEVERAL_ALBUMS_PLAYLIST_LABEL);
            }
        } else if (elementName.equals(Messages.COL_ARTIST_NAME_LABEL)) {
            findSimilarElementMenu.setText(Messages.FIND_SIMILAR_ARTISTS_LABEL);
            findNonSimilarElementMenu.setText(Messages.FIND_NON_SIMILAR_ARTISTS_LABEL);

            if (type.equals(ElementType.SIMPLE)) {
                previewElementMenu.setText(Messages.PREVIEW_ARTIST_LABEL);
                addElementToPlaylistMenu.setText(Messages.ADD_ARTIST_PLAYLIST_LABEL);
            } else {
                previewElementMenu.setText(Messages.PREVIEW_SEVERAL_ARTISTS_LABEL);
                addElementToPlaylistMenu.setText(Messages.ADD_SEVERAL_ARTISTS_PLAYLIST_LABEL);
            }
        } else {
            return contextMenu;
        }

        closeMenu.setText(Messages.CLOSE_LABEL);

        assignActionListeners();

        contextMenu.add(previewElementMenu);
        contextMenu.add(findSimilarElementMenu);
        contextMenu.add(findNonSimilarElementMenu);
        contextMenu.add(addElementToPlaylistMenu);
        contextMenu.addSeparator();
        contextMenu.add(closeMenu);

        return contextMenu;
    }
}
