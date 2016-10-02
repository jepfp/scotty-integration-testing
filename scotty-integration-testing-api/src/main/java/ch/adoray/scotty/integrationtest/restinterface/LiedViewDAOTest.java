package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPUTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Interactor.RpcInteractorConfiguration;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiederbuchHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper.LastUpdateAssertHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.Page;
public class LiedViewDAOTest {
    private static final int ID_DIR_SINGEN_WIR_2 = 2;
    private static final int ID_ADORAY_LIEDERORDNER = 1;
    private static final String LIEDNR_KEY = "Liednr";

    @Test
    public void read_showAll_amountLiedViewAndLiedEqual() throws JSONException, ClassNotFoundException, SQLException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedView");
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        boolean success = (boolean) json.get("success");
        assertTrue(success);
        int totalCount = (int) json.get("totalCount");
        int expectedCount = DatabaseAccess.determineAmountOfEntriesInTable("lied");
        assertEquals(expectedCount, totalCount);
    }

    @Test
    public void read_changeLiederbuchId_nrOfSongChanges() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        switchToLiederbuch(ID_ADORAY_LIEDERORDNER);
        // act & assert 1
        Page result = viewBlessTheLord();
        assertTitleAndNumber(result, "1000");
        //
        switchToLiederbuch(ID_DIR_SINGEN_WIR_2);
        // act & assert 2
        result = viewBlessTheLord();
        assertTitleAndNumber(result, "100");
        //clean up
        Interactor.setupNewWebClient();
    }

    private Page viewBlessTheLord() {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedView/1");
        Page result = Interactor.performRequest(config);
        return result;
    }

    private void switchToLiederbuch(int liederbuchId) {
        String action = "SessionInfoProvider";
        String method = "setCurrentLiederbuchId";
        RpcInteractorConfiguration rpcconfig = new RpcInteractorConfiguration(action, method)//
            .addMethodParam(liederbuchId + "");
        Interactor.performRawRequest(rpcconfig);
    }

    private void assertTitleAndNumber(Page result, String expectedNr) throws JSONException {
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        JSONArray data = (JSONArray) json.get("data");
        JSONObject singleEntry = (JSONObject) data.get(0);
        String title = singleEntry.getString("Titel");
        assertEquals("Bless the Lord my Soul", title);
        String nr = singleEntry.getString("Liednr");
        assertEquals(expectedNr, nr);
    }

    @Test
    public void read_orderByTonality_correctOrder() throws JSONException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("liedView");
        interactor.addSortParam("Tonality", true);
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        restResponse.assertIdsInOrder(1, 6, 3, 2);
    }

    @Test
    public void read_orderByLiednrDesc_correctOrder() throws JSONException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("liedView");
        interactor.addSortParam("Liednr", false);
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        restResponse.assertIdsInOrder(3, 2, 1);
    }

    @Test
    public void read_quicksearchLiednr_found1Entry() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedView");
        config.addParam("quicksearch", "1001");
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 2);
    }

    @Test
    public void read_quicksearchForTitelPart_found3Entries() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedView");
        config.addParam("quicksearch", "le");
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 6, 1, 3);
        assertEquals("Three entries must be found", 3, (int) json.get("totalCount"));
    }

    @Test
    public void read_quicksearchForHalleluja_found1Entry() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedView");
        config.addParam("quicksearch", "Halleluja");
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 3);
        assertEquals("1 entry must be found", 1, (int) json.get("totalCount"));
    }

    @Test
    //here especially the braces in the select statement are tested. We look for a Liednr in another liederbuch.
    public void read_quicksearch100_noEntryFound() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedView");
        config.addParam("quicksearch", "100");
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        assertEquals("Resultset must be empty.", 0, (int) json.get("totalCount"));
    }

    @Test
    public void destroy_liedview_liedDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedView/" + liedFixture.getId());
        config.setMethodDelete();
        Interactor.performRequest(config);
        // assert
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, liedFixture.getId());
        assertNull("Record must not be found", record);
    }

    @Test
    public void update_changeExistingEntry_rowIsUpdated() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        long fkLiederbuchLiedId = LiedHelper.addNumberInBookToLied(liedFixture.getId(), 1, "199");
        String neueLiedNr = "2997";
        changeLiedNrAndAssertNr(liedFixture, neueLiedNr);
        // assert
        assertUpdateDbLogEntry(neueLiedNr, liedFixture.getId(), ID_ADORAY_LIEDERORDNER, fkLiederbuchLiedId);
        //clean up
        liedFixture.cleanUp();
    }

    private void assertUpdateDbLogEntry(String liednr, long liedId, long liederbuchId, long fkLiederbuchLiedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getRecordFromLogHistory(Tables.LOGGING, 2);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## fkliederbuchlied ## UPDATE fkliederbuchlied SET Liednr = ?, lied_id = ?, liederbuch_id= ? WHERE id = ? ## ssss, " //
            + liednr + ", " + liedId + ", " + liederbuchId + ", " + fkLiederbuchLiedId;
        assertEquals(expectedMessage, message);
    }

    @Test
    public void update_changeExistingEntryToAlreadyExistingNumber_exception() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        String firstLiednr = "12";
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = createLiedWithNumber(firstLiednr);
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture2 = createLiedWithNumber("12a");
        // act
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("liedView", liedFixture2.getId());
        interactor.setFailOnJsonSuccessFalse(false);
        RestResponse result = interactor//
            .setField(LIEDNR_KEY, firstLiednr)//
            .performRequestAsRestResponse();
        // assert
        assertFalse(result.isSuccess());
        String expectedMessage = "Fehler im Feld Liednr: Die Nummer '12' ist in diesem Liederbuch bereits vergeben.";
        assertEquals(expectedMessage, result.getMessage());
        //clean up
        liedFixture.cleanUp();
        liedFixture2.cleanUp();
    }

    private LiedWithLiedtextsRefrainsAndNumbersInBookFixture createLiedWithNumber(String firstLiednr) {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LiedHelper.addNumberInBookToLied(liedFixture.getLiedId(), LiederbuchHelper.BOOKID_ADORAY_LIEDERBUCH, firstLiednr);
        return liedFixture;
    }

    @Test
    public void update_setNewNumberWhichAlreadyExists_exception() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        String firstLiednr = "12";
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = createLiedWithNumber(firstLiednr);
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture2 = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        // act
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("liedView", liedFixture2.getLiedId());
        interactor.setFailOnJsonSuccessFalse(false);
        RestResponse result = interactor//
            .setField(LIEDNR_KEY, firstLiednr)//
            .performRequestAsRestResponse();
        // assert
        assertFalse(result.isSuccess());
        String expectedMessage = "Fehler im Feld Liednr: Die Nummer '12' ist in diesem Liederbuch bereits vergeben.";
        assertEquals(expectedMessage, result.getMessage());
        //clean up
        liedFixture.cleanUp();
        liedFixture2.cleanUp();
    }

    @Test
    public void update_updateNumberToTheSameNumber_success() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        String firstLiednr = "12";
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = createLiedWithNumber(firstLiednr);
        // act
        changeLiedNrAndAssertNr(liedFixture, firstLiednr);
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_changeExistingEntrySetToNull_rowIsUpdated() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        long createdFkLiederbuchLiedId = LiedHelper.addNumberInBookToLied(liedFixture.getId(), 1, "199");
        String neueLiedNr = null;
        // act
        changeLiedNrAndAssertNr(liedFixture, neueLiedNr);
        // assert
        assertDeleteDbLogEntry(createdFkLiederbuchLiedId);
        //clean up
        liedFixture.cleanUp();
    }

    private void assertDeleteDbLogEntry(long fkLiederbuchLiedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## fkliederbuchlied ## DELETE FROM fkliederbuchlied WHERE id = " + fkLiederbuchLiedId + " ## ";
        assertEquals(expectedMessage, message);
    }

    private RestResponse changeLiedNrAndAssertNr(LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture, String neueLiedNr) {
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("liedView", liedFixture.getId());
        Page result = interactor//
            .setField(LIEDNR_KEY, neueLiedNr)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        assertEquals(neueLiedNr, response.getDataValueByKeyFromFirst(LIEDNR_KEY));
        return response;
    }

    @Test
    public void update_noEntryExistsAndNotDefaultLiederbuch_redirectToCreateAndCreateNewEntry() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String neueLiedNr = "8797";
        switchToLiederbuch(ID_DIR_SINGEN_WIR_2);
        changeLiedNrAndAssertNr(liedFixture, neueLiedNr);
        assertCreateDbLogEntry(neueLiedNr, liedFixture.getId(), ID_DIR_SINGEN_WIR_2);
        // clean up
        liedFixture.cleanUp();
        Interactor.setupNewWebClient();
    }

    private void assertCreateDbLogEntry(String liednr, long liedId, long liederbuchId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## fkliederbuchlied ## INSERT INTO fkliederbuchlied (Liednr, lied_id, liederbuch_id) VALUES (?, ?, ?) ## sss, " //
            + (liednr != null ? liednr : "") + ", " + liedId + ", " + liederbuchId;
        assertEquals(expectedMessage, message);
    }

    @Test
    public void update_updateNumberToFoo_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        liedFixture.addTwoNumberInBookAssociations();
        String newLiedNr = "1foo";
        updateLiedNrAndAssertUpdatedAtOnLied(liedFixture, newLiedNr);
    }

    private void updateLiedNrAndAssertUpdatedAtOnLied(LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture, String newLiedNr) {
        //arrange
        LastUpdateAssertHelper lastUpdateAssertHelper = new LiedHelper.LastUpdateAssertHelper(liedFixture.getId());
        changeLiedNrAndAssertNr(liedFixture, newLiedNr);
        // assert
        lastUpdateAssertHelper.assertUpdatedAtChangedAndLastUserHasChangedToCurrentTestUser();
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_updateNumberToNull_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LiedHelper.addNumberInBookToLied(liedFixture.getId(), 1, "18999");
        String newLiedNr = null;
        updateLiedNrAndAssertUpdatedAtOnLied(liedFixture, newLiedNr);
    }

    @Test
    public void create_createNewNumber_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String newLiedNr = "9000";
        updateLiedNrAndAssertUpdatedAtOnLied(liedFixture, newLiedNr);
    }

    @Test
    public void create_createNewNullNumber_error() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String newLiedNr = null;
        //act
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("liedView", liedFixture.getId());
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        Page result = interactor//
            .setField(LIEDNR_KEY, newLiedNr)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        assertFalse(response.isSuccess());
        assert (response.getMessage().contains("Fehler im Feld Liednr: Das Feld darf nicht leer sein."));
        //clean up
        liedFixture.cleanUp();
    }
}
