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

import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
//import javax.swing.UIManager;
//import org.jvnet.substance.skin.SubstanceModerateLookAndFeel;
//import org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel;
//import org.jvnet.substance.skin.SubstanceSaharaLookAndFeel;

/**
 * Entry point of the MuVis project.
 * @author Ricardo
 */
public class MuVisappTest {

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    JFrame.setDefaultLookAndFeelDecorated(true);

                    try {
                        // Set System L&F
                        //UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel());
                        //UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
                        UIManager.setLookAndFeel(new net.sourceforge.napkinlaf.NapkinLookAndFeel());
                        //UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
                    } catch (Exception e){
                        e.printStackTrace();
                    }


                    MuVisApp app = new MuVisApp();
                    app.run();
                }
            });
        } catch (InterruptedException ex) {
            System.out.println("Cannot start the application!");
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            System.out.println("Cannot start the application!");
            ex.printStackTrace();
        }
    }
}