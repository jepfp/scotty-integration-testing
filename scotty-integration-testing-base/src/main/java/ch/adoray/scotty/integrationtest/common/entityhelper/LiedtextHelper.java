package ch.adoray.scotty.integrationtest.common.entityhelper;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Tables;
public class LiedtextHelper {
    public static long createLiedtext(long liedId, int reihenfolge, String strophe, Optional<Long> refrainId) throws SQLException, ClassNotFoundException {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("INSERT INTO " + Tables.LIEDTEXT + " (lied_id, Reihenfolge, strophe, refrain_id, language_id) VALUES (?, ?, ?, ?, ?);")) {
            statement.setLong(1, liedId);
            statement.setInt(2, reihenfolge);
            statement.setString(3, strophe);
            if (refrainId.isPresent()) {
                statement.setLong(4, refrainId.get());
            } else {
                statement.setNull(4, Types.BIGINT);
            }
            statement.setInt(5, 1);
            int rowCount = statement.executeUpdate();
            assertEquals("must have created one row", 1, rowCount);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        }
    }
}
