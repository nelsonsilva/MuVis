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
package muvis.view.filters;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import net.bouthier.treemapSwing.*;

/**
 * The TMAlgorithmAscOrderFilter class implements a squarified treemap drawing
 * algorithm for the treemap representation filters.
 */
public class TMAlgorithmAscOrderFilter
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
    protected void drawChildren(Graphics2D 			 g,
        						TMNodeModelComposite node,
        						short 				 axis,
        						int 				 level) {

        float pSize = node.getSize();
        Rectangle pArea = node.getArea();

        int x = pArea.x;
        int y = pArea.y;
        int width = pArea.width;
        int height = pArea.height;

        TMNodeModel child = null;

        if (pSize == 0.0f) {
            return;
        }

        if ((width > borderLimit) && (height > borderLimit)) {
            x += borderSize;
            y += borderSize;
            width -= borderSize * 2;
            height -= borderSize * 2;
        }

        Vector sortedChilds = new Vector();

        for (Enumeration e = node.children(); e.hasMoreElements();) {
            sortedChilds.add((TMNodeModel) e.nextElement());

        }

        Collections.sort(sortedChilds, new AscTMNodeComparator());

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

}
class AscTMNodeComparator implements Comparator<TMNodeModel>{

    @Override
    public int compare(TMNodeModel o1, TMNodeModel o2) {

        if ((o1.getNode() instanceof MuVisFilterNode) && (o2.getNode() instanceof MuVisFilterNode)){

            MuVisFilterNode node1 = (MuVisFilterNode)o1.getNode();
            MuVisFilterNode node2 = (MuVisFilterNode)o2.getNode();

            if (node1.getOrder() > node2.getOrder()){
                return -1;
            }
            else if (node1.getOrder() < node2.getOrder()){
                return 1;
            }
            else return 0;
        }
        else return 0;

    }
}
