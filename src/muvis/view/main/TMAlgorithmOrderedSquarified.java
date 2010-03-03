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
package muvis.view.main;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;
import muvis.Environment;
import muvis.database.MusicLibraryDatabaseManager;
import nbtree.NBTree;
import net.bouthier.treemapSwing.TMAlgorithm;
import net.bouthier.treemapSwing.TMNodeModel;
import net.bouthier.treemapSwing.TMNodeModelComposite;

/**
 * The TMAlgorithmOrderedSquarified class implements a squarified treemap drawing
 * algorithm ordered by similarity.
 */
public class TMAlgorithmOrderedSquarified
        extends TMAlgorithm {

    /**
     * Draws the children of a node, by setting their drawing area first,
     * dependant of the algorithm used.
     *
     * @param g        the graphic context
     * @param node     the node whose children should be drawn
     * @param axis     the axis of separation
     * @param level    the level of deep
     */
    protected void drawChildren(Graphics2D g,
            TMNodeModelComposite node,
            short axis,
            int level) {

        float pSize = node.getSize();
        Rectangle pArea = node.getArea();

        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        NBTree nbtree = Environment.getWorkspaceInstance().getNbtreesManager().getNBTree("artistsNBTree");

        int x = pArea.x;
        int y = pArea.y;
        int width = pArea.width;
        int height = pArea.height;

        TMNodeModel child = null;

        Vector sortedChilds = new Vector();

        if (pSize == 0.0f) {
            return;
        }

        if ((width > borderLimit) && (height > borderLimit)) {
            x += borderSize;
            y += borderSize;
            width -= borderSize * 2;
            height -= borderSize * 2;
        }

        // sort child in increasing size order
        boolean isFirst = true;
        for (Enumeration e = node.children(); e.hasMoreElements();) {
            child = (TMNodeModel) e.nextElement();
            float cSize = child.getSize();
            if (isFirst) {
                sortedChilds.add(child);
                isFirst = false;
            } else {
                boolean childSorted = false;
                TMNodeModel candidate = null;
                for (int index = 0; index < sortedChilds.size(); index++) {
                    candidate = (TMNodeModel) sortedChilds.get(index);
                    float candidateSize = candidate.getSize();
                    if (candidateSize > cSize) {        //changed here to order in increasing size order
                        sortedChilds.add(index, child);
                        childSorted = true;
                        break;
                    }
                }
                if (!childSorted) {
                    sortedChilds.add(child);
                }
            }
        }

        TMNodeModel seed = (TMNodeModel) sortedChilds.get(0);
        sortedChilds.clear();
        sortedChilds.add(seed);

        // sort child in similarity (decreasing similarity from the seed)
        //isFirst = true;
        for (Enumeration e = node.children(); e.hasMoreElements();) {
            child = (TMNodeModel) e.nextElement();
            float cSize = child.getSize();
            MuVisTreemapNode fNode = (MuVisTreemapNode) child.getNode();
            double nodeKey = dbManager.getArtistKey(fNode.getName());

            /*if (isFirst) {
            sortedChilds.add(child);
            isFirst = false;
            } else {*/
            boolean childSorted = false;
            TMNodeModel candidate = null;
            for (int index = 0; index < sortedChilds.size(); index++) {
                candidate = (TMNodeModel) sortedChilds.get(index);
                float candidateSize = candidate.getSize();
                MuVisTreemapNode candidateNode = (MuVisTreemapNode) candidate.getNode();
                double candidateKey = dbManager.getArtistKey(candidateNode.getName());

                //if (candidateSize > cSize) {        //changed here to order in increasing size order
                if (candidateKey > nodeKey || candidateSize > cSize) {
                    sortedChilds.add(index, child);
                    childSorted = true;
                    break;
                }
            }
            if (!childSorted) {
                sortedChilds.add(child);
            }
        //}
        }

        //        Function AllocatePosition (s,r) {
//            ﬂoat d← sqrt(r.area / n);
//            boolean isHorizontal← (r.width < r.height);
//            List positions;
//            for i←0to < n {
//                if (isHorizontal) {
//                    x ←r.x + mod(i∗d, r.width);
//                    y ←r.y + f loor(i∗d/ r.width)∗d;
//                } else {
//                    x ←r.x + f loor(i∗d/ r.height)∗d;
//                    y ←r.y + mod(i∗d, r.height);
//                }
//                positions.add(x,y);
//            }
//            sortByDistance(positions);
//            for each node in s {
//            node← positions(i++);
//            }
//        }

        while (!sortedChilds.isEmpty()) {
            child = (TMNodeModel) sortedChilds.remove(0);
            Vector block = new Vector();
            block.add(child);
            float blockSize = child.getSize();
            short blockAxis = HORIZONTAL;
            if (width < height) {
                blockAxis = VERTICAL;
            }
            float w = 0.0f;
            float h = 0.0f;
            if (blockAxis == HORIZONTAL) {
                w = (blockSize / pSize) * width;
                h = height;
            } else {
                w = width;
                h = (blockSize / pSize) * height;
            }
            float ratio = ratio(w, h);
            boolean blockDone = false;
            while ((!sortedChilds.isEmpty()) && (!blockDone)) {
                TMNodeModel candidate =
                        (TMNodeModel) sortedChilds.firstElement();
                float newSize = candidate.getSize();
                float newBlockSize = blockSize + newSize;
                float newW = 0.0f;
                float newH = 0.0f;
                if (blockAxis == HORIZONTAL) {
                    newW = (newBlockSize / pSize) * width;
                    newH = (newSize / newBlockSize) * height;
                } else {
                    newW = (newSize / newBlockSize) * width;
                    newH = (newBlockSize / pSize) * height;
                }
                float newRatio = ratio(newW, newH);
                if (newRatio > ratio) {
                    blockDone = true;
                } else {
                    sortedChilds.remove(0);
                    block.add(candidate);
                    ratio = newRatio;
                    blockSize = newBlockSize;
                }
            }

            int childWidth = 0;
            int childHeight = 0;
            int childX = x;
            int childY = y;
            int maxX = x + width - 1;
            int maxY = y + height - 1;

            if (blockAxis == HORIZONTAL) {
                childWidth = Math.round((blockSize / pSize) * width);
            } else {
                childHeight = Math.round((blockSize / pSize) * height);
            }

            float proportion = 0.0f;
            float remaining = 0.0f;

            for (Enumeration e = block.elements(); e.hasMoreElements();) {
                child = (TMNodeModel) e.nextElement();
                Rectangle cArea = child.getArea();
                cArea.x = childX;
                cArea.y = childY;
                proportion = (child.getSize()) / blockSize;
                if (e.hasMoreElements()) {
                    if (blockAxis == HORIZONTAL) {
                        float fHeight = proportion * height;
                        childHeight = Math.round(fHeight);
                        remaining += fHeight - childHeight;
                        if (remaining >= 1) {
                            childHeight += 1;
                            remaining -= 1;
                        } else if (remaining <= -1) {
                            childHeight -= 1;
                            remaining += 1;
                        }
                        childY += childHeight;
                    } else { // VERTICAL
                        float fWidth = proportion * width;
                        childWidth = Math.round(fWidth);
                        remaining += fWidth - childWidth;
                        if (remaining >= 1) {
                            childWidth += 1;
                            remaining -= 1;
                        } else if (remaining <= -1) {
                            childWidth -= 1;
                            remaining += 1;
                        }
                        childX += childWidth;
                    }
                } else { // last element fills
                    if (blockAxis == HORIZONTAL) {
                        childHeight = (maxY - childY) + 1;
                    } else {
                        childWidth = (maxX - childX) + 1;
                    }
                }
                cArea.width = childWidth;
                cArea.height = childHeight;
                drawNodes(g, child, switchAxis(axis), (level + 1));
            }

            pSize -= blockSize;
            if (blockAxis == HORIZONTAL) {
                x += childWidth;
                width -= childWidth;
            } else {
                y += childHeight;
                height -= childHeight;
            }
        }
    }

    private float ratio(float w, float h) {
        return Math.max((w / h), (h / w));
    }

    private float euclideanDistance(double[] a1, double[] a2) {

        if (a1.length != a2.length) {
            return 0;
        } else {

            float distance = 0;

            for (int i = 0; i < a1.length; i++) {
                double parc = a1[i] - a2[i];
                parc = Math.pow(parc, 2);
                distance += parc;
            }

            distance = (float) Math.sqrt(distance);

            return distance;
        }
    }

    private void reorderVector(Vector sortedVector) {

        Vector newSortedVector = new Vector();
        TMNodeModel child;
        MusicLibraryDatabaseManager dbManager = Environment.getWorkspaceInstance().getDatabaseManager();
        NBTree nbtree = Environment.getWorkspaceInstance().getNbtreesManager().getNBTree("artistsNBTree");

        for (Object obj : sortedVector) {
            child = (TMNodeModel) obj;
            //float cSize = child.getSize();
            MuVisTreemapNode fNode = (MuVisTreemapNode) child.getNode();
            double nodeKey = dbManager.getArtistKey(fNode.getName());

            if (newSortedVector.isEmpty()) {
                newSortedVector.add(child);
            } else {
                boolean childSorted = false;
                TMNodeModel candidate = null;
                for (int index = 0; index < newSortedVector.size(); index++) {
                    candidate = (TMNodeModel) newSortedVector.get(index);
                    //float candidateSize = candidate.getSize();
                    MuVisTreemapNode candidateNode = (MuVisTreemapNode) candidate.getNode();
                    double candidateKey = dbManager.getArtistKey(candidateNode.getName());

                    //if (candidateSize > cSize) {        //changed here to order in increasing size order
                    if (candidateKey > nodeKey) {
                        newSortedVector.add(index, child);
                        childSorted = true;
                        break;
                    }
                }
                if (!childSorted) {
                    newSortedVector.add(child);
                }
            //}
            }
        }
        
        sortedVector.clear();
        sortedVector.addAll(newSortedVector);
    }
}

