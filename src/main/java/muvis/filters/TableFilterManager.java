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
package muvis.filters;

import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;
import muvis.util.Observable;
import muvis.util.Observer;
import muvis.view.table.TracksTableModel;

/**
 *
 * @author Ricardo
 */
public class TableFilterManager implements Observer {

    private ArrayList<TableFilter> filters;
    private TableRowSorter<TracksTableModel> sorter;

    public TableFilterManager(TableRowSorter<TracksTableModel> sorter) {
        this.filters = new ArrayList<TableFilter>();
        this.sorter = sorter;
    }

    public TableFilterManager() {
        // TODO - TableFilterManager
    }

    public void addTableFilter(TableFilter filter) {
        filters.add(filter);
    }

    public void resetFilters() {
        for(TableFilter filter : filters){
            filter.reset();
        }
    }

    public void removeTableFilter(TableFilter filter) {
        filters.remove(filter);
    }

    public void filter() {

        List<RowFilter<TracksTableModel, Object>> listFilters = new ArrayList<RowFilter<TracksTableModel, Object>>();

        for (TableFilter filter : filters) {
            listFilters.add(filter.filter());
        }

        RowFilter<Object, Object> composedFilter = RowFilter.andFilter(listFilters);
        sorter.setRowFilter(composedFilter);
    }

    @Override
    public void update(Observable obs, Object arg) {
        for (Observer observer : filters){
            observer.update(obs, arg);
        }
        filter();
    }
}
