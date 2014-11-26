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
import ch.adoray.scotty.integrationtest.common.Tables;
public class UserHelper {
    public static long createDummyUser() throws SQLException, ClassNotFoundException {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("INSERT INTO " + Tables.USER + " (email, hash, firstname, lastname, additionalInfos, active) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, "fixtureUser@fixture.ch");
            statement.setString(2, "3da541559918a808c2402bba5012f6c60b27661c");
            statement.setString(3, "Fixture");
            statement.setString(4, "User");
            statement.setString(5, "Adorayanien");
            statement.setInt(6, 1);
            int rowCount = statement.executeUpdate();
            assertEquals("must have created one row", 1, rowCount);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        }
    }
}
