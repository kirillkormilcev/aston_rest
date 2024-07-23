package ru.aston.rest_service.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.aston.rest_service.exception.DataBaseDriverLoadException;
import ru.aston.rest_service.utility.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionManagerImpl implements ConnectionManager {
    private static final String DRIVER_CLASS_KEY = "db.driver-class-name";
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static ConnectionManager connection;

    public static synchronized ConnectionManager getInstance() {
        if (connection == null) {
            loadDriver(PropertiesUtil.getProperties(DRIVER_CLASS_KEY));
            connection = new ConnectionManagerImpl();
        }
        return connection;
    }

    private static void loadDriver(String driverClass) {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new DataBaseDriverLoadException("Database driver not loaded.");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PropertiesUtil.getProperties(URL_KEY),
                PropertiesUtil.getProperties(USERNAME_KEY),
                PropertiesUtil.getProperties(PASSWORD_KEY)
        );
    }
}
