package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

import com.gargoylesoftware.htmlunit.UnexpectedPage;
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
    public void select_testPdfFile_contentEqual() throws IOException {
        // arrange
        FileFixture fileFixture = FileFixture.setupAndCreate();
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("file", fileFixture.getId());
        Path actualFilePath = Paths.get(testFolder.getRoot().getAbsolutePath(), "fileFromDb.pdf");
        // act
        UnexpectedPage response = interactor.performRequest();
        Files.copy(response.getInputStream(), actualFilePath);
        // assert
        assertTrue(FileUtils.contentEquals(new File(fileFixture.getPdfResourcePath()), new File(actualFilePath.toUri())));
        // clean up
        fileFixture.cleanUp();
    }

    @Test
    public void select_notExistingEntry_error() throws Exception {
        // arrange
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("file", (long) 999999);
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        // act
        RestResponse response = interactor.performRequestAsRestResponse();
        // assert
        assertFalse(response.isSuccess());
    }
}
