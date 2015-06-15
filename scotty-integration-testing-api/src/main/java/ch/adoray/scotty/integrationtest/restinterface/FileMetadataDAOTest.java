package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.FileFixture;
public class FileMetadataDAOTest {
    @Test
    public void read_withId_idCorrect() throws Exception {
        // arrange
        FileFixture fileFixture = FileFixture.setupAndCreate();
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("fileMetadata", fileFixture.getFileMetadataId());
        // act
        RestResponse result = interactor.performRequestAsRestResponse();
        // assert
        assertEquals(fileFixture.getFileMetadataId(), result.getFirstId());
        // clean up
        fileFixture.cleanUp();
    }

    @Test
    public void read_byLiedId_idCorrect() throws Exception {
        // arrange
        FileFixture fileFixtureAdditional = FileFixture.setupAndCreate();
        FileFixture fileFixture = FileFixture.setupAndCreate();
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("fileMetadata");
        interactor.addFilterParam("lied_id", fileFixture.getLiedId());
        // act
        RestResponse result = interactor.performRequestAsRestResponse();
        // assert
        assertEquals(fileFixture.getFileMetadataId(), result.getFirstId());
        // clean up
        fileFixture.cleanUp();
        fileFixtureAdditional.cleanUp();
    }
}
