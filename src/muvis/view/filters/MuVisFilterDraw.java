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

/**
 *
 * @author Ricardo
 */

import java.awt.Color;
import java.awt.Paint;
import net.bouthier.treemapSwing.TMComputeDraw;
import net.bouthier.treemapSwing.TMExceptionBadTMNodeKind;
import net.bouthier.treemapSwing.TMNode;
import net.bouthier.treemapSwing.TMNodeAdapter;

public class MuVisFilterDraw
	implements TMComputeDraw {

    /**
     * Test if this TMComputeDraw could be used
     * with the kind of TMNode passed in parameter.
     *
     * @param node    the TMNode to test the compatibility with
     * @return        <CODE>true</CODE> if this kind of node is compatible;
     *                <CODE>false</CODE> otherwise
     */
    @Override
    public boolean isCompatibleWith(TMNode node) {
        if (node instanceof MuVisFilterNode) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the filling of the node.
     * The nodeAdapter should return an instance of TMFileNode.
     *
     * @param nodeAdapter               we compute the filling of this node;
     *                                  should return an instance of TMFileNode
     * @return                          the filling of the node
     * @throws TMExceptionBadTMNodeKind If the node does not return an
     *                                  instance of TMFileNode
     */
    @Override
    public Paint getFilling(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisFilterNode) {

            MuVisFilterNode fNode = (MuVisFilterNode) node;
            if (fNode.isSelected())
                //return Color.GREEN;
                //return SystemColor.textHighlight;
                //return SystemColor.controlHighlight;
                //return SystemColor.menuText;
                return new Color(0,0, 255);
            else {
                return Color.WHITE;
            }

            /*TMFileNode fNode = (TMFileNode) node;
            long time = fNode.getDate();
            long diff = (new Date()).getTime() - time;
            if (diff <= 3600000L) { // less than an hour
                nodeAdapter.setUserData("Less than an hour");
                return Color.white;
            } else if (diff <= 86400000L) { // less than a day
                nodeAdapter.setUserData("Less than a day");
                return Color.green;
            } else if (diff <= 604800000L) { // less than a week
                nodeAdapter.setUserData("Less than a week");
                return Color.yellow;
            } else if (diff <= 2592000000L) { // less than a month
                nodeAdapter.setUserData("Less than a month");
                return Color.orange;
            } else if (diff <= 31536000000L) { // less than a year
                nodeAdapter.setUserData("Less than a year");
                return Color.red;
            } else { // more than a year
                nodeAdapter.setUserData("More than a year");
                return Color.blue;
            }*/
        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

    /**
     * Returns the tooltip of the node.
     * The nodeAdapter should return an instance of TMFileNode.
     *
     * @param nodeAdapter               we compute the tooltip of this node;
     *                                  should return an instance of TMFileNode
     * @return                          the tooltip of the node
     * @throws TMExceptionBadTMNodeKind If the node does not return an
     *                                  instance of TMFileNode
     */
    public String getTooltip(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisFilterNode) {
            MuVisFilterNode fNode = (MuVisFilterNode) node;

            String name = fNode.getName();

            String tooltip = name;

            return tooltip;

            /*float size = nodeAdapter.getSize();
            String state = (String) nodeAdapter.getUserData();

            long modTime = fNode.getDate();
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);
            String date = df.format(new Date(modTime));
            String time = tf.format(new Date(modTime));

            String tooltip =
                "<html>"
                    + name
                    + "<p>"
                    + date
                    + " : "
                    + time
                    + "<p>"
                    + state
                    + "<p>"
                    + size
                    + " octets";
            return tooltip;*/
        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

    /**
     * Returns the color of the title of the node.
     *
     * @param nodeAdapter               the node for which we want the title
     * @return                          the title of the node
     * @throws TMExceptionBadTMNodeKind if the kind of TMNode returned is
     *                                  incompatible with this TMComputeDraw.
     */
    public Paint getTitleColor(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisFilterNode) {
            MuVisFilterNode fNode = (MuVisFilterNode) node;

            //return Color.DARK_GRAY;

            return Color.blue;
        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

    /**
     * Returns the title of the node.
     *
     * @param nodeAdapter               the node for which we want the title
     * @return                          the title of the node
     * @throws TMExceptionBadTMNodeKind if the kind of TMNode returned is
     *                                  incompatible with this TMComputeDraw.
     */
    public String getTitle(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisFilterNode) {
            MuVisFilterNode fNode = (MuVisFilterNode) node;

            return fNode.getName();
        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

}

