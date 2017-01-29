package ch.adoray.scotty.acceptancetest.modifying;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;

import org.junit.Test;

import com.appfoundation.automation.framework.BaseSeleniumTest;

import ch.adoray.scotty.acceptancetest.base.macros.LiedViewMacros;
import ch.adoray.scotty.acceptancetest.base.macros.LogInScreenMacros;
import ch.adoray.scotty.acceptancetest.base.macros.MessageBoxMacros;
import ch.adoray.scotty.acceptancetest.base.model.LogInScreenModel;
import ch.adoray.scotty.acceptancetest.base.model.ViewportModel;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiederbuchHelper;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
public class LiedViewTest extends BaseSeleniumTest {
    private final LogInScreenModel logInScreenModel;
    private final ViewportModel viewportModel;
    private final LogInScreenMacros<LiedViewTest> logInMacros;
    private final LiedViewMacros<LiedViewTest> liedViewMacros;
    private MessageBoxMacros<BaseSeleniumTest> messageBoxMacros;

    public LiedViewTest() {
        this.logInScreenModel = new LogInScreenModel(this);
        this.viewportModel = new ViewportModel(this);
        this.logInMacros = new LogInScreenMacros<LiedViewTest>(this, logInScreenModel);
        this.liedViewMacros = new LiedViewMacros<LiedViewTest>(this, viewportModel);
        messageBoxMacros = new MessageBoxMacros<BaseSeleniumTest>(this);
    }

    @Test
    public void TwoLiederOneOfThemWithNumber15a_changeLiednrOfOtherLiedTo15a_exception() {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture fixtureLied1 = createLiedWithNumber15aInDefaultSongbook();
        String titleOfSecondLied = "Lied to change Liednr";
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture fixtureLied2 = createLiedWithTitle(titleOfSecondLied);
        driver.get(config().getBaseUrl());
        logInMacros.login(config().getTesterEmail(), config().getTesterPassword());
        // act & assert
        liedViewMacros.performQuicksearch(fixtureLied2.getTitel());
        String liednrToSet = "15a";
        liedViewMacros.setNumberInSongbook(liednrToSet);
        messageBoxMacros.clickButtonInMessageBox("Yes");
        messageBoxMacros.clickButtonInMessageBoxAndAssertMessage("OK", "Fehler im Feld Liednr: Die Nummer '" + liednrToSet + "' ist in diesem Liederbuch bereits vergeben.");
        // clean up
        fixtureLied1.cleanUp();
        fixtureLied2.cleanUp();
    }

    private LiedWithLiedtextsRefrainsAndNumbersInBookFixture createLiedWithTitle(String title) {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture fixture = new LiedWithLiedtextsRefrainsAndNumbersInBookFixture();
        fixture.setTitel(title);
        fixture.create();
        return fixture;
    }

    private LiedWithLiedtextsRefrainsAndNumbersInBookFixture createLiedWithNumber15aInDefaultSongbook() {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture fixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String firstLiednr = "15a";
        LiedHelper.addNumberInBookToLied(fixture.getLiedId(), LiederbuchHelper.BOOKID_ADORAY_LIEDERBUCH, firstLiednr);
        return fixture;
    }
}
