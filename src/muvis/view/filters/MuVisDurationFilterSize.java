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

import muvis.Environment;
import net.bouthier.treemapSwing.TMComputeSize;
import net.bouthier.treemapSwing.TMExceptionBadTMNodeKind;
import net.bouthier.treemapSwing.TMNode;


/**
 * The TMFileSize class implements an example of a TMComputeSize
 * for a TMFileNode.
 *
 * @author Christophe Bouthier [bouthier@loria.fr]
 * @version 2.5
 */
public class MuVisDurationFilterSize
	implements TMComputeSize {

    /**
     * Test if this TMComputeSize could be used
     * with the kind of TMNode passed in parameter.
     *
     * @param node    the TMNode to test the compatibility with
     * @return        <CODE>true</CODE> if this kind of node is compatible;
     *                <CODE>false</CODE> otherwise
     */
    public boolean isCompatibleWith(TMNode node) {
        if (node instanceof MuVisFilterNode) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the size of the node.
     * The node should be an instance of TMFileNode.
     * Returns <CODE>0</CODE> for a folder, and the size
     * of the file, in byte, for a file.
     *
     * @param node                      we compute the size of this node;
     *                                  should be an instance of TMFileNode
     * @return                          the size of the node;
     *                                  <CODE>0</CODE> for a folder;
     *                                  the size of the file in byte for a file
     * @throws TMExceptionBadTMNodeKind If the node is not an
     *                                  instance of TMFileNode
     */
    @Override
    public float getSize(TMNode node) 
    	throws TMExceptionBadTMNodeKind {

        if (node instanceof MuVisDurationFilterNode) {
            MuVisDurationFilterNode fNode = (MuVisDurationFilterNode) node;

            int max = fNode.getMaxValue();
            int min = fNode.getMinValue();

            return Environment.getWorkspaceInstance().getDatabaseManager().getTracksBetweenTimeRange(min, max);

        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

}
