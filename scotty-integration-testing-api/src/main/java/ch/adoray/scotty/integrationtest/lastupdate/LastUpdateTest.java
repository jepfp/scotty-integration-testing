package ch.adoray.scotty.integrationtest.lastupdate;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ch.adoray.scotty.integrationtest.common.LastUpdateFromServerReader;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import ch.adoray.scotty.integrationtest.common.ExtRestDeleteInteractor;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
public class LastUpdateTest {
    @Test
    public void readLastUpdate_changeANumber_readLastUpdatedChanges() {
        //arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        LiedHelper.setUpdatedAtToFarBehind(liedFixture.getId());
        Long liedtextIdToDelete = liedFixture.getCreatedIdsByTable(Tables.LIEDTEXT).get(0);
        ExtRestDeleteInteractor interactor = new ExtRestDeleteInteractor("liedtext", liedtextIdToDelete);
        String before = LastUpdateFromServerReader.read();
        // act
        interactor.performRequest();
        // assert
        String after = LastUpdateFromServerReader.read();
        assertFalse(before.equals(after));
        // clean up
        liedFixture.removeTableIdTuple(Tables.LIEDTEXT, liedtextIdToDelete);
        liedFixture.cleanUp();
    }

}
