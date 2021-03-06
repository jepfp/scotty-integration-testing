package ch.adoray.scotty.integrationtest.auth;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;

import com.gargoylesoftware.htmlunit.Page;

public class AuthTest {
	@Test
	public void login_wrongCredentials_noSuccess() throws Exception {
		Page result = login("philippjenni@bluemail.ch", "foo");
		JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(),
				false);
	}

	private Page login(String email, String password) {
		InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config()
				.getExtDirectUrl())//
				.addParam("email", email)//
				.addParam("password", password)//
				.setMethodPost()//
				.disableCookies();
		addBasicParams(config);
		Page result = Interactor.performRequest(config);
		return result;
	}

	private void addBasicParams(InteractorConfigurationWithParams config) {
		config.addParam("extTID", "1")//
				.addParam("extAction", "Authentication")//
				.addParam("extMethod", "login")//
				.addParam("extType", "rpc")//
				.addParam("extUpload", "false");
	}

	@Test
	public void login_notActive_noSuccess() throws Exception {
		Page result = login("notActive@google.com", "asdf");
		JSONAssert.assertEquals(ResourceLoader.loadTestData(), result.getWebResponse().getContentAsString(),
				false);
	}

	@Test
	public void login_correctLogin_firstnameCorrect() throws Exception {
		JSONObject loginResult = loginWithValidCredentials();
		String firstname = (String) loginResult.get("firstname");
		assertEquals("Correct-Hans", firstname);
	}

	private JSONObject loginWithValidCredentials() throws JSONException {
		Page result = login("correct@login.ch", "jfjf");
		JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
		JSONObject loginResult = (JSONObject) json.get("result");
		return loginResult;
	}

	@Test
	public void login_correctLogin_lastnameCorrect() throws Exception {
		JSONObject loginResult = loginWithValidCredentials();
		String lastname = (String) loginResult.get("lastname");
		assertEquals("Login-Bucher", lastname);
	}

	@Test
	public void login_correctLogin_emailCorrect() throws Exception {
		JSONObject loginResult = loginWithValidCredentials();
		String email = (String) loginResult.get("email");
		assertEquals("correct@login.ch", email);
	}

	@Test
	public void login_correctLogin_idPresent() throws Exception {
		JSONObject loginResult = loginWithValidCredentials();
		// no exception must be thrown
		loginResult.get("id");
	}
}
