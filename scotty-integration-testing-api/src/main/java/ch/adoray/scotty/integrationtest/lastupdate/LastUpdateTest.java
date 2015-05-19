package ch.adoray.scotty.integrationtest.lastupdate;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
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
        String before = readLastUpdateFromServer();
        // act
        interactor.performRequest();
        // assert
        String after = readLastUpdateFromServer();
        assertFalse(before.equals(after));
        // clean up
        liedFixture.removeTableIdTuple(Tables.LIEDTEXT, liedtextIdToDelete);
        liedFixture.cleanUp();
    }

    private static String readLastUpdateFromServer() {
        try {
            URL url = new URL(config().getLastUpdateUrl());
            System.out.println("Getting " + config().getLastUpdateUrl());
            try (InputStream input = url.openStream()) {
                return IOUtils.toString(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while getting config().getLastUpdateUrl().", e);
        }
    }
}
