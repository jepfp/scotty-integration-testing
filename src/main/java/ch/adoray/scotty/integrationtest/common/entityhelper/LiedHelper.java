package ch.adoray.scotty.integrationtest.common.entityhelper;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Tables;
public class LiedHelper {
    public static Date getDateUpdatedAtOf(long liedId) {
        try {
            Map<String, String> record = DatabaseAccess.getRecordById("lied", liedId);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse((String) record.get("updated_at"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getDateCreatedAtOf(long liedId) {
        try {
            Map<String, String> record = DatabaseAccess.getRecordById("lied", liedId);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse((String) record.get("created_at"));
        } catch (ParseException e) {
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
     * @return time which has been set.
     */
    public static LocalDateTime setUpdatedAtToFarBehind(long liedId) {
        PreparedStatement statement = DatabaseAccess.prepareStatement("UPDATE lied SET updated_at = ? where id = ?");
        try {
            statement.setDate(1, java.sql.Date.valueOf("2014-09-16"));
            statement.setLong(2, liedId);
            int affectedRows = statement.executeUpdate();
            assertEquals("Update updated_at must have touched 1 row!", 1, affectedRows);
            return determineUpdatedAtOfLiedById(liedId);
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

    // TODO: remove method and use the one from above maybe better with this code.
    public static LocalDateTime determineUpdatedAtOfLiedById(Long liedId) {
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, liedId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDateTime.parse(record.get("updated_at"), formatter);
    }

    public static String getValueForIdAndColumn(Long liedId, String column) {
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, liedId);
        return record.get(column);
    }

    public static void assertLastUserHasChangedToCurrentTestUser(long liedId, String lastEditUserIdBefore) {
        String lastEditUserIdAfter = determineLastEditUserId(liedId);
        assertFalse(lastEditUserIdBefore.equals(lastEditUserIdAfter));
        String testerUserId = UserHelper.getUserEntryByEmail(config().getTesterEmail()).get("id");
        assertEquals(testerUserId, lastEditUserIdAfter);
    }

    public static String determineLastEditUserId(long liedId) {
        return getValueForIdAndColumn(liedId, "lastEditUser_id");
    }

    public static class LastUpdateAssertHelper {
        private final LocalDateTime updatedAtBefore;
        private final String lastEditUserIdBefore;
        private final long liedId;

        public LastUpdateAssertHelper(long liedId) {
            this.liedId = liedId;
            setUpdatedAtToFarBehind(liedId);
            updatedAtBefore = determineUpdatedAtOfLiedById(liedId);
            lastEditUserIdBefore = determineLastEditUserId(liedId);
        }

        public void assertUpdatedAtChangedAndLastUserHasChangedToCurrentTestUser() {
            LocalDateTime updatedAtAfter = LiedHelper.determineUpdatedAtOfLiedById(liedId);
            assertFalse(updatedAtBefore.equals(updatedAtAfter));
            LiedHelper.assertLastUserHasChangedToCurrentTestUser(liedId, lastEditUserIdBefore);
        }
    }
}
