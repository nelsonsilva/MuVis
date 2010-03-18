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
package muvis;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import nbtree.NBTree;

/**
 * Holds the manager for the nbtrees
 * @author Ricardo
 */
public class NBTreeManager implements Serializable {

    private HashMap<String, NBTree> trees;

    public NBTreeManager() {
        trees = new HashMap<String, NBTree>();
    }

    public void addNBTree(String treeName, NBTree tree) {
        trees.put(treeName, tree);
    }

    public NBTree getNBTree(String treeName) {
        return trees.get(treeName);
    }

    public Set getNBTreesNames() {
        return trees.keySet();
    }

    public Collection<NBTree> getAllNBTrees() {
        return trees.values();
    }

    public void save() {
        for(NBTree tree : trees.values()){
            try {
                tree.save();
            } catch (IOException ex) {
                System.out.println("Couldn't save tree!");
                ex.printStackTrace();
            }
        }
    }
}
