package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestPUTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Interactor.RpcInteractorConfiguration;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
public class LiedViewDAOTest {
    private static final int ID_DIR_SINGEN_WIR_2 = 2;
    private static final int ID_ADORAY_LIEDERORDNER = 1;
    private static final String LIEDNR_KEY = "Liednr";

    @Test
    public void read_showAll_amountLiedViewAndLiedEqual() throws JSONException, ClassNotFoundException, SQLException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
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
        JavaScriptPage result = viewBlessTheLord();
        assertTitleAndNumber(result, "1000");
        //
        switchToLiederbuch(ID_DIR_SINGEN_WIR_2);
        // act & assert 2
        result = viewBlessTheLord();
        assertTitleAndNumber(result, "100");
        //clean up
        Interactor.setupNewWebClient();
    }

    private JavaScriptPage viewBlessTheLord() {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview/1");
        JavaScriptPage result = Interactor.performRequest(config);
        return result;
    }

    private void switchToLiederbuch(int liederbuchId) {
        String action = "SessionInfoProvider";
        String method = "setCurrentLiederbuchId";
        RpcInteractorConfiguration rpcconfig = new RpcInteractorConfiguration(action, method)//
            .addMethodParam(liederbuchId + "");
        Interactor.performRawRequest(rpcconfig);
    }

    private void assertTitleAndNumber(JavaScriptPage result, String expectedNr) throws JSONException {
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
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
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview");
        Helper.addSortParameter("Tonality", true, config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 1, 6, 3, 2);
    }

    @Test
    public void read_orderByLiednrDesc_correctOrder() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview");
        Helper.addSortParameter("Liednr", false, config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 3, 2, 1);
    }

    @Test
    public void read_quicksearchLiednr_found1Entry() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview");
        config.addParam("quicksearch", "1001");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 2);
    }

    @Test
    public void read_quicksearchForTitelPart_found3Entries() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview");
        config.addParam("quicksearch", "le");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 6, 1, 3);
        assertEquals("Three entries must be found", 3, (int) json.get("totalCount"));
    }

    @Test
    public void read_quicksearchForHalleluja_found1Entry() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview");
        config.addParam("quicksearch", "Halleluja");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 3);
        assertEquals("1 entry must be found", 1, (int) json.get("totalCount"));
    }

    @Test
    //here especially the braces in the select statement are tested. We look for a Liednr in another liederbuch.
    public void read_quicksearch100_noEntryFound() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview");
        config.addParam("quicksearch", "100");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        assertEquals("Resultset must be empty.", 0, (int) json.get("totalCount"));
    }

    @Test
    public void destroy_liedview_liedDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = new LiedWithLiedtextsRefrainsAndNumbersInBookFixture();
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedview/" + liedFixture.getLiedId());
        config.setMethodDelete();
        Interactor.performRequest(config);
        // assert
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, liedFixture.getLiedId());
        assertNull("Record must not be found", record);
    }

    @Test
    public void update_changeExistingEntry_rowIsUpdated() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = new LiedWithLiedtextsRefrainsAndNumbersInBookFixture();
        LiedHelper.addNumberInBookToLied(liedFixture.getLiedId(), 1, "199");
        String neueLiedNr = "2997";
        changeLiedNrAndAssertNr(liedFixture, neueLiedNr);
        // assert
        assertUpdateDbLogEntry(neueLiedNr, liedFixture.getLiedId(), ID_ADORAY_LIEDERORDNER);
        //clean up
        liedFixture.cleanUp();
    }

    private void assertUpdateDbLogEntry(String liednr, long liedId, long liederbuchId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage =
            "3 ## correct@login.ch ## fkliederbuchlied ## UPDATE fkliederbuchlied SET Liednr = ?, lied_id = ?, liederbuch_id= ? WHERE lied_id = ? and liederbuch_id = ? ## sssss, " //
                + (liednr != null ? liednr : "") + ", " + liedId + ", " + liederbuchId + ", " + liedId + ", " + liederbuchId;
        assertEquals(expectedMessage, message);
    }

    @Test
    public void update_changeExistingEntrySetToNull_rowIsUpdated() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = new LiedWithLiedtextsRefrainsAndNumbersInBookFixture();
        LiedHelper.addNumberInBookToLied(liedFixture.getLiedId(), 1, "199");
        String neueLiedNr = null;
        // act
        changeLiedNrAndAssertNr(liedFixture, neueLiedNr);
        // assert
        assertUpdateDbLogEntry(neueLiedNr, liedFixture.getLiedId(), ID_ADORAY_LIEDERORDNER);
        //clean up
        liedFixture.cleanUp();
    }

    private RestResponse changeLiedNrAndAssertNr(LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture, String neueLiedNr) throws ClassNotFoundException, SQLException {
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("liedView", liedFixture.getLiedId());
        JavaScriptPage result = interactor//
            .setField(LIEDNR_KEY, neueLiedNr)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        assertEquals(neueLiedNr, response.getDataValueByKeyFromFirst(LIEDNR_KEY));
        return response;
    }

    @Test
    public void update_noEntryExists_redirectToCreateAndCreateNewEntry() throws JSONException, ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = new LiedWithLiedtextsRefrainsAndNumbersInBookFixture();
        String neueLiedNr = "8797";
        switchToLiederbuch(ID_DIR_SINGEN_WIR_2);
        changeLiedNrAndAssertNr(liedFixture, neueLiedNr);
        assertCreateDbLogEntry(neueLiedNr, liedFixture.getLiedId(), ID_DIR_SINGEN_WIR_2);
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
}
