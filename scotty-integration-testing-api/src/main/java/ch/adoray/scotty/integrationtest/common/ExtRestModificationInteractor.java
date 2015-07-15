package ch.adoray.scotty.integrationtest.common;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.collect.Maps;
/**
 * Interactor for ext js restful POST and PUT (create and update operation) requests.
 *
 */
public abstract class ExtRestModificationInteractor extends ExtRestInteractor {
    Map<String, String> modelFields = Maps.newHashMap();

    public ExtRestModificationInteractor(String controller) {
        super(controller);
    }

    public ExtRestModificationInteractor(String controller, Long id) {
        super(controller, id);
    }

    public ExtRestModificationInteractor setField(String key, String value) {
        modelFields.put(key, value);
        return this;
    }

    @Override
    void setRequestParamsAndBody(WebRequest request) {
        request.setAdditionalHeader("Content-Type", "application/json");
        request.setRequestBody(getRequestContentBody());
    }

    private String getRequestContentBody() {
        try {
            JSONObject request = new JSONObject();
            for (Map.Entry<String, String> p : modelFields.entrySet()) {
                request.put(p.getKey(), p.getValue() != null ? p.getValue() : JSONObject.NULL);
            }
            return request.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Error while building request body.", e);
        }
    }
}
