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
import ch.adoray.scotty.integrationtest.fixture.LiedContainingFixture;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.util.KeyDataPair;
public class FileDAOTest {
    private static final String MIME_TYPE_APPLICATION_PDF = "application/pdf";
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    private static final String GENERAL_NO_FILE_ERROR_MESSAGE = "Es wurde keine hochgeladene Datei gefunden. Eventuell ist die Datei zu gross oder es liegt ein Server-Konfigurationsfehler vor.";

    @Test
    public void createFileByFixture_bigger10mb_storeAndReadSuccessful() throws IOException {
        // arrange
        FileFixture fileFixture = new FileFixture();
        fileFixture.setPdfResourceName("fixture/largeTestFile.pdf");
        // act
        fileFixture.create();
        // assert
        Path actualFilePath = Paths.get(testFolder.getRoot().getAbsolutePath(), "fileFromDb.pdf");
        FileHelper.readFileByIdAndSaveTo(fileFixture.getId(), actualFilePath);
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
        tellInteractorNotToFailWhileProcessingBinaryResponse(interactor);
        interactor.addFilterParam("filemetadata_id", fileMetadataId);
        UnexpectedPage response = interactor.performRequest();
        Files.copy(response.getInputStream(), targetFilePath);
        return new File(targetFilePath.toUri());
    }

    private void tellInteractorNotToFailWhileProcessingBinaryResponse(ExtRestGETInteractor interactor) {
        // TODO: This is necessary, because at the moment ExtRestGETInteractor expects a valid JSON. Could be improved.
        interactor.setFailOnUnparsableJson(false);
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
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

    private Page uploadFile(LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture, String pdfPath) {
        ExtRestMultipartFormPostInteractor interactor = new ExtRestMultipartFormPostInteractor("file");
        interactor.addRequestParameter("lied_id", String.valueOf(liedFixture.getLiedId()));
        interactor.addRequestParameter(new KeyDataPair("file", new File(pdfPath), MIME_TYPE_APPLICATION_PDF, "utf-8"));
        Page response = interactor.performRequest();
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
        Page response = uploadFile(liedFixture, pdfPath);
        // assert
        String expectedResult = ResourceLoader.loadTestData();
        JSONAssert.assertEquals(expectedResult, response.getWebResponse().getContentAsString(), false);
        // clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_uploadWordDocument_dtoException() throws IOException, JSONException {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();
        String pdfPath = FileHelper.getPdfResourcePathByName("fixture/foo.docx");
        // act
        RestResponse response = uploadFileWithoutExceptionIfNoSuccess(liedFixture, pdfPath, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        // assert
        assertFalse(response.isSuccess());
        assertEquals("Die hochgeladene Datei ist keine PDF-Datei.", response.getMessage());
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
        // act
        RestResponse response = uploadFileWithoutExceptionIfNoSuccess(liedFixture, pdfPath, MIME_TYPE_APPLICATION_PDF);
        // assert
        assertFalse(response.isSuccess());
        assertEquals(GENERAL_NO_FILE_ERROR_MESSAGE, response.getMessage());
        // clean up
        liedFixture.cleanUp();
    }

    @Test
    public void create_songAlreadyHasSongsheet_exception() throws IOException {
        // arrange
        FileFixture fileFixture = FileFixture.setupAndCreate();
        // act
        String pdfPath = FileHelper.getPdfResourcePathByName("fixture/fixturePdf.pdf");
        RestResponse response = uploadFileWithoutExceptionIfNoSuccess(fileFixture, pdfPath, MIME_TYPE_APPLICATION_PDF);
        // assert
        assertFalse(response.isSuccess());
        assertEquals("Fehler beim Hochladen der Noten, weil bereits Noten zu diesem Lied vorhanden sind. Bitte aktualisiere die Seite und lösche zuerst vorhandene Noten.", response.getMessage());
        // clean up
        fileFixture.cleanUp();
    }

    private RestResponse uploadFileWithoutExceptionIfNoSuccess(LiedContainingFixture fileFixture, String pdfPath, String mimeType) {
        ExtRestMultipartFormPostInteractor interactor = new ExtRestMultipartFormPostInteractor("file");
        interactor.setFailOnJsonSuccessFalse(false);
        interactor.setThrowExceptionOnFailingStatusCode(false);
        interactor.addRequestParameter("lied_id", String.valueOf(fileFixture.getLiedId()));
        interactor.addRequestParameter(new KeyDataPair("file", new File(pdfPath), mimeType, "utf-8"));
        RestResponse response = interactor.performRequestAsRestResponse();
        return response;
    }
}
