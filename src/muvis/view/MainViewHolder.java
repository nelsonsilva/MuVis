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
package muvis.view;

import muvis.view.table.ListViewTableView;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import muvis.Elements;
import muvis.Environment;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.filters.BeatTableFilter;
import muvis.filters.DurationTableFilter;
import muvis.filters.GenreTableFilter;
import muvis.filters.MoodTableFilter;
import muvis.filters.TableFilterManager;
import muvis.filters.TextTableFilter;
import muvis.filters.YearTableFilter;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.filters.MuVisBeatFilterDraw;
import muvis.view.filters.MuVisBeatFilterNode;
import muvis.view.filters.MuVisBeatFilterSize;
import muvis.view.filters.MuVisDurationFilterDraw;
import muvis.view.filters.MuVisDurationFilterNode;
import muvis.view.filters.MuVisDurationFilterSize;
import muvis.view.filters.MuVisFilterAction;
import muvis.view.filters.MuVisFilterDraw;
import muvis.view.filters.MuVisGenreFilterDraw;
import muvis.view.filters.MuVisGenreFilterNode;
import muvis.view.filters.MuVisGenreFilterSize;
import muvis.view.filters.MuVisMoodFilterDraw;
import muvis.view.filters.MuVisMoodFilterNode;
import muvis.view.filters.MuVisMoodFilterSize;
import muvis.view.filters.MuVisYearFilterDraw;
import muvis.view.filters.MuVisYearFilterNode;
import muvis.view.filters.MuVisYearFilterSize;
import muvis.view.main.MuVisComputeAlbumSize;
import muvis.view.main.MuVisComputeEqualSize;
import muvis.view.main.MuVisComputeTrackSize;
import muvis.view.main.TMAlgorithmAscOrder;
import muvis.view.main.TMAlgorithmDescOrder;
import muvis.view.filters.TMAlgorithmAscOrderFilter;
import muvis.view.main.filters.TreemapFilterManager;
import net.bouthier.treemapSwing.TMAlgorithmSquarified;
import net.bouthier.treemapSwing.TMView;
import net.bouthier.treemapSwing.TreeMap;

/**
 * Panel that holds the filter visualization and the main visualization
 * @author Ricardo
 */
public class MainViewHolder extends MainViewHolderUI implements Dockable, ActionListener, Observer, ComponentListener {

    private DockKey key;
    private JPanel currentPanel;
    private String currentPanelStr;
    private Hashtable<String, JPanel> mainViewPanels;
    private TableFilterManager tableFilterManager;
    private TreemapFilterManager treemapFilterManager;
    private ScheduledExecutorService scheduler;

    /*
     * Table Filter elements
     */
    private DurationTableFilter durationFilter;
    private TextTableFilter textTableFilter;
    private YearTableFilter yearTableFilter;
    private GenreTableFilter genreTableFilter;
    private MoodTableFilter moodTableFilter;
    private BeatTableFilter beatTableFilter;

    /*
     * Duration Filter Elements
     */
    private MuVisDurationFilterNode durationFilterRoot;
    private TreeMap durationFilterTreeMap;
    private MuVisDurationFilterSize durationFilterSize;
    private MuVisFilterDraw durationFilterDraw;
    private TMView durationFilterView;
    private MuVisFilterAction durationAction;
    /*
     * Year Filter Elements
     */
    MuVisYearFilterNode yearFilterRoot;
    TreeMap yearFilterTreeMap;
    MuVisYearFilterSize yearFilterSize;
    MuVisFilterDraw yearFilterDraw;
    TMView yearFilterView;
    MuVisFilterAction yearAction;

    /*
     * Genre Filter Elements
     */
    MuVisGenreFilterNode genreFilterRoot;
    TreeMap genreFilterTreeMap;
    MuVisGenreFilterSize genreFilterSize;
    MuVisFilterDraw genreFilterDraw;
    TMView genreFilterView;
    MuVisFilterAction genreAction;

    /*
     * Mood Filter Elements
     */
    MuVisMoodFilterNode moodFilterRoot;
    TreeMap moodFilterTreeMap;
    MuVisMoodFilterSize moodFilterSize;
    MuVisFilterDraw moodFilterDraw;
    TMView moodFilterView;
    MuVisFilterAction moodAction;

    /*
     * Beat Filter Elements
     */
    MuVisBeatFilterNode beatFilterRoot;
    TreeMap beatFilterTreeMap;
    MuVisBeatFilterSize beatFilterSize;
    MuVisFilterDraw beatFilterDraw;
    TMView beatFilterView;
    MuVisFilterAction beatAction;

    /*
     * TreeMap features
     */
    MuVisComputeTrackSize treemapTrackSize;
    MuVisComputeAlbumSize treemapAlbumsSize;
    MuVisComputeEqualSize treemapEqualSize;
    TMAlgorithmAscOrder treemapAlgorithmAscOrder;
    TMAlgorithmDescOrder treemapAlgorithmDescOrder;

    public void addView(String viewName, JPanel panel) {
        mainViewPanels.put(viewName, panel);
    }

    public void setView(String viewName) {
        if (currentPanel != null) {
            mainViewPanel.remove(currentPanel);
        }
        currentPanelStr = viewName;
        currentPanel = mainViewPanels.get(viewName);
        mainViewPanel.add(currentPanel, viewName);
        mainViewPanel.validate();

        if (viewName.equals(Elements.TREEMAP_VIEW) || viewName.equals(Elements.ARTIST_INSPECTOR_VIEW)) {
            mainViewHolderTabs.setEnabledAt(1, true);
        } else {
            if (mainViewHolderTabs.getSelectedIndex() == 1) {
                mainViewHolderTabs.setSelectedIndex(0);
            }
            mainViewHolderTabs.setEnabledAt(1, false);
        }
    }

    public JPanel getView(String viewName) {
        if (mainViewPanels.containsKey(viewName)) {
            return mainViewPanels.get(viewName);
        } else {
            return null;
        }
    }

    public String getActiveView() {
        return currentPanelStr;
    }

    private void createDurationFilterView() {
        durationFilterRoot = new MuVisDurationFilterNode();
        durationFilterTreeMap = new TreeMap(durationFilterRoot);
        durationFilterSize = new MuVisDurationFilterSize();
        durationFilterDraw = new MuVisDurationFilterDraw();//new MuVisFilterDraw();
        durationFilterView = durationFilterTreeMap.getView(durationFilterSize, durationFilterDraw);
        durationAction = new MuVisFilterAction(durationFilterView);
        durationFilterView.setAction(durationAction);
        durationFilterView.addAlgorithm(new TMAlgorithmAscOrderFilter(), "durationOrdered");
        durationFilterView.setAlgorithm("durationOrdered");

        durationTreemapFilterPanel.add(durationFilterView, "DurationFilterView");
        durationFilterView.setEnabled(true);
    }

    private void createYearFilterView() {
        yearFilterRoot = new MuVisYearFilterNode();
        yearFilterTreeMap = new TreeMap(yearFilterRoot);
        yearFilterSize = new MuVisYearFilterSize();
        yearFilterDraw = new MuVisYearFilterDraw();//new MuVisFilterDraw();
        yearFilterView = yearFilterTreeMap.getView(yearFilterSize, yearFilterDraw);
        yearAction = new MuVisFilterAction(yearFilterView);
        yearFilterView.setAction(yearAction);
        //yearFilterView.setAlgorithm(TMView.SQUARIFIED);
        yearFilterView.addAlgorithm(new TMAlgorithmAscOrderFilter(), "yearOrdered");
        yearFilterView.setAlgorithm("yearOrdered");

        yearTreemapFilterPanel.add(yearFilterView, "YearFilterView");
        yearFilterView.setEnabled(true);
    }

    private void createGenreFilterView() {
        genreFilterRoot = new MuVisGenreFilterNode();
        genreFilterTreeMap = new TreeMap(genreFilterRoot);
        genreFilterSize = new MuVisGenreFilterSize();
        genreFilterDraw = new MuVisGenreFilterDraw();//new MuVisFilterDraw();
        genreFilterView = genreFilterTreeMap.getView(genreFilterSize, genreFilterDraw);
        genreAction = new MuVisFilterAction(genreFilterView);
        genreFilterView.setAction(genreAction);
        //genreFilterView.setAlgorithm(TMView.SQUARIFIED);
        genreFilterView.addAlgorithm(new TMAlgorithmAscOrderFilter(), "genreOrdered");
        genreFilterView.setAlgorithm("genreOrdered");

        genreTreemapFilterPanel.add(genreFilterView, "GenreFilterView");
        genreFilterView.setEnabled(true);
    }

    private void createMoodFilterView() {
        moodFilterRoot = new MuVisMoodFilterNode();
        moodFilterTreeMap = new TreeMap(moodFilterRoot);
        moodFilterSize = new MuVisMoodFilterSize();
        moodFilterDraw = new MuVisMoodFilterDraw();//new MuVisFilterDraw();
        moodFilterView = moodFilterTreeMap.getView(moodFilterSize, moodFilterDraw);
        moodAction = new MuVisFilterAction(moodFilterView);
        moodFilterView.setAction(moodAction);
        moodFilterView.addAlgorithm(new TMAlgorithmAscOrderFilter(), "moodOrdered");
        moodFilterView.setAlgorithm("moodOrdered");

        moodTreemapFilterPanel.add(moodFilterView, "MoodFilterView");
        moodFilterView.setEnabled(true);
    }

    private void createBeatFilterView() {
        beatFilterRoot = new MuVisBeatFilterNode();
        beatFilterTreeMap = new TreeMap(beatFilterRoot);
        beatFilterSize = new MuVisBeatFilterSize();
        beatFilterDraw = new MuVisBeatFilterDraw(); //new MuVisFilterDraw();
        beatFilterView = beatFilterTreeMap.getView(beatFilterSize, beatFilterDraw);
        beatAction = new MuVisFilterAction(beatFilterView);
        beatFilterView.setAction(beatAction);
        beatFilterView.addAlgorithm(new TMAlgorithmAscOrderFilter(), "beatOrdered");
        beatFilterView.setAlgorithm("beatOrdered");

        beatTreemapFilterPanel.add(beatFilterView, "BeatFilterView");
        beatFilterView.setEnabled(true);
    }

    public void initializeFilters() {

        createDurationFilterView();
        createYearFilterView();
        createGenreFilterView();
        createMoodFilterView();
        createBeatFilterView();
        durationFilter = new DurationTableFilter();
        textTableFilter = new TextTableFilter();
        yearTableFilter = new YearTableFilter();
        genreTableFilter = new GenreTableFilter();
        beatTableFilter = new BeatTableFilter();
        moodTableFilter = new MoodTableFilter();

        if (currentPanel instanceof ListViewTableView) {

            tableFilterManager = new TableFilterManager(((ListViewTableView) currentPanel).getSorter());
            Environment.getEnvironmentInstance().setTableFilterManager(tableFilterManager);

            tableFilterManager.addTableFilter(durationFilter);
            tableFilterManager.addTableFilter(textTableFilter);
            tableFilterManager.addTableFilter(yearTableFilter);
            tableFilterManager.addTableFilter(genreTableFilter);
            tableFilterManager.addTableFilter(beatTableFilter);
            tableFilterManager.addTableFilter(moodTableFilter);
            durationAction.registerObserver(tableFilterManager);
            yearAction.registerObserver(tableFilterManager);
            genreAction.registerObserver(tableFilterManager);
            beatAction.registerObserver(tableFilterManager);
            moodAction.registerObserver(tableFilterManager);
        }

        treemapFilterManager = Environment.getEnvironmentInstance().getTreemapFilterManager();

        durationAction.registerObserver(treemapFilterManager);
        yearAction.registerObserver(treemapFilterManager);
        genreAction.registerObserver(treemapFilterManager);
        beatAction.registerObserver(treemapFilterManager);
        moodAction.registerObserver(treemapFilterManager);

        TextFieldListener textListener = new TextFieldListener(searchTextField);
        searchTextField.getDocument().addDocumentListener(textListener);
        textListener.registerObserver(treemapFilterManager);
        textListener.registerObserver(tableFilterManager);

        treemapFilterManager.registerObserver(this);
        durationFilterLabel.addMouseListener(new FilterInspector(durationTreemapFilterPanel, durationFilterView));
        yearFilterLabel.addMouseListener(new FilterInspector(yearTreemapFilterPanel, yearFilterView));
        genreFilterLabel.addMouseListener(new FilterInspector(genreTreemapFilterPanel, genreFilterView));
        beatFilterLabel.addMouseListener(new FilterInspector(beatTreemapFilterPanel, beatFilterView));
        moodFilterLabel.addMouseListener(new FilterInspector(moodTreemapFilterPanel, moodFilterView));
        updateDisplayInfo();
    }

    public MainViewHolder(JFrame parent) {
        key = new DockKey("Main");
        mainViewPanels = new Hashtable<String, JPanel>();

        key.setTooltip("Main view of your library");
        key.setCloseEnabled(false);
        key.setAutoHideEnabled(false);
        key.setMaximizeEnabled(false);

        treemapTrackSize = new MuVisComputeTrackSize();
        treemapAlbumsSize = new MuVisComputeAlbumSize();
        treemapEqualSize = new MuVisComputeEqualSize();
        /*treemapAlgorithmAscOrder = new TMAlgorithmAscOrder();
        treemapAlgorithmDescOrder = new TMAlgorithmDescOrder();*/

        treemapSquareSizeComboBox.addActionListener(this);
        treemapStructureVisualization.addActionListener(this);

        scheduler = Executors.newSingleThreadScheduledExecutor();

        parent.addComponentListener(this);

        // Get a handle, starting now, with a 10 second delay
        class UpdateInfoDisplay implements Runnable, Observer {

            boolean update = false;

            @Override
            public void run() {
                if (update) {
                    update = false;
                    updateDisplayInfo();
                }
            }

            @Override
            public void update(Observable obs, Object arg) {
                if (obs instanceof MusicLibraryDatabaseManager) {
                    update = true;
                }
            }
        }

        UpdateInfoDisplay updateTable = new UpdateInfoDisplay();
        Environment.getEnvironmentInstance().getDatabaseManager().registerObserver(updateTable);

        scheduler.scheduleAtFixedRate(updateTable, 10, 45, TimeUnit.SECONDS);

        resetFiltersButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        treemapFilterManager.reset();
                        searchTextField.setText("");
                        tableFilterManager.resetFilters();
                        tableFilterManager.filter();
                    }
                });
            }
        });
    }

    @Override
    public DockKey getDockKey() {
        return key;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == treemapSquareSizeComboBox) {
            JComboBox cb = (JComboBox) e.getSource();
            String typeSize = (String) cb.getSelectedItem();

            TreemapView tView = (TreemapView) mainViewPanels.get("TreeMapView");
            if (typeSize.contains("tracks")) { //size depends on the number of tracks
                tView.getCurrentView().changeTMComputeSize(treemapTrackSize);
            } else if (typeSize.contains("albums")) { //size depends on the number of artist albums
                tView.getCurrentView().changeTMComputeSize(treemapAlbumsSize);
            } else { //the size is equal to all artists
                tView.getCurrentView().changeTMComputeSize(treemapEqualSize);
            }
        } else if (e.getSource() == treemapStructureVisualization) {

            JComboBox cb = (JComboBox) e.getSource();
            String structureType = (String) cb.getSelectedItem();
            TreemapView tView = (TreemapView) mainViewPanels.get("TreeMapView");
            if (structureType.contains("Similarity")) {
                tView.getCurrentView().setAlgorithm("SimilarityAlgorithm");
                chooseSeedArtistButton.setEnabled(true);
            } else if (structureType.contains("Asc")) {
                tView.getCurrentView().setAlgorithm("AscOrderAlgorithm");
                chooseSeedArtistButton.setEnabled(false);
            } else if (structureType.contains("Desc")) {
                tView.getCurrentView().setAlgorithm("DescOrderAlgorithm");
                chooseSeedArtistButton.setEnabled(false);
            }
        }
    }

    @Override
    public void update(Observable obs, Object arg) {
        if (obs instanceof TreemapFilterManager) {
            updateDisplayInfo();
        }
    }

    protected void updateDisplayInfo() {
        String name = "Main - Browsing " + treemapFilterManager.getCountFilteredTracks() + " tracks";
        name += " in " + treemapFilterManager.getCountFilteredAlbums() + " albums";
        key.setName(name);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (e.getComponent() instanceof JFrame) {
            Dimension size = e.getComponent().getSize();
            double width = featuresFilterPanel.getSize().getWidth();
            double diffHeight = 770 - size.getHeight();
            double height = 667 - diffHeight;
            Dimension newSize = new Dimension((int) width, (int) height);

            mainViewHolderTabs.setSize(newSize);
            featuresFilterPanel.setSize(newSize);
            filtersPanel.setSize(newSize);
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //TO NOTHING
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //TO NOTHING
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //TO NOTHING
    }
}

class FilterInspector implements MouseListener {

    JFrame frame = null;
    JPanel panel;
    TMView view;

    FilterInspector(JPanel panel, TMView view) {
        this.panel = panel;
        this.view = view;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (frame == null) {
                final JLabel label = (JLabel)e.getSource();
                frame = new JFrame(label.getText());
                frame.setSize(new Dimension(320, 240));
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        panel.add(view, label.getText());
                        panel.validate();
                    }
                });
            }
            frame.add(view);
            frame.setSize(new Dimension(320, 240));
            frame.setVisible(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
