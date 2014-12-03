package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedtextHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsAndRefrainsFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.Maps;
public class LiedtextDAOTest {
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
        LiedWithLiedtextsAndRefrainsFixture liedFixture = new LiedWithLiedtextsAndRefrainsFixture();
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
    @Ignore
    public void insertLiedtext_insertLiedtextWithoutReihenfolgeWithOtherLiedtexts_triggerSetsReihenfolgeToMax() {
        fail("Implement");
    }

    @Test
    @Ignore
    public void insertLiedtext_insertLiedtextWithoutReihenfolgeWithoutOtherLiedtexts_triggerSetsReihenfolgeTo1() {
        fail("Implement");
    }

    @Test
    @Ignore
    public void insertLiedtext_insertLiedtextWithReihenfolge_triggerDoesntChangeReihenfolge() {
        fail("Implement");
    }

    @Test
    public void create_happyCase_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsAndRefrainsFixture liedFixture = new LiedWithLiedtextsAndRefrainsFixture();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String stropheKey = "Strophe";
        String liedIdKey = "lied_id";
        String strophe = "Testcase, der das Hinzufügen einer Strophe ohne Verknüpfung zu einem Refrain testet.";
        String liedId = String.valueOf(liedFixture.getLiedId());
        // act
        JavaScriptPage result = interactor//
            .setField(stropheKey, strophe)//
            .setField(liedIdKey, liedId)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        assertEquals(liedFixture.getLiedId(), response.getDataValueByKeyFromFirstAsLong(liedIdKey));
        assertEquals(strophe, response.getDataValueByKeyFromFirst(stropheKey));
        assertNull(response.getDataValueByKeyFromFirst("refrain_id"));
        assertDbLogEntry(liedFixture.getLiedId());
        //clean up
        liedFixture.cleanUp();
    }

    private void assertDbLogEntry(long liedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage =
            "3 ## correct@login.ch ## liedtext ## INSERT INTO liedtext (Strophe, refrain_id, lied_id) VALUES (?, ?, ?) ## sss, Testcase, der das Hinzufügen einer Strophe ohne Verknüpfung zu einem Refrain testet., , "
                + String.valueOf(liedId);
        assertEquals("Format correct?", expectedMessage, message);
    }

    @Test
    public void create_noRefrainSelectedWhichMeansRefrainId0_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsAndRefrainsFixture liedFixture = new LiedWithLiedtextsAndRefrainsFixture();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String stropheKey = "Strophe";
        String liedIdKey = "lied_id";
        String refrainIdKey = "refrain_id";
        String strophe = "Testcase, der das Hinzufügen einer Strophe ohne Verknüpfung zu einem Refrain testet.";
        String liedId = String.valueOf(liedFixture.getLiedId());
        // act
        JavaScriptPage result = interactor//
            .setField(stropheKey, strophe)//
            .setField(liedIdKey, liedId)//
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
        LiedWithLiedtextsAndRefrainsFixture liedFixture = new LiedWithLiedtextsAndRefrainsFixture();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("liedtext");
        String stropheKey = "Strophe";
        String liedIdKey = "lied_id";
        String refrainIdKey = "refrain_id";
        String strophe = "Strophe mit Link zu Refrain.";
        String liedId = String.valueOf(liedFixture.getLiedId());
        String refrainId = String.valueOf(liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(0));
        // act
        JavaScriptPage result = interactor//
            .setField(stropheKey, strophe)//
            .setField(liedIdKey, liedId)//
            .setField(refrainIdKey, refrainId)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIEDTEXT, response.getFirstId());
        assertEquals(refrainId, record.get(refrainIdKey));
        //clean up
        liedFixture.addTableIdTuple(Tables.LIEDTEXT, response.getFirstId());
        liedFixture.cleanUp();
    }
}
