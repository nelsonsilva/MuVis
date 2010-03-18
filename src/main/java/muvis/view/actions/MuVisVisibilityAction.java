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
package muvis.view.actions;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import muvis.Environment;
import muvis.Messages;

/**
 *
 * @author Ricardo
 */
public class MuVisVisibilityAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {

        JFrame frame = Environment.getEnvironmentInstance().getRootFrame();
        boolean visibility = frame.isVisible();
        visibility = !visibility;

        if (e.getSource() instanceof MenuItem) {
            MenuItem item = (MenuItem) e.getSource();
            if (visibility) {
                item.setLabel(Messages.SYSTEM_TRAY_HIDE_MENU_ITEM);
            } else {
                item.setLabel(Messages.SYSTEM_TRAY_SHOW_MENU_ITEM);
            }
        }
        frame.setVisible(visibility);
    }
}
