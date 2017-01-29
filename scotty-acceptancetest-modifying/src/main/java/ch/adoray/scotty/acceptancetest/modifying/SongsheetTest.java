package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.appfoundation.automation.framework.BaseSeleniumTest;

import ch.adoray.scotty.acceptancetest.base.macros.LiedViewMacros;
import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.macros.SongMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.SongModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;
import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.entityhelper.FileHelper;
import ch.adoray.scotty.integrationtest.fixture.FileFixture;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
public class SongsheetTest extends BaseSeleniumTest {
    private final LogInScreenModel logInScreenModel;
    private final ViewportModel viewportModel;
    private final LogInScreenMacros<SongsheetTest> logInMacros;
    private final LiedViewMacros<SongsheetTest> liedViewMacros;
    private SongModel songModel;
    private SongMacros<SongsheetTest> songMacros;

    public SongsheetTest() {
        this.logInScreenModel = new LogInScreenModel(this);
        this.viewportModel = new ViewportModel(this);
        this.songModel = new SongModel(this);
        songMacros = new SongMacros<SongsheetTest>(songModel);
        logInMacros = new LogInScreenMacros<SongsheetTest>(this, logInScreenModel);
        this.liedViewMacros = new LiedViewMacros<SongsheetTest>(this, viewportModel);
    }

    @Test
    public void addSongsheetAndVerifyPresentAfterwords() {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture fixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        driver.get(config().getBaseUrl());
        logInMacros.login(config().getTesterEmail(), config().getTesterPassword());
        liedViewMacros.openLiedFromQuicksearchResult(fixture.getTitel());
        this.songModel.findNoSongsheetAvailable();
        // act & assert
        uploadFile();
        // clean up
        fixture.cleanUp();
    }

    private void uploadFile() {
        WebElement uploadFileField = this.songModel.findSongsheetUploadFileField();
        uploadFileField.sendKeys(FileHelper.getPdfResourcePathByName("fixture/fixturePdf.pdf"));
        this.waitToBeClickable(SongModel.SONGSHEET_DOWNLOAD_LINK_XPATH);
    }

    @Test
    public void deleteSongsheetAndVerifyNoSongsheetPresentAfterwords() {
        // arrange
        FileFixture fileFixture = FileFixture.setupAndCreate();
        driver.get(config().getBaseUrl());
        logInMacros.loginWithDefaults();
        String songTitle = "Lied with file";
        liedViewMacros.openLiedFromQuicksearchResult(songTitle);
        // verify download link available
        this.songModel.findSongsheetDownloadLink();
        // act
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

    @Test
    public void addASongsheet_BeforeSongIsSavedAndAfterSongIsSaved_ExpectUploadControlDisabledAndThenEnabled() {
        // arrange
        String songTitle = "New added song by test";
        driver.get(config().getBaseUrl());
        logInMacros.loginWithDefaults();
        liedViewMacros.addNewSong();
        this.songModel.findNoSongsheetAvailable();
        // act & assert 1
        WebElement uploadFileField = this.songModel.findSongsheetUploadFileField();
        assertFalse("Upload field must be deactivated as long as the new song is not yet saved.", uploadFileField.isEnabled());
        // act & assert 2
        setSomePropertiesAndSaveSong(songTitle);
        uploadFileField = this.songModel.findSongsheetUploadFileField();
        assertTrue("Upload field must be enabled as soon as the new song is saved.", uploadFileField.isEnabled());
        // clean up
        assertEquals("should have deleted one song", 1, deleteSongByTitle(songTitle));
    }

    private long deleteSongByTitle(String title) {
        try (PreparedStatement statement = DatabaseAccess.prepareStatement("DELETE FROM lied WHERE titel = ?")) {
            statement.setString(1, title);
            int rowCount = statement.executeUpdate();
            return rowCount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setSomePropertiesAndSaveSong(String songTitle) {
        songModel.findTitelField().sendKeys(songTitle);
        songMacros.selectTonality(2);
        songMacros.selectRubrik(1);
        songMacros.saveSong();
    }
}
