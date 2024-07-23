package ru.aston.rest_service.utility;

import ru.aston.rest_service.db.ConnectionManager;
import ru.aston.rest_service.exception.DataBaseStatementException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlSchemeAndDataInit {
    private static final String SCHEME = "schema.sql";
    private static final String DATA = "data.sql";
    private static String schemeSql;
    private static String dataSql;

    static {
        loadInitSQL();
    }

    private SqlSchemeAndDataInit() {
    }

    public static void initSqlScheme(ConnectionManager connectionManager) {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(schemeSql);
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при создании таблиц базы данных."
                    + System.lineSeparator() + e.getMessage());
        }
    }

    public static void initSqlData(ConnectionManager connectionManager) {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(dataSql);
        } catch (SQLException e) {
            throw new DataBaseStatementException("Ошибка при заполнении таблиц базы данных инициализационными данными."
                    + System.lineSeparator() + e.getMessage());
        }
    }

    private static void loadInitSQL() {
        try (InputStream inFile = SqlSchemeAndDataInit.class.getClassLoader().getResourceAsStream(SCHEME)) {
            assert inFile != null;
            schemeSql = new String(inFile.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        try (InputStream inFile = SqlSchemeAndDataInit.class.getClassLoader().getResourceAsStream(DATA)) {
            assert inFile != null;
            dataSql = new String(inFile.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }
}