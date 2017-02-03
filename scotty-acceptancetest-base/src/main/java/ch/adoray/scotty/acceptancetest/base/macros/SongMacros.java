package ch.adoray.scotty.acceptancetest.base.macros;

import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.ExtJs5XPathUtils;
import com.appfoundation.automation.util.TestUtils;

import ch.adoray.scotty.acceptancetest.base.model.SongModel;
public class SongMacros<T extends BaseSeleniumTest> {
    private SongModel model;

    public SongMacros(SongModel model) {
        this.model = model;
    }

    public void setNumberInSongbook(long songbookRowId, String liednr) {
        WebElement inputField = model.findSongbookNumberEditFieldByRowNr(songbookRowId);
        inputField.clear();
        inputField.sendKeys(liednr);
        inputField.sendKeys("\t");
    }

    public void saveSong() {
        WebElement button = model.findSpeichernButton();
        button.click();
        TestUtils.waitToBeClickable(model.getTest().getDriver(), SongModel.SONG_SAVED_XPATH);
    }
    
    public void selectTonality(int row){
        model.findTonalityComboBox().click();
        waitUntilComboBoxExists();
        String xPath = ExtJs5XPathUtils.findComboBoxItemByRow(row);
        model.find(xPath).click();
        waitUntilComboBoxDoesNotExist();
    }
    
    public void setTitel(String newTitel){
        WebElement field = model.findTitelField();
        field.click();
        field.clear();
        field.sendKeys(newTitel);
    }

    private void waitUntilComboBoxExists() {
        try {
            TestUtils.waitUntilElementExists(model.getTest().getDriver(), ExtJs5XPathUtils.findComboBoxPopup());
        } catch (Exception e) {
            throw new RuntimeException("Exception while waiting for ComboBox popup.", e);
        }
    }
    
    private void waitUntilComboBoxDoesNotExist() {
        try {
            TestUtils.waitUntilElementDoesNotExist(model.getTest().getDriver(), ExtJs5XPathUtils.findComboBoxPopup());
        } catch (Exception e) {
            throw new RuntimeException("Exception while waiting for ComboBox popup.", e);
        }
    }

    public void selectRubrik(int row) {
        model.findRubrikComboBox().click();
        waitUntilComboBoxExists();
        String xPath = ExtJs5XPathUtils.findComboBoxItemByRow(row);
        model.find(xPath).click();
        waitUntilComboBoxDoesNotExist();
    }
}
