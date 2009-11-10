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

/*
 * PlaylistViewUI.java
 *
 * Created on 11/Mai/2009, 1:37:13
 */

package muvis.view;

/**
 *
 * @author Ricardo
 */
public class PlaylistViewUI extends javax.swing.JPanel {

    /** Creates new form PlaylistViewUI */
    public PlaylistViewUI() {
        initComponents();
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listTracksPanel = new javax.swing.JPanel();
        listTracksScrollPanel = new javax.swing.JScrollPane();
        listTracks = new javax.swing.JList();
        totalTracksInfoLabel = new javax.swing.JLabel();
        playlistButtonsBar = new javax.swing.JPanel();
        remTrackButton = new javax.swing.JButton();
        loadPlaylistButton = new javax.swing.JButton();
        savePlaylistButton = new javax.swing.JButton();
        managePlaylistPanel = new javax.swing.JPanel();
        managePlaylistButton = new javax.swing.JButton();
        infoPanelSeparator = new javax.swing.JSeparator();

        setMaximumSize(new java.awt.Dimension(250, 300));
        setMinimumSize(new java.awt.Dimension(150, 56));
        setPreferredSize(new java.awt.Dimension(200, 375));
        setLayout(new java.awt.BorderLayout());

        listTracks.setToolTipText("Playlist Tracks");
        listTracksScrollPanel.setViewportView(listTracks);

        totalTracksInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        totalTracksInfoLabel.setText("Total:");

        javax.swing.GroupLayout listTracksPanelLayout = new javax.swing.GroupLayout(listTracksPanel);
        listTracksPanel.setLayout(listTracksPanelLayout);
        listTracksPanelLayout.setHorizontalGroup(
            listTracksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(listTracksScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(totalTracksInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        listTracksPanelLayout.setVerticalGroup(
            listTracksPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, listTracksPanelLayout.createSequentialGroup()
                .addComponent(listTracksScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalTracksInfoLabel))
        );

        add(listTracksPanel, java.awt.BorderLayout.CENTER);

        playlistButtonsBar.setLayout(new java.awt.GridLayout(1, 0));

        remTrackButton.setText("Rem");
        remTrackButton.setEnabled(false);
        playlistButtonsBar.add(remTrackButton);

        loadPlaylistButton.setText("Load");
        loadPlaylistButton.setToolTipText("Load a previously saved playlist");
        playlistButtonsBar.add(loadPlaylistButton);

        savePlaylistButton.setText("Save");
        savePlaylistButton.setToolTipText("Save your current playlist");
        playlistButtonsBar.add(savePlaylistButton);

        add(playlistButtonsBar, java.awt.BorderLayout.PAGE_START);

        managePlaylistPanel.setLayout(new java.awt.BorderLayout());

        managePlaylistButton.setText("Manage Playlist");
        managePlaylistPanel.add(managePlaylistButton, java.awt.BorderLayout.CENTER);
        managePlaylistPanel.add(infoPanelSeparator, java.awt.BorderLayout.PAGE_START);

        add(managePlaylistPanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator infoPanelSeparator;
    protected javax.swing.JList listTracks;
    private javax.swing.JPanel listTracksPanel;
    private javax.swing.JScrollPane listTracksScrollPanel;
    protected javax.swing.JButton loadPlaylistButton;
    protected javax.swing.JButton managePlaylistButton;
    private javax.swing.JPanel managePlaylistPanel;
    private javax.swing.JPanel playlistButtonsBar;
    protected javax.swing.JButton remTrackButton;
    protected javax.swing.JButton savePlaylistButton;
    protected javax.swing.JLabel totalTracksInfoLabel;
    // End of variables declaration//GEN-END:variables

}
