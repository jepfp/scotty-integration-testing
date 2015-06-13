package ch.adoray.scotty.integrationtest.common.entityhelper;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
public class FileMetadataHelper {
    public static long createDummyMetadataFileWithDummyFile(long liedId, String pdfResourceName) {
        long dummyMetadataFileId = createDummyMetadataFile(liedId);
        return FileHelper.createDummyFile(dummyMetadataFileId, pdfResourceName);
    }

    private static long createDummyMetadataFile(long liedId) {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("INSERT INTO filemetadata (lied_id) VALUES (?);")) {
            statement.setLong(1, liedId);
            int rowCount = statement.executeUpdate();
            assertEquals("must have created one row", 1, rowCount);
            return getGeneratedKeys(statement);
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating entry in filemetadata.", e);
        }
    }

    private static long getGeneratedKeys(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            generatedKeys.next();
            return generatedKeys.getLong(1);
        }
    }
}
