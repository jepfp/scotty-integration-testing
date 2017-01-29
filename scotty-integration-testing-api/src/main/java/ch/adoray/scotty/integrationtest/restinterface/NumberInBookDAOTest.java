package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
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
import ch.adoray.scotty.integrationtest.common.entityhelper.LiederbuchHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper.LastUpdateAssertHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.Page;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
public class NumberInBookDAOTest {
    private static final String LIEDERBUCH_ID_KEY = "liederbuch_id";
    private static final String LIED_ID_KEY = "lied_id";
    private final static String LIEDNR_KEY = "Liednr";

    @Test
    public void read_LiedId1_correctBookNumbers() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook");
        Map<String, String> filter = Maps.newHashMap();
        filter.put(LIED_ID_KEY, "1");
        Helper.addFilterParameter(filter, config);
        Page result = Interactor.performRequest(config);
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
    }

    @Test
    // This would be the case for a new song which should be created
    public void read_withoutLiedId_liedIdIs0ForEveryEntry() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook");
        addFilterAsExtDoes(config);
        Page result = Interactor.performRequest(config);
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
    }

    // Adds the filter in the same way as ext js does: only the key is set without any "value" node.
    private void addFilterAsExtDoes(InteractorConfigurationWithParams config) {
        JSONArray filter = new JSONArray();
        filter.put(new JSONObject(ImmutableMap.of("property", LIED_ID_KEY)));
        config.addParam("filter", filter.toString());
    }

    @Test
    public void read_singleEntryWithId3_correctData() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook/3");
        Page result = Interactor.performRequest(config);
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
    }

    @Test
    public void read_withNoFilterAndId_error() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/numberInBook");
        config.disableFailOnJsonSuccessFalse().disableThrowExceptionOnFailingStatusCode();
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        boolean success = json.getBoolean("success");
        assertFalse("no filter and no id is set --> success must be false", success);
    }

    @Test
    public void create_happyCase_rowCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        //act
        Page result = createNewNumberInBookAssociation(liedFixture.getId(), LiederbuchHelper.BOOKID_ADONAI_ZUG, "8888");
        // assert
        String testData = removeIdAndLiedId(ResourceLoader.loadTestData());
        String content = result.getWebResponse().getContentAsString();
        JSONAssert.assertEquals(testData, removeIdAndLiedId(content), false);
        assertDbLogEntry(liedFixture.getId());
        //clean up
        liedFixture.cleanUp();
    }

    private Page createNewNumberInBookAssociation(long liedId, long liederbuchId, String liednr) {
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("numberInBook");
        Page result = interactor.setField(LIEDNR_KEY, liednr)//
            .setField(LIED_ID_KEY, String.valueOf(liedId))//
            .setField(LIEDERBUCH_ID_KEY, String.valueOf(liederbuchId))//
            .performRequest();
        return result;
    }

    private String removeIdAndLiedId(String jsonString) {
        return Helper.removeInDataNode(Helper.removeInDataNode(jsonString, LIED_ID_KEY), "id");
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
        // act
        RestResponse result = performUpdateLiednrRequest(neueLiedNr, numberInBookIdToUpdate, true);
        // assert
        assertEquals(neueLiedNr, result.getDataValueByKeyFromFirst(LIEDNR_KEY));
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

    private RestResponse changeLiedNrAndExpectError(String neueLiedNr) {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        liedFixture.addTwoNumberInBookAssociations();
        Long numberInBookIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.FK_LIEDERBUCH_LIED).get(0);
        // act
        RestResponse result = performUpdateLiednrRequest(neueLiedNr, numberInBookIdToUpdate, false);
        // assert
        assertFalse(result.isSuccess());
        //clean up
        liedFixture.cleanUp();
        return result;
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
        String newLiedNr = "1foo";
        updateLiedNrAndAssertUpdatedAtOnLied(newLiedNr);
    }

    private void updateLiedNrAndAssertUpdatedAtOnLied(String newLiedNr) {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        liedFixture.addTwoNumberInBookAssociations();
        LastUpdateAssertHelper lastUpdateAssertHelper = new LiedHelper.LastUpdateAssertHelper(liedFixture.getId());
        Long numberInBookIdToUpdate = liedFixture.getCreatedIdsByTable(Tables.FK_LIEDERBUCH_LIED).get(0);
        // act
        performUpdateLiednrRequest(newLiedNr, numberInBookIdToUpdate, true);
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
        createNewNumberInBookAssociation(liedFixture.getId(), LiederbuchHelper.BOOKID_ADONAI_ZUG, "8888");
        // assert
        lastUpdateAssertHelper.assertUpdatedAtChangedAndLastUserHasChangedToCurrentTestUser();
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_changeExistingEntryToNrWithSpaces_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        // arrange
        String neueLiedNr = "38 9";
        RestResponse result = changeLiedNrAndExpectError(neueLiedNr);
        // assert
        String expectedMessage = "Fehler im Feld Liednr: Das Feld darf keine Leerzeichen enthalten.";
        assertEquals(expectedMessage, result.getMessage());
    }
    
    @Test
    public void update_changeExistingEntryToNrWithBeginningCharacter_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        // arrange
        String neueLiedNr = "Lu29";
        RestResponse result = changeLiedNrAndExpectError(neueLiedNr);
        // assert
        String expectedMessagePart = "Die Liednummer muss mit einer Zahl beginnen.";
        Assert.assertThat(result.getMessage(), CoreMatchers.containsString(expectedMessagePart));
    }

    @Test
    public void update_changeExistingEntryToAlreadyExistingNumber_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String firstLiednr = "12";
        LiedHelper.addNumberInBookToLied(liedFixture.getLiedId(), LiederbuchHelper.BOOKID_DIR_SINGEN_WIR2, firstLiednr);
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture2 = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        long numberInBookId = LiedHelper.addNumberInBookToLied(liedFixture2.getLiedId(), LiederbuchHelper.BOOKID_DIR_SINGEN_WIR2, "12a");
        RestResponse result = performUpdateLiednrRequest(firstLiednr, numberInBookId, false);
        // assert
        assertFalse(result.isSuccess());
        String expectedMessage = "Fehler im Feld Liednr: Die Nummer '12' ist in diesem Liederbuch bereits vergeben.";
        assertEquals(expectedMessage, result.getMessage());
        //clean up
        liedFixture.cleanUp();
        liedFixture2.cleanUp();
    }

    private RestResponse performUpdateLiednrRequest(String newLiednr, long numberInBookId, boolean failOnJsonSuccessFalse) {
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("numberInBook", numberInBookId);
        interactor.setFailOnJsonSuccessFalse(failOnJsonSuccessFalse);
        // act
        RestResponse result = interactor//
            .setField(LIEDNR_KEY, newLiednr)//
            .performRequestAsRestResponse();
        return result;
    }

    @Test
    public void update_setNewNumberWhichAlreadyExists_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String firstLiednr = "12";
        LiedHelper.addNumberInBookToLied(liedFixture.getLiedId(), LiederbuchHelper.BOOKID_DIR_SINGEN_WIR2, firstLiednr);
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture2 = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("numberInBook");
        interactor.setFailOnJsonSuccessFalse(false);
        // act
        RestResponse result = interactor//
            .setField(LIEDNR_KEY, firstLiednr)//
            .setField(LIED_ID_KEY, String.valueOf(liedFixture2.getLiedId()))//
            .setField(LIEDERBUCH_ID_KEY, String.valueOf(LiederbuchHelper.BOOKID_DIR_SINGEN_WIR2))//
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
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LiedHelper.addNumberInBookToLied(liedFixture.getLiedId(), LiederbuchHelper.BOOKID_ADORAY_LIEDERBUCH, firstLiednr);
        // act
        performUpdateLiednrRequest(firstLiednr, LiederbuchHelper.BOOKID_ADORAY_LIEDERBUCH, false);
        //clean up
        liedFixture.cleanUp();
    }
}
