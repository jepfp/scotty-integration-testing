package ch.adoray.scotty.integrationtest.common;

import com.gargoylesoftware.htmlunit.HttpMethod;
/**
 * Interactor for ext js restful POST (create operation) requests.
 *
 */
public class ExtRestPOSTInteractor extends ExtRestModificationInteractor {
    public ExtRestPOSTInteractor(String controller) {
        super(controller);
    }

    @Override
    HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }
}
