package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsAndRefrainsFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.Maps;
public class NumberInBookDAOTest {
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
        LiedWithLiedtextsAndRefrainsFixture liedFixture = new LiedWithLiedtextsAndRefrainsFixture();
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("numberInBook");
        interactor.setThrowExceptionOnFailingStatusCode(false);
        
        String liednrKey = "Liednr";
        String liedIdKey = "lied_id";
        String liederbuchIdKey = "liederbuch_id";
        String liednr = "8888";
        String liedId = String.valueOf(liedFixture.getLiedId());
        String liederbuchId = "3";
        // act
        JavaScriptPage result = interactor.setField(liednrKey, liednr)//
            .setField(liedIdKey, liedId)//
            .setField(liederbuchIdKey, liederbuchId)//
            .performRequest();
        // assert
        String testData = removeIdAndLiedId(ResourceLoader.loadTestData());
        String content = result.getContent();
        JSONAssert.assertEquals(testData, removeIdAndLiedId(content), false);
        assertDbLogEntry(liedFixture.getLiedId());
        //clean up
        liedFixture.cleanUp();
    }

    private String removeIdAndLiedId(String jsonString) {
        return Helper.removeInDataNode(Helper.removeInDataNode(jsonString, "lied_id"), "id");
    }

    private void assertDbLogEntry(long liedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getSecondLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## fkliederbuchlied ## INSERT INTO fkliederbuchlied (Liednr, lied_id, liederbuch_id) VALUES (?, ?, ?) ## sss, 8888, " + String.valueOf(liedId) + ", 3";
        assertEquals("Format correct?", expectedMessage, message);
    }
}
