package ru.aston.rest_service;

import ru.aston.rest_service.db.ConnectionManager;
import ru.aston.rest_service.db.ConnectionManagerImpl;
import ru.aston.rest_service.utility.SqlSchemeAndDataInit;

public class Main {
    public static void main(String[] args) {
        ConnectionManager connection = ConnectionManagerImpl.getInstance();
        SqlSchemeAndDataInit.initSqlScheme(connection);
        SqlSchemeAndDataInit.initSqlData(connection);
    }
}