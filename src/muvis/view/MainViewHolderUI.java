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
 * ListViewTableUI.java
 *
 * Created on 11/Mai/2009, 1:21:28
 */

package muvis.view;

/**
 *
 * @author Ricardo
 */
public class MainViewHolderUI extends javax.swing.JPanel {

    /** Creates new form ListViewTableUI */
    public MainViewHolderUI() {
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

        filtersButtonGroup = new javax.swing.ButtonGroup();
        featuresListSplitPane = new javax.swing.JSplitPane();
        featuresFilterPanel = new javax.swing.JPanel();
        mainViewHolderTabs = new javax.swing.JTabbedPane();
        filtersPanel = new javax.swing.JPanel();
        filtersInnerPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        durationTreemapFilterPanel = new javax.swing.JPanel();
        durationFilterLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        yearTreemapFilterPanel = new javax.swing.JPanel();
        yearFilterLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        genreFilterLabel = new javax.swing.JLabel();
        genreTreemapFilterPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        beatFilterLabel = new javax.swing.JLabel();
        beatTreemapFilterPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        moodTreemapFilterPanel = new javax.swing.JPanel();
        moodFilterLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        searchTextField = new javax.swing.JTextField();
        resetFiltersButton = new javax.swing.JButton();
        configurationPropertiesPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        treemapSquareSizeComboBox = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        chooseSeedArtistButton = new javax.swing.JButton();
        treemapStructureVisualization = new javax.swing.JComboBox();
        jPanel10 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jButton2 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        mainViewPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(690, 690));

        featuresListSplitPane.setMinimumSize(new java.awt.Dimension(600, 200));
        featuresListSplitPane.setPreferredSize(new java.awt.Dimension(800, 770));

        featuresFilterPanel.setMinimumSize(new java.awt.Dimension(0, 100));
        featuresFilterPanel.setPreferredSize(new java.awt.Dimension(175, 770));
        featuresFilterPanel.setLayout(new javax.swing.BoxLayout(featuresFilterPanel, javax.swing.BoxLayout.LINE_AXIS));

        mainViewHolderTabs.setAutoscrolls(true);
        mainViewHolderTabs.setMinimumSize(new java.awt.Dimension(175, 300));
        mainViewHolderTabs.setPreferredSize(new java.awt.Dimension(175, 770));

        filtersPanel.setMinimumSize(new java.awt.Dimension(175, 500));
        filtersPanel.setPreferredSize(new java.awt.Dimension(175, 770));
        filtersPanel.setLayout(new javax.swing.BoxLayout(filtersPanel, javax.swing.BoxLayout.Y_AXIS));

        filtersInnerPanel.setLayout(new java.awt.GridLayout(5, 1));

        jPanel1.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 60));
        jPanel1.setPreferredSize(new java.awt.Dimension(175, 153));
        jPanel1.setLayout(new java.awt.BorderLayout());

        durationTreemapFilterPanel.setToolTipText("Filters");
        durationTreemapFilterPanel.setMaximumSize(new java.awt.Dimension(1000, 1000));
        durationTreemapFilterPanel.setMinimumSize(new java.awt.Dimension(100, 50));
        durationTreemapFilterPanel.setName("treemapFilter"); // NOI18N
        durationTreemapFilterPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        durationTreemapFilterPanel.setLayout(new java.awt.CardLayout());
        jPanel1.add(durationTreemapFilterPanel, java.awt.BorderLayout.CENTER);

        durationFilterLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        durationFilterLabel.setText("Duration:");
        jPanel1.add(durationFilterLabel, java.awt.BorderLayout.PAGE_START);

        filtersInnerPanel.add(jPanel1);

        jPanel3.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel3.setMinimumSize(new java.awt.Dimension(100, 60));
        jPanel3.setPreferredSize(new java.awt.Dimension(175, 153));
        jPanel3.setLayout(new java.awt.BorderLayout());

        yearTreemapFilterPanel.setToolTipText("Filters");
        yearTreemapFilterPanel.setMaximumSize(new java.awt.Dimension(1000, 1000));
        yearTreemapFilterPanel.setMinimumSize(new java.awt.Dimension(100, 50));
        yearTreemapFilterPanel.setName("treemapFilter"); // NOI18N
        yearTreemapFilterPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        yearTreemapFilterPanel.setLayout(new java.awt.CardLayout());
        jPanel3.add(yearTreemapFilterPanel, java.awt.BorderLayout.CENTER);

        yearFilterLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        yearFilterLabel.setText("Year:");
        jPanel3.add(yearFilterLabel, java.awt.BorderLayout.PAGE_START);

        filtersInnerPanel.add(jPanel3);

        jPanel5.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel5.setMinimumSize(new java.awt.Dimension(100, 60));
        jPanel5.setPreferredSize(new java.awt.Dimension(175, 153));
        jPanel5.setLayout(new java.awt.BorderLayout());

        genreFilterLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        genreFilterLabel.setText("Genre:");
        genreFilterLabel.setPreferredSize(new java.awt.Dimension(50, 14));
        jPanel5.add(genreFilterLabel, java.awt.BorderLayout.PAGE_START);

        genreTreemapFilterPanel.setToolTipText("Filters");
        genreTreemapFilterPanel.setMaximumSize(new java.awt.Dimension(1000, 1000));
        genreTreemapFilterPanel.setMinimumSize(new java.awt.Dimension(100, 50));
        genreTreemapFilterPanel.setName("treemapFilter"); // NOI18N
        genreTreemapFilterPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        genreTreemapFilterPanel.setLayout(new java.awt.CardLayout());
        jPanel5.add(genreTreemapFilterPanel, java.awt.BorderLayout.CENTER);

        filtersInnerPanel.add(jPanel5);

        jPanel6.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel6.setMinimumSize(new java.awt.Dimension(100, 60));
        jPanel6.setPreferredSize(new java.awt.Dimension(175, 153));
        jPanel6.setLayout(new java.awt.BorderLayout());

        beatFilterLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        beatFilterLabel.setText("Beat:");
        jPanel6.add(beatFilterLabel, java.awt.BorderLayout.PAGE_START);

        beatTreemapFilterPanel.setToolTipText("Filters");
        beatTreemapFilterPanel.setMaximumSize(new java.awt.Dimension(1000, 1000));
        beatTreemapFilterPanel.setMinimumSize(new java.awt.Dimension(100, 50));
        beatTreemapFilterPanel.setName("treemapFilter"); // NOI18N
        beatTreemapFilterPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        beatTreemapFilterPanel.setLayout(new java.awt.CardLayout());
        jPanel6.add(beatTreemapFilterPanel, java.awt.BorderLayout.CENTER);

        filtersInnerPanel.add(jPanel6);

        jPanel7.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel7.setMinimumSize(new java.awt.Dimension(100, 60));
        jPanel7.setPreferredSize(new java.awt.Dimension(175, 153));
        jPanel7.setLayout(new java.awt.BorderLayout());

        moodTreemapFilterPanel.setToolTipText("Filters");
        moodTreemapFilterPanel.setMaximumSize(new java.awt.Dimension(1000, 1000));
        moodTreemapFilterPanel.setMinimumSize(new java.awt.Dimension(100, 50));
        moodTreemapFilterPanel.setName("treemapFilter"); // NOI18N
        moodTreemapFilterPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        moodTreemapFilterPanel.setLayout(new java.awt.CardLayout());
        jPanel7.add(moodTreemapFilterPanel, java.awt.BorderLayout.CENTER);

        moodFilterLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        moodFilterLabel.setText("Mood:");
        jPanel7.add(moodFilterLabel, java.awt.BorderLayout.PAGE_START);

        filtersInnerPanel.add(jPanel7);

        filtersPanel.add(filtersInnerPanel);

        jPanel2.setMaximumSize(new java.awt.Dimension(1000, 1000));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 60));
        jPanel2.setPreferredSize(new java.awt.Dimension(168, 90));

        searchTextField.setMaximumSize(new java.awt.Dimension(200, 100));
        searchTextField.setPreferredSize(new java.awt.Dimension(175, 20));

        resetFiltersButton.setText("Reset Filters");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(resetFiltersButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetFiltersButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        filtersPanel.add(jPanel2);

        mainViewHolderTabs.addTab("Filters", filtersPanel);

        configurationPropertiesPanel.setPreferredSize(new java.awt.Dimension(120, 700));
        configurationPropertiesPanel.setLayout(new java.awt.BorderLayout());

        jPanel4.setMaximumSize(new java.awt.Dimension(100, 200));
        jPanel4.setPreferredSize(new java.awt.Dimension(145, 1000));

        jLabel2.setText("Configurate visualization:");

        jLabel3.setText("Size of the boxes:");

        treemapSquareSizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Number of tracks", "Number of albums", "Equal to all artists" }));
        treemapSquareSizeComboBox.setEditor(null);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(treemapSquareSizeComboBox, 0, 150, Short.MAX_VALUE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treemapSquareSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel4.setText("Structure of visualization:");

        chooseSeedArtistButton.setText("Choose seed artist");

        treemapStructureVisualization.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Artist Similarity", "Artist name - Asc", "Artist name - Desc" }));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(treemapStructureVisualization, 0, 150, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addComponent(chooseSeedArtistButton, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(treemapStructureVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chooseSeedArtistButton)
                .addContainerGap())
        );

        jLabel5.setText("Boxes background:");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Colors", "Album Covers", "Mixed" }));

        jButton2.setText("Configurate Colors");
        jButton2.setEnabled(false);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox3, 0, 150, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel5))
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jCheckBox1.setText("Use line selection mode");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(38, Short.MAX_VALUE))
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(622, 622, 622))
        );

        configurationPropertiesPanel.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        mainViewHolderTabs.addTab("Properties", configurationPropertiesPanel);

        featuresFilterPanel.add(mainViewHolderTabs);

        featuresListSplitPane.setLeftComponent(featuresFilterPanel);

        mainViewPanel.setMaximumSize(new java.awt.Dimension(600, 600));
        mainViewPanel.setLayout(new java.awt.CardLayout());
        featuresListSplitPane.setRightComponent(mainViewPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(featuresListSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(featuresListSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JLabel beatFilterLabel;
    protected javax.swing.JPanel beatTreemapFilterPanel;
    protected javax.swing.JButton chooseSeedArtistButton;
    protected javax.swing.JPanel configurationPropertiesPanel;
    protected javax.swing.JLabel durationFilterLabel;
    protected javax.swing.JPanel durationTreemapFilterPanel;
    protected javax.swing.JPanel featuresFilterPanel;
    private javax.swing.JSplitPane featuresListSplitPane;
    protected javax.swing.ButtonGroup filtersButtonGroup;
    private javax.swing.JPanel filtersInnerPanel;
    protected javax.swing.JPanel filtersPanel;
    protected javax.swing.JLabel genreFilterLabel;
    protected javax.swing.JPanel genreTreemapFilterPanel;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    protected javax.swing.JTabbedPane mainViewHolderTabs;
    protected javax.swing.JPanel mainViewPanel;
    protected javax.swing.JLabel moodFilterLabel;
    protected javax.swing.JPanel moodTreemapFilterPanel;
    protected javax.swing.JButton resetFiltersButton;
    protected javax.swing.JTextField searchTextField;
    protected javax.swing.JComboBox treemapSquareSizeComboBox;
    protected javax.swing.JComboBox treemapStructureVisualization;
    protected javax.swing.JLabel yearFilterLabel;
    protected javax.swing.JPanel yearTreemapFilterPanel;
    // End of variables declaration//GEN-END:variables

}
