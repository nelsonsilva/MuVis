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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import muvis.Environment;
import muvis.Messages;
import muvis.util.Util;

/**
 * Action for displaying information about the MuVis Application
 * Relevant information displayed: Version and Year
 * @author Ricardo
 */
public class AboutMuVisAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        String appVersion = Environment.getEnvironmentInstance().getProperty("muvis.app_version").toString();
        String appVersionYear = Environment.getEnvironmentInstance().getProperty("muvis.app_version_year").toString();
        String message = Messages.MUVIS_QUOTE + "\nv" + appVersion + " @ " + appVersionYear + "\n Created by Ricardo Dias";
        Util.displayInformationMessage(Environment.getEnvironmentInstance().getRootFrame(), Messages.MUVIS_ABOUT_LABEL, message);
    }
}
