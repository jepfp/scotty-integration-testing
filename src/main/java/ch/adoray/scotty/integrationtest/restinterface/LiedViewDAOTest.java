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
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Interactor.RpcInteractorConfiguration;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
public class LiedViewDAOTest {
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
        switchToLiederbuch(1);
        // act & assert 1
        JavaScriptPage result = viewBlessTheLord();
        assertTitleAndNumber(result, "1000");
        //
        switchToLiederbuch(2);
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
}
