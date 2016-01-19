package ch.adoray.scotty.integrationtest.sqlitedbdump.songbooksongnumberassociation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiederbuchHelper;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
import ch.adoray.scotty.integrationtest.sqlitedbdump.SqliteDatabaseAccess;
import ch.adoray.scotty.integrationtest.sqlitedbdump.SqliteDatabaseDownloader;

import com.google.common.collect.Iterables;
public class CrossAssociationFillerTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private SqliteDatabaseAccess db;

    @Test
    public void downloadDatabase_liedWithAssociationInOneBook_foreachBookOneAssociationCreated() throws IOException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedWithNrInLuBookFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String nrInLuOrdner = "5";
        LiedHelper.addNumberInBookToLied(liedWithNrInLuBookFixture.getLiedId(), LiederbuchHelper.BOOKID_ADORAY_LUZERN, nrInLuOrdner);
        // act
        db = downloadDatabaseAndOpenConnection();
        // assert
        List<Map<String, String>> associations = findLiedNrsByLied(db, liedWithNrInLuBookFixture.getLiedId());
        int amountOfSongbooks = DatabaseAccess.selectAllFrom(Tables.Liederbuch.TABLE).size();
        assertEquals("There must be an association for each songbook", amountOfSongbooks, associations.size());
        associations.stream().allMatch(matchWithoutMnemonicForLuSongbookAndWithForOthers(nrInLuOrdner));
        // clean up
        liedWithNrInLuBookFixture.cleanUp();
    }

    private Predicate<? super Map<String, String>> matchWithoutMnemonicForLuSongbookAndWithForOthers(String nrInLuOrdner) {
        String mnemonicAdorayLuzernSongbook = LiederbuchHelper.getBookMnemonic(LiederbuchHelper.BOOKID_ADORAY_LUZERN);
        return s -> {
            long liederbuchIdOfSong = Long.parseLong(s.get(Tables.FkLiederbuchLied.LIEDERBUCH_ID));
            String liedNrOfSong = s.get(Tables.FkLiederbuchLied.LIEDNR);
            if (liederbuchIdOfSong == LiederbuchHelper.BOOKID_ADORAY_LUZERN) {
                return liedNrOfSong == nrInLuOrdner;
            } else {
                return liedNrOfSong == mnemonicAdorayLuzernSongbook + nrInLuOrdner;
            }
        };
    }

    @Test
    public void downloadDatabase_bookLuAndZgBothWithAssociations_associationsFromLuCopiedToZgAndViceVersa() throws IOException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedWithNrInLuBookFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String nrInLuOrdner = "5";
        LiedHelper.addNumberInBookToLied(liedWithNrInLuBookFixture.getLiedId(), LiederbuchHelper.BOOKID_ADORAY_LUZERN, nrInLuOrdner);
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedWithNrInZgBookFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String nrInZgOrdner = "62";
        LiedHelper.addNumberInBookToLied(liedWithNrInZgBookFixture.getLiedId(), LiederbuchHelper.BOOKID_ADONAI_ZUG, nrInZgOrdner);
        // act
        db = downloadDatabaseAndOpenConnection();
        // assert
        assertNumberInOwnBook(LiederbuchHelper.BOOKID_ADORAY_LUZERN, liedWithNrInLuBookFixture.getLiedId(), nrInLuOrdner);
        assertNumberInOwnBook(LiederbuchHelper.BOOKID_ADONAI_ZUG, liedWithNrInZgBookFixture.getLiedId(), nrInZgOrdner);
        assertNumberCopied(LiederbuchHelper.BOOKID_ADORAY_LUZERN, LiederbuchHelper.BOOKID_ADONAI_ZUG, liedWithNrInLuBookFixture.getLiedId(), nrInLuOrdner);
        assertNumberCopied(LiederbuchHelper.BOOKID_ADONAI_ZUG, LiederbuchHelper.BOOKID_ADORAY_LUZERN, liedWithNrInZgBookFixture.getLiedId(), nrInZgOrdner);
        // clean up
        liedWithNrInLuBookFixture.cleanUp();
        liedWithNrInZgBookFixture.cleanUp();
    }

    @Test
    public void downloadDatabase_liedWithoutAssociations_noAssociationsGreated() throws IOException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedWithoutAssociations = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        // act
        db = downloadDatabaseAndOpenConnection();
        // assert
        List<Map<String, String>> associations = findLiedNrsByLied(db, liedWithoutAssociations.getLiedId());
        assertTrue(associations.isEmpty());
        // clean up
        liedWithoutAssociations.cleanUp();
    }

    private void assertNumberInOwnBook(long bookId, long liedId, String number) {
        String actualNumber = findSingleLiedNrByLiedAndLiederbuch(db, bookId, liedId);
        assertEquals(number, actualNumber);
    }

    private void assertNumberCopied(long originalBookId, long otherBookId, long liedIdWithNumberInOriginalBook, String numberInOriginalBook) {
        String nrInOtherBook = findSingleLiedNrByLiedAndLiederbuch(db, otherBookId, liedIdWithNumberInOriginalBook);
        assertEquals(LiederbuchHelper.getBookMnemonic(originalBookId) + numberInOriginalBook, nrInOtherBook);
    }

    private String findSingleLiedNrByLiedAndLiederbuch(SqliteDatabaseAccess db, long liederbuchId, long liedId) {
        List<Map<String, String>> entries = findLiedNrsByLiedAndLiederbuch(db, liederbuchId, liedId);
        assertEquals("This MUST return exactly one entry as a result", 1, entries.size());
        return Iterables.getOnlyElement(entries).get(Tables.FkLiederbuchLied.LIEDNR);
    }

    private List<Map<String, String>> findLiedNrsByLiedAndLiederbuch(SqliteDatabaseAccess db, long liederbuchId, long liedId) {
        List<Map<String, String>> entries = db.executeStatement("select * from " + Tables.FK_LIEDERBUCH_LIED //
            + " where " + Tables.FkLiederbuchLied.LIEDERBUCH_ID + " = " + liederbuchId //
            + " and " + Tables.FkLiederbuchLied.LIED_ID + " = " + liedId);
        return entries;
    }

    private List<Map<String, String>> findLiedNrsByLied(SqliteDatabaseAccess db, long liedId) {
        List<Map<String, String>> entries = db.executeStatement("select * from " + Tables.FK_LIEDERBUCH_LIED //
            + " where " + Tables.FkLiederbuchLied.LIED_ID + " = " + liedId);
        return entries;
    }

    private SqliteDatabaseAccess downloadDatabaseAndOpenConnection() {
        File database = SqliteDatabaseDownloader.downloadDatabase(folder);
        SqliteDatabaseAccess db = new SqliteDatabaseAccess(database.getAbsolutePath());
        db.openConnection();
        return db;
    }
}
