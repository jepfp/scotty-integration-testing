package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertNull;

import java.io.IOException;
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
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.RefrainHelper;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsAndRefrainsFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.Maps;
public class RefrainDAOTest {
    @Test
    public void read_LiedId6_oneRefrainLoaded() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/refrain");
        Map<String, String> filter = Maps.newHashMap();
        filter.put("lied_id", "6");
        Helper.addFilterParameter(filter, config);
        JavaScriptPage result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        JSONArray data = (JSONArray) json.get("data");
        Helper.assertIdsInOrder(data, 1);
    }
    
    @Test
    public void destroy_refrain_refrainDeleted() throws JSONException, ClassNotFoundException, SQLException {
        //arrange
        LiedWithLiedtextsAndRefrainsFixture liedFixture = new LiedWithLiedtextsAndRefrainsFixture();
        long refrainIdToDelete = RefrainHelper.createRefrain(liedFixture.getLiedId(), 100, "This is to be deleted.");
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
}
