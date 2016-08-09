package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;

import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.FileHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.FileMetadataHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
public class FileFixture extends AbstractFixture implements LiedContainingFixture {
    private long liedId;
    private long fileMetadataId;
    private String pdfResourceName = "fixture/fixturePdf.pdf";

    public String getPdfResourceName() {
        return pdfResourceName;
    }

    public String getPdfResourcePath() {
        return FileHelper.getPdfResourcePathByName(pdfResourceName);
    }

    public void setPdfResourceName(String pdfResourceName) {
        this.pdfResourceName = pdfResourceName;
    }

    public static FileFixture setupAndCreate() {
        FileFixture fixture = new FileFixture();
        fixture.create();
        return fixture;
    }

    @Override
    public long create() {
        try {
            createLied();
            createFile();
            return id;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while creating " + this.getClass().getSimpleName(), e);
        }
    }

    private void createLied() throws SQLException, ClassNotFoundException {
        liedId = LiedHelper.createDummyLied("Lied with file");
        addTableIdTuple(Tables.LIED, liedId);
    }

    private void createFile() throws SQLException, ClassNotFoundException {
        fileMetadataId = FileMetadataHelper.createDummyFileMetadata(liedId);
        addTableIdTuple(Tables.FileMetadata.TABLE, fileMetadataId);
        id =  FileHelper.createDummyFile(fileMetadataId, pdfResourceName);
        addTableIdTuple(Tables.File.TABLE, getId());
    }

    @Override
    public long getLiedId() {
        return liedId;
    }
    
    public long getFileMetadataId(){
        return fileMetadataId;
    }
}
