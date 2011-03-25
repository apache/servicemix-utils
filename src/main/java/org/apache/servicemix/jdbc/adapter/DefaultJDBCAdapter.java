/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.jdbc.adapter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.servicemix.jdbc.JDBCAdapter;
import org.apache.servicemix.jdbc.JDBCAdapterFactory;
import org.apache.servicemix.jdbc.Statements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements all the default JDBC operations that are used by the
 * JDBCPersistenceAdapter. <p/> sub-classing is encouraged to override the
 * default implementation of methods to account for differences in JDBC Driver
 * implementations. <p/> The JDBCAdapter inserts and extracts BLOB data using
 * the getBytes()/setBytes() operations. <p/> The databases/JDBC drivers that
 * use this adapter are:
 * <ul>
 * <li></li>
 * </ul>
 * 
 * @org.apache.xbean.XBean element="defaultJDBCAdapter"
 * 
 * @version $Revision: 1.10 $
 */
public class DefaultJDBCAdapter implements JDBCAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJDBCAdapter.class);

    protected Statements statements;

    protected void setBinaryData(PreparedStatement s, int index, byte data[]) throws SQLException {
        s.setBytes(index, data);
    }

    protected byte[] getBinaryData(ResultSet rs, int index) throws SQLException {
        return rs.getBytes(index);
    }

    public void doCreateTables(Connection connection) throws SQLException, IOException {
        Statement s = null;
        try {
            // Check to see if the table already exists. If it does, then don't
            // log warnings during startup.
            // Need to run the scripts anyways since they may contain ALTER
            // statements that upgrade a previous version of the table
            boolean alreadyExists = false;
            ResultSet rs = null;
            try {
                rs = connection.getMetaData().getTables(null, null, statements.getFullStoreTableName(),
                        new String[] {"TABLE" });
                alreadyExists = rs.next();
            } catch (Throwable ignore) {
                // Do nothing
            } finally {
                close(rs);
            }

            // If the dataSource is a managed DataSource, executing a statement
            // that throws
            // an exception will make the connection unusable.
            // So if the table already exists, do not try to re-create them
            if (alreadyExists) {
                return;
            }

            s = connection.createStatement();
            String[] createStatments = statements.getCreateSchemaStatements();
            for (int i = 0; i < createStatments.length; i++) {
                // This will fail usually since the tables will be
                // created already.
                try {
                    LOG.debug("Executing SQL: " + createStatments[i]);
                    s.execute(createStatments[i]);
                } catch (SQLException e) {
                    if (alreadyExists) {
                        LOG.debug("Could not create JDBC tables; The message table already existed." + " Failure was: "
                                + createStatments[i] + " Message: " + e.getMessage() + " SQLState: " + e.getSQLState()
                                + " Vendor code: " + e.getErrorCode());
                    } else {
                        LOG.warn("Could not create JDBC tables; they could already exist." + " Failure was: "
                                + createStatments[i] + " Message: " + e.getMessage() + " SQLState: " + e.getSQLState()
                                + " Vendor code: " + e.getErrorCode());
                        JDBCAdapterFactory.log("Failure details: ", e);
                    }
                }
            }
        } finally {
            close(s);
        }
    }

    public void doDropTables(Connection connection) throws SQLException, IOException {
        Statement s = null;
        try {
            s = connection.createStatement();
            String[] dropStatments = statements.getDropSchemaStatements();
            for (int i = 0; i < dropStatments.length; i++) {
                // This will fail usually since the tables will be
                // created already.
                try {
                    s.execute(dropStatments[i]);
                } catch (SQLException e) {
                    LOG.warn("Could not drop JDBC tables; they may not exist." + " Failure was: " + dropStatments[i]
                            + " Message: " + e.getMessage() + " SQLState: " + e.getSQLState() + " Vendor code: "
                            + e.getErrorCode());
                    JDBCAdapterFactory.log("Failure details: ", e);
                }
            }
        } finally {
            close(s);
        }
    }

    public void doStoreData(Connection connection, String id, byte[] data) throws SQLException, IOException {
        PreparedStatement s = null;
        try {
            if (s == null) {
                s = connection.prepareStatement(statements.getStoreDataStatement());
            }
            s.setString(1, id);
            setBinaryData(s, 2, data);
            if (s.executeUpdate() != 1) {
                throw new SQLException("Failed to insert data");
            }
        } finally {
            close(s);
        }
    }

    public byte[] doLoadData(Connection connection, String id) throws SQLException, IOException {
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
            s = connection.prepareStatement(statements.getFindDataStatement());
            s.setString(1, id);
            rs = s.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return getBinaryData(rs, 1);
        } finally {
            close(rs);
            close(s);
        }
    }

    public void doUpdateData(Connection connection, String id, byte[] data) throws SQLException, IOException {
        PreparedStatement s = null;
        try {
            if (s == null) {
                s = connection.prepareStatement(statements.getUpdateDataStatement());
            }
            s.setString(2, id);
            setBinaryData(s, 1, data);
            if (s.executeUpdate() != 1) {
                throw new SQLException("Failed to update data");
            }
        } finally {
            close(s);
        }
    }

    public void doRemoveData(Connection connection, String id) throws SQLException, IOException {
        PreparedStatement s = null;
        try {
            s = connection.prepareStatement(statements.getRemoveDataStatement());
            s.setString(1, id);
            if (s.executeUpdate() != 1) {
                throw new SQLException("Failed to remove data");
            }
        } finally {
            close(s);
        }
    }

    private static void close(Statement s) {
        try {
            if (s != null) {
                s.close();
            }
        } catch (Throwable e) {
            // Do nothing
        }
    }

    private static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Throwable e) {
            // Do nothing
        }
    }

    public Statements getStatements() {
        return statements;
    }

    public void setStatements(Statements statements) {
        this.statements = statements;
    }

    public byte[][] doLoadData(Connection connection, String[] ids) throws SQLException, IOException {
        PreparedStatement s = null;
        byte[][] datas = new byte[ids.length][];
        try {
            s = connection.prepareStatement(statements.getFindDataStatement());
            for (int i = 0; i < ids.length; i++) {
                s.setString(1, ids[i]);
                ResultSet rs = s.executeQuery();
                if (rs.next()) {
                    datas[i] = getBinaryData(rs, 1);
                }
                close(rs);
            }
            return datas;
        } finally {
            close(s);
        }
    }

    public void doRemoveData(Connection connection, String[] ids) throws SQLException, IOException {
        PreparedStatement s = null;
        try {
            s = connection.prepareStatement(statements.getRemoveDataStatement());
            for (int i = 0; i < ids.length; i++) {
                s.setString(1, ids[i]);
                s.executeUpdate();
            }
        } finally {
            close(s);
        }
    }

    public int doGetCount(Connection connection) throws SQLException, IOException {
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
            s = connection.prepareStatement(statements.getCountStatement());
            rs = s.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            close(rs);
            close(s);
        }
    }

    public String[] doGetIds(Connection connection) throws SQLException, IOException {
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
            List<String> ids = new ArrayList<String>();
            s = connection.prepareStatement(statements.getFindAllIdsStatement());
            rs = s.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
            return ids.toArray(new String[ids.size()]);
        } finally {
            close(rs);
            close(s);
        }
    }

    public String[] doGetIds(Connection connection, int fromIndex, int toIndex) throws SQLException, IOException {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            s.setFetchSize(toIndex - fromIndex);
            rs = s.executeQuery(statements.getFindAllIdsStatement());
            rs.absolute(fromIndex + 1);
            String[] ids = new String[toIndex - fromIndex];
            for (int row = 0; row < toIndex - fromIndex; row++) {
                ids[row] = rs.getString(1);
                if (!rs.next()) {
                    break;
                }
            }
            return ids;
        } finally {
            close(rs);
            close(s);
        }
    }
    
    public Map<String, byte[]> doLoadData(Connection connection) throws SQLException, IOException {
        Statement s = null;
        ResultSet rs = null;
        Map<String, byte[]> data = new HashMap<String, byte[]>();
        try {
            s = connection.createStatement();
            rs = s.executeQuery(statements.getFindAllDataStatement());
            while (rs.next()) {
                data.put(rs.getString(1), getBinaryData(rs, 2));
            }
        } finally {
            close(rs);
            close(s);
        }
        return data;
    }

}
