package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import ch.adoray.scotty.integrationtest.common.ExtRestDeleteInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.UserFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.Maps;
public class BaseDAOTest {
    @Test
    public void read_findAll_atLeast4Entries() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/user");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        assertTrue("There must be at least for entries for 'user'.", data.length() >= 4);
    }

    @Test
    public void read_find2nd_2ndEntryFound() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/user/3");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        assertEquals("There must be exactly one entry for 'user'.", 1, data.length());
        assertEquals("Correct-Hans", Helper.extractAttributeValueAt(data, "firstname", 0));
    }

    @Test
    public void read_filterForLuzern_3Entries() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/user");
        Map<String, String> filter = Maps.newHashMap();
        filter.put("additionalInfos", "Luzern");
        Helper.addFilterParameter(filter, config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        assertEquals("There must be exactly 2 entries for 'user'.", 2, data.length());
    }

    @Test
    public void read_filterForFirstnameAndLastname_1Entry() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/user");
        Map<String, String> filters = Maps.newHashMap();
        filters.put("firstname", "Peter");
        filters.put("lastname", "Schnur");
        Helper.addFilterParameter(filters, config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        assertEquals("There must be exactly 1 entry for 'user'.", 1, data.length());
        assertEquals("Peter", Helper.extractAttributeValueAt(data, "firstname", 0));
        assertEquals("gleicher.name@2.ch", Helper.extractAttributeValueAt(data, "email", 0));
    }

    @Test
    public void read_withoutOrder_correctOrder() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/user");
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 1, 2, 3, 4, 5);
    }

    @Test
    public void read_withoutValidLogin_securityException() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/user")//
            .disableCookies()//
            .disableFailOnJsonSuccessFalse()//
            .disableThrowExceptionOnFailingStatusCode();
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        boolean success = json.getBoolean("success");
        assertFalse("No login --> false", success);
    }

    @Test
    public void read_orderByIdDesc_correctOrder() throws JSONException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("user");
        interactor.addSortParam("id", false);
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        assertEquals("The last entry must have the id 1.", 1, restResponse.getDataValueAtByKey(restResponse.getDataLength() - 1, "id"));
    }

    @Test
    public void read_orderByLastnameAsc_correctOrder() throws JSONException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("user");
        interactor.addSortParam("lastname", true);
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        assertEquals("The first entry must have the lastname Active.", "Active", restResponse.getDataValueByKeyFromFirst("lastname"));
        assertEquals("The second entry must have the lastname Anders.", "Anders", restResponse.getDataValueAtByKey(1, "lastname"));
    }

    @Test
    public void read_orderByInvalidParameter_correctOrder() throws JSONException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("user");
        interactor.addSortParam("\" sql injection test", false);
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        assertFalse("Must return false", restResponse.isSuccess());
    }

    @Test
    public void read_start1limit1_onlyTheSecondEntry() throws JSONException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("user");
        interactor.addParam("start", "1") //
            .addParam("limit", "1");
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        assertEquals("There must be exactly 1 entry for 'user'.", 1, restResponse.getDataLength());
        assertEquals("Not", restResponse.getDataValueByKeyFromFirst("firstname"));
    }

    @Test
    public void read_countWithLimits_totalCountCorrect() throws JSONException, ClassNotFoundException, SQLException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("user");
        interactor.addParam("start", "1") //
            .addParam("limit", "1");
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        int expectedCount = DatabaseAccess.determineAmountOfEntriesInTable("user");
        assertEquals("There should be " + expectedCount + " entries in table users.", expectedCount, restResponse.getTotalCount());
    }

    @Test
    public void read_countWithoutLimits_totalCountCorrect() throws JSONException, ClassNotFoundException, SQLException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("user");
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        int expectedCount = DatabaseAccess.determineAmountOfEntriesInTable("user");
        assertEquals("There should be " + expectedCount + " entries in table users.", expectedCount, restResponse.getTotalCount());
    }

    @Test
    public void read_countWithLimitsAndWhereParam_totalCountCorrect() throws JSONException, ClassNotFoundException, SQLException {
        // act
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("user");
        interactor.addParam("start", "1") //
            .addParam("limit", "1") //
            .addFilterParam("firstname", "Peter");
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        int expectedCount = 2;
        assertEquals("There should be " + expectedCount + " entries in table users.", 2, restResponse.getTotalCount());
        assertEquals("There must be exactly 1 entry for 'user'.", 1, restResponse.getDataLength());
        assertEquals("Peter", restResponse.getDataValueByKeyFromFirst("firstname"));
        assertEquals("gleicher.name@2.ch", restResponse.getDataValueByKeyFromFirst("email"));
    }

    @Test
    public void destroy_user_userDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        UserFixture fixture = UserFixture.setupAndCreate();
        ExtRestDeleteInteractor interactor = new ExtRestDeleteInteractor("user", fixture.getId());
        // act
        interactor.performRequest();
        // assert
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.USER, fixture.getId());
        assertNull("Record must not be found", record);
        assertDbLogEntry(fixture.getId());
    }

    private void assertDbLogEntry(long userID) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getLastRecord(Tables.LOGGING);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## user ## DELETE FROM user WHERE id = " + userID + " ## ";
        assertEquals("Format correct?", expectedMessage, message);
    }

    @Test
    public void read_notExistingController_noSuccess() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/notExistingController")//
            .disableFailOnJsonSuccessFalse()//
            .disableThrowExceptionOnFailingStatusCode();
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        boolean success = json.getBoolean("success");
        assertFalse("Success must be false", success);
    }

    @Test
    public void update_logging_noSuccess() throws JSONException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/logging")//
            .disableFailOnJsonSuccessFalse()//
            .disableThrowExceptionOnFailingStatusCode();
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        boolean success = json.getBoolean("success");
        assertFalse("Success must be false", success);
    }
}
