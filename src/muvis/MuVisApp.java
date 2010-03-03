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

package muvis;

import java.io.FileNotFoundException;
import javax.swing.JFrame;
import muvis.view.MuVisAppView;
import muvis.view.controllers.LoadLibraryController;
import muvis.view.loader.LoadLibraryView;

/**
 * This is the entry point of the application.
 * @author Ricardo
 */
public class MuVisApp extends JFrame implements Runnable {

    private void processLibrary() {
        Environment workspace = Environment.getWorkspaceInstance();

        if (!workspace.configFileExists()) {
            workspace.initConfigFile();

            LoadLibraryController controller = new LoadLibraryController();
            LoadLibraryView loadLibrary = new LoadLibraryView(this, controller);
            add(loadLibrary);
            setSize(loadLibrary.getPreferredSize());
            validate();
            setVisible(true);
            setDefaultCloseOperation(EXIT_ON_CLOSE); //EXIT_ON_CLOSE
        } else {//library already loaded

            try {
                //library already loaded
                workspace.loadWorkspace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.out.println("Continuing without the loaded configuration");
            }

            MuVisAppView frameTest = new MuVisAppView();
            frameTest.setSize(1280, 770);
            frameTest.setResizable(true);
            frameTest.validate();
            frameTest.setVisible(true);
            frameTest.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
    }

    public MuVisApp() {}

    @Override
    public void run() {
        processLibrary();
    }
}
