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
 * MusicControllerViewUI.java
 *
 * Created on 9/Mai/2009, 20:24:05
 */

package muvis.view;

import java.io.IOException;
import muvis.util.JImagePanel;

/**
 *
 * @author Ricardo
 */
public class MusicControllerViewUI extends javax.swing.JPanel {

    /** Creates new form MusicControllerViewUI */
    public MusicControllerViewUI() {
        initComponents();

        playingModeGroup.add(filteredTracksRadioButton);
        playingModeGroup.add(playlistRadioButton);
        playingModeGroup.add(allTracksRadioButton);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playingModeGroup = new javax.swing.ButtonGroup();
        musicControllerPanel = new javax.swing.JPanel();
        playingModePanel = new javax.swing.JPanel();
        playingModeLabel = new javax.swing.JLabel();
        filteredTracksRadioButton = new javax.swing.JRadioButton();
        playlistRadioButton = new javax.swing.JRadioButton();
        allTracksRadioButton = new javax.swing.JRadioButton();
        musicControllerSepator = new javax.swing.JSeparator();
        playerControllerPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        previousTrackButton = new javax.swing.JButton();
        stopPlayerButton = new javax.swing.JButton();
        playTrackButton = new javax.swing.JButton();
        nextTrackButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        musicTimelineSlider = new javax.swing.JSlider();
        trackInfoPanel = new javax.swing.JPanel();
        trackInfoLabel = new javax.swing.JLabel();
        trackInformationPanel = new javax.swing.JPanel();
        artistAlbumInfoLabel = new javax.swing.JLabel();
        trackNameLabel = new javax.swing.JLabel();
        trackyearLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        volumeSlider = new javax.swing.JSlider();
        albumCoverPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridLayout(1, 0));

        musicControllerPanel.setMinimumSize(new java.awt.Dimension(874, 100));
        musicControllerPanel.setPreferredSize(new java.awt.Dimension(874, 125));

        playingModeLabel.setFont(new java.awt.Font("Arial", 1, 11));
        playingModeLabel.setText("Play from:");

        filteredTracksRadioButton.setFont(new java.awt.Font("Arial", 0, 11));
        filteredTracksRadioButton.setText("Filtered tracks");
        filteredTracksRadioButton.setToolTipText("This mode allows to play the filtered tracks in the main view");

        playlistRadioButton.setFont(new java.awt.Font("Arial", 0, 11));
        playlistRadioButton.setText("Playlist");
        playlistRadioButton.setToolTipText("This mode allows to play the tracks in the built playlist");

        allTracksRadioButton.setFont(new java.awt.Font("Arial", 0, 11));
        allTracksRadioButton.setText("All");
        allTracksRadioButton.setToolTipText("This mode is used for combine the previous modes: play the filtered tracks and the tracks in the playlist");

        org.jdesktop.layout.GroupLayout playingModePanelLayout = new org.jdesktop.layout.GroupLayout(playingModePanel);
        playingModePanel.setLayout(playingModePanelLayout);
        playingModePanelLayout.setHorizontalGroup(
            playingModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(playingModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(playingModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(allTracksRadioButton)
                    .add(playlistRadioButton)
                    .add(playingModeLabel)
                    .add(filteredTracksRadioButton))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        playingModePanelLayout.setVerticalGroup(
            playingModePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(playingModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(playingModeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filteredTracksRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(playlistRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(allTracksRadioButton)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        musicControllerSepator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        previousTrackButton.setFont(new java.awt.Font("Arial", 0, 11));
        previousTrackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/StepBack16.gif"))); // NOI18N
        previousTrackButton.setToolTipText("Play previous track");
        previousTrackButton.setPreferredSize(new java.awt.Dimension(73, 23));
        jPanel2.add(previousTrackButton);

        stopPlayerButton.setFont(new java.awt.Font("Arial", 0, 11));
        stopPlayerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Stop16.gif"))); // NOI18N
        stopPlayerButton.setToolTipText("Play the currently selected track");
        stopPlayerButton.setMaximumSize(new java.awt.Dimension(73, 23));
        stopPlayerButton.setMinimumSize(new java.awt.Dimension(73, 23));
        stopPlayerButton.setPreferredSize(new java.awt.Dimension(73, 23));
        jPanel2.add(stopPlayerButton);

        playTrackButton.setFont(new java.awt.Font("Arial", 0, 11));
        playTrackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play16.gif"))); // NOI18N
        playTrackButton.setToolTipText("Play the currently selected track");
        playTrackButton.setMaximumSize(new java.awt.Dimension(73, 23));
        playTrackButton.setMinimumSize(new java.awt.Dimension(73, 23));
        playTrackButton.setPreferredSize(new java.awt.Dimension(73, 23));
        jPanel2.add(playTrackButton);

        nextTrackButton.setFont(new java.awt.Font("Arial", 0, 11));
        nextTrackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/StepForward16.gif"))); // NOI18N
        nextTrackButton.setToolTipText("Play the next track");
        nextTrackButton.setMaximumSize(new java.awt.Dimension(73, 23));
        nextTrackButton.setMinimumSize(new java.awt.Dimension(73, 23));
        nextTrackButton.setPreferredSize(new java.awt.Dimension(73, 23));
        jPanel2.add(nextTrackButton);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        musicTimelineSlider.setMaximum(1);
        musicTimelineSlider.setValue(0);
        jPanel3.add(musicTimelineSlider);

        org.jdesktop.layout.GroupLayout playerControllerPanelLayout = new org.jdesktop.layout.GroupLayout(playerControllerPanel);
        playerControllerPanel.setLayout(playerControllerPanelLayout);
        playerControllerPanelLayout.setHorizontalGroup(
            playerControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(playerControllerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(playerControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, playerControllerPanelLayout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 315, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        playerControllerPanelLayout.setVerticalGroup(
            playerControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, playerControllerPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        trackInfoLabel.setFont(new java.awt.Font("Arial", 1, 11));
        trackInfoLabel.setText("Track information:");

        org.jdesktop.layout.GroupLayout trackInfoPanelLayout = new org.jdesktop.layout.GroupLayout(trackInfoPanel);
        trackInfoPanel.setLayout(trackInfoPanelLayout);
        trackInfoPanelLayout.setHorizontalGroup(
            trackInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(trackInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(trackInfoLabel)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        trackInfoPanelLayout.setVerticalGroup(
            trackInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(trackInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(trackInfoLabel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        artistAlbumInfoLabel.setFont(new java.awt.Font("Arial", 0, 11));
        artistAlbumInfoLabel.setText("Artist & album - not available");

        trackNameLabel.setFont(new java.awt.Font("Arial", 0, 11));
        trackNameLabel.setText("Track name - not available");

        trackyearLabel.setFont(new java.awt.Font("Arial", 0, 11));
        trackyearLabel.setText("Year - not available");

        org.jdesktop.layout.GroupLayout trackInformationPanelLayout = new org.jdesktop.layout.GroupLayout(trackInformationPanel);
        trackInformationPanel.setLayout(trackInformationPanelLayout);
        trackInformationPanelLayout.setHorizontalGroup(
            trackInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(trackInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(trackInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(artistAlbumInfoLabel)
                    .add(trackNameLabel)
                    .add(trackyearLabel))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        trackInformationPanelLayout.setVerticalGroup(
            trackInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(trackInformationPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(artistAlbumInfoLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(trackNameLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(trackyearLabel))
        );

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Volume16.gif"))); // NOI18N

        volumeSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        volumeSlider.setToolTipText("Set the volume of the track being played");

        albumCoverPanel.setMaximumSize(new java.awt.Dimension(80, 80));
        albumCoverPanel.setMinimumSize(new java.awt.Dimension(80, 80));
        albumCoverPanel.setPreferredSize(new java.awt.Dimension(80, 80));
        albumCoverPanel.setSize(new java.awt.Dimension(80, 80));
        try {
            albumCoverPanel = new JImagePanel(
                getClass().getResource("/images/not_available.jpg"),
                0,
                0,
                80,
                80);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        org.jdesktop.layout.GroupLayout albumCoverPanelLayout = new org.jdesktop.layout.GroupLayout(albumCoverPanel);
        albumCoverPanel.setLayout(albumCoverPanelLayout);
        albumCoverPanelLayout.setHorizontalGroup(
            albumCoverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 89, Short.MAX_VALUE)
        );
        albumCoverPanelLayout.setVerticalGroup(
            albumCoverPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 80, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout musicControllerPanelLayout = new org.jdesktop.layout.GroupLayout(musicControllerPanel);
        musicControllerPanel.setLayout(musicControllerPanelLayout);
        musicControllerPanelLayout.setHorizontalGroup(
            musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(musicControllerPanelLayout.createSequentialGroup()
                .add(playingModePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(playerControllerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(volumeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(musicControllerSepator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(musicControllerPanelLayout.createSequentialGroup()
                        .add(trackInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(trackInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(10, 10, 10)
                        .add(albumCoverPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(musicControllerPanelLayout.createSequentialGroup()
                        .add(41, 41, 41)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        musicControllerPanelLayout.setVerticalGroup(
            musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(musicControllerPanelLayout.createSequentialGroup()
                .add(musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(playingModePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(musicControllerPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(musicControllerPanelLayout.createSequentialGroup()
                                .add(musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, playerControllerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, musicControllerPanelLayout.createSequentialGroup()
                                        .add(jLabel1)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(volumeSlider, 0, 0, Short.MAX_VALUE)))
                                .add(18, 115, Short.MAX_VALUE))
                            .add(musicControllerPanelLayout.createSequentialGroup()
                                .add(musicControllerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(trackInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(trackInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(musicControllerSepator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .add(albumCoverPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(109, 109, 109)))
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        add(musicControllerPanel);
    }// </editor-fold>//GEN-END:initComponents
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel albumCoverPanel;
    protected javax.swing.JRadioButton allTracksRadioButton;
    protected javax.swing.JLabel artistAlbumInfoLabel;
    protected javax.swing.JRadioButton filteredTracksRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel musicControllerPanel;
    private javax.swing.JSeparator musicControllerSepator;
    protected javax.swing.JSlider musicTimelineSlider;
    protected javax.swing.JButton nextTrackButton;
    protected javax.swing.JButton playTrackButton;
    private javax.swing.JPanel playerControllerPanel;
    protected javax.swing.ButtonGroup playingModeGroup;
    private javax.swing.JLabel playingModeLabel;
    private javax.swing.JPanel playingModePanel;
    protected javax.swing.JRadioButton playlistRadioButton;
    protected javax.swing.JButton previousTrackButton;
    protected javax.swing.JButton stopPlayerButton;
    private javax.swing.JLabel trackInfoLabel;
    private javax.swing.JPanel trackInfoPanel;
    private javax.swing.JPanel trackInformationPanel;
    protected javax.swing.JLabel trackNameLabel;
    protected javax.swing.JLabel trackyearLabel;
    protected javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables

}
