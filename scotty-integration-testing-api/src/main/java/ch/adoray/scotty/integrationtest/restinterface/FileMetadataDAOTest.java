package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestDeleteInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.FileFixture;
public class FileMetadataDAOTest extends AbstractSongBelongingDAOTest<FileFixture> {
    @Override
    String getController() {
        return "fileMetadata";
    }

    @Override
    String getTable() {
        return Tables.FileMetadata.TABLE;
    }

    @Override
    Class<FileFixture> getDefaultFixture() {
        return FileFixture.class;
    }

    @Override
    long getFixtureCreatedId(FileFixture fixture) {
        return fixture.getFileMetadataId();
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
    
    @Test
    public void destroyLied_liedHasSongsheet_fileMetadataAndFileAreDeletedToo() throws Exception {
        // arrange
        FileFixture fileFixture = FileFixture.setupAndCreate();
        ExtRestDeleteInteractor interactor = new ExtRestDeleteInteractor("lied", fileFixture.getLiedId());
        assertFileAndFileMetadataPresent(fileFixture.getFileMetadataId(), fileFixture.getId());
        // act
        interactor.performRequestAsRestResponse();
        // assert
        assertFileAndFileMetadataNotPresent(fileFixture.getFileMetadataId(), fileFixture.getId());
        // clean up
        fileFixture.cleanUp();
    }

    private void assertFileAndFileMetadataPresent(long fileMetadataId, Long fileId) {
        Assert.assertNotNull(DatabaseAccess.getRecordById(Tables.FileMetadata.TABLE, fileMetadataId));
        Assert.assertNotNull(DatabaseAccess.getRecordById(Tables.File.TABLE, fileId));
    }
    
    private void assertFileAndFileMetadataNotPresent(long fileMetadataId, Long fileId) {
        Assert.assertNull(DatabaseAccess.getRecordById(Tables.FileMetadata.TABLE, fileMetadataId));
        Assert.assertNull(DatabaseAccess.getRecordById(Tables.File.TABLE, fileId));
    }
}
