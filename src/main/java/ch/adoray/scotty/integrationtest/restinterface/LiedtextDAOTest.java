package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPUTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedtextHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
public class LiedtextDAOTest {
    private static final String LIED_ID_KEY = "lied_id";
    private static final String REFRAIN_ID_KEY = "refrain_id";
    private static final String STROPHE_KEY = "Strophe";

    @Test
    public void read_LiedId6_correctOrderOfReihenfolge() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedtext");
        Map<String, String> filter = Maps.newHashMap();
        filter.put("lied_id", "6");
        Helper.addFilterParameter(filter, config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 2, 1, 3, 4);
    }

    @Test
    public void destroy_liedtext_liedtextDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        long liedtextIdToDelete = LiedtextHelper.createLiedtext(liedFixture.getLiedId(), 100, "This is to be deleted.", Optional.empty());
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/liedtext/" + liedtextIdToDelete);
        config.setMethodDelete();
        Interactor.performRequest(config);
        // assert
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIEDTEXT, liedtextIdToDelete);
        assertNull("Record must not be found", record);
        // clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_withoutReihenfolge_reihenfolgeToMax() {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String liedId = String.valueOf(liedFixture.getLiedId());
        String refrainId = String.valueOf(liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(0));
        // act
        JavaScriptPage result = interactor//
            .setField(LIED_ID_KEY, liedId)//
            .setField(REFRAIN_ID_KEY, refrainId)//
            .performRequest();
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        // assert
        RestResponse allLiedtexts = RestResponse.createFromResponse(Helper.readWithFkAttributeFilter("liedtext", "lied_id", liedId));
        List<Long> expectedOrderOfIds = liedFixture.getCreatedIdsByTable(Tables.LIEDTEXT);
        expectedOrderOfIds.add(response.getFirstId());
        allLiedtexts.assertIdsInOrder(Longs.toArray(expectedOrderOfIds));
        //clean up
        liedFixture.addTableIdTuple(Tables.LIEDTEXT, response.getFirstId());
        liedFixture.cleanUp();
    }

    @Test
    public void create_happyCase_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String strophe = "Testcase, der das Hinzufügen einer Strophe ohne Verknüpfung zu einem Refrain testet.";
        String liedId = String.valueOf(liedFixture.getLiedId());
        // act
        JavaScriptPage result = interactor//
            .setField(STROPHE_KEY, strophe)//
            .setField(LIED_ID_KEY, liedId)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        assertEquals(liedFixture.getLiedId(), response.getDataValueByKeyFromFirstAsLong(LIED_ID_KEY));
        assertEquals(strophe, response.getDataValueByKeyFromFirst(STROPHE_KEY));
        assertNull(response.getDataValueByKeyFromFirst("refrain_id"));
        assertDbLogEntry(liedFixture.getLiedId());
        //clean up
        liedFixture.cleanUp();
    }

    private void assertDbLogEntry(long liedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage =
            "3 ## correct@login.ch ## liedtext ## INSERT INTO liedtext (Strophe, refrain_id, lied_id, Reihenfolge) VALUES (?, ?, ?, ?) ## ssss, Testcase, der das Hinzufügen einer Strophe ohne Verknüpfung zu einem Refrain testet., , "
                + String.valueOf(liedId);
        assertTrue("Does\n" + message + "\ncontain\n" + expectedMessage, message.contains(expectedMessage));
    }

    @Test
    public void create_noRefrainSelectedWhichMeansRefrainId0_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String refrainIdKey = "refrain_id";
        String strophe = "Testcase, der das Hinzufügen einer Strophe ohne Verknüpfung zu einem Refrain testet.";
        String liedId = String.valueOf(liedFixture.getLiedId());
        // act
        JavaScriptPage result = interactor//
            .setField(STROPHE_KEY, strophe)//
            .setField(LIED_ID_KEY, liedId)//
            .setField(refrainIdKey, "0")//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIEDTEXT, response.getFirstId());
        assertNull(record.get(refrainIdKey));
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_refrainSelected_rowCreatedWithKeyToRefrain() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String strophe = "Strophe mit Link zu Refrain.";
        String liedId = String.valueOf(liedFixture.getLiedId());
        String refrainId = String.valueOf(liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(0));
        // act
        JavaScriptPage result = interactor//
            .setField(STROPHE_KEY, strophe)//
            .setField(LIED_ID_KEY, liedId)//
            .setField(REFRAIN_ID_KEY, refrainId)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIEDTEXT, response.getFirstId());
        assertEquals(refrainId, record.get(REFRAIN_ID_KEY));
        //clean up
        liedFixture.addTableIdTuple(Tables.LIEDTEXT, response.getFirstId());
        liedFixture.cleanUp();
    }

    @Test
    public void create_refrainSelectedButNoStrophe_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String liedId = String.valueOf(liedFixture.getLiedId());
        String refrainId = String.valueOf(liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(0));
        // act
        JavaScriptPage result = interactor//
            .setField(LIED_ID_KEY, liedId)//
            .setField(REFRAIN_ID_KEY, refrainId)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        //clean up
        liedFixture.addTableIdTuple(Tables.LIEDTEXT, response.getFirstId());
        liedFixture.cleanUp();
    }

    @Test
    public void update_happyCase_rowUpdated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        Long liedtextIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.LIEDTEXT).get(3);
        Long refrainIdToSet = liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(0);
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("liedtext", liedtextIdToUpdate);
        String strophe = "Geänderte Strophe";
        // act
        JavaScriptPage result = interactor//
            .setField(STROPHE_KEY, strophe)//
            .setField(REFRAIN_ID_KEY, String.valueOf(refrainIdToSet))//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        assertEquals(strophe, response.getDataValueByKeyFromFirst(STROPHE_KEY));
        assertEquals("lied_Id must not be changed!", liedFixture.getLiedId(), response.getDataValueByKeyFromFirstAsLong(LIED_ID_KEY));
        assertEquals("refrain_id must habe changed!", refrainIdToSet, response.getDataValueByKeyFromFirstAsLong(REFRAIN_ID_KEY));
        assertUpdateDbLogEntry(liedtextIdToUpdate, refrainIdToSet);
        //clean up
        liedFixture.cleanUp();
    }

    private void assertUpdateDbLogEntry(long liedtextId, long refrainId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## liedtext ## UPDATE liedtext SET Strophe = ?, refrain_id= ? WHERE id = ? ## sss, Geänderte Strophe, " + refrainId + ", " + liedtextId;
        assertEquals(expectedMessage, message);
    }

    @Test
    public void update_refrainIdSetToNull_rowUpdatedAndRefrainIdNull() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        Long liedtextIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.LIEDTEXT).get(0);
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("liedtext", liedtextIdToUpdate);
        // act
        JavaScriptPage result = interactor//
            .setField(REFRAIN_ID_KEY, null)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        assertEquals("refrain_id must be null!", null, response.getDataValueByKeyFromFirstAsLong(REFRAIN_ID_KEY));
        //clean up
        liedFixture.cleanUp();
    }
}
