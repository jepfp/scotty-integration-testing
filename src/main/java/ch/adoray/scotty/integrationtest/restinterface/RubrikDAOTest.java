package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;

import com.gargoylesoftware.htmlunit.JavaScriptPage;

public class RubrikDAOTest {

	@Test
	public void read_queryAll_12Entries() throws JSONException {
		// act
		InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config()
				.getRestInterfaceUrl() + "/rubrik");
		JavaScriptPage result = Interactor.performRequest(config);
		// assert
		JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
		JSONArray data = (JSONArray) json.get("data");
		assertTrue("There must be 12 entries for 'rubrik'.",
				data.length() >= 12);
	}

}
