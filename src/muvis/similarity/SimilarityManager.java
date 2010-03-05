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
package muvis.similarity;

import java.io.IOException;
import java.util.ArrayList;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import muvis.Elements;
import muvis.Environment;
import muvis.NBTreeManager;
import muvis.database.MusicLibraryDatabaseManager;
import nbtree.NBPoint;
import nbtree.NBTree;
import nbtree.exceptions.NBTreeException;

/**
 * Class that handles the similarity between tracks, albums and artists
 * @author Ricardo
 */
public class SimilarityManager {

    public enum SimilarityMode {

        SIMILAR, NON_SIMILAR
    }

    public static ArrayList<Integer> getSimilarTracks(int[] trackIds, int numberSimilarTracks, SimilarityMode mode) {

        ArrayList<Integer> tracks = new ArrayList();
        NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();
        MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();

        for (int i = 0; i < trackIds.length; i++) {
            try {

                NBTree trackNBTree = nbtreeManager.getNBTree(Elements.TRACKS_NBTREE);
                double trackKey = dbManager.getTrackKey(trackIds[i]);
                if (trackKey < 0) {
                    //Util.displayErrorMessage(Environment.getEnvironmentInstance().getRootFrame(), "Similarity Filter", "Can't get non-similar tracks!Try later!");
                    break;
                }
                NBPoint trackPoint = trackNBTree.lookupPoint(trackKey);
                BTree resultTree = trackNBTree.knnQuery(trackPoint, dbManager.getCountTracks());
                TupleBrowser browser = resultTree.browse();
                Tuple tuple = new Tuple();

                if (mode.equals(SimilarityMode.NON_SIMILAR)) while (browser.getNext(tuple));

                for (int j = 0; ((mode.equals(SimilarityMode.NON_SIMILAR))? 
                    browser.getPrevious(tuple) : browser.getNext(tuple))  && j < numberSimilarTracks;
                    j++) {
                    if (tuple.getValue() instanceof NBPoint) {
                        NBPoint point = (NBPoint) tuple.getValue();
                        int track = dbManager.getTrackId(point.norm());
                        tracks.add(track);
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NBTreeException ex) {
                ex.printStackTrace();
            }
        }
        return tracks;
    }

    public static ArrayList<Integer> getSimilarAlbums(int[] trackIds, int numberSimilarAlbums, SimilarityMode mode) {

        ArrayList<Integer> tracks = new ArrayList();
        NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();
        MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();

        NBTree albumsNBTree = nbtreeManager.getNBTree(Elements.ALBUMS_NBTREE);
        for (int i = 0; i < trackIds.length; i++) {
            try {
                double albumKey = dbManager.getAlbumTrackKey(trackIds[i]);
                if (albumKey < 0) {
                    //Util.displayErrorMessage(parent, "Similarity Filter", "Can't get non-similar albums!Try later!");
                    break;
                }
                NBPoint albumPoint = albumsNBTree.lookupPoint(albumKey);
                BTree resultTree = albumsNBTree.knnQuery(albumPoint, dbManager.getCountAlbums());
                TupleBrowser browser = resultTree.browse();
                Tuple tuple = new Tuple();

                if (mode.equals(SimilarityMode.NON_SIMILAR)) while (browser.getNext(tuple));
                
                for (int j = 0; ((mode.equals(SimilarityMode.NON_SIMILAR))? 
                    browser.getPrevious(tuple) : browser.getNext(tuple)) && j < numberSimilarAlbums;
                    j++) {
                    if (tuple.getValue() instanceof NBPoint) {
                        NBPoint point = (NBPoint) tuple.getValue();
                        int albumId = dbManager.getAlbumId(point.norm());
                        ArrayList<Integer> albumTracks = dbManager.getAlbumTracksIds(albumId);
                        tracks.addAll(albumTracks);
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NBTreeException ex) {
                ex.printStackTrace();
            }
        }
        return tracks;
    }

    public static ArrayList<Integer> getSimilarArtists(int[] trackIds, int numberSimilarArtists, SimilarityMode mode) {

        ArrayList<Integer> tracks = new ArrayList();
        NBTreeManager nbtreeManager = Environment.getEnvironmentInstance().getNbtreesManager();
        MusicLibraryDatabaseManager dbManager = Environment.getEnvironmentInstance().getDatabaseManager();

        NBTree artistNBTree = nbtreeManager.getNBTree(Elements.ARTISTS_NBTREE);
        for (int i = 0; i < trackIds.length; i++) {
            try {
                double artistKey = dbManager.getArtistTrackKey(trackIds[i]);
                if (artistKey < 0) {
                    //Util.displayErrorMessage(Environment.getEnvironmentInstance().getRootFrame(), "Similarity Filter", "Can't get non-similar artists!Try later!");
                    break;
                }
                NBPoint artistPoint = artistNBTree.lookupPoint(artistKey);
                BTree resultTree = artistNBTree.knnQuery(artistPoint, dbManager.getCountArtists());
                TupleBrowser browser = resultTree.browse();
                Tuple tuple = new Tuple();

                if (mode.equals(SimilarityMode.NON_SIMILAR)) while (browser.getNext(tuple));
                
                for (int j = 0; ((mode.equals(SimilarityMode.NON_SIMILAR))? 
                    browser.getPrevious(tuple) : browser.getNext(tuple)) && j < numberSimilarArtists;
                    j++) {
                    if (tuple.getValue() instanceof NBPoint) {
                        NBPoint point = (NBPoint) tuple.getValue();
                        String artistName = dbManager.getArtistName(point.norm());
                        ArrayList<Integer> artistTracks = dbManager.getArtistTracksIds(artistName);
                        tracks.addAll(artistTracks);
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NBTreeException ex) {
                ex.printStackTrace();
            }
        }

        return tracks;
    }
}