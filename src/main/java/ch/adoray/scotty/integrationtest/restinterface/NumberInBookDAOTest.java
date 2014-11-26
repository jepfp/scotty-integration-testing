package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;

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
}
