package ch.adoray.scotty.integrationtest.installationverification;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
public class ConfigurationTest {
    @Test
    public void verifySubdomain() throws Exception {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getBaseUrl() + "/integration-testing/base/subdomain/determineSubdomain.php")//
            .disableFailOnJsonSuccessFalse();
        JavaScriptPage result = Interactor.performRequest(config);
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getContent(), false);
    }

    @Test
    public void verifyProjectPath() throws Exception {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getBaseUrl() + "/integration-testing/base/determineProjectPath.php")//
            .disableFailOnJsonSuccessFalse();
        JavaScriptPage result = Interactor.performRequest(config);
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        String projectPath = (String) json.get("projectPath");
        assertTrue("Should contain data/projects/integration-testing", projectPath.matches(".*data.projects.integration-testing.*"));
    }
}
