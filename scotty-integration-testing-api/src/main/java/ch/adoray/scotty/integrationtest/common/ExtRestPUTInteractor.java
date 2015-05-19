package ch.adoray.scotty.integrationtest.common;

import com.gargoylesoftware.htmlunit.HttpMethod;
/**
 * Interactor for ext js restful PUT (update operation) requests.
 *
 */
public class ExtRestPUTInteractor extends ExtRestModificationInteractor {
    public ExtRestPUTInteractor(String controller, Long id) {
        super(controller, id);
    }

    @Override
    HttpMethod getHttpMethod() {
        return HttpMethod.PUT;
    }
}
