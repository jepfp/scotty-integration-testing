package ch.adoray.scotty.integrationtest.common.entityhelper;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Tables;
public class RefrainHelper {
   
    public static long createRefrain(long liedId, int reihenfolge, String refrain) throws SQLException, ClassNotFoundException {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("INSERT INTO " + Tables.REFRAIN + " (lied_id, Reihenfolge, Refrain, language_id) VALUES (?, ?, ?, ?);")) {
            statement.setLong(1, liedId);
            statement.setInt(2, reihenfolge);
            statement.setString(3, refrain);
            statement.setInt(4, 1);
            int rowCount = statement.executeUpdate();
            assertEquals("must have created one row", 1, rowCount);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        }
    }
}
