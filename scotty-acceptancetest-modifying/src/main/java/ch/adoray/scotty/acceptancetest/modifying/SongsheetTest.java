package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Test;

import ch.adoray.scotty.acceptancetest.base.macros.LiedViewMacros;
import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.SongModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;
import ch.adoray.scotty.integrationtest.fixture.FileFixture;

import com.appfoundation.automation.framework.BaseSeleniumTest;
public class SongsheetTest extends BaseSeleniumTest {
    private final LogInScreenModel logInScreenModel;
    private final ViewportModel viewportModel;
    private final LogInScreenMacros<SongsheetTest> logInMacros;
    private final LiedViewMacros<SongsheetTest> liedViewMacros;
    private SongModel songModel;

    public SongsheetTest() {
        this.logInScreenModel = new LogInScreenModel(this);
        this.viewportModel = new ViewportModel(this);
        this.songModel = new SongModel(this);
        logInMacros = new LogInScreenMacros<SongsheetTest>(this, logInScreenModel);
        this.liedViewMacros = new LiedViewMacros<SongsheetTest>(this, viewportModel);
    }

    @Test
    public void deleteSongsheetAndVerifyNoSongsheetPresentAfterwords() {
        // arrange
        FileFixture fileFixture = FileFixture.setupAndCreate();
        driver.get(config().getBaseUrl());
        logInMacros.login(config().getTesterEmail(), config().getTesterPassword());
        String songTitle = "Lied with file";
        liedViewMacros.openLiedFromQuicksearchResult(songTitle);
        // verify download link available
        this.songModel.findSongsheetDownloadLink();
        clickDeleteSongsheetButDontConfirm();
        deleteSongsheet();
        // assert
        this.waitToBeClickable(SongModel.SONGSHEET_NO_SONGSHEET_AVAILABLE_XPATH);
        // clean up
        fileFixture.cleanUp();
    }

    private void clickDeleteSongsheetButDontConfirm() {
        this.songModel.findSongsheetDeleteButton().click();
        this.waitToBeClickable(SongModel.NO_BUTTON_IN_MESSAGE_BOX_XPATH);
        this.songModel.findNoButtonInMessageBox().click();
        this.songModel.findSongsheetDownloadLink();
    }

    private void deleteSongsheet() {
        this.songModel.findSongsheetDeleteButton().click();
        this.waitToBeClickable(SongModel.YES_BUTTON_IN_MESSAGE_BOX_XPATH);
        this.songModel.findYesButtonInMessageBox().click();
        this.waitToBeClickable(SongModel.SONGSHEET_DELETED_XPATH);
    }
}
