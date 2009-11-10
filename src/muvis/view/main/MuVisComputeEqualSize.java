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

import net.bouthier.treemapSwing.TMComputeSize;
import net.bouthier.treemapSwing.TMExceptionBadTMNodeKind;
import net.bouthier.treemapSwing.TMNode;

/**
 * Size of each node is the same.
 *
 * @author Ricardo Dias
 * @version 1
 */
public class MuVisComputeEqualSize
	implements TMComputeSize {

    /**
     * Test if this TMComputeSize could be used
     * with the kind of TMNode passed in parameter.
     */
    @Override
    public boolean isCompatibleWith(TMNode node) {
        if (node instanceof MuVisTreemapNode) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the size of the node.
     * The size of the node is the same to all the nodes.
     */
    @Override
    public float getSize(TMNode node) 
    	throws TMExceptionBadTMNodeKind {

        if (node instanceof MuVisTreemapNode) {
            MuVisTreemapNode fNode = (MuVisTreemapNode) node;
            return (float) fNode.getSize();
        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

}
