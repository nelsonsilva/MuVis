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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import muvis.NBTreeManager;
import muvis.Environment;
import muvis.analyser.loader.Loader;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.MP3AudioFile;
import muvis.util.Observable;
import muvis.util.Observer;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;

public class ContentUnprocessor implements Observer, Observable {

    private ArrayList<Observer> observers;
    private ExecutorService threadPool;
    private MusicLibraryDatabaseManager dbManager;
    private NBTree tracksNBTree,  albumsTree,  artistsTree;
    protected File[] filesToProcess;

    public ContentUnprocessor() {
        observers = new ArrayList<Observer>();
        threadPool = Executors.newFixedThreadPool(1);
        dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        NBTreeManager nbtreeManager = Environment.getWorkspaceInstance().getNbtreesManager();
        albumsTree = nbtreeManager.getNBTree("albumsNBTree");
        artistsTree = nbtreeManager.getNBTree("artistsNBTree");
    }

    class TrackLibraryRemoverThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < filesToProcess.length; i++) {
                try {
                    File nextFile = filesToProcess[i];

                    double key = dbManager.getTrackKey(nextFile.getCanonicalPath());
                    tracksNBTree.removePoint(key);

                    dbManager.removeTrack(nextFile.getAbsolutePath());
                } catch (IOException ex) {
                    System.out.println("Couldn't delete this file!");
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    System.out.println("Couldn't delete this file!");
                    ex.printStackTrace();
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

            for (String artist : artistNames) {

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

    /**
     * These method is invoked when some files must be removed from the library
     * @param obs
     * @param arg
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

            Executors.newFixedThreadPool(1).execute(new Thread() {

                @Override
                public void run() {

                    System.out.println("Removing the specified tracks:");

                    TrackLibraryRemoverThread removeThread = new TrackLibraryRemoverThread();
                    removeThread.setPriority(Thread.NORM_PRIORITY);

                    threadPool.execute(removeThread);

                    try {
                        removeThread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
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

                    System.out.println("Tracks Removed!");

                    System.out.println("Re-processing library started!");

                    if (filesToProcess.length > 0){
                        new ContentProcessorAlbumsArtistsThread().run();
                        try {
                            albumsTree.save();
                            artistsTree.save();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("Re-processing library finished!");
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
