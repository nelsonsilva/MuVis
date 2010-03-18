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
package muvis.analyser.processor;

import comirva.audio.extraction.AudioFeatureExtractionThread;
import comirva.audio.extraction.FluctuationPatternExtractionThread;
import comirva.data.DataMatrix;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import muvis.Elements;
import muvis.NBTreeManager;
import muvis.Messages;
import muvis.analyser.loader.Loader;
import muvis.audio.AudioMetadata;
import muvis.audio.AudioMetadataExtractor;
import muvis.audio.MP3AudioMetadataExtractor;
import muvis.audio.MP3AudioSnippetExtractor;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.exceptions.CannotRetrieveMP3TagException;
import muvis.util.MP3AudioFile;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.util.Util;
import muvis.view.loader.LoadingLibraryViewUI;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentProcessor implements Observer, Observable {
    @Autowired
    PropertiesConfiguration configuration;
    private ArrayList<Observer> observers;
    private ExecutorService threadPool;
    private int nextFileToProcess = -1;
    @Autowired private MusicLibraryDatabaseManager dbManager;
    @Autowired private NBTreeManager nbtreeManager;
    private NBTree tracksNBTree, albumsTree, artistsTree;
    private File[] filesToProcess;
    private LoadingLibraryViewUI loadingLibraryUI;
    private boolean libraryLoadingFinished;
    private State processorState;

    
    enum State{
        RUN, PAUSE, STOP
    }

    public ContentProcessor() {
        observers = new ArrayList<Observer>();
        threadPool = Executors.newFixedThreadPool(5);       
        libraryLoadingFinished = false;
    }

    public void init(){
       tracksNBTree = nbtreeManager.getNBTree(Elements.TRACKS_NBTREE);
        albumsTree = nbtreeManager.getNBTree(Elements.ALBUMS_NBTREE);
        artistsTree = nbtreeManager.getNBTree(Elements.ARTISTS_NBTREE);
    }
    /**
     * Thread that extracts the main track tags
     */
    class PreliminarContentProcessorThread extends Thread {

        private AudioMetadataExtractor metadataExtractor;
        private int numFilesProcessed = 0;
        final private Object parent;
        private int fileToProc = -1;
        Random rnd = new Random();
        Random rnd2 = new Random();

        public PreliminarContentProcessorThread(Object parent) {
            metadataExtractor = new MP3AudioMetadataExtractor();
            this.parent = parent;
        }

        @Override
        public void run() {

            File file;

            while (true) {
                if (processorState.equals(ContentProcessor.State.PAUSE)){
                    try {
                        sleep(2000);
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else if (processorState.equals(ContentProcessor.State.STOP)){
                    //ends the processing abruptly
                    return;
                }
                synchronized (parent) {
                    if (nextFileToProcess < (filesToProcess.length - 1)) {
                        nextFileToProcess++;
                        fileToProc = nextFileToProcess;
                        file = filesToProcess[fileToProc];

                        loadingLibraryUI.loadingTracksLabel.setText("Loading track " + fileToProc + " of " + filesToProcess.length);
                        loadingLibraryUI.trackPathNameLabel.setText(file.getAbsolutePath());
                        loadingLibraryUI.loadingLibraryProgressBar.setValue(fileToProc);
                    } else {
                        return;
                    }

                    if (numFilesProcessed == 100) {
                        try {
                            sleep(2000);
                        } catch (InterruptedException ex) {
                            System.out.print("Couldn't sleep this thread in PreliminarContentProcessor");
                        }
                        numFilesProcessed = 0;
                    }
                }


                try {

                    AudioMetadata metadata =
                            metadataExtractor.getAudioMetadata(file.getAbsolutePath());
                    String artistName = metadata.getAuthor();
                    String albumName = metadata.getAlbum();
                    String filename = file.getAbsolutePath();

                    //add to the database to make the main informations available in the interface
                    dbManager.addNewSong(filename, artistName, albumName, metadata);

                    dbManager.setTrackMood(filename, Util.mood[rnd.nextInt(4)]);
                    dbManager.setTrackBeat(filename, Util.beat[rnd2.nextInt(4)]);

                    numFilesProcessed++;

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    continue;
                } catch (CannotRetrieveMP3TagException ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * Class that extracts the main features and saves them in the database and
     * in the nbtree structure
     */
    class ContentProcessorTrackThread extends Thread {

        @Autowired private MP3AudioSnippetExtractor mp3AudioSnippetExtractor;

        final private Object lock;
        private int id, numTries = 0, numFilesProcessed = 0;
        private int fileToProc = -1;

        public ContentProcessorTrackThread(Object lock, int threadId) {
            this.lock = lock;
            id = threadId;
        }

        @Override
        public void run() {

            File file = null;

            System.out.println("Starting thread with id: " + id);
            while (true) {
                if (processorState.equals(ContentProcessor.State.PAUSE)){
                    try {
                        sleep(2000);
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else if (processorState.equals(ContentProcessor.State.STOP)){
                    //ends the processing abruptly
                    return;
                }
                if (numTries == 0) {
                    synchronized (lock) {
                        if (nextFileToProcess < (filesToProcess.length - 1)) {
                            nextFileToProcess++;
                            fileToProc = nextFileToProcess;
                            file = filesToProcess[fileToProc];

                            loadingLibraryUI.loadingTracksLabel.setText("Loading track " + fileToProc + " of " + filesToProcess.length);
                            loadingLibraryUI.trackPathNameLabel.setText(file.getAbsolutePath());
                            loadingLibraryUI.loadingLibraryProgressBar.setValue(fileToProc);
                        } else {
                            System.out.println("Finishing thread with id: " + id);
                            return;
                        }
                    }
                }
                //process now the file
                try {

                    if (numFilesProcessed == 100) {
                        sleep(5000);
                        numFilesProcessed = 0;
                    }

                    File[] filesInQueue = new File[1];
                    filesInQueue[0] = file;

                    byte[] snippet;
                    AudioFeatureExtractionThread thread = null;

                    if (numTries == 0) {
                        try {
                            snippet = mp3AudioSnippetExtractor.extractAudioSnippet(file.getAbsolutePath());
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(snippet);

                            thread = new FluctuationPatternExtractionThread(inputStream);
                            thread.run();
                        } catch (Exception e) {
                            System.out.println("Couldn't extract with snippet, trying with file: " + file.getAbsolutePath());
                            //numTries++;
                            numTries = 0;
                            continue;
                        }
                    }

                    //obtaining the processed datamatrix
                    DataMatrix featuresMatrix = thread.getMatrix();

                    //updating the track with their descriptor
                    double[] descriptor = featuresMatrix.toDoubleArray()[0];

                    //normalizing the descriptor
                    descriptor = Util.normalize(descriptor);

                    double key = -1;
                    key = dbManager.getTrackKey(file.getAbsolutePath());
                    if (key != -1) {
                        tracksNBTree.removePoint(key);
                    }
                    //insert track in the respective NBTree
                    key = tracksNBTree.insertPoint(new NBPoint(descriptor));
                    //update the database with the new key for the track
                    dbManager.setTrackKey(file.getAbsolutePath(), key);

                    numFilesProcessed++;
                    System.out.println("Track processed: " + file.getAbsolutePath());

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (NBTreeException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        }
    }

    class ContentProcessorAlbumsArtistsThread extends Thread {

        @Override
        public void run() {

            //updating the artist and albums descriptors
            ArrayList<String> artistNames = dbManager.getAllArtistNames();
            ArrayList<double[]> albumsDescriptors = new ArrayList<double[]>();
            int i = 0, total = artistNames.size();

            loadingLibraryUI.loadingLibraryProgressBar.setMinimum(0);
            loadingLibraryUI.loadingLibraryProgressBar.setMaximum(total);
            loadingLibraryUI.loadingTracksLabel.setText(Messages.EMPTY_STRING);
            loadingLibraryUI.trackPathNameLabel.setText(Messages.EMPTY_STRING);

            for (String artist : artistNames) {

                while (processorState.equals(ContentProcessor.State.PAUSE)){
                    try {
                        sleep(2000);
                        if (processorState.equals(ContentProcessor.State.STOP)){
                            //ends the processing abruptly
                            return;
                        }
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (processorState.equals(ContentProcessor.State.STOP)){
                    //ends the processing abruptly
                    return;
                }
                i++;
                loadingLibraryUI.loadingTracksLabel.setText("Processing artist " + i + " of " + total);
                loadingLibraryUI.trackPathNameLabel.setText("");

                ArrayList<String> artistAlbums = dbManager.getArtistAlbums(artist);
                for (String album : artistAlbums) {

                    ArrayList<String> albumTracks = dbManager.getAlbumTracks(artist, album);

                    int numAlbumTracks = 1;
                    double[] albumDescriptor = new double[1200];
                    for (String track : albumTracks) {

                        double key = dbManager.getTrackKey(track);
                        try {

                            NBPoint point = tracksNBTree.lookupPoint(key);
                            if (point != null) {

                                double[] trackDescriptor = tracksNBTree.lookupPoint(key).toArray();
                                albumDescriptor = Util.sum(albumDescriptor, trackDescriptor);
                                numAlbumTracks++;
                            } else {
                                continue;
                            }

                        } catch (NBTreeException ex) {
                            ex.printStackTrace();
                        }
                    }

                    //calculating the new descriptor
                    for (int j = 0; j < 1200; j++) {
                        albumDescriptor[j] /= numAlbumTracks;
                    }

                    try {

                        //normalize audio descriptor
                        albumDescriptor = Util.normalize(albumDescriptor);

                        double albumKey = dbManager.getAlbumKey(artist, album);

                        if (albumKey != -1) {
                            //must remove the key to update it again
                            albumsTree.removePoint(albumKey);
                        }

                        //updating the nbTree with the new point
                        albumKey = albumsTree.insertPoint(new NBPoint(albumDescriptor));
                        //updates the database with the new album key
                        dbManager.setAlbumKey(artist, album, albumKey);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (NBTreeException ex) {
                        ex.printStackTrace();
                    }
                    albumsDescriptors.add(albumDescriptor);
                }

                double[] artistDescriptor = new double[1200];
                for (double[] albumDescriptor : albumsDescriptors) {
                    artistDescriptor = Util.sum(artistDescriptor, albumDescriptor);
                }
                //calculating the new descriptor
                for (int j = 0; j < 1200; j++) {
                    artistDescriptor[j] /= albumsDescriptors.size();
                }
                try {
                    //normalize artist descriptor
                    artistDescriptor = Util.normalize(artistDescriptor);

                    double artistKey = dbManager.getArtistKey(artist);
                    if (artistKey != -1) {
                        //remove the artist key so we can update it again
                        artistsTree.removePoint(artistKey);
                    }

                    //insert the new artist key
                    artistKey = artistsTree.insertPoint(new NBPoint(artistDescriptor));
                    //updates the key in the artist
                    dbManager.setArtistKey(artist, artistKey);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (NBTreeException ex) {
                    ex.printStackTrace();
                }
                albumsDescriptors.clear();
            }
        }
    }

    class ContentProcessorGUIThread extends Thread {

        ArrayList<Thread> threads;

        private void buildGUI() {

            final JFrame frame = new JFrame(Messages.LOAD_LIBRARY_FRAME_LABEL);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            loadingLibraryUI = new LoadingLibraryViewUI();
            loadingLibraryUI.skipLoadingLibraryButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //library processing must be stopped before disposing the frame
                    if (!libraryLoadingFinished) {
                        int skip = Util.displayConfirmationMessage(frame, Messages.LOAD_LIBRARY_CONFIRMATION_SCREEN, Messages.CONFIRMATION_LABEL);
                        if (skip == 0) { //must stop all the library processing
                            processorState = ContentProcessor.State.STOP;
                            frame.dispose();
                        }
                        //else do nothing
                    } else {
                        frame.dispose();
                    }
                }
            });

            loadingLibraryUI.pauseLibraryLoadingButton.addActionListener(new ActionListener() {

                boolean paused = false;

                @Override
                @SuppressWarnings("static-access")
                public void actionPerformed(ActionEvent e) {

                    JButton button = (JButton) e.getSource();
                    if (!paused) {
                        button.setText(Messages.RESUME_LABEL);
                        paused = true;
                        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/music/media-playback-start.png")));
                        processorState = ContentProcessor.State.PAUSE;
                    } else {
                        button.setText(Messages.PAUSE_LABEL);
                        paused = false;
                        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/music/media-playback-pause.png")));
                        processorState = ContentProcessor.State.RUN;
                    }
                }
            });

            loadingLibraryUI.loadingLibraryProgressBar.setMinimum(0);
            loadingLibraryUI.loadingLibraryProgressBar.setMaximum(filesToProcess.length);
            loadingLibraryUI.processingStageLabel.setText(Messages.LOAD_LIBRARY_INITIALIZE_STAGES);
            loadingLibraryUI.loadingTracksLabel.setText(Messages.EMPTY_STRING);
            loadingLibraryUI.trackPathNameLabel.setText(Messages.EMPTY_STRING);

            frame.add(loadingLibraryUI);
            frame.pack();
            frame.setVisible(true);
        }

        @Override
        public void run() {

            buildGUI();
            Executors.newFixedThreadPool(1).execute(new Thread() {

                @Override
                public void run() {

                    processorState = ContentProcessor.State.RUN;
                    int threadNum = configuration.getInt("loader.threads_tags_extraction");

                    threads = new ArrayList<Thread>(threadNum);

                    loadingLibraryUI.processingStageLabel.setText(Messages.LOAD_LIBRARY_TAGS_EXTRACTION);

                    for (int i = 0; i < threadNum; i++) {

                        Thread th = new PreliminarContentProcessorThread(this);
                        th.setPriority(Thread.MIN_PRIORITY);
                        threads.add(th);
                        threadPool.execute(th);
                        try {
                            th.join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    //shutdown the threadPool and wait for it to finish
                    threadPool.shutdown();

                    while (!threadPool.isTerminated()) {
                        try {
                            sleep(3000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }

                    //dispose this threads
                    threads.clear();
                    threadNum = configuration.getInt("loader.threads_content_extraction");
                    threadPool = Executors.newFixedThreadPool(threadNum);

                    loadingLibraryUI.processingStageLabel.setText(Messages.LOAD_LIBRARY_CONTENT_TRACKS_EXTRACTION);
                    loadingLibraryUI.loadingLibraryProgressBar.setValue(0);
                    loadingLibraryUI.loadingTracksLabel.setText(Messages.EMPTY_STRING);
                    loadingLibraryUI.trackPathNameLabel.setText(Messages.EMPTY_STRING);

                    nextFileToProcess = -1;

                    for (int i = 0; i < threadNum; i++) {

                        Thread t = new ContentProcessorTrackThread(this, i);
                        t.setPriority(Thread.MIN_PRIORITY);
                        threads.add(t);
                        threadPool.execute(t);
                        try {
                            t.join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    threadPool.shutdown();

                    while (!threadPool.isTerminated()) {
                        try {
                            sleep(3000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }

                    //dispose some more threads
                    threads.clear();

                    try {
                        tracksNBTree.save();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    loadingLibraryUI.processingStageLabel.setText(Messages.LOAD_LIBRARY_FULL_CONTENT_EXTRACTION);
                    loadingLibraryUI.loadingLibraryProgressBar.setValue(0);
                    loadingLibraryUI.loadingTracksLabel.setText(Messages.EMPTY_STRING);
                    loadingLibraryUI.trackPathNameLabel.setText(Messages.EMPTY_STRING);

                    threadPool = Executors.newFixedThreadPool(1);

                    Thread th = new ContentProcessorAlbumsArtistsThread();
                    th.setPriority(Thread.MIN_PRIORITY);
                    threadPool.execute(th);
                    threadPool.shutdown();
                    while (!threadPool.isTerminated()) {
                        try {
                            sleep(3000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }

                    try {
                        albumsTree.save();
                        artistsTree.save();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    loadingLibraryUI.processingStageLabel.setText(Messages.LOAD_LIBRARY_FINISH_LABEL);
                    loadingLibraryUI.loadingTracksLabel.setText(Messages.EMPTY_STRING);
                    loadingLibraryUI.trackPathNameLabel.setText(Messages.EMPTY_STRING);
                    loadingLibraryUI.pauseLibraryLoadingButton.setEnabled(false);
                    loadingLibraryUI.pauseLibraryLoadingButton.setVisible(false);
                    loadingLibraryUI.skipLoadingLibraryButton.setText(Messages.CLOSE_LABEL);
                    loadingLibraryUI.skipLoadingLibraryButton.setIcon(null);
                    libraryLoadingFinished = true;
                }
            });
        }
    }

    /**
     * ###############################
     * Observable and Observer methods
     * ###############################
     */
    @Override
    public void update(Observable obs, Object arg) {

        if (obs instanceof Loader) {
            ArrayList<MP3AudioFile> files = (ArrayList<MP3AudioFile>) arg;
            filesToProcess = new File[files.size()];

            int i = 0;
            for (MP3AudioFile file : files) {
                filesToProcess[i] = file.getAudioFile();
                i++;
            }
            Executors.newFixedThreadPool(1).execute(new ContentProcessorGUIThread());
        }
    }

    @Override
    public void registerObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public void unregisterObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void updateObservers() {
        for (Observer obs : observers) {
            obs.update(this, null);
        }
    }
}
