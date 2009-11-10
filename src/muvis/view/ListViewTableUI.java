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
public class ListViewTableUI extends javax.swing.JPanel {

    /** Creates new form ListViewTableUI */
    public ListViewTableUI() {
        initComponents();
        tracksTableView.setAutoCreateRowSorter(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tableViewPanel = new javax.swing.JScrollPane();
        tracksTableView = new javax.swing.JTable();

        setLayout(new java.awt.CardLayout());

        tracksTableView.setAutoCreateRowSorter(true);
        tracksTableView.setModel( new TracksTableModel());
        tracksTableView.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tracksTableView.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tracksTableView.getTableHeader().setReorderingAllowed(false);
        tableViewPanel.setViewportView(tracksTableView);
        tracksTableView.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tracksTableView.getColumnModel().getColumn(0).setPreferredWidth(2);

        add(tableViewPanel, "card3");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane tableViewPanel;
    protected javax.swing.JTable tracksTableView;
    // End of variables declaration//GEN-END:variables

}
