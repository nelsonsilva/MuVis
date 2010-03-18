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
        Environment environment = Environment.getEnvironmentInstance();

        if (!environment.configFileExists()) {
            environment.initConfigFile();

            JFrame frame = new JFrame();
            environment.setRootFrame(frame);
            LoadLibraryController controller = new LoadLibraryController();
            new LoadLibraryView(controller);
        } else {//library already loaded

            try {
                //library already loaded
                environment.loadWorkspace();
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
            environment.setRootFrame(frameTest);
        }
    }

    public MuVisApp() {}

    @Override
    public void run() {
        processLibrary();
    }
}
