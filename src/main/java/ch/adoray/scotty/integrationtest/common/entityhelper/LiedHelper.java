package ch.adoray.scotty.integrationtest.common.entityhelper;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
public class LiedHelper {
    public static Date getDateUpdatedAtOf(int liedId) {
        try {
            Map<String, String> record = DatabaseAccess.getRecordById("lied", liedId);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse((String) record.get("updated_at"));
        } catch (ParseException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getDateCreatedAtOf(long liedId) {
        try {
            Map<String, String> record = DatabaseAccess.getRecordById("lied", liedId);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse((String) record.get("created_at"));
        } catch (ParseException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the updated_at field of the lied row with the given id to 2014-09-16.
     * 
     * This method is e. g. helpful if trigger tests could run in one second and because of that the
     * assertions could fail.
     * 
     * @param liedId
     *            ID of the lied to update.
     */
    public static void setUpdatedAtToFarBehind(int liedId) {
        PreparedStatement statement = DatabaseAccess.prepareStatement("UPDATE lied SET updated_at = ? where id = ?");
        try {
            statement.setDate(1, java.sql.Date.valueOf("2014-09-16"));
            statement.setLong(2, liedId);
            int affectedRows = statement.executeUpdate();
            assertEquals("Update updated_at must have touched 1 row!", 1, affectedRows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static long createDummyLied(String titel) throws SQLException, ClassNotFoundException {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("INSERT INTO lied (Titel, rubrik_id, lastEditUser_id, tonality) VALUES (?, ?, ?, ?);")) {
            statement.setString(1, titel);
            statement.setInt(2, 3);
            statement.setInt(3, 1);
            statement.setString(4, "E");
            int rowCount = statement.executeUpdate();
            assertEquals("must have created one row", 1, rowCount);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        }
    }

    public static long addNumberInBookToLied(long liedId, long liederbuchId, String liedNr) {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("INSERT INTO fkliederbuchlied (lied_id, liederbuch_id, LiedNr) VALUES (?, ?, ?);")) {
            statement.setLong(1, liedId);
            statement.setLong(2, liederbuchId);
            statement.setString(3, liedNr);
            int rowCount = statement.executeUpdate();
            assertEquals("must have created one row", 1, rowCount);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding numberInBook association to Lied.", e);
        }
    }
}
