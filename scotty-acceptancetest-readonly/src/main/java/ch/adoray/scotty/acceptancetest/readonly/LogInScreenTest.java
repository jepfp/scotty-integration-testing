package ch.adoray.scotty.acceptancetest.readonly;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.XPathUtils;

import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
public class LogInScreenTest extends BaseSeleniumTest {
    private LogInScreenModel model;

    public LogInScreenTest() {
        this.model = new LogInScreenModel(this);
    }

    @Test
    public void testLogin() {
        driver.get(config().getBaseUrl());
        login(config().getTesterEmail(), config().getTesterPassword());
    }

    private void login(String email, String password) {
        this.waitToBeClickable(LogInScreenModel.EMAIL_XPATH);
        WebElement emailField = model.findEmail();
        WebElement passwordField = model.findPassword();
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        this.waitToBeClickable(LogInScreenModel.LOGIN_BUTTON_XPATH);
        WebElement loginButton = model.findLogInButton();
        loginButton.click();
        final String quicksearch = XPathUtils.findInputByName("quicksearch");
        this.waitToBeClickable(quicksearch);
        this.outputScreenShot();
    }
}
