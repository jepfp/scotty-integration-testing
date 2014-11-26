package ch.adoray.scotty.integrationtest.common;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.collect.Maps;
/**
 * Interactor for ext js restful POST (create operation) requests.
 *
 */
public class ExtRestPOSTInteractor extends ExtRestInteractor {
    Map<String, String> modelFields = Maps.newHashMap();

    public ExtRestPOSTInteractor(String controller) {
        super(controller);
    }

    public ExtRestPOSTInteractor setField(String key, String value) {
        modelFields.put(key, value);
        return this;
    }

    @Override
    HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    void setRequestBody(WebRequest request) {
        request.setRequestBody(getRequestContentBody());
    }

    private String getRequestContentBody() {
        try {
            JSONObject request = new JSONObject();
            for (Map.Entry<String, String> p : modelFields.entrySet()) {
                request.put(p.getKey(), p.getValue());
            }
            return request.toString();
        } catch (JSONException e) {
            throw new RuntimeException("Error while building request body.", e);
        }
    }
}
