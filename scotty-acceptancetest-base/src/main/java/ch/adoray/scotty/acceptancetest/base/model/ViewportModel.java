package ch.adoray.scotty.acceptancetest.base.model;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.appfoundation.automation.framework.BaseModel;
import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.ExtJs5XPathUtils;
import com.appfoundation.automation.util.XPathUtils;
import com.google.common.base.Predicate;
public class ViewportModel extends BaseModel {
    public static final String QUICKSEARCH_XPATH = XPathUtils.findInputByName("quicksearch");
    public static final String LIED_VIEW_TITLE = "Integration-Testing Scotty Project - Willkommen Correct";
    public static final String EDIT_BUTTON_XPATH = ExtJs5XPathUtils.findButtonByText("Bearbeiten");

    public ViewportModel(BaseSeleniumTest test) {
        super(test);
    }

    public WebElement findQuicksearchField() {
        return this.find(QUICKSEARCH_XPATH);
    }

    public List<WebElement> findViewportRows() {
        String xpath = ExtJs5XPathUtils.findRowsInGridByHeaderText(LIED_VIEW_TITLE);
        return this.getTest().getDriver().findElements(By.xpath(xpath));
    }

    public void waitForAmountOfRowsInLiedView(int amountOfRows) {
        WebDriverWait wait = new WebDriverWait(getTest().getDriver(), 5);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return findViewportRows().size() == amountOfRows;
            }
        });
    }
    
    public WebElement findEditButton() {
        return this.find(EDIT_BUTTON_XPATH);
    }
}
