package ch.adoray.scotty.integrationtest.sqlitedbdump;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class SqliteDatabaseAccess {
    private Connection connection;
    private String sqliteDbPath;

    public SqliteDatabaseAccess(String sqliteDbPath) {
        this.sqliteDbPath = sqliteDbPath;
    }

    public void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteDbPath);
            System.out.println("Opened sqlite database connection successfully (" + sqliteDbPath + ").");
            //        connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while opening sqlite db connection to " + sqliteDbPath, e);
        }
    }

    public List<Map<String, String>> getAllRecords(String table) {
        String sqlStatement = "select * from " + table + ";";
        try {
            return executeStatement(sqlStatement);
        } catch (Exception e) {
            throw new RuntimeException("Error while executing the following statement against sqlite database: " + sqlStatement, e);
        }
    }

    public Map<String, String> getById(String table, long id) {
        String sqlStatement = "select * from " + table + " where id = " + id + ";";
        try {
            List<Map<String, String>> result = executeStatement(sqlStatement);
            return result.size() > 0 ? result.get(0) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error while executing the following statement against sqlite database: " + sqlStatement, e);
        }
    }

    public List<Map<String, String>> executeStatement(String sqlStatement) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sqlStatement);) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, String> tuple;
            while (resultSet.next()) {
                tuple = new LinkedHashMap<String, String>();
                for (int i = 1; i <= columnCount; i++) {
                    tuple.put(metaData.getColumnLabel(i), resultSet.getString(i));
                }
                result.add(tuple);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error while executing the following statement against sqlite database: " + sqlStatement, e);
        }
    }
}