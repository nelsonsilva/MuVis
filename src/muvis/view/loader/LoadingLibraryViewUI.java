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
 * LoadingLibraryViewUI.java
 *
 * Created on 12/Mai/2009, 23:48:23
 */

package muvis.view.loader;

/**
 *
 * @author Ricardo
 */
public class LoadingLibraryViewUI extends javax.swing.JPanel {

    /** Creates new form LoadingLibraryViewUI */
    public LoadingLibraryViewUI() {
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

        jPanel2 = new javax.swing.JPanel();
        loadingLibraryProgressBar = new javax.swing.JProgressBar();
        loadingTracksLabel = new javax.swing.JLabel();
        processingStageLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        trackPathNameLabel = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        pauseLibraryLoadingButton = new javax.swing.JButton();
        skipLoadingLibraryButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        pleaseWaitLabel = new javax.swing.JLabel();
        loadingLibraryLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(600, 300));

        loadingTracksLabel.setText("Loading track x of y:");

        processingStageLabel.setText("Stage x of y");

        jScrollPane3.setBorder(null);
        jScrollPane3.setEnabled(false);
        jScrollPane3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(226, 76));

        trackPathNameLabel.setBackground(new java.awt.Color(240, 240, 240));
        trackPathNameLabel.setColumns(10);
        trackPathNameLabel.setEditable(false);
        trackPathNameLabel.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        trackPathNameLabel.setLineWrap(true);
        trackPathNameLabel.setRows(2);
        trackPathNameLabel.setWrapStyleWord(true);
        trackPathNameLabel.setBorder(null);
        trackPathNameLabel.setFocusable(false);
        trackPathNameLabel.setOpaque(false);
        trackPathNameLabel.setRequestFocusEnabled(false);
        trackPathNameLabel.setVerifyInputWhenFocusTarget(false);
        jScrollPane3.setViewportView(trackPathNameLabel);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(loadingLibraryProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(processingStageLabel))
                            .addComponent(loadingTracksLabel))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(processingStageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadingLibraryProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loadingTracksLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(3, Short.MAX_VALUE))
        );

        pauseLibraryLoadingButton.setText("Pause");

        skipLoadingLibraryButton.setText("Skip");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pauseLibraryLoadingButton)
                .addGap(10, 10, 10)
                .addComponent(skipLoadingLibraryButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(skipLoadingLibraryButton)
                    .addComponent(pauseLibraryLoadingButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pleaseWaitLabel.setFont(new java.awt.Font("Arial", 2, 18));
        pleaseWaitLabel.setText("Please Wait...");
        pleaseWaitLabel.setDoubleBuffered(true);

        loadingLibraryLabel.setFont(new java.awt.Font("Arial", 2, 36));
        loadingLibraryLabel.setText("Loading your library");
        loadingLibraryLabel.setDoubleBuffered(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pleaseWaitLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                    .addComponent(loadingLibraryLabel))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(loadingLibraryLabel)
                .addGap(18, 18, 18)
                .addComponent(pleaseWaitLabel))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel loadingLibraryLabel;
    public javax.swing.JProgressBar loadingLibraryProgressBar;
    public javax.swing.JLabel loadingTracksLabel;
    public javax.swing.JButton pauseLibraryLoadingButton;
    private javax.swing.JLabel pleaseWaitLabel;
    public javax.swing.JLabel processingStageLabel;
    public javax.swing.JButton skipLoadingLibraryButton;
    public javax.swing.JTextArea trackPathNameLabel;
    // End of variables declaration//GEN-END:variables

}
