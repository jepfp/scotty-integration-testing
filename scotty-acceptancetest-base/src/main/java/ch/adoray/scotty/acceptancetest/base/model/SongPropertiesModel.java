package ch.adoray.scotty.acceptancetest.base.model;

import org.openqa.selenium.WebElement;

import ch.adoray.scotty.acceptancetest.base.util.AwaitFinder;

import com.appfoundation.automation.framework.BaseModel;
import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.ExtJs5XPathUtils;
import com.appfoundation.automation.util.XPathUtils;
public class SongPropertiesModel extends BaseModel {
    public SongPropertiesModel(BaseSeleniumTest test) {
        super(test);
    }

    public WebElement findSongbookNumberCellByRowNr(int rowNr) {
        String headerTextOfSongbookNumberGrid = ""; //empty because it has no title
        String xpath = ExtJs5XPathUtils.findCellByRowAndColumnInGridByHeaderText(rowNr, 2, headerTextOfSongbookNumberGrid);
        return findElement(xpath);
    }

    public WebElement findSongbookNumberEditFieldByRowNr(int rowNr) {
        String headerTextOfSongbookNumberGrid = ""; //empty because it has no title
        String xpath = ExtJs5XPathUtils.findCellByRowAndColumnInGridByHeaderText(rowNr, 2, headerTextOfSongbookNumberGrid);
        WebElement cell = findElement(xpath);
        cell.click();
        String xpathInputField = XPathUtils.findInputByName("Liednr");
        WebElement inputField = findElement(xpathInputField);
        return inputField;
    }

    public WebElement findSpeichernButton() {
        String xpath = ExtJs5XPathUtils.findButtonByText("Speichern");
        return findElement(xpath);
    }

    private WebElement findElement(String xpath) {
        return AwaitFinder.awaitFindElement(this.getTest().getDriver(), xpath);
    }

    public WebElement waitAndFindMessageBoxWithOkButton() {
        WebElement button = findElement(ExtJs5XPathUtils.findMessageBoxButtonByText("OK"));
        return button;
    }

    public String readMessageBoxBodyTextOfOpenMessageBox() {
        WebElement body = findElement(ExtJs5XPathUtils.findMessageBoxBody());
        return body.getText();
    }
}
