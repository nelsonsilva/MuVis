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

/**
 * Class responsible for drawing the nodes of the MuVis Main Treemap View
 * @author Ricardo
 */

import java.awt.Color;
import java.awt.Paint;
import java.util.Enumeration;
import muvis.Elements;
import muvis.database.MusicLibraryDatabaseManager;
import muvis.util.Util;
import muvis.view.main.filters.TreemapFilterManager;
import net.bouthier.treemapSwing.TMComputeDraw;
import net.bouthier.treemapSwing.TMExceptionBadTMNodeKind;
import net.bouthier.treemapSwing.TMNode;
import net.bouthier.treemapSwing.TMNodeAdapter;
import org.springframework.beans.factory.annotation.Autowired;

public class MuVisNodeDraw
	implements TMComputeDraw {

    @Autowired private MusicLibraryDatabaseManager dbManager;
    @Autowired private TreemapFilterManager treemapFilterManager;

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
        if (node instanceof MuVisTreemapNode) {
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
        if (node instanceof MuVisTreemapNode) {

            MuVisTreemapNode fNode = (MuVisTreemapNode)node;

            if (fNode.isSelected()){
                return new Color(122, 122, 122);
            }

            return Util.getGenreColor(dbManager.getArtistGenre(fNode.getName()));
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
    @Override
    public String getTooltip(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisTreemapNode) {
            MuVisTreemapNode fNode = (MuVisTreemapNode) node;

            String name = fNode.getName();

            String tooltip = "<html>" + name;

            int numAlbums = 0, numTracks = 0;

            if (fNode.getName().equals(Elements.OTHERS_NODE)){

                for(Enumeration children = fNode.children(); children.hasMoreElements();){
                    MuVisTreemapNode n = (MuVisTreemapNode)children.nextElement();
                    numTracks += treemapFilterManager.getCountFilteredTracks(n.getName());
                }

                for(Enumeration children = fNode.children(); children.hasMoreElements(); ){
                    MuVisTreemapNode n = (MuVisTreemapNode)children.nextElement();
                    numAlbums += treemapFilterManager.getCountFilteredAlbuns(n.getName());
                }

            }
            else {

                numAlbums = treemapFilterManager.getCountFilteredAlbuns(fNode.getName());

                numTracks = treemapFilterManager.getCountFilteredTracks(fNode.getName());
            }

            tooltip += "<p>" + numAlbums + " albums with " + numTracks + " tracks";

            return tooltip;
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
    @Override
    public Paint getTitleColor(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisTreemapNode) {
            MuVisTreemapNode fNode = (MuVisTreemapNode) node;
            return Color.white;
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
    @Override
    public String getTitle(TMNodeAdapter nodeAdapter)
        throws TMExceptionBadTMNodeKind {

        TMNode node = nodeAdapter.getNode();
        if (node instanceof MuVisTreemapNode) {
            MuVisTreemapNode fNode = (MuVisTreemapNode) node;

            return fNode.getName();
        } else {
            throw new TMExceptionBadTMNodeKind(this, node);
        }
    }

}

