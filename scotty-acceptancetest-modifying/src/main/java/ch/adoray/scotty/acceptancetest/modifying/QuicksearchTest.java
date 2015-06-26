package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import ch.adoray.scotty.acceptancetest.base.macros.LiedViewMacros;
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
    private LiedViewMacros<QuicksearchTest> liedViewMacros;

    public QuicksearchTest() {
        this.logInScreenModel = new LogInScreenModel(this);
        this.viewportModel = new ViewportModel(this);
        this.songModel = new SongModel(this);
        this.liedViewMacros = new LiedViewMacros<QuicksearchTest>(this, viewportModel);
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
        liedViewMacros.openLiedFromQuicksearchResult(songTitle);
        // assert
        assertEquals(songTitle, songModel.findTitelField().getAttribute("value"));
    }
}
