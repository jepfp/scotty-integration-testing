package ch.adoray.scotty.integrationtest.common;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
/**
 * Interactor for ext js restful DELETE (delete operation) requests.
 *
 */
public class ExtRestDeleteInteractor extends ExtRestInteractor {
    public ExtRestDeleteInteractor(String controller, Long id) {
        super(controller, id);
    }

    @Override
    HttpMethod getHttpMethod() {
        return HttpMethod.DELETE;
    }

    @Override
    void setRequestParamsAndBody(WebRequest request) {
        // We don't need to set anything in the body
    }
}
