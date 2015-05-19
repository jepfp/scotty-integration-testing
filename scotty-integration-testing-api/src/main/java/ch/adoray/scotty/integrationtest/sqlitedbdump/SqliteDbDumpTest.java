package ch.adoray.scotty.integrationtest.sqlitedbdump;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
public class SqliteDbDumpTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void downloadDatabase_wholeDb_amountOfRowsEquals() throws IOException {
        // arrange
        // act
        SqliteDatabaseAccess db = downloadDatabaseAndOpenConnection();
        // assert
        assertEqualAmountOfEntriesInTable(db, Tables.LIED);
        assertEqualAmountOfEntriesInTable(db, Tables.FK_LIEDERBUCH_LIED);
        assertEqualAmountOfEntriesInTable(db, Tables.LANGUAGE);
        assertEqualAmountOfEntriesInTable(db, Tables.LIEDERBUCH);
        assertEqualAmountOfEntriesInTable(db, Tables.LIEDTEXT);
        assertEqualAmountOfEntriesInTable(db, Tables.REFRAIN);
        assertEqualAmountOfEntriesInTable(db, Tables.RUBRIK);
    }

    @Test
    public void downloadDatabase_characterWhichNeedToBeEscapedAndSpecialCharacters_correct() throws IOException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = new LiedWithLiedtextsRefrainsAndNumbersInBookFixture();
        String liedTitel = "Thät's a song which is very speçiale";
        liedFixture.setTitel(liedTitel);
        liedFixture.create();
        // act
        SqliteDatabaseAccess db = downloadDatabaseAndOpenConnection();
        // assert
        Map<String, String> record = db.getById(Tables.LIED, liedFixture.getId());
        assertEquals(liedTitel, record.get("Titel"));
        // clean up
        liedFixture.cleanUp();
    }

    private void assertEqualAmountOfEntriesInTable(SqliteDatabaseAccess db, String table) {
        assertEquals(DatabaseAccess.determineAmountOfEntriesInTable(table), db.getAllRecords(table).size());
    }

    private static SqliteDatabaseAccess downloadDatabaseAndOpenConnection() {
        File database = downloadDatabase();
        SqliteDatabaseAccess db = new SqliteDatabaseAccess(database.getAbsolutePath());
        db.openConnection();
        return db;
    }

    private static File downloadDatabase() {
        try {
            URL website = new URL(config().getSqliteDbDumpUrl());
            System.out.println("Download DB from " + config().getSqliteDbDumpUrl());
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File database = folder.newFile("adonai.sqlite");
            try (FileOutputStream fos = new FileOutputStream(database)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            return database;
        } catch (Exception e) {
            throw new RuntimeException("Error while downloading sqlite database.", e);
        }
    }
}
