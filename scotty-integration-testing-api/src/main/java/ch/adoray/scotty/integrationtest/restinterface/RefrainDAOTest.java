package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestDeleteInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPUTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.RefrainHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.Page;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;
public class RefrainDAOTest {
    private static final String REFRAIN_KEY = "Refrain";
    private static final String LIED_ID_KEY = "lied_id";

    @Test
    public void read_LiedId6_oneRefrainLoaded() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/refrain");
        Map<String, String> filter = Maps.newHashMap();
        filter.put("lied_id", "6");
        Helper.addFilterParameter(filter, config);
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 1);
    }

    @Test
    public void destroy_refrain_refrainDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        long refrainIdToDelete = RefrainHelper.createRefrain(liedFixture.getId(), 100, "This is to be deleted.");
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/refrain/" + refrainIdToDelete);
        config.setMethodDelete();
        Interactor.performRequest(config);
        // assert
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.REFRAIN, refrainIdToDelete);
        assertNull("Record must not be found", record);
        // clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_withoutReihenfolge_reihenfolgeToMax() {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("refrain");
        String liedId = String.valueOf(liedFixture.getId());
        // act
        Page result = interactor//
            .setField(LIED_ID_KEY, liedId)//
            .setField(REFRAIN_KEY, "foo")//
            .performRequest();
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        // assert
        RestResponse allLiedtexts = RestResponse.createFromResponse(Helper.readWithFkAttributeFilter("refrain", "lied_id", liedId));
        List<Long> expectedOrderOfIds = liedFixture.getCreatedIdsByTable(Tables.REFRAIN);
        expectedOrderOfIds.add(response.getFirstId());
        allLiedtexts.assertIdsInOrder(Longs.toArray(expectedOrderOfIds));
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_happyCase_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("refrain");
        String refrain = "Testcase, der das Hinzufügen eines Refrains testet.";
        String liedId = String.valueOf(liedFixture.getId());
        // act
        Page result = interactor//
            .setField(REFRAIN_KEY, refrain)//
            .setField(LIED_ID_KEY, liedId)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        assertEquals(liedFixture.getId(), response.getDataValueByKeyFromFirstAsLong(LIED_ID_KEY));
        assertEquals(refrain, response.getDataValueByKeyFromFirst(REFRAIN_KEY));
        assertDbLogEntry(liedFixture.getId());
        //clean up
        liedFixture.cleanUp();
    }

    private void assertDbLogEntry(long liedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage =
            "3 ## correct@login.ch ## refrain ## INSERT INTO refrain (Refrain, lied_id, Reihenfolge) VALUES (?, ?, ?) ## sss, Testcase, der das Hinzufügen eines Refrains testet., "
                + String.valueOf(liedId);
        assertTrue("Does\n" + message + "\ncontain\n" + expectedMessage, message.contains(expectedMessage));
    }

    @Test
    public void create_withoutRefrain_error() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("refrain");
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        String liedId = String.valueOf(liedFixture.getId());
        // act
        Page result = interactor//
            .setField(REFRAIN_KEY, null)//
            .setField(LIED_ID_KEY, liedId)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        assertFalse(response.isSuccess());
        assertEquals("Fehler im Feld Refrain: Das Feld darf nicht leer sein.", response.getMessage());
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_happyCase_rowUpdated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        Long refrainIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(0);
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("refrain", refrainIdToUpdate);
        String refrain = "Geänderter Refrain";
        // act
        Page result = interactor//
            .setField(REFRAIN_KEY, refrain)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        assertEquals(refrain, response.getDataValueByKeyFromFirst(REFRAIN_KEY));
        assertEquals("lied_Id must not be changed!", liedFixture.getId(), response.getDataValueByKeyFromFirstAsLong(LIED_ID_KEY));
        assertUpdateDbLogEntry(refrainIdToUpdate);
        //clean up
        liedFixture.cleanUp();
    }

    private void assertUpdateDbLogEntry(long refrainId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## refrain ## UPDATE refrain SET Refrain= ? WHERE id = ? ## ss, Geänderter Refrain, " + refrainId;
        assertEquals(expectedMessage, message);
    }

    @Test
    public void update_idNull_error() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("refrain", null);
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        // act
        Page result = interactor//
            .setField(REFRAIN_KEY, "foo")//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        assertFalse(response.isSuccess());
        assertEquals("Allgemeiner Fehler beim Zugriff auf die Daten. Falls das Problem weiterhin auftritt, melde dich bei lieder@adoray.ch.", response.getMessage());
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_id0_error() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("refrain", new Long(0));
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        // act
        Page result = interactor//
            .setField(REFRAIN_KEY, "foo")//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getWebResponse().getContentAsString());
        assertFalse(response.isSuccess());
        assertEquals("Allgemeiner Fehler beim Zugriff auf die Daten. Falls das Problem weiterhin auftritt, melde dich bei lieder@adoray.ch.", response.getMessage());
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_updateText_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LiedHelper.setUpdatedAtToFarBehind(liedFixture.getId());
        LocalDateTime updatedAtBefore = LiedHelper.determineUpdatedAtOfLiedById(liedFixture.getId());
        String lastEditUserIdBefore = LiedHelper.determineLastEditUserId(liedFixture.getId());
        Long refrainIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(0);
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("refrain", refrainIdToUpdate);
        String refrain = "Geänderter Refrain";
        // act
        interactor//
            .setField(REFRAIN_KEY, refrain)//
            .performRequest();
        // assert
        LocalDateTime updatedAtAfter = LiedHelper.determineUpdatedAtOfLiedById(liedFixture.getId());
        assertFalse(updatedAtBefore.equals(updatedAtAfter));
        LiedHelper.assertLastUserHasChangedToCurrentTestUser(liedFixture.getId(), lastEditUserIdBefore);
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_createText_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LiedHelper.setUpdatedAtToFarBehind(liedFixture.getId());
        LocalDateTime updatedAtBefore = LiedHelper.determineUpdatedAtOfLiedById(liedFixture.getId());
        String lastEditUserIdBefore = LiedHelper.determineLastEditUserId(liedFixture.getId());
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("refrain");
        String refrain = "Because of me the field updated at of Lied should change.";
        // act
        interactor//
            .setField(REFRAIN_KEY, refrain)//
            .setField(LIED_ID_KEY, liedFixture.getId().toString())//
            .performRequest();
        // assert
        LocalDateTime updatedAtAfter = LiedHelper.determineUpdatedAtOfLiedById(liedFixture.getId());
        assertFalse(updatedAtBefore.equals(updatedAtAfter));
        LiedHelper.assertLastUserHasChangedToCurrentTestUser(liedFixture.getId(), lastEditUserIdBefore);
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void delete_deleteText_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LiedHelper.setUpdatedAtToFarBehind(liedFixture.getId());
        LocalDateTime updatedAtBefore = LiedHelper.determineUpdatedAtOfLiedById(liedFixture.getId());
        String lastEditUserIdBefore = LiedHelper.determineLastEditUserId(liedFixture.getId());
        Long refrainIdToDelete = liedFixture.getCreatedIdsByTable(Tables.REFRAIN).get(2);
        ExtRestDeleteInteractor interactor = new ExtRestDeleteInteractor("refrain", refrainIdToDelete);
        // act
        interactor.performRequest();
        // assert
        LocalDateTime updatedAtAfter = LiedHelper.determineUpdatedAtOfLiedById(liedFixture.getId());
        assertFalse(updatedAtBefore.equals(updatedAtAfter));
        LiedHelper.assertLastUserHasChangedToCurrentTestUser(liedFixture.getId(), lastEditUserIdBefore);
        //clean up
        liedFixture.removeTableIdTuple(Tables.REFRAIN, refrainIdToDelete);
        liedFixture.cleanUp();
    }
}
