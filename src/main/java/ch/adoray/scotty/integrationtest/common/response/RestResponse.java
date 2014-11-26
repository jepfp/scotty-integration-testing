package ch.adoray.scotty.integrationtest.common.response;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;

public class RestResponse {
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SUCCESS = "success";
    private final String message;
    private final boolean success;
    private final String type;

    RestResponse(String type, boolean success, String message) {
        this.type = type;
        this.success = success;
        this.message = message;
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
    
    public static RestResponse createFromResponse(String response){
        JSONObject json = parseJson(response);
        try {
            boolean success = (boolean) json.get(KEY_SUCCESS);
            String type = (String) json.get(KEY_TYPE);
            String message = (String) json.get(KEY_MESSAGE);
            return new RestResponse(type, success, message);
        } catch (JSONException e) {
            throw new RuntimeException("Error while creating RestUnsuccessfulResponse.", e);
        }
    }
    
    private static JSONObject parseJson(String jsonString) {
        try {
            JSONObject json = (JSONObject) JSONParser.parseJSON(jsonString);
            return json;
        } catch (JSONException e) {
            throw new RuntimeException("Parsing json failed. Text is:\n" + jsonString);
        }
    }
}
