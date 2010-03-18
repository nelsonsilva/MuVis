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
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This is the entry point of the application.
 * @author Ricardo
 */
public class MuVisApp extends JFrame implements Runnable {
    public static JFrame rootFrame;

    @Autowired private Environment environment;
    
    @Autowired
    private LoadLibraryView loadLibraryView;

    @Autowired
    private MuVisAppView muVisAppView;
    
    private void processLibrary() {
     

        if (!environment.configFileExists()) {
            environment.initConfigFile();

            rootFrame = new JFrame();
            loadLibraryView.setParent(this);
            
            this.setVisible(true);
        } else {//library already loaded

            try {
                //library already loaded
                environment.loadWorkspace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.out.println("Continuing without the loaded configuration");
            }


            muVisAppView.setSize(1280, 770);
            muVisAppView.setResizable(true);
            muVisAppView.validate();
            muVisAppView.setVisible(true);
            muVisAppView.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            rootFrame=muVisAppView;
        }

    }

    public MuVisApp() {}

    @Override
    public void run() {
        processLibrary();
    }

    public static JFrame getRootFrame() {
        return rootFrame;
    }

    public static void setRootFrame(MuVisAppView rootFrame) {
        MuVisApp.rootFrame = rootFrame;
    }
}
