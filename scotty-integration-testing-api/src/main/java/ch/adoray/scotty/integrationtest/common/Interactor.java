package ch.adoray.scotty.integrationtest.common;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONParser;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
public class Interactor {
    private static WebClient webClient;

    public static WebClient getWebClient() {
        if (webClient == null) {
            webClient = new WebClient();
            doLogin();
        }
        webClient.getOptions().setJavaScriptEnabled(false);
        return webClient;
    }

    public static void setupNewWebClient() {
        webClient = null;
    }

    public static Page performRequest(InteractorConfigurationWithParams config) {
        try {
            WebRequest requestSettings = new WebRequest(new URL(config.getUrl()), config.getHttpMethod());
            if (!config.getParams().isEmpty()) {
                requestSettings.setRequestParameters(config.getParams());
            }
            Page page = getPage(config, requestSettings);
            printAndValidate(page.getWebResponse().getContentAsString(), config);
            return page;
        } catch (FailingHttpStatusCodeException | IOException e) {
            throw new RuntimeException("Performing http request failed.", e);
        } finally {
            webClient.closeAllWindows();
        }
    }

    private static void printAndValidate(String asText, InteractorConfiguration config) {
        JSONObject json = parseJson(asText, config.failOnUnparsableJson);
        prettyPrintPageAsJson(json);
        validateJsonSuccessTrue(json, config.failOnJsonSuccessFalse);
    }

    public static void printAndValidate(String asText, boolean failOnUnparsableJson, boolean failOnJsonSuccessFalse) {
        JSONObject json = parseJson(asText, failOnUnparsableJson);
        prettyPrintPageAsJson(json);
        validateJsonSuccessTrue(json, failOnJsonSuccessFalse);
    }

    private static <P extends Page> P getPage(InteractorConfiguration config, WebRequest requestSettings) throws IOException {
        WebClient c = getWebClient();
        c.getOptions().setThrowExceptionOnFailingStatusCode(config.isThrowExceptionOnFailingStatusCode());
        c.getCookieManager().setCookiesEnabled(config.isEnableCookies());
        System.out.println("---New Request---");
        System.out.println(config.getUrl());
        return c.getPage(requestSettings);
    }

    public static Page performRawRequest(RpcInteractorConfiguration config) {
        try {
            WebRequest requestSettings = new WebRequest(new URL(config.getUrl()), config.getHttpMethod());
            requestSettings.setAdditionalHeader("Content-Type", "application/json");
            requestSettings.setRequestBody(config.getRequestContentBody());
            Page page = getPage(config, requestSettings);
            printAndValidate(page.getWebResponse().getContentAsString(), config);
            return page;
        } catch (Exception e) {
            throw new RuntimeException("Performing http request failed.", e);
        }
    }

    private static void validateJsonSuccessTrue(JSONObject json, boolean failOnJsonSuccessFalse) {
        if (!failOnJsonSuccessFalse) {
            //Skip the check
            return;
        }
        boolean successInRoot = false;
        boolean successInResult = false;
        try {
            successInRoot = json.getBoolean("success");
        } catch (JSONException e) {
            // we have another chance
        }
        try {
            JSONObject result = json.getJSONObject("result");
            successInResult = result.getBoolean("success");
        } catch (JSONException e) {
            // lost all chances
        }
        if (!successInRoot && !successInResult) {
            throw new RuntimeException("Success=false in JSON response from server!");
        }
    }

    private static JSONObject parseJson(String content, boolean failOnUnparsableJson) {
        try {
            JSONObject json = (JSONObject) JSONParser.parseJSON(content);
            return json;
        } catch (JSONException e) {
            createErrorHtml(content);
            System.err.println("Warning, parsing as json failed:");
            System.out.println(content);
            if (failOnUnparsableJson) {
                throw new RuntimeException("Parsing json failed. Page text is:\n" + content);
            } else {
                return null;
            }
        }
    }

    private static void createErrorHtml(String content) {
        try {
            String currentTime = SimpleDateFormat.getInstance().format(new Date());
            File file = new File("error.html");
            FileUtils.writeStringToFile(file, currentTime + "<br>" + content);
            System.err.println("Path of error.html: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Exception while writing error.html.", e);
        }
    }

    private static void prettyPrintPageAsJson(JSONObject json) {
        if (json == null) {
            return;
        }
        try {
            System.out.println(json.toString(2));
        } catch (JSONException e) {
            System.err.println("JSON pretty print failed.");
            e.printStackTrace();
        }
    }

    public static JSONObject doLogin() {
        try {
            String action = "Authentication";
            String method = "login";
            InteractorConfigurationWithParams config = new RpcFormInteractorConfiguration(action, method)//
                .addParam("email", config().getTesterEmail())//
                .addParam("password", config().getTesterPassword());
            Page result = performRequest(config);
            JSONObject json = (JSONObject) JSONParser.parseJSON(result.getWebResponse().getContentAsString());
            JSONObject loginResult = (JSONObject) json.get("result");
            String email = (String) loginResult.get("email");
            System.out.println("Logged in as " + email + ".");
            return json;
        } catch (Exception ex) {
            throw new RuntimeException("Login failed.", ex);
        }
    }

    public static DomElement findLogoutLink(HtmlPage page) {
        return page.getElementById("logoutLink");
    }

    public static class InteractorConfigurationWithParams extends InteractorConfiguration<InteractorConfigurationWithParams> {
        protected List<NameValuePair> params = new ArrayList<NameValuePair>();
        private boolean addDebugParam = true;

        public InteractorConfigurationWithParams(String url) {
            super(url);
        }

        public <T extends InteractorConfigurationWithParams> InteractorConfigurationWithParams addParam(String key, String value) {
            params.add(new NameValuePair(key, value));
            return this;
        }

        public List<NameValuePair> getParams() {
            List<NameValuePair> paramCopy = new ArrayList<>(params);
            if (addDebugParam) {
                paramCopy.add(new NameValuePair("XDEBUG_SESSION_START", "ECLIPSE_DBGP"));
            }
            return paramCopy;
        }

        public <T extends InteractorConfigurationWithParams> InteractorConfigurationWithParams removeDebugParam() {
            addDebugParam = false;
            return this;
        }
    }

    public static class InteractorConfiguration<CHILD extends InteractorConfiguration<CHILD>> {
        private final String url;
        private HttpMethod httpMethod = HttpMethod.GET;
        private boolean enableCookies = true;
        private boolean throwExceptionOnFailingStatusCode = true;
        private boolean failOnUnparsableJson = true;
        private boolean failOnJsonSuccessFalse = true;

        public InteractorConfiguration(String url) {
            this.url = url;
        }

        /**
         * Sets the Http method to PUT (this is used for an update request in terms of a restful
         * API).
         */
        @SuppressWarnings("unchecked")
        public CHILD setMethodPut() {
            setHttpMethod(HttpMethod.PUT);
            return (CHILD) this;
        }

        /**
         * Sets the Http method to POST (this is used for a create request in terms of a restful
         * API).
         */
        @SuppressWarnings("unchecked")
        public CHILD setMethodPost() {
            setHttpMethod(HttpMethod.POST);
            return (CHILD) this;
        }

        @SuppressWarnings("unchecked")
        public CHILD setMethodDelete() {
            setHttpMethod(HttpMethod.DELETE);
            return (CHILD) this;
        }

        @SuppressWarnings("unchecked")
        public CHILD disableCookies() {
            enableCookies = false;
            return (CHILD) this;
        }

        @SuppressWarnings("unchecked")
        public CHILD disableThrowExceptionOnFailingStatusCode() {
            throwExceptionOnFailingStatusCode = false;
            return (CHILD) this;
        }

        @SuppressWarnings("unchecked")
        public CHILD disableFailOnUnparsableJson() {
            failOnUnparsableJson = false;
            return (CHILD) this;
        }

        @SuppressWarnings("unchecked")
        public CHILD disableFailOnJsonSuccessFalse() {
            failOnJsonSuccessFalse = false;
            return (CHILD) this;
        }

        public String getUrl() {
            return url;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public boolean isEnableCookies() {
            return enableCookies;
        }

        public boolean isThrowExceptionOnFailingStatusCode() {
            return throwExceptionOnFailingStatusCode;
        }
    }

    public static class RpcFormInteractorConfiguration extends InteractorConfigurationWithParams {
        protected int extTID = 1;
        protected final String action;
        protected final String method;
        protected String extType = "rpc";
        protected boolean extUpload = false;

        public RpcFormInteractorConfiguration(String action, String method) {
            super(config().getExtDirectUrl());
            this.action = action;
            this.method = method;
            this.setMethodPost();
        }

        @Override
        public List<NameValuePair> getParams() {
            List<NameValuePair> copiedParams = super.getParams();
            copiedParams.add(new NameValuePair("extTID", String.valueOf(extTID)));
            copiedParams.add(new NameValuePair("extAction", action));
            copiedParams.add(new NameValuePair("extMethod", method));
            copiedParams.add(new NameValuePair("extType", extType));
            copiedParams.add(new NameValuePair("extUpload", String.valueOf(extUpload)));
            return copiedParams;
        }
    }

    public static class RpcInteractorConfiguration extends RpcFormInteractorConfiguration {
        private List<String> methodParams = new ArrayList<>();

        public RpcInteractorConfiguration(String action, String method) {
            super(action, method);
        }

        public RpcInteractorConfiguration addMethodParam(String value) {
            methodParams.add(value);
            return this;
        }

        @Override
        public <T extends InteractorConfigurationWithParams> RpcInteractorConfiguration addParam(String key, String value) {
            // todo jep: Liskov!!
            throw new IllegalStateException("RpcInteractorConfiguration only allows method params.");
        }

        public String getRequestContentBody() {
            try {
                JSONObject request = new JSONObject();
                request.put("tid", extTID);
                request.put("action", action);
                request.put("method", method);
                request.put("type", extType);
                request.put("upload", extUpload);
                request.put("data", methodParams);
                return request.toString();
            } catch (JSONException e) {
                throw new RuntimeException("Error while building request body.", e);
            }
        }

        @Override
        public List<NameValuePair> getParams() {
            // todo jep: Liskov!!
            throw new IllegalStateException("RpcInteractorConfiguration only allows getRequestContentBody().");
        }
    }
}
