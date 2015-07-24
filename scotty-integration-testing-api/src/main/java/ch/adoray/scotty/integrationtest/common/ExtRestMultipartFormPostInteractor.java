package ch.adoray.scotty.integrationtest.common;

import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.FormEncodingType;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
/**
 * Interactor for ext js restful POST requests with Content-Type multipart/form-data.
 *
 */
public class ExtRestMultipartFormPostInteractor extends ExtRestInteractor {
    ArrayList<NameValuePair> requestParameters = new ArrayList<>();

    public ExtRestMultipartFormPostInteractor(String controller) {
        super(controller);
    }

    public ExtRestMultipartFormPostInteractor addRequestParameter(String key, String value) {
        requestParameters.add(new NameValuePair(key, value));
        return this;
    }
    
    public ExtRestMultipartFormPostInteractor addRequestParameter(NameValuePair parameter) {
        requestParameters.add(parameter);
        return this;
    }

    @Override
    void setRequestParamsAndBody(WebRequest request) {
        request.setRequestParameters(requestParameters);
        request.setEncodingType(FormEncodingType.MULTIPART);
    }

    @Override
    HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }
}
