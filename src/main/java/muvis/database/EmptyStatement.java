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

package muvis.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * Statement with an empty implementation.
 * Methods are no-operation.
 * @author Ricardo
 *
 */
public class EmptyStatement implements Statement {

    @Override
    public void addBatch(String arg0) throws SQLException {
    }

    @Override
    public void cancel() throws SQLException {
    }

    @Override
    public void clearBatch() throws SQLException {
    }

    @Override
    public void clearWarnings() throws SQLException {
    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public boolean execute(String arg0) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String arg0, int arg1) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String arg0, int[] arg1) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String arg0, String[] arg1) throws SQLException {
        return false;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return null;
    }

    @Override
    public ResultSet executeQuery(String arg0) throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String arg0) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String arg0, int arg1) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String arg0, int[] arg1) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String arg0, String[] arg1) throws SQLException {
        return 0;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public boolean getMoreResults(int arg0) throws SQLException {
        return false;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void setCursorName(String arg0) throws SQLException {
    }

    @Override
    public void setEscapeProcessing(boolean arg0) throws SQLException {
    }

    @Override
    public void setFetchDirection(int arg0) throws SQLException {
    }

    @Override
    public void setFetchSize(int arg0) throws SQLException {
    }

    @Override
    public void setMaxFieldSize(int arg0) throws SQLException {
    }

    @Override
    public void setMaxRows(int arg0) throws SQLException {
    }

    @Override
    public void setPoolable(boolean arg0) throws SQLException {
    }

    @Override
    public void setQueryTimeout(int arg0) throws SQLException {
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
}

