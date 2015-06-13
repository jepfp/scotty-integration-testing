package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;

import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.FileHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.FileMetadataHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
public class FileFixture extends AbstractFixture {
    private long liedId;
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
        id = FileMetadataHelper.createDummyMetadataFileWithDummyFile(liedId, pdfResourceName);
        addTableIdTuple(Tables.FILE, getId());
    }

    public long getLiedId() {
        return liedId;
    }
}
