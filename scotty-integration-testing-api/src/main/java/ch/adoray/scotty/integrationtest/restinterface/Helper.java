package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.entityhelper.UserHelper;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
public class Helper {
    public static Object extractAttributeValueAt(JSONArray inputData, String attributeKey, int at) throws JSONException {
        JSONObject singleEntry = (JSONObject) inputData.get(at);
        Object value = singleEntry.get(attributeKey);
        return value;
    }

    public static Object extractAttributeValueAtLast(JSONArray inputData, String attributeKey) throws JSONException {
        int lastOfArray = inputData.length() - 1;
        return Helper.extractAttributeValueAt(inputData, attributeKey, lastOfArray);
    }

    public static void addSortParameter(String property, boolean directionAsc, InteractorConfigurationWithParams config) {
        String direction = directionAsc ? "ASC" : "DESC";
        JSONArray sortArray = new JSONArray();
        sortArray.put(new JSONObject(ImmutableMap.of("property", property, "direction", direction)));
        config.addParam("sort", sortArray.toString());
    }

    public static void assertIdsInOrder(JSONArray data, long... orderOfIds) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            int id = ((JSONObject) data.get(i)).getInt("id");
            if (i >= orderOfIds.length) {
                return;
            }
            assertEquals("Wrong order of ids at position (zero based) " + i, orderOfIds[i], id);
        }
    }

    public static void addFilterParameter(Map<String, String> filters, InteractorConfigurationWithParams config) {
        JSONArray filter = new JSONArray();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            filter.put(new JSONObject(ImmutableMap.of("property", entry.getKey(), "value", entry.getValue())));
        }
        config.addParam("filter", filter.toString());
    }

    public static JSONArray extractData(String content) {
        try {
            JSONObject json = (JSONObject) JSONParser.parseJSON(content);
            JSONArray data = (JSONArray) json.get("data");
            return data;
        } catch (JSONException e) {
            throw new RuntimeException("Data part could not be extracted.");
        }
    }

    /**
     * Parses the given JSON input and removes the nodes created_at and updated_at inside the data
     * node.
     * 
     * @param input
     * @return Json String with the nodes removed.
     */
    public static String removeTimestampNodes(String input) {
        return removeInDataNode(input, "created_at", "updated_at");
    }

    private static JSONObject parseJson(String content) {
        try {
            JSONObject json = (JSONObject) JSONParser.parseJSON(content);
            return json;
        } catch (JSONException e) {
            throw new RuntimeException("Parsing json failed. Text is:\n" + content);
        }
    }

    public static String removeInDataNode(String input, String... nodeToRemove) {
        JSONObject json = parseJson(input);
        try {
            JSONArray data = (JSONArray) json.get("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject aNode = (JSONObject) data.get(i);
                for (String key : nodeToRemove) {
                    aNode.remove(key);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("No data node found inside " + input);
        }
        return json.toString();
    }

    public static JSONObject readWithFkAttributeFilter(String controller, String filterAttribute, String value) {
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/" + controller);
        Map<String, String> filter = Maps.newHashMap();
        filter.put(filterAttribute, String.valueOf(value));
        Helper.addFilterParameter(filter, config);
        JavaScriptPage result = Interactor.performRequest(config);
        JSONObject json = (JSONObject) parseJson(result.getContent());
        return json;
    }
    
    public static long determineTesterId(){
        String testerEmail = config().getTesterEmail();
        return new Long(UserHelper.getUserEntryByEmail(testerEmail).get("id"));
    }
}
