package ch.adoray.scotty.integrationtest.session;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Interactor.RpcFormInteractorConfiguration;
import ch.adoray.scotty.integrationtest.common.Interactor.RpcInteractorConfiguration;

import com.gargoylesoftware.htmlunit.Page;
public class SessionInfoProviderTest {
    @Test
    public void getCurrentLiederbuchId_neverSet_1() throws Exception {
        Interactor.setupNewWebClient();
        int liederbuchId = requestCurrentLiederbuchId();
        assertEquals(1, liederbuchId);
    }

    private int requestCurrentLiederbuchId() throws JSONException {
        String action = "SessionInfoProvider";
        String method = "getCurrentLiederbuchId";
        InteractorConfigurationWithParams config = new RpcFormInteractorConfiguration(action, method);
        Page result = Interactor.performRequest(config);
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        int liederbuchId = json.getInt("result");
        return liederbuchId;
    }

    @Test
    public void getCurrentLiederbuchId_withoutValidLogin_securityException() throws JSONException {
        // act
        String action = "SessionInfoProvider";
        String method = "getCurrentLiederbuchId";
        InteractorConfigurationWithParams config = new RpcFormInteractorConfiguration(action, method)//
            .disableCookies().disableFailOnJsonSuccessFalse();
        Page result = Interactor.performRequest(config);
        // assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
        String type= json.getString("type");
        assertEquals("exception", type);
    }

    @Test
    public void setCurrentLiederbuchId_2_getReturns2() throws Exception {
        // act
        String action = "SessionInfoProvider";
        String method = "setCurrentLiederbuchId";
        RpcInteractorConfiguration config = new RpcInteractorConfiguration(action, method)//
            .addMethodParam("2");
        Interactor.performRawRequest(config);
        // assert
        int liederbuchId = requestCurrentLiederbuchId();
        assertEquals(2, liederbuchId);
        //clean up
        Interactor.setupNewWebClient();
    }
}
