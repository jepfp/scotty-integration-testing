package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseSeleniumTest;
import com.appfoundation.automation.util.TestUtils;

import ch.adoray.scotty.acceptancetest.base.macros.LiedViewMacros;
import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.macros.MessageBoxMacros;
import ch.adoray.scotty.acceptancetest.base.macros.SongMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.SongModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiederbuchHelper;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
public class SongPropertiesTest extends BaseSeleniumTest {
    private final LogInScreenModel logInScreenModel;
    private final ViewportModel viewportModel;
    private final SongModel songModel;
    private final LogInScreenMacros<SongPropertiesTest> logInMacros;
    private final LiedViewMacros<SongPropertiesTest> liedViewMacros;
    private SongMacros<SongPropertiesTest> songMacros;
    private MessageBoxMacros<BaseSeleniumTest> messageBoxMacros;

    public SongPropertiesTest() {
        this.logInScreenModel = new LogInScreenModel(this);
        this.viewportModel = new ViewportModel(this);
        this.songModel = new SongModel(this);
        this.logInMacros = new LogInScreenMacros<SongPropertiesTest>(this, logInScreenModel);
        this.liedViewMacros = new LiedViewMacros<SongPropertiesTest>(this, viewportModel);
        this.songMacros = new SongMacros<SongPropertiesTest>(songModel);
        messageBoxMacros = new MessageBoxMacros<BaseSeleniumTest>(this);
    }

    @Test
    public void lied_changeLiednrToNrWithSpaces_exception() {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture fixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String firstLiednr = "15a";
        LiedHelper.addNumberInBookToLied(fixture.getLiedId(), LiederbuchHelper.BOOKID_DIR_SINGEN_WIR2, firstLiednr);
        driver.get(config().getBaseUrl());
        logInMacros.login(config().getTesterEmail(), config().getTesterPassword());
        liedViewMacros.openLiedFromQuicksearchResult(fixture.getTitel());
        // act & assert
        String liednrWithSpace = "15 3";
        songMacros.setNumberInSongbook(2, liednrWithSpace);
        clickSave();
        messageBoxMacros.clickButtonInMessageBoxAndAssertMessage("OK", "Fehler im Feld Liednr: Das Feld darf keine Leerzeichen enthalten.");
        // clean up
        fixture.cleanUp();
    }
    
    private void clickSave() {
        WebElement button = songModel.findSpeichernButton();
        button.click();
    }
}
