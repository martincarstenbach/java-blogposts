/*
 * Copyright 2022 Martin Bach
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ---------------------------------------------------------------------------------------------------
 * 
 * Version History
 * 200716   Initial Version
 * 220605   major rewrite
 */

package de.martin.seps;

import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

import java.util.Properties;
import java.net.URL;

/**
 * The equivalent of a "hello world" application for connecting to the
 * Oracle database using (an existing) secure external password store
 * 
 * Please refer to my blog for more details, especially setting up the external
 * password store.
 */
public class SEPS {

    /**
     * EZ Connect Plus string, for example
     * "jdbc:oracle:thin:/@swingbench1?TNS_ADMIN=/home/martin/tns"
     * This string should be defined in app.properties
     */
    private String ezConnectPlusString = null;

    public String getEzConnectPlusString() {
        return ezConnectPlusString;
    }

    /**
     * Read properties from app.properties
     */
    public void init() {
        Properties properties = new Properties();
        URL url = ClassLoader.getSystemResource("app.properties");

        try {
            properties.load(url.openStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
        ezConnectPlusString = properties.getProperty("conf.ezConnectPlusString");
    }

    /**
     * Connnect to the database and print a bit of metadata.
     * 
     * @throws SQLException
     */
    public void connect() throws SQLException {

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(this.ezConnectPlusString);

        try (OracleConnection connection = (OracleConnection) ods.getConnection()) {

            // Grab some randome metadata
            DatabaseMetaData dbmd = connection.getMetaData();

            System.out.println("Driver Name:       " + dbmd.getDriverName());
            System.out.println("Driver Version:    " + dbmd.getDriverVersion());

            System.out.println("Database username: " + connection.getUserName());

            System.out.println();

            connection.close();
        }
    }

    public static void main(String[] args) {
        System.out.println("Getting ready to connect to the database\n");
        SEPS demo = new SEPS();
        try {
            demo.init();
            System.out.println("Got the following connection string: " + demo.getEzConnectPlusString());
            demo.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}