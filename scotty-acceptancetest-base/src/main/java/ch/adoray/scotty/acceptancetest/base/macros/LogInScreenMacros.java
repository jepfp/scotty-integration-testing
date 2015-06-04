package ch.adoray.scotty.acceptancetest.base.macros;

import org.openqa.selenium.WebElement;

import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;

import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.TestUtils;
public class LogInScreenMacros<T extends BaseSeleniumTest> {
    private T test;
    private LogInScreenModel model;

    public LogInScreenMacros(T test, LogInScreenModel model) {
        this.test = test;
        this.model = model;
    }

    public void login(String email, String password) {
        TestUtils.waitToBeClickable(test.getDriver(), LogInScreenModel.EMAIL_XPATH);
        WebElement emailField = model.findEmail();
        WebElement passwordField = model.findPassword();
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        TestUtils.waitToBeClickable(test.getDriver(), LogInScreenModel.LOGIN_BUTTON_XPATH);
        WebElement loginButton = model.findLogInButton();
        loginButton.click();
        TestUtils.waitToBeClickable(test.getDriver(), ViewportModel.QUICKSEARCH_XPATH);
    }
}
