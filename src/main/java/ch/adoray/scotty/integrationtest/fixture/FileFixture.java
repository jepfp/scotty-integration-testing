package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;

import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
public class FileFixture extends AbstractFixture {
    private String titel = "Dummy-Lied";

    public String getTitel() {
        return titel;
    }

    public void setTitel(String liedTitle) {
        this.titel = liedTitle;
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
            return id;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while creating " + this.getClass().getSimpleName(), e);
        }
    }

    private void createLied() throws SQLException, ClassNotFoundException {
        id = LiedHelper.createDummyLied(titel);
        addTableIdTuple(Tables.LIED, getId());
    }
}
