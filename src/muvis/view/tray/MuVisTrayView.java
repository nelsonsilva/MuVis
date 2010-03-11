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
package muvis.view.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.net.URL;
import javax.swing.ImageIcon;
import muvis.Messages;
import muvis.view.actions.AboutMuVisAction;
import muvis.view.actions.MuVisExitAction;
import muvis.view.actions.MuVisVisibilityAction;
import muvis.audio.actions.MusicPlayerPlayNextTrackAction;
import muvis.audio.actions.MusicPlayerPauseAction;
import muvis.audio.actions.MusicPlayerPlayPrevTrackAction;
import muvis.audio.actions.MusicPlayerStopTrackAction;

/**
 * System Tray View for MuVis Application.
 * The following operations are available:
 * -About information
 * -Application Visibility
 * -Music player controllers (play, pause, stop, next and previous track)
 * -Exit application
 * @author Ricardo
 */
public class MuVisTrayView{

    public MuVisTrayView() {

        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println(Messages.SYSTEM_TRAY_NOT_SUPPORTED);
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(createImage("/images/logo.png", Messages.MUVIS_QUOTE));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem(Messages.SYSTEM_TRAY_ABOUT_MENU_ITEM);
        MenuItem displayMenu = new MenuItem(Messages.SYSTEM_TRAY_HIDE_MENU_ITEM);
        MenuItem playItem = new MenuItem(Messages.SYSTEM_TRAY_PLAY_MENU_ITEM);
        MenuItem stopItem = new MenuItem(Messages.SYSTEM_TRAY_STOP_MENU_ITEM);
        MenuItem nextTrackItem = new MenuItem(Messages.SYSTEM_TRAY_NEXT_TRACK_MENU_ITEM);
        MenuItem prevTrackItem = new MenuItem(Messages.SYSTEM_TRAY_PREV_TRACK_MENU_ITEM);
        MenuItem exitItem = new MenuItem(Messages.SYSTEM_TRAY_EXIT_MENU_ITEM);

        aboutItem.addActionListener(new AboutMuVisAction());
        playItem.addActionListener(new MusicPlayerPauseAction(playItem));
        stopItem.addActionListener(new MusicPlayerStopTrackAction());
        nextTrackItem.addActionListener(new MusicPlayerPlayNextTrackAction());
        prevTrackItem.addActionListener(new MusicPlayerPlayPrevTrackAction());
        displayMenu.addActionListener(new MuVisVisibilityAction());
        exitItem.addActionListener(new MuVisExitAction());

        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.add(displayMenu);
        popup.addSeparator();
        popup.add(playItem);
        popup.add(stopItem);
        popup.add(nextTrackItem);
        popup.add(prevTrackItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

    }

    //Obtain the image URL
    protected Image createImage(String path, String description) {
        URL imageURL = MuVisTrayView.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}
