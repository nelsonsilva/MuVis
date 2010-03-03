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
import javax.swing.JFrame;
import muvis.NBTreeManager;
import muvis.Environment;
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

public class ContentProcessor implements Observer, Observable {

    private ArrayList<Observer> observers;
    private ExecutorService threadPool;
    private int nextFileToProcess = -1;
    private MusicLibraryDatabaseManager dbManager;
    private NBTree tracksNBTree,  albumsTree,  artistsTree;
    private File[] filesToProcess;
    private LoadingLibraryViewUI loadingLibraryUI;

    public ContentProcessor() {
        observers = new ArrayList<Observer>();
        threadPool = Executors.newFixedThreadPool(5);
        dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        NBTreeManager nbtreeManager = Environment.getWorkspaceInstance().getNbtreesManager();
        tracksNBTree = nbtreeManager.getNBTree("tracksNBTree");
        albumsTree = nbtreeManager.getNBTree("albumsNBTree");
        artistsTree = nbtreeManager.getNBTree("artistsNBTree");
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
                synchronized (parent) {
                    if (nextFileToProcess < (filesToProcess.length - 1)) {
                        nextFileToProcess++;
                        fileToProc = nextFileToProcess;
                        file = filesToProcess[fileToProc];

                        loadingLibraryUI.loadingTracksLabel.setText("Loading track "+fileToProc+" of "+filesToProcess.length);
                        loadingLibraryUI.trackPathNameLabel.setText(file.getAbsolutePath());
                        loadingLibraryUI.loadingLibraryProgressBar.setValue(fileToProc);
                    } else {
                        return;
                    }

                    if (numFilesProcessed == 100){
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

        final private Object lock;
        private int id,  numTries = 0, numFilesProcessed = 0;
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
                if (numTries == 0) {
                    synchronized (lock) {
                        if (nextFileToProcess < (filesToProcess.length - 1)) {
                            nextFileToProcess++;
                            fileToProc = nextFileToProcess;
                            file = filesToProcess[fileToProc];

                            loadingLibraryUI.loadingTracksLabel.setText("Loading track "+fileToProc+" of "+filesToProcess.length);
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

                    if (numFilesProcessed == 100){
                        sleep(5000);
                        numFilesProcessed = 0;
                    }

                    File[] filesInQueue = new File[1];
                    filesInQueue[0] = file;

                    byte[] snippet;
                    AudioFeatureExtractionThread thread = null;

                    if (numTries == 0) {
                        try {
                            snippet = MP3AudioSnippetExtractor.extractAudioSnippet(file.getAbsolutePath());
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
                    descriptor = normalize(descriptor);

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
            loadingLibraryUI.loadingTracksLabel.setText("");
            loadingLibraryUI.trackPathNameLabel.setText("");
            
            for (String artist : artistNames) {

                i++;
                loadingLibraryUI.loadingTracksLabel.setText("Processing artist " + i + " of "+total);
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
                                albumDescriptor = sum(albumDescriptor, trackDescriptor);
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
                        albumDescriptor = normalize(albumDescriptor);

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
                    artistDescriptor = sum(artistDescriptor, albumDescriptor);
                }
                //calculating the new descriptor
                for (int j = 0; j < 1200; j++) {
                    artistDescriptor[j] /= albumsDescriptors.size();
                }
                try {
                    //normalize artist descriptor
                    artistDescriptor = normalize(artistDescriptor);

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

            Executors.newFixedThreadPool(1).execute(new Thread() {

                @Override
                public void run() {

                    final JFrame frame = new JFrame("Loading Library");
                    loadingLibraryUI = new LoadingLibraryViewUI();
                    loadingLibraryUI.skipLoadingLibraryButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.dispose();
                        }
                    });

                    loadingLibraryUI.loadingLibraryProgressBar.setMinimum(0);
                    loadingLibraryUI.loadingLibraryProgressBar.setMaximum(filesToProcess.length);
                    loadingLibraryUI.processingStageLabel.setText("Initilizing stages...");
                    loadingLibraryUI.loadingTracksLabel.setText("");
                    loadingLibraryUI.trackPathNameLabel.setText("");
                    
                    frame.add(loadingLibraryUI);
                    frame.pack();
                    frame.setVisible(true);

                    int threadNum = 2;
                    System.out.println("Library tags extraction started!");

                    ArrayList<PreliminarContentProcessorThread> threadsP =
                            new ArrayList<PreliminarContentProcessorThread>(threadNum);

                    for (int i = 0; i < threadNum; i++) {

                        PreliminarContentProcessorThread th = new PreliminarContentProcessorThread(this);
                        th.setPriority(Thread.MIN_PRIORITY);
                        threadsP.add(th);
                        threadPool.execute(th);
                    }

                    loadingLibraryUI.processingStageLabel.setText("Stage 1 of 3 - Tags extraction");
                    ActionListener listener1 = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                        }
                    };
                    loadingLibraryUI.pauseLibraryLoadingButton.addActionListener(listener1);

                    for (PreliminarContentProcessorThread th : threadsP) {
                        try {
                            th.join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    threadPool.shutdown();

                    while (!threadPool.isTerminated()) {
                        try {
                            sleep(30000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }

                    //dispose this threads
                    threadsP.clear();
                    threadPool = Executors.newFixedThreadPool(threadNum);

                    System.out.println("Library tags extraction finished!");
                    System.gc();

                    loadingLibraryUI.processingStageLabel.setText("Stage 2 of 3 -  Content processing");
                    loadingLibraryUI.loadingLibraryProgressBar.setValue(0);
                    loadingLibraryUI.loadingTracksLabel.setText("");
                    loadingLibraryUI.trackPathNameLabel.setText("");

                    loadingLibraryUI.pauseLibraryLoadingButton.removeActionListener(listener1);

                    ActionListener listener2 = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                        }
                    };
                    loadingLibraryUI.pauseLibraryLoadingButton.addActionListener(listener2);

                    System.out.println("Library processing started!");
                    nextFileToProcess = -1;

                    ArrayList<ContentProcessorTrackThread> threads =
                            new ArrayList<ContentProcessorTrackThread>(threadNum);

                    for (int i = 0; i < threadNum; i++) {
                        ContentProcessorTrackThread t = new ContentProcessorTrackThread(this, i);
                        t.setPriority(Thread.MIN_PRIORITY);
                        threads.add(t);
                        threadPool.execute(t);
                    }

                    for (ContentProcessorTrackThread th : threads) {
                        try {
                            th.join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    threadPool.shutdown();

                    while (!threadPool.isTerminated()) {
                        try {
                            sleep(30000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }
                    try {
                        tracksNBTree.save();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    loadingLibraryUI.processingStageLabel.setText("Stage 3 of 3 - Finishing library processing");
                    loadingLibraryUI.loadingLibraryProgressBar.setValue(0);
                    loadingLibraryUI.loadingTracksLabel.setText("");
                    loadingLibraryUI.trackPathNameLabel.setText("");

                    loadingLibraryUI.pauseLibraryLoadingButton.removeActionListener(listener2);
                    ActionListener listener3 = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            
                        }
                    };
                    loadingLibraryUI.pauseLibraryLoadingButton.addActionListener(listener3);

                    //dispose some more threads
                    threads.clear();
                    ContentProcessorAlbumsArtistsThread th = new ContentProcessorAlbumsArtistsThread();
                    th.setPriority(Thread.MIN_PRIORITY);
                    th.run();
                    try {
                        albumsTree.save();
                        artistsTree.save();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Processing library finished!");

                    loadingLibraryUI.processingStageLabel.setText("Library Processing Finished!Please restart MuVis!");
                    loadingLibraryUI.loadingTracksLabel.setText("");
                    loadingLibraryUI.trackPathNameLabel.setText("");
                    loadingLibraryUI.pauseLibraryLoadingButton.setVisible(false);
                    loadingLibraryUI.skipLoadingLibraryButton.setText("Close");
                }
            });
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


    private double[] sum(double[] vec1, double[] vec2) {

        if (vec1.length != vec2.length) {
            return null;
        } else {

            double[] result = new double[vec1.length];
            for (int i = 0; i < vec1.length; i++) {
                result[i] = vec1[i] + vec2[i];
            }
            return result;
        }
    }

    private double[] normalize(double[] descriptor) {

        int max = 200;
        int min = 0;
        int minNorm = 0;
        int maxNorm = 1;

        double[] normalizedDescriptor = new double[descriptor.length];

        for (int i = 0; i < descriptor.length; i++) {

            double parc1 = descriptor[i] - min;
            double parc2 = max - min;
            double num = minNorm + (parc1 / parc2) * (maxNorm - minNorm);

            //update the new position
            normalizedDescriptor[i] = num;
        }

        return normalizedDescriptor;
    }
}
