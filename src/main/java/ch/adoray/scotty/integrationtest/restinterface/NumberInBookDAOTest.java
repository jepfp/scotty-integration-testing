package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestDeleteInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPUTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper.LastUpdateAssertHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
public class NumberInBookDAOTest {
    private final static String LIEDNR_KEY = "Liednr";

    @Test
    public void read_LiedId1_correctBookNumbers() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook");
        Map<String, String> filter = Maps.newHashMap();
        filter.put("lied_id", "1");
        Helper.addFilterParameter(filter, config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getContent(), false);
    }

    @Test
    // This would be the case for a new song which should be created
    public void read_withoutLiedId_liedIdIs0ForEveryEntry() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook");
        addFilterAsExtDoes(config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getContent(), false);
    }

    // Adds the filter in the same way as ext js does: only the key is set without any "value" node.
    private void addFilterAsExtDoes(InteractorConfigurationWithParams config) {
        JSONArray filter = new JSONArray();
        filter.put(new JSONObject(ImmutableMap.of("property", "lied_id")));
        config.addParam("filter", filter.toString());
    }

    @Test
    public void read_singleEntryWithId3_correctData() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook/3");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getContent(), false);
    }

    @Test
    public void read_withNoFilterAndId_error() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook");
        config.disableFailOnJsonSuccessFalse().disableThrowExceptionOnFailingStatusCode();
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        boolean success = json.getBoolean("success");
        assertFalse("no filter and no id is set --> success must be false", success);
    }

    @Test
    public void create_happyCase_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        //act
        JavaScriptPage result = createNewNumberInBookAssociation(liedFixture);
        // assert
        String testData = removeIdAndLiedId(ResourceLoader.loadTestData());
        String content = result.getContent();
        JSONAssert.assertEquals(testData, removeIdAndLiedId(content), false);
        assertDbLogEntry(liedFixture.getId());
        //clean up
        liedFixture.cleanUp();
    }

    private JavaScriptPage createNewNumberInBookAssociation(LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture) {
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("numberInBook");
        String liedIdKey = "lied_id";
        String liederbuchIdKey = "liederbuch_id";
        String liednr = "8888";
        String liedId = String.valueOf(liedFixture.getId());
        String liederbuchId = "3";
        JavaScriptPage result = interactor.setField(LIEDNR_KEY, liednr)//
            .setField(liedIdKey, liedId)//
            .setField(liederbuchIdKey, liederbuchId)//
            .performRequest();
        return result;
    }

    private String removeIdAndLiedId(String jsonString) {
        return Helper.removeInDataNode(Helper.removeInDataNode(jsonString, "lied_id"), "id");
    }

    private void assertDbLogEntry(long liedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage =
            "3 ## correct@login.ch ## fkliederbuchlied ## INSERT INTO fkliederbuchlied (Liednr, lied_id, liederbuch_id) VALUES (?, ?, ?) ## sss, 8888, " + String.valueOf(liedId) + ", 3";
        assertEquals("Format correct?", expectedMessage, message);
    }

    @Test
    public void update_changeExistingEntry_updated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        String neueLiedNr = "12345";
        changeExistingEntry(neueLiedNr);
    }

    private void changeExistingEntry(String neueLiedNr) throws ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        liedFixture.addTwoNumberInBookAssociations();
        Long numberInBookIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.FK_LIEDERBUCH_LIED).get(0);
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("numberInBook", numberInBookIdToUpdate);
        // act
        JavaScriptPage result = interactor//
            .setField(LIEDNR_KEY, neueLiedNr)//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        assertEquals(neueLiedNr, response.getDataValueByKeyFromFirst(LIEDNR_KEY));
        assertUpdateDbLogEntry(numberInBookIdToUpdate, neueLiedNr);
        //clean up
        liedFixture.cleanUp();
    }

    private void assertUpdateDbLogEntry(long numberInBookId, String liednr) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## fkliederbuchlied ## UPDATE fkliederbuchlied SET Liednr= ? WHERE id = ? ## ss, " + (liednr != null ? liednr : "") + ", " + numberInBookId;
        assertEquals(expectedMessage, message);
    }

    @Test
    public void update_changeExistingEntrySetToNull_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        // arrange
        String neueLiedNr = null;
        changeLiedNrAndExpectError(neueLiedNr);
    }

    private void changeLiedNrAndExpectError(String neueLiedNr) {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        liedFixture.addTwoNumberInBookAssociations();
        Long numberInBookIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.FK_LIEDERBUCH_LIED).get(0);
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("numberInBook", numberInBookIdToUpdate);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        interactor.setFailOnJsonSuccessFalse(false);
        // act
        RestResponse result = interactor//
            .setField(LIEDNR_KEY, neueLiedNr)//
            .performRequestAsRestResponse();
        // assert
        assertFalse(result.isSuccess());
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_changeExistingEntrySetToEmpty_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        // arrange
        String neueLiedNr = "";
        changeLiedNrAndExpectError(neueLiedNr);
    }

    @Test
    public void update_setLiedNrNullOnDb_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture1 = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        // act & assert: expect exception
        try {
            LiedHelper.addNumberInBookToLied(liedFixture1.getId(), 1, null);
            fail("Must throw an exception");
        } catch (RuntimeException ex) {
            //clean up
            liedFixture1.cleanUp();
        }
    }

    @Test
    public void update_updateNumberToFoo_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        String newLiedNr = "foo";
        updateLiedNrAndAssertUpdatedAtOnLied(newLiedNr);
    }

    private void updateLiedNrAndAssertUpdatedAtOnLied(String newLiedNr) {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        liedFixture.addTwoNumberInBookAssociations();
        LastUpdateAssertHelper lastUpdateAssertHelper = new LiedHelper.LastUpdateAssertHelper(liedFixture.getId());
        Long numberInBookIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.FK_LIEDERBUCH_LIED).get(0);
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("numberInBook", numberInBookIdToUpdate);
        // act
        interactor.setField(LIEDNR_KEY, newLiedNr).performRequest();
        // assert
        lastUpdateAssertHelper.assertUpdatedAtChangedAndLastUserHasChangedToCurrentTestUser();
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void delete_deleteAnEntry_deletedAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        liedFixture.addTwoNumberInBookAssociations();
        LastUpdateAssertHelper lastUpdateAssertHelper = new LiedHelper.LastUpdateAssertHelper(liedFixture.getId());
        Long numberInBookIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.FK_LIEDERBUCH_LIED).get(0);
        ExtRestDeleteInteractor interactor = new ExtRestDeleteInteractor("numberInBook", numberInBookIdToUpdate);
        //act
        interactor.performRequest();
        //assert
        lastUpdateAssertHelper.assertUpdatedAtChangedAndLastUserHasChangedToCurrentTestUser();
        //clean up
        liedFixture.removeTableIdTuple(Tables.FK_LIEDERBUCH_LIED, numberInBookIdToUpdate); // test deletes it already
        liedFixture.cleanUp();
    }

    @Test
    public void create_createNewLiedNrFoo_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LastUpdateAssertHelper lastUpdateAssertHelper = new LiedHelper.LastUpdateAssertHelper(liedFixture.getId());
        // act
        createNewNumberInBookAssociation(liedFixture);
        // assert
        lastUpdateAssertHelper.assertUpdatedAtChangedAndLastUserHasChangedToCurrentTestUser();
        //clean up
        liedFixture.cleanUp();
    }
}
