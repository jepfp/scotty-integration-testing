package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.ExtRestMultipartFormPostInteractor;
import ch.adoray.scotty.integrationtest.common.ResourceLoader;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.FileHelper;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.FileFixture;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.util.KeyDataPair;
public class FileDAOTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    private static final String GENERAL_NO_FILE_ERROR_MESSAGE = "Fehler im Feld $_FILES[\"file\"]: Es wurde keine hochgeladene Datei gefunden. Eventuell ist die Datei zu gross oder es liegt ein Server-Konfigurationsfehler vor.";

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
        Path actualFilePath = Paths.get(testFolder.getRoot().getAbsolutePath(), "fileFromDb.pdf");
        // act
        File downloadedFile = downloadFileFromRestInterfaceByFileMetadataId(fileFixture.getFileMetadataId(), actualFilePath);
        // assert
        assertTrue(FileUtils.contentEquals(new File(fileFixture.getPdfResourcePath()), downloadedFile));
        // clean up
        fileFixture.cleanUp();
    }

    private File downloadFileFromRestInterfaceByFileMetadataId(long fileMetadataId, Path targetFilePath) throws IOException {
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("file");
        interactor.addFilterParam("filemetadata_id", fileMetadataId);
        UnexpectedPage response = interactor.performRequest();
        Files.copy(response.getInputStream(), targetFilePath);
        return new File(targetFilePath.toUri());
    }

    @Test
    public void select_notExistingEntry_error() throws Exception {
        // arrange
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("file");
        interactor.addFilterParam("filemetadata_id", (long) 999999);
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        // act
        RestResponse response = interactor.performRequestAsRestResponse();
        // assert
        assertFalse(response.isSuccess());
    }

    @Test
    public void create_happyCase_fileDownloadableAndContentIsEqual() throws IOException, ClassNotFoundException, SQLException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String pdfPath = FileHelper.getPdfResourcePathByName("fixture/scottyUpAndDownload.pdf");
        // act
        uploadFile(liedFixture, pdfPath);
        // assert
        Path actualFilePath = Paths.get(testFolder.getRoot().getAbsolutePath(), "fileFromDb.pdf");
        long fileMetadataId = findFileMetadataIdByLiedId(liedFixture.getLiedId());
        File downloadedFile = downloadFileFromRestInterfaceByFileMetadataId(fileMetadataId, actualFilePath);
        assertTrue(FileUtils.contentEquals(new File(pdfPath), downloadedFile));
        assertDbLogEntry(fileMetadataId);
        // clean up
        liedFixture.cleanUp();
    }

    private JavaScriptPage uploadFile(LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture, String pdfPath) {
        ExtRestMultipartFormPostInteractor interactor = new ExtRestMultipartFormPostInteractor("file");
        interactor.addRequestParameter("lied_id", String.valueOf(liedFixture.getLiedId()));
        interactor.addRequestParameter(new KeyDataPair("file", new File(pdfPath), "application/pdf", "utf-8"));
        JavaScriptPage response = interactor.performRequest();
        return response;
    }

    private long findFileMetadataIdByLiedId(long liedId) {
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("fileMetadata");
        interactor.addFilterParam("lied_id", liedId);
        RestResponse response = interactor.performRequestAsRestResponse();
        return response.getFirstId();
    }

    private void assertDbLogEntry(long fileMetadataId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getRecordFromLogHistory(Tables.LOGGING, 2);
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## file ## INSERT INTO file (filemetadata_id, filename, filesize, filetype, data) " //
            + "VALUES (?, ?, ?, ?, ?) ## sssss, " + fileMetadataId + ", scottyUpAndDownload.pdf, 368754, pdf, ";
        Assert.assertThat(message, CoreMatchers.containsString(expectedMessage));
    }

    @Test
    public void create_happyCase14mb_jsonAnswer() throws IOException, JSONException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String pdfPath = FileHelper.getPdfResourcePathByName("fixture/approx14mbFile.pdf");
        // act
        JavaScriptPage response = uploadFile(liedFixture, pdfPath);
        // assert
        String expectedResult = ResourceLoader.loadTestData();
        JSONAssert.assertEquals(expectedResult, response.getContent(), false);
        // clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_uploadWordDocument_dtoException() throws IOException, JSONException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String pdfPath = FileHelper.getPdfResourcePathByName("fixture/foo.docx");
        ExtRestMultipartFormPostInteractor interactor = new ExtRestMultipartFormPostInteractor("file");
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        interactor.addRequestParameter("lied_id", String.valueOf(liedFixture.getLiedId()));
        interactor.addRequestParameter(new KeyDataPair("file", new File(pdfPath), "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "utf-8"));
        // act
        RestResponse response = interactor.performRequestAsRestResponse();
        // assert
        assertFalse(response.isSuccess());
        assertEquals("Fehler im Feld : Die hochgeladene Datei ist keine PDF-Datei.", response.getMessage());
        // clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_noFileSent_dtoException() throws IOException, JSONException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        ExtRestMultipartFormPostInteractor interactor = new ExtRestMultipartFormPostInteractor("file");
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        interactor.addRequestParameter("lied_id", String.valueOf(liedFixture.getLiedId()));
        // act
        RestResponse response = interactor.performRequestAsRestResponse();
        // assert
        assertFalse(response.isSuccess());
        assertEquals(GENERAL_NO_FILE_ERROR_MESSAGE, response.getMessage());
        // clean up
        liedFixture.cleanUp();
    }
    
    @Test
    public void create_20mbFile_dtoException() throws IOException, JSONException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String pdfPath = FileHelper.getPdfResourcePathByName("fixture/dummy20mb.pdf");
        ExtRestMultipartFormPostInteractor interactor = new ExtRestMultipartFormPostInteractor("file");
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        interactor.addRequestParameter("lied_id", String.valueOf(liedFixture.getLiedId()));
        interactor.addRequestParameter(new KeyDataPair("file", new File(pdfPath), "application/pdf", "utf-8"));
        // act
        RestResponse response = interactor.performRequestAsRestResponse();
        // assert
        assertFalse(response.isSuccess());
        assertEquals(GENERAL_NO_FILE_ERROR_MESSAGE, response.getMessage());
        // clean up
        liedFixture.cleanUp();
    }
}
