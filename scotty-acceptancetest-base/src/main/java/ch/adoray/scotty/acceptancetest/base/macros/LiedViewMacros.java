package ch.adoray.scotty.acceptancetest.base.macros;

import org.openqa.selenium.WebElement;

import ch.adoray.scotty.acceptancetest.base.model.SongModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;

import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.TestUtils;
public class LiedViewMacros<T extends BaseSeleniumTest> {
    private T test;
    private ViewportModel model;

    public LiedViewMacros(T test, ViewportModel model) {
        this.test = test;
        this.model = model;
    }

    public void openLiedFromQuicksearchResult(String songTitle) {
        performQuicksearch(songTitle);
        model.findViewportRows().get(0).click();
        TestUtils.waitToBeClickable(test.getDriver(), ViewportModel.EDIT_BUTTON_XPATH);
        model.findEditButton().click();
        TestUtils.waitToBeClickable(test.getDriver(), SongModel.SONG_LOADED_XPATH);
    }

    public WebElement performQuicksearch(String songTitle) {
        test.getDriver().manage().window().maximize();
        searchAndAssertOneRow(songTitle);
        return model.findViewportRows().get(0);
    }

    private void searchAndAssertOneRow(String search) {
        WebElement quicksearchField = model.findQuicksearchField();
        quicksearchField.sendKeys(search);
        model.waitForAmountOfRowsInLiedView(1);
    }

    public void setNumberInSongbook(String liednr) {
        WebElement inputField = model.findSongbookNumberEditFieldInFirstRow();
        inputField.clear();
        inputField.sendKeys(liednr);
        inputField.sendKeys("\t");
    }

    public void clickInQuicksearchField() {
        WebElement quicksearchField = model.findQuicksearchField();
        quicksearchField.click();
    }
}
