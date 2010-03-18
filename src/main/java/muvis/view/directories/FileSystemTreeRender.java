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
package muvis.view.directories;

import java.awt.Component;
import java.io.File;

import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FileSystemTreeRender extends DefaultTreeCellRenderer {

    private FileSystemView fsv;

    public FileSystemTreeRender(FileSystemView fsv) {
        this.fsv = fsv;
    }

    public FileSystemTreeRender() {
        this(FileSystemView.getFileSystemView());
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {
        if (!(value instanceof File)) {
            return super.getTreeCellRendererComponent(tree, value,
                    sel, expanded, leaf, row, hasFocus);
        }

        super.getTreeCellRendererComponent(
                tree, value, sel, expanded, leaf, row, hasFocus);

        setText(fsv.getSystemDisplayName((File) value));
        //setIcon(fsv.getSystemIcon((File) value));

        return this;
    }
}
