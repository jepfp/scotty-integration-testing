package ch.adoray.scotty.integrationtest.common.response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;
public class RestResponse {
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_TOTAL_COUNT = "totalCount";
    private static final String KEY_DATA = "data";
    private final String message;
    private final boolean success;
    private final String type;
    private final int totalCount;
    private final JSONArray data;

    RestResponse(String type, boolean success, String message, int totalCount, JSONArray data) {
        this.type = type;
        this.success = success;
        this.message = message;
        this.data = data;
        this.totalCount = totalCount;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getType() {
        return type;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getDataLength() {
        if (data == null) {
            throw new RuntimeException("Data attribute is null. Why did you try to determine the size?");
        }
        return data.length();
    }

    public static RestResponse createFromResponse(JSONObject json) {
        try {
            boolean success = (boolean) json.get(KEY_SUCCESS);
            String type = (String) json.get(KEY_TYPE);
            String message = (String) json.get(KEY_MESSAGE);
            int totalCount = json.isNull(KEY_TOTAL_COUNT) ? 0 : (int) json.get(KEY_TOTAL_COUNT);
            JSONArray data = json.isNull(KEY_DATA) ? null : (JSONArray) json.get(KEY_DATA);
            RestResponse r = new RestResponse(type, success, message, totalCount, data);
            return r;
        } catch (JSONException e) {
            throw new RuntimeException("Error while creating RestResponse.", e);
        }
    }

    public static RestResponse createFromResponse(String response) {
        JSONObject json = parseJson(response);
        return createFromResponse(json);
    }

    private static JSONObject parseJson(String jsonString) {
        try {
            JSONObject json = (JSONObject) JSONParser.parseJSON(jsonString);
            return json;
        } catch (JSONException e) {
            throw new RuntimeException("Parsing json failed. Text is:\n" + jsonString);
        }
    }

    public String getDataValueByKeyFromFirst(String key) {
        Object value = getDataValueAtByKey(0, key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public Long getDataValueByKeyFromFirstAsLong(String key) {
        Object valueObject = getDataValueAtByKey(0, key);
        if (valueObject == null) {
            return null;
        } else {
            return new Long((Integer) valueObject);
        }
    }

    public Object getDataValueAtByKey(int at, String key) {
        try {
            JSONObject singleEntry = (JSONObject) data.get(at);
            return singleEntry.isNull(key) ? null : singleEntry.get(key);
        } catch (JSONException e) {
            throw new RuntimeException("Error while getting key '" + key + "' at data position " + at, e);
        }
    }

    public long getIdAt(int at) {
        Integer idObject = (Integer) getDataValueAtByKey(at, "id");
        return new Long(idObject);
    }

    public long getFirstId() {
        Integer idObject = (Integer) getDataValueAtByKey(0, "id");
        return new Long(idObject);
    }

    public void assertIdsInOrder(long... orderOfIds) {
        if (orderOfIds.length > data.length()) {
            fail("Response contains only " + data.length() + " entries in data node but there are " + orderOfIds.length + " to check.");
        }
        try {
            for (int i = 0; i < data.length(); i++) {
                int id = ((JSONObject) data.get(i)).getInt("id");
                if (i >= orderOfIds.length) {
                    return;
                }
                assertEquals("Wrong order of ids at position (zero based) " + i, orderOfIds[i], id);
            }
        } catch (JSONException e) {
            throw new RuntimeException("Assert of IDs in order failed.", e);
        }
    }
}
