package ch.adoray.scotty.acceptancetest.base.model;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.adoray.scotty.acceptancetest.base.util.AwaitFinder;

import com.appfoundation.automation.framework.BaseModel;
import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.ExtJs5XPathUtils;
import com.google.common.base.Predicate;
public class MessageBoxModel extends BaseModel {
    public MessageBoxModel(BaseSeleniumTest test) {
        super(test);
    }

    public WebElement findMessageBoxButtonByButtonText(String buttonText) {
        WebElement button = AwaitFinder.awaitFindElement(getTest().getDriver(), ExtJs5XPathUtils.findMessageBoxButtonByText(buttonText));
        return button;
    }

    public String getMessageBoxBodyTextOfOpenMessageBox() {
        WebElement body = findMessageBoxBodyOfOpenMessageBox();
        waitForTextToBeNotEmpty(body);
        return body.getText();
    }

    public WebElement findMessageBoxBodyOfOpenMessageBox() {
        return AwaitFinder.awaitFindElement(getTest().getDriver(), ExtJs5XPathUtils.findMessageBoxBody());
    }

    public void waitForTextToBeNotEmpty(WebElement element) {
        WebDriverWait wait = new WebDriverWait(getTest().getDriver(), 5);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return StringUtils.isNotEmpty(element.getText());
            }
        });
    }
}
