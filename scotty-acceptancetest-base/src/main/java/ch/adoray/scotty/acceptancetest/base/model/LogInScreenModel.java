package ch.adoray.scotty.acceptancetest.base.model;

import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseModel;
import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.ExtJs5XPathUtils;
import com.appfoundation.automation.util.XPathUtils;
public class LogInScreenModel extends BaseModel {
    public static final String EMAIL_XPATH = XPathUtils.findInputByName("email");
    public static final String PASSWORD_XPATH = XPathUtils.findInputByName("password");
    public static final String LOGIN_BUTTON_XPATH = ExtJs5XPathUtils.findButtonByText("Anmelden");

    public LogInScreenModel(BaseSeleniumTest test) {
        super(test);
    }

    public WebElement findEmail() {
        return this.find(EMAIL_XPATH);
    }

    public WebElement findPassword() {
        return this.find(PASSWORD_XPATH);
    }

    public WebElement findLogInButton() {
        return this.find(LOGIN_BUTTON_XPATH);
    }
}
