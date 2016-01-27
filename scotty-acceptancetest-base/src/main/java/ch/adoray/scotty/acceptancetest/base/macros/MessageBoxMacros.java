package ch.adoray.scotty.acceptancetest.base.macros;

import org.junit.Assert;
import org.openqa.selenium.WebElement;

import ch.adoray.scotty.acceptancetest.base.model.MessageBoxModel;

import com.appfoundation.automation.framework.BaseSeleniumTest;
public class MessageBoxMacros<T extends BaseSeleniumTest> {
    private MessageBoxModel messageBoxModel;

    public MessageBoxMacros(T test) {
        messageBoxModel = new MessageBoxModel(test);
    }

    public void clickButtonInMessageBoxAndAssertMessage(String buttonText, String message) {
        WebElement button = messageBoxModel.findMessageBoxButtonByButtonText(buttonText);
        String actualMessage = messageBoxModel.getMessageBoxBodyTextOfOpenMessageBox();
        Assert.assertEquals(message, actualMessage);
        button.click();
    }

    public void clickButtonInMessageBox(String buttonText) {
        WebElement button = messageBoxModel.findMessageBoxButtonByButtonText(buttonText);
        button.click();
    }
}
