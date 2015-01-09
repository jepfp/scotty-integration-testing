package ch.adoray.scotty.integrationtest.common;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import ch.adoray.scotty.integrationtest.common.response.RestResponse;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.JavaScriptPage;
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

    abstract void setRequestBody(WebRequest request);

    public JavaScriptPage performRequest() {
        try {
            String requestUrl = url + (addDebugParam ? "?" + debugParamString : "");
            WebRequest request = new WebRequest(new URL(requestUrl), getHttpMethod());
            request.setAdditionalHeader("Content-Type", "application/json");
            request.setCharset("utf-8");
            setRequestBody(request);
            JavaScriptPage page = getPage(request);
            Interactor.printAndValidate(page.getContent(), failOnUnparsableJson, failOnJsonSuccessFalse);
            return page;
        } catch (Exception e) {
            throw new RuntimeException("Performing http request failed.", e);
        }
    }

    public RestResponse performRequestAsRestResponse() {
        JavaScriptPage page = this.performRequest();
        return RestResponse.createFromResponse(page.getContent());
    }

    private <P extends Page> P getPage(WebRequest request) throws IOException {
        WebClient c = Interactor.getWebClient();
        c.getOptions().setThrowExceptionOnFailingStatusCode(throwExceptionOnFailingStatusCode);
        c.getCookieManager().setCookiesEnabled(enableCookies);
        System.out.println("New " + getHttpMethod() + " request to " + controller + "/" + id.orElse("") + " (" + request.getUrl() + ")");
        return c.getPage(request);
    }

    public void setFailOnJsonSuccessFalse(boolean value) {
        this.failOnJsonSuccessFalse = value;
    }

    public void setThrowExceptionOnFailingStatusCode(boolean value) {
        this.throwExceptionOnFailingStatusCode = value;
    }
}