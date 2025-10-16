package com.example;

import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import java.util.Properties;

/**
 * Utility class to manage Oracle JDBC Universal Connection Pool.
 * Build around the following premises:
 * - Reads DB config (url, username, password) from db.properties.
 * - Ensures the properties file is found and loaded safely.
 * - Fails fast with meaningful error if required properties are missing.
 * - Singleton Pattern: Ensures only one pool is created.
 * - Double-checked locking: For thread-safe lazy initialization.
 * - Connection Unwrapping: Returns actual oracle.jdbc.OracleConnection.
 *
 */
public final class OracleUCPDataSource {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final Properties dbProperties = new Properties();
    private static volatile PoolDataSource poolDataSource;

    private static final String poolName = "demopool";

    private OracleUCPDataSource() {}

    static {
        loadProperties();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // ensure the connection pool is shut down cleanly
            try {
                UniversalConnectionPoolManager mgr = UniversalConnectionPoolManagerImpl.
                        getUniversalConnectionPoolManager();
                mgr.destroyConnectionPool(poolName);
                System.out.println("pool has been shut down");
            } catch (UniversalConnectionPoolException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    /**
     * Load DB properties from classpath.
     */
    private static void loadProperties() {
        try (InputStream input = OracleUCPDataSource.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (input == null) {
                throw new IllegalStateException("Unable to find " + PROPERTIES_FILE);
            }

            dbProperties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DB properties file", e);
        }
    }

    /**
     * Lazily initializes and returns the Oracle PoolDataSource.
     */
    private static PoolDataSource getPoolDataSource() throws SQLException {
        if (poolDataSource == null) {
            synchronized (OracleUCPDataSource.class) {
                if (poolDataSource == null) {
                    PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();

                    pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
                    pds.setURL(getRequiredProperty("db.url"));
                    pds.setUser(getRequiredProperty("db.username"));
                    pds.setPassword(getRequiredProperty("db.password"));

                    pds.setConnectionPoolName(poolName);

                    pds.setInitialPoolSize(5);
                    pds.setMinPoolSize(5);
                    pds.setMaxPoolSize(5);
                    pds.setTimeoutCheckInterval(60);
                    pds.setInactiveConnectionTimeout(300);

                    poolDataSource = pds;
                }
            }
        }
        return poolDataSource;
    }

    /**
     * Returns a pooled OracleConnection from the UCP.
     */
    public static OracleConnection getPooledConnection() throws SQLException {
        return getPoolDataSource()
                .getConnection()
                .unwrap(OracleConnection.class);
    }

    /**
     * Retrieves a required property or throws an exception.
     */
    private static String getRequiredProperty(String key) {
        String value = dbProperties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required property: " + key);
        }
        return value;
    }
}