package ch.adoray.scotty.integrationtest.sqlitedbdump;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
public class SqliteDbDumpTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void downloadDatabase_wholeDb_amountOfRowsEquals() throws IOException {
        // arrange
        // act
        SqliteDatabaseAccess db = downloadDatabaseAndOpenConnection();
        // assert
        assertEqualAmountOfEntriesInTable(db, Tables.LIED);
        // cannot count fk_liederbuch_lied because the data is enriched with all associations from the other songbooks
        // assertEqualAmountOfEntriesInTable(db, Tables.FK_LIEDERBUCH_LIED);
        assertEqualAmountOfEntriesInTable(db, Tables.LANGUAGE);
        assertEqualAmountOfEntriesInTable(db, Tables.Liederbuch.TABLE);
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

    private SqliteDatabaseAccess downloadDatabaseAndOpenConnection() {
        File database = SqliteDatabaseDownloader.downloadDatabase(folder);
        SqliteDatabaseAccess db = new SqliteDatabaseAccess(database.getAbsolutePath());
        db.openConnection();
        return db;
    }
}
