package ch.adoray.scotty.integrationtest.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
/**
 * Interactor for ext js restful GET (read operation) requests.
 *
 */
public class ExtRestGETInteractor extends ExtRestInteractor {
    protected List<NameValuePair> params = new ArrayList<NameValuePair>();
    Map<String, Object> filters = Maps.newHashMap();

    public ExtRestGETInteractor(String controller) {
        super(controller);
    }

    public ExtRestGETInteractor(String controller, Long id) {
        super(controller, id);
    }

    @Override
    HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    @Override
    void setRequestParamsAndBody(WebRequest request) {
        setRequestParametersIfNecessary(request);
    }

    private void setRequestParametersIfNecessary(WebRequest request) {
        ArrayList<NameValuePair> paramsToReturn = new ArrayList<>(params);
        if (!filters.isEmpty()) {
            paramsToReturn.add(new NameValuePair("filter", buildFilter()));
        }
        if (!paramsToReturn.isEmpty()) {
            request.setRequestParameters(paramsToReturn);
        }
    }

    public ExtRestGETInteractor addParam(String key, String value) {
        params.add(new NameValuePair(key, value));
        return this;
    }

    public ExtRestGETInteractor addFilterParam(String key, Object value) {
        filters.put(key, value);
        return this;
    }

    public List<NameValuePair> getParams() {
        return new ArrayList<>(params);
    }

    private String buildFilter() {
        JSONArray filter = new JSONArray();
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            filter.put(new JSONObject(ImmutableMap.of("property", entry.getKey(), "value", entry.getValue())));
        }
        return filter.toString();
    }
}
