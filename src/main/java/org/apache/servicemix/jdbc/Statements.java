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
package org.apache.servicemix.jdbc;

/**
 * @version $Revision: 1.4 $
 * 
 * @org.apache.xbean.XBean element="statements"
 * 
 */
public class Statements {

    protected String storeTableName = "SM_STORE";
    protected String binaryDataType = "BLOB";
    protected String idDataType = "VARCHAR(255)";
    private String tablePrefix = "";
    private String storeDataStatement;
    private String updateDataStatement;
    private String removeDataStatement;
    private String findDataStatement;
    private String findAllIdsStatement;
    private String findAllDataStatement;
    private String countStatement;
    private String[] createSchemaStatements;
    private String[] dropSchemaStatements;

    public String[] getCreateSchemaStatements() {
        if (createSchemaStatements == null) {
            createSchemaStatements = new String[] {
                "CREATE TABLE " + getFullStoreTableName() + "(" + "ID " + idDataType + " NOT NULL"
                            + ", DATA " + binaryDataType
                            + ", PRIMARY KEY ( ID ) )",
            };
        }
        return createSchemaStatements;
    }

    public String[] getDropSchemaStatements() {
        if (dropSchemaStatements == null) {
            dropSchemaStatements = new String[] { 
                "DROP TABLE " + getFullStoreTableName() + "", 
            };
        }
        return dropSchemaStatements;
    }

    public String getStoreDataStatement() {
        if (storeDataStatement == null) {
            storeDataStatement = "INSERT INTO " + getFullStoreTableName()
                    + "(ID, DATA) VALUES (?, ?)";
        }
        return storeDataStatement;
    }

    public String getUpdateDataStatement() {
        if (updateDataStatement == null) {
            updateDataStatement = "UPDATE " + getFullStoreTableName() + " SET DATA=? WHERE ID=?";
        }
        return updateDataStatement;
    }

    public String getRemoveDataStatement() {
        if (removeDataStatement == null) {
            removeDataStatement = "DELETE FROM " + getFullStoreTableName() + " WHERE ID=?";
        }
        return removeDataStatement;
    }

    public String getFindDataStatement() {
        if (findDataStatement == null) {
            findDataStatement = "SELECT DATA FROM " + getFullStoreTableName() + " WHERE ID=?";
        }
        return findDataStatement;
    }

    public String getFindAllIdsStatement() {
        if (findAllIdsStatement == null) {
            findAllIdsStatement = "SELECT ID FROM " + getFullStoreTableName() 
                    + " ORDER BY ID"; 
        }
        return findAllIdsStatement;
    }

    public String getFindAllDataStatement() {
        if (findAllDataStatement == null) {
            findAllDataStatement = "SELECT ID, DATA FROM " + getFullStoreTableName()
                    + " ORDER BY ID";
        }
        return findAllDataStatement;
    }

    public String getCountStatement() {
        if (countStatement == null) {
            countStatement = "SELECT COUNT(ID) FROM " + getFullStoreTableName();
        }
        return countStatement;
    }

    public String getFullStoreTableName() {
        return getTablePrefix() + getStoreTableName();
    }

    /**
     * @return Returns the messageDataType.
     */
    public String getBinaryDataType() {
        return binaryDataType;
    }

    /**
     * @param messageDataType
     *            The messageDataType to set.
     */
    public void setBinaryDataType(String messageDataType) {
        this.binaryDataType = messageDataType;
    }

    /**
     * @return Returns the storeTableName.
     */
    public String getStoreTableName() {
        return storeTableName;
    }

    /**
     * @param storeTableName
     *            The storeTableName to set.
     */
    public void setStoreTableName(String storeTableName) {
        this.storeTableName = storeTableName;
    }

    /**
     * @return Returns the idDataType.
     */
    public String getIdDataType() {
        return idDataType;
    }

    /**
     * @param idDataType
     *            The idDataType to set.
     */
    public void setIdDataType(String msgIdDataType) {
        this.idDataType = msgIdDataType;
    }

    /**
     * @return Returns the tablePrefix.
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * @param tablePrefix
     *            The tablePrefix to set.
     */
    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public void setStoreDataStatement(String addMessageStatment) {
        this.storeDataStatement = addMessageStatment;
    }

    public void setCreateSchemaStatements(String[] createSchemaStatments) {
        this.createSchemaStatements = createSchemaStatments;
    }

    public void setDropSchemaStatements(String[] dropSchemaStatments) {
        this.dropSchemaStatements = dropSchemaStatments;
    }

    public void setFindAllDataStatement(String findAllMessagesStatment) {
        this.findAllDataStatement = findAllMessagesStatment;
    }

    public void setFindDataStatement(String findMessageStatment) {
        this.findDataStatement = findMessageStatment;
    }

    public void setRemoveDataStatement(String removeMessageStatment) {
        this.removeDataStatement = removeMessageStatment;
    }

    public void setUpdateDataStatement(String updateMessageStatment) {
        this.updateDataStatement = updateMessageStatment;
    }

    public void setFindAllIdsStatement(String findAllIdsStatement) {
        this.findAllIdsStatement = findAllIdsStatement;
    }

    public void setCountStatement(String getCountStatement) {
        this.countStatement = getCountStatement;
    }

}
