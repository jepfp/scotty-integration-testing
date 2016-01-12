package ch.adoray.scotty.integrationtest.user;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;
import ch.adoray.scotty.integrationtest.common.Tables;

import com.gargoylesoftware.htmlunit.Page;
public class ManageUserTest {
    @Test
    public void register_userAlreadyExists_error() throws Exception {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getExtDirectUrl()).setMethodPost()//
            .addParam("email", "philippjenni@bluemail.ch")//
            .addParam("password", "asdf")//
            .addParam("passwordRepeat", "asdf")//
            .addParam("adoray", "Luzern");
        addBasicParams(config);
        config.disableFailOnJsonSuccessFalse();
        Page result = Interactor.performRequest(config);
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
    }

    private void addBasicParams(InteractorConfigurationWithParams config) {
        config.addParam("extTID", "1")//
            .addParam("extAction", "ManageUser")//
            .addParam("extMethod", "register")//
            .addParam("extType", "rpc")//
            .addParam("extUpload", "false")//
            .addParam("firstname", "Philipp")//
            .addParam("lastname", "Jenni");
    }

    @Test
    public void register_passwordsDontMatch_error() throws Exception {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getExtDirectUrl())//
            .setMethodPost()//
            .addParam("email", "newUser@bluemail.ch")//
            .addParam("password", "asdff")//
            .addParam("passwordRepeat", "asdf")//
            .addParam("adoray", "Luzern");
        config.disableFailOnJsonSuccessFalse();
        addBasicParams(config);
        Page result = Interactor.performRequest(config);
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
    }

    @Test
    public void register_noEmail_error() throws Exception {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getExtDirectUrl()).setMethodPost()//
            .addParam("password", "asdf")//
            .addParam("passwordRepeat", "asdf")//
            .addParam("adoray", "Luzern");
        config.disableFailOnJsonSuccessFalse();
        addBasicParams(config);
        Page result = Interactor.performRequest(config);
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
    }

    @Test
    public void register_correctData_success() throws Exception {
        String expectedEmail = "newUserCorrect@bluemail.ch";
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getExtDirectUrl()).setMethodPost()//
            .addParam("email", expectedEmail)//
            .addParam("password", "asdf")//
            .addParam("passwordRepeat", "asdf")//
            .addParam("adoray", "Bern");
        addBasicParams(config);
        Page result = Interactor.performRequest(config);
        // assert
        JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(), false);
        Map<String, String> record = DatabaseAccess.getLastRecord("user");
        assertEquals(expectedEmail, record.get("email"));
        // clean up
        DatabaseAccess.deleteRow(Tables.USER, new Long(record.get("id")));
    }
}
