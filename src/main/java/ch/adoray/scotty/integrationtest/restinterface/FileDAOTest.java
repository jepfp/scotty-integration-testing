package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.entityhelper.FileHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.FileFixture;
public class FileDAOTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void createFileByFixture_bigger10mb_storeAndReadSuccessful() throws IOException {
        // arrange
        FileFixture fileFixture = new FileFixture();
        fileFixture.setPdfResourceName("fixture/largeTestFile.pdf");
        // act
        fileFixture.create();
        // assert
        Path actualFilePath = Paths.get(testFolder.getRoot().getAbsolutePath(), "fileFromDb.pdf");
        FileHelper.readFileById(fileFixture.getId(), actualFilePath);
        assertTrue(FileUtils.contentEquals(new File(fileFixture.getPdfResourcePath()), new File(actualFilePath.toUri())));
        // clean up
        fileFixture.cleanUp();
    }

    @Test
    public void select_testPdfFile_metadataCorrectAndDataNotIncluded() {
        // arrange
        FileFixture fileFixture = FileFixture.setupAndCreate();
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("file", fileFixture.getId());
        // act
        RestResponse response = interactor.performRequestAsRestResponse();
        // assert
        assertEquals("aFilename.pdf", response.getDataValueByKeyFromFirst("filename"));
        // TODO: For the moment we don't really save the size. Either store it or remove the column
        assertEquals("FILESIZE", response.getDataValueByKeyFromFirst("filesize"));
        assertEquals("pdf", response.getDataValueByKeyFromFirst("filetype"));
        // clean up
        fileFixture.cleanUp();
    }
}
