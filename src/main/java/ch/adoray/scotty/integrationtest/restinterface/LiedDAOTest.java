package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestPUTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
public class LiedDAOTest {
    private static final String TITEL_KEY = "Titel";
    private static final String RUBRIK_ID_KEY = "rubrik_id";
    private static final String TONALITY_KEY = "tonality";

    @Test
    public void destroy_lied_liedDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/lied/" + liedFixture.getLiedId());
        config.setMethodDelete();
        Interactor.performRequest(config);
        // assert
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, liedFixture.getLiedId());
        assertNull("Record must not be found", record);
    }

    @Test
    public void insertLied_creating_triggerSetsCreatedAt() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        Date testStartTime = new Date();
        // act
        long idCreatedRow = LiedHelper.createDummyLied("Dummy-Lied");
        // assert
        Date createdAt = LiedHelper.getDateCreatedAtOf(idCreatedRow);
        assertCreatedAtAfterOrEqualsTestStartTime(createdAt, testStartTime);
        // clean up
        DatabaseAccess.deleteRow("lied", idCreatedRow);
    }

    @Test
    public void create_createdAtAndUpdatedAtIncludedInRequest_successful() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("lied");
        // act
        interactor.setField(TITEL_KEY, "Lied mit Zeitstempeln")//
            .setField(RUBRIK_ID_KEY, "3")//
            .setField(TONALITY_KEY, "E")//
            .setField("created_at", "foo")//
            .setField("updated_at", "bar");
        interactor.setFailOnJsonSuccessFalse(false);
        RestResponse response = RestResponse.createFromResponse(interactor.performRequest().getContent());
        // assert
        assertTrue(response.isSuccess());
        // clean up
        DatabaseAccess.deleteRow(Tables.LIED, response.getFirstId());
    }

    private void assertCreatedAtAfterOrEqualsTestStartTime(Date createdAt, Date testStartTime) {
        int compareResult = createdAt.compareTo(testStartTime);
        assert (compareResult >= 0);
    }

    @Test
    public void create_happyCase_liedCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("lied");
        String titel = "My Int-Testi-Song";
        String rubrikId = "3";
        String tonality = "E";
        // act
        JavaScriptPage result = interactor.setField(TITEL_KEY, titel)//
            .setField(RUBRIK_ID_KEY, rubrikId)//
            .setField(TONALITY_KEY, tonality)//
            .performRequest();
        // assert
        String testData = removeIdAndTimestamps(ResourceLoader.loadTestData());
        String content = result.getContent();
        JSONAssert.assertEquals(testData, removeIdAndTimestamps(content), false);
        Long id = new Long((int) Helper.extractAttributeValueAt(Helper.extractData(content), "id", 0));
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, id);
        assertEquals(titel, record.get(TITEL_KEY));
        assertEquals(rubrikId, record.get(RUBRIK_ID_KEY));
        assertEquals(tonality, record.get(TONALITY_KEY));
        assertDbLogEntry();
        // clean up
        DatabaseAccess.deleteRow(Tables.LIED, id);
    }

    private String removeIdAndTimestamps(String jsonString) {
        return Helper.removeInDataNode(Helper.removeTimestampNodes(jsonString), "id");
    }

    private void assertDbLogEntry() throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## lied ## INSERT INTO lied (Titel, rubrik_id, lastEditUser_id, tonality) VALUES (?, ?, ?, ?) ## ssss, My Int-Testi-Song, 3, 3, E";
        assertEquals("Format correct?", expectedMessage, message);
    }

    @Test
    public void update_happyCase_rowUpdated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("lied", liedFixture.getLiedId());
        String titel = "Geänderter Titel";
        Integer rubrikId = 12;
        // act
        JavaScriptPage result = interactor//
            .setField(TITEL_KEY, titel)//
            .setField(RUBRIK_ID_KEY, String.valueOf(rubrikId))//
            .performRequest();
        // assert
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        assertEquals(titel, response.getDataValueByKeyFromFirst(TITEL_KEY));
        assertEquals(rubrikId, response.getDataValueByKeyFromFirst(RUBRIK_ID_KEY));
        assertUpdateDbLogEntry(liedFixture.getLiedId(), new Long(rubrikId));
        //clean up
        liedFixture.cleanUp();
    }

    private void assertUpdateDbLogEntry(Long liedId, Long rubrikId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## lied ## UPDATE lied SET Titel = ?, rubrik_id= ? WHERE id = ? ## sss, Geänderter Titel, " + rubrikId + ", " + liedId;
        assertEquals(expectedMessage, message);
    }

    @Test
    public void update_tonalityWithUtf8Character_updatedWithUtf8Character() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String tonality = "E / cis (4♯)";
        // act
        RestResponse response = changeTonality(liedFixture, tonality);
        // assert
        assertEquals(tonality, response.getDataValueByKeyFromFirst(TONALITY_KEY));
        //clean up
        liedFixture.cleanUp();
    }

    @Test
    public void update_tonalitySetToEmpty_tonalityIsNull() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String tonality = "";
        // act
        RestResponse response = changeTonality(liedFixture, tonality);
        // assert
        assertNull(response.getDataValueByKeyFromFirst(TONALITY_KEY));
        //clean up
        liedFixture.cleanUp();
    }

    private RestResponse changeTonality(LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture, String tonality) {
        ExtRestPUTInteractor interactor = new ExtRestPUTInteractor("lied", liedFixture.getLiedId());
        JavaScriptPage result = interactor//
            .setField(TONALITY_KEY, tonality)//
            .performRequest();
        RestResponse response = RestResponse.createFromResponse(result.getContent());
        return response;
    }

    @Test
    public void read_all_dataCorrect() throws Exception {
        // arrange
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("lied");
        // act
        JavaScriptPage result = interactor.performRequest();
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getContent(), false);
    }

    @Test
    public void read_withId_dataCorrect() throws Exception {
        // arrange
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("lied", (long) 6);
        // act
        JavaScriptPage result = interactor.performRequest();
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getContent(), false);
    }
}
