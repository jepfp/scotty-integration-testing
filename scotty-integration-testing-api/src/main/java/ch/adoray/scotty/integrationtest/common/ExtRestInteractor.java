package ch.adoray.scotty.integrationtest.common;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import ch.adoray.scotty.integrationtest.common.response.RestResponse;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
abstract class ExtRestInteractor {
    private final String controller;
    private final Optional<String> id;
    private final String url;
    private boolean enableCookies = true;
    private boolean throwExceptionOnFailingStatusCode = true;
    private boolean failOnUnparsableJson = true;
    private boolean failOnJsonSuccessFalse = true;
    private boolean addDebugParam = true;
    private String debugParamString = "XDEBUG_SESSION_START=XDEBUG_SESSION_START";

    public ExtRestInteractor(String controller) {
        this.controller = controller;
        this.url = config().getRestInterfaceUrl() + "/" + controller;
        this.id = Optional.empty();
    }

    public ExtRestInteractor(String controller, Long id) {
        this.controller = controller;
        this.id = Optional.of(String.valueOf(id));
        this.url = config().getRestInterfaceUrl() + "/" + controller + "/" + id;
    }

    public String getController() {
        return controller;
    }

    public Optional<String> getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    abstract HttpMethod getHttpMethod();

    abstract void setRequestParamsAndBody(WebRequest request);

    public <P extends Page> P performRequest() {
        try {
            String requestUrl = url + (addDebugParam ? "?" + debugParamString : "");
            WebRequest request = new WebRequest(new URL(requestUrl), getHttpMethod());
            request.setCharset("utf-8");
            setRequestParamsAndBody(request);
            P page = getPage(request);
            System.out.println("Response:");
            Interactor.printAndValidate(page.getWebResponse().getContentAsString(), failOnUnparsableJson, failOnJsonSuccessFalse);
            return page;
        } catch (Exception e) {
            throw new RuntimeException("Performing http request failed.", e);
        }
    }

    public RestResponse performRequestAsRestResponse() {
        Page page = this.performRequest();
        return RestResponse.createFromResponse(page.getWebResponse().getContentAsString());
    }

    private <P extends Page> P getPage(WebRequest request) throws IOException {
        WebClient c = Interactor.getWebClient();
        c.getOptions().setThrowExceptionOnFailingStatusCode(throwExceptionOnFailingStatusCode);
        c.getCookieManager().setCookiesEnabled(enableCookies);
        System.out.println("---------- " + this.getClass().getSimpleName() + " ----------");
        System.out.println("New " + getHttpMethod() + " request to " + controller + "/" + id.orElse("") + " (" + request.getUrl() + ")");
        System.out.println("Request Parameters:\n" + request.getRequestParameters());
        System.out.println("Request Body:\n" + request.getRequestBody());
        return c.getPage(request);
    }

    public void setFailOnJsonSuccessFalse(boolean value) {
        this.failOnJsonSuccessFalse = value;
    }

    public void setThrowExceptionOnFailingStatusCode(boolean value) {
        this.throwExceptionOnFailingStatusCode = value;
    }

    public void setFailOnUnparsableJson(boolean value) {
        this.failOnUnparsableJson = value;
    }
}