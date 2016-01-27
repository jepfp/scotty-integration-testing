package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Test;

import ch.adoray.scotty.acceptancetest.base.macros.LiedViewMacros;
import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.macros.MessageBoxMacros;
import ch.adoray.scotty.acceptancetest.base.macros.SongPropertiesMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.SongPropertiesModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiederbuchHelper;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.appfoundation.automation.framework.BaseSeleniumTest;
public class SongPropertiesTest extends BaseSeleniumTest {
    private final LogInScreenModel logInScreenModel;
    private final ViewportModel viewportModel;
    private final LogInScreenMacros<SongPropertiesTest> logInMacros;
    private final LiedViewMacros<SongPropertiesTest> liedViewMacros;
    private SongPropertiesModel songPropertiesModel;
    private SongPropertiesMacros<SongPropertiesTest> songPropertiesMacros;
    private MessageBoxMacros<BaseSeleniumTest> messageBoxMacros;

    public SongPropertiesTest() {
        this.logInScreenModel = new LogInScreenModel(this);
        this.viewportModel = new ViewportModel(this);
        this.songPropertiesModel = new SongPropertiesModel(this);
        this.logInMacros = new LogInScreenMacros<SongPropertiesTest>(this, logInScreenModel);
        this.liedViewMacros = new LiedViewMacros<SongPropertiesTest>(this, viewportModel);
        this.songPropertiesMacros = new SongPropertiesMacros<SongPropertiesTest>(songPropertiesModel);
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
        songPropertiesMacros.setNumberInSongbook(2, liednrWithSpace);
        songPropertiesMacros.clickSpeichern();
        messageBoxMacros.clickButtonInMessageBoxAndAssertMessage("OK", "Fehler im Feld Liednr: Das Feld darf keine Leerzeichen enthalten.");
        // clean up
        fixture.cleanUp();
    }
}
