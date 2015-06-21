package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Test;

import ch.adoray.scotty.acceptancetest.base.macros.LiedViewMacros;
import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.SongModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;

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
        driver.get(config().getBaseUrl());
        logInMacros.login(config().getTesterEmail(), config().getTesterPassword());
        // act
        String songTitle = "Lied with file";
        liedViewMacros.openLiedFromFirstQuicksearchResult(songTitle);
        // assert
    }
}
