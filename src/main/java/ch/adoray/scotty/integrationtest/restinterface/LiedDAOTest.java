package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsAndRefrainsFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
public class LiedDAOTest {
    @Test
    public void destroy_lied_liedDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        LiedWithLiedtextsAndRefrainsFixture liedFixture = new LiedWithLiedtextsAndRefrainsFixture();
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
        long idCreatedRow = LiedHelper.createDummyLied();
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
        interactor.setField("Titel", "Lied mit Zeitstempeln")//
            .setField("rubrik_id", "3")//
            .setField("tonality", "E")//
            .setField("created_at", "foo")//
            .setField("updated_at", "bar");
        interactor.setFailOnJsonSuccessFalse(false);
        RestResponse response = RestResponse.createFromResponse(interactor.performRequest().getContent());
        assertTrue(response.isSuccess());
    }

    private void assertCreatedAtAfterOrEqualsTestStartTime(Date createdAt, Date testStartTime) {
        int compareResult = createdAt.compareTo(testStartTime);
        assert (compareResult >= 0);
    }

    @Test
    public void create_happyCase_liedCreated() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("lied");
        String titelKey = "Titel";
        String rubrikIdKey = "rubrik_id";
        String tonalityKey = "tonality";
        String titel = "My Int-Testi-Song";
        String rubrikId = "3";
        String tonality = "E";
        // act
        JavaScriptPage result = interactor.setField(titelKey, titel)//
            .setField(rubrikIdKey, rubrikId)//
            .setField(tonalityKey, tonality)//
            .performRequest();
        // assert
        String testData = removeIdAndTimestamps(ResourceLoader.loadTestData());
        String content = result.getContent();
        JSONAssert.assertEquals(testData, removeIdAndTimestamps(content), false);
        Long id = new Long((int) Helper.extractAttributeValueAt(Helper.extractData(content), "id", 0));
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, id);
        assertEquals(titel, record.get(titelKey));
        assertEquals(rubrikId, record.get(rubrikIdKey));
        assertEquals(tonality, record.get(tonalityKey));
        assertDbLogEntry(id);
    }

    private String removeIdAndTimestamps(String jsonString) {
        return Helper.removeDataNode(Helper.removeTimestampNodes(jsonString), "id");
    }

    private void assertDbLogEntry(long id) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## lied ## INSERT INTO lied (Titel, rubrik_id, lastEditUser_id, tonality) VALUES (?, ?, ?, ?) ## ssss, My Int-Testi-Song, 3, 3, E";
        assertEquals("Format correct?", expectedMessage, message);
    }
}
