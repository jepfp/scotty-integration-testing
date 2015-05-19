package ch.adoray.scotty.integrationtest.common;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
public class DatabaseAccess {
    // TODO: Database connection is not yet closed properly. Change.
    private static Connection connection = null;

    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(config().getConnectionString());
        }
        return connection;
    }

    public static Map<String, String> getLastRecord(String table) throws SQLException, ClassNotFoundException {
        return getRecordFromLogHistory(table, 0);
    }

    public static Map<String, String> getSecondLastRecord(String table) throws SQLException, ClassNotFoundException {
        return getRecordFromLogHistory(table, 1);
    }

    public static Map<String, String> getRecordFromLogHistory(String table, int numberOfEntriesBackFromNewest) throws SQLException, ClassNotFoundException {
        String sqlStatement = "select * from " + table + " where id = (select max(id) from " + table + ") - " + numberOfEntriesBackFromNewest;
        return executeStatementAndReturnLastResult(sqlStatement);
    }

    public static Map<String, String> getRecordById(String table, long id) {
        try {
            String sqlStatement = "select * from " + table + " where id = " + id;
            return executeStatementAndReturnLastResult(sqlStatement);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while determining record with id " + id + " from table " + table + ".", e);
        }
    }

    public static int determineAmountOfEntriesInTable(String table) {
        try {
            String sqlStatement = "select count(*) as count from " + table;
            Map<String, String> result;
            result = executeStatementAndReturnLastResult(sqlStatement);
            return Integer.parseInt(result.get("count"));
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while determining amount of entries in table " + table, e);
        }
    }

    public static Map<String, String> executeStatementAndReturnLastResult(String sqlStatement) throws SQLException, ClassNotFoundException {
        try (Statement statement = getConnection().createStatement(); ResultSet resultSet = statement.executeQuery(sqlStatement);) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.last()) {
                int columnCount = metaData.getColumnCount();
                Map<String, String> tuple = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columnCount; i++) {
                    tuple.put(metaData.getColumnLabel(i), resultSet.getString(i));
                }
                return tuple;
            } else {
                return null;
            }
        }
    }

    public static Map<String, String> executeUpdate(String sqlStatement) throws SQLException, ClassNotFoundException {
        try (Statement statement = getConnection().createStatement(); ResultSet resultSet = statement.executeQuery(sqlStatement);) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            resultSet.last();
            Map<String, String> tuple = new LinkedHashMap<String, String>();
            for (int i = 1; i <= columnCount; i++) {
                tuple.put(metaData.getColumnLabel(i), resultSet.getString(i));
            }
            return tuple;
        }
    }

    public static PreparedStatement prepareStatement(String statement) {
        try {
            return getConnection().prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteRow(String table, long idCreatedRow) throws SQLException {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("DELETE FROM " + table + " WHERE id = ?")) {
            statement.setLong(1, idCreatedRow);
            int rowCount = statement.executeUpdate();
            assertEquals("must have deleted one row", 1, rowCount);
        }
    }
}