package ch.adoray.scotty.integrationtest.installationverification;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;

import com.gargoylesoftware.htmlunit.Page;
public class ConfigurationTest {
    @Test
    @Ignore("Subdomain not configured yet in proxy")
    public void verifySubdomain() throws Exception {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getBaseUrl() + "/integration-testing/base/subdomain/determineSubdomain.php")//
            .disableFailOnJsonSuccessFalse();
        Page result = Interactor.performRequest(config);
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
    }

  
}
