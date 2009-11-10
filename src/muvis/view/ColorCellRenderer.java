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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import muvis.util.Util;

/**
 * Specific cell renderer for displaying the track time correctly and the background
 * of the table rows.
 * @author Ricardo
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int col) {

        Component comp = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, col);

        if (isSelected == false && hasFocus == false) {
            ((JComponent) comp).setOpaque(true); //if comp is a JLabel
            if ((row % 2) == 1) { //you can specify arbitrary row
                comp.setBackground( Color.getHSBColor(0, 0, 0.9f));
            } else {
                comp.setBackground(Color.WHITE);
            }
        }

        if (col == 4) {
            ((JLabel) comp).setText(Util.secondsToTimeDisplay(value));
        }

        return comp;
    }
}