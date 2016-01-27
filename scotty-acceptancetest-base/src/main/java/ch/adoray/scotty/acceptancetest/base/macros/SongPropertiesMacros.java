package ch.adoray.scotty.acceptancetest.base.macros;

import org.openqa.selenium.WebElement;

import ch.adoray.scotty.acceptancetest.base.model.SongPropertiesModel;

import com.appfoundation.automation.framework.BaseSeleniumTest;
public class SongPropertiesMacros<T extends BaseSeleniumTest> {
    private SongPropertiesModel model;

    public SongPropertiesMacros(SongPropertiesModel model) {
        this.model = model;
    }

    public void setNumberInSongbook(int songbookRowId, String liednr) {
        WebElement inputField = model.findSongbookNumberEditFieldByRowNr(songbookRowId);
        inputField.clear();
        inputField.sendKeys(liednr);
        inputField.sendKeys("\t");
    }

    public void clickSpeichern() {
        WebElement button = model.findSpeichernButton();
        button.click();
    }
}
