package ch.adoray.scotty.acceptancetest.readonly;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Test;

import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;

import com.appfoundation.automation.framework.BaseSeleniumTest;
public class LogInScreenTest extends BaseSeleniumTest {
    private final LogInScreenModel model;
    private final LogInScreenMacros<LogInScreenTest> logInMacros;

    public LogInScreenTest() {
        this.model = new LogInScreenModel(this);
        logInMacros = new LogInScreenMacros<LogInScreenTest>(this, model);
    }

    @Test
    public void testLogin() {
        driver.get(config().getBaseUrl());
        logInMacros.login(config().getTesterEmail(), config().getTesterPassword());
    }
}
