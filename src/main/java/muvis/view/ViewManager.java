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

package muvis.view;

import java.util.Collection;
import java.util.HashMap;

/**
 * Manager view - manages the view
 * @author Ricardo
 */
public class ViewManager {

    private HashMap<String, Object> views;

    public ViewManager(){
        views = new HashMap<String, Object>();
    }

    public void addView(String key, Object view){
        views.put(key, view);
    }

    public Object getView(String key){
        return views.get(key);
    }

    public Collection<Object> getAllViews(){
        return views.values();
    }
}
