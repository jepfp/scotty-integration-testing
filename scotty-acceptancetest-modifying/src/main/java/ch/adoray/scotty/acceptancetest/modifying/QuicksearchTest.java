package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.SongModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;

import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.XPathUtils;
public class QuicksearchTest extends BaseSeleniumTest {
    private final LogInScreenModel logInScreenModel;
    private final ViewportModel viewportModel;
    private final LogInScreenMacros<QuicksearchTest> logInMacros;
    private SongModel songModel;

    public QuicksearchTest() {
        this.logInScreenModel = new LogInScreenModel(this);
        this.viewportModel = new ViewportModel(this);
        this.songModel = new SongModel(this);
        logInMacros = new LogInScreenMacros<QuicksearchTest>(this, logInScreenModel);
    }

    @Test
    public void testQuicksearch() {
        // arrange
        driver.get(config().getBaseUrl());
        logInMacros.login(config().getTesterEmail(), config().getTesterPassword());
        // act
        String songTitle = "Bless the Lord my Soul";
        assertEquals(4, viewportModel.findViewportRows().size());
        openLiedFromFirstQuicksearchResult(songTitle);
        // assert
        assertEquals(songTitle, songModel.findTitelField().getAttribute("value"));
    }

    private void openLiedFromFirstQuicksearchResult(String songTitle) {
        driver.manage().window().maximize();
        searchAndAssertOneRow(songTitle);
        viewportModel.findViewportRows().get(0).click();
        this.waitToBeClickable(ViewportModel.EDIT_BUTTON_XPATH);
        viewportModel.findEditButton().click();
        this.waitToBeClickable(SongModel.SONG_LOADED_XPATH);
    }

    private void searchAndAssertOneRow(String search) {
        WebElement quicksearchField = viewportModel.findQuicksearchField();
        quicksearchField.sendKeys(search);
        viewportModel.waitForAmountOfRowsInLiedView(1);
    }
}
