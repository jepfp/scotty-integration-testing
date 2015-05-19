package ch.adoray.scotty.integrationtest.common;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
/**
 * Interactor for ext js restful GET (read operation) requests.
 *
 */
public class ExtRestGETInteractor extends ExtRestInteractor {
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
    void setRequestBody(WebRequest request) {
        // We don't need to set anything in the body
    }
}
