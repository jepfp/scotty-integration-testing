package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiederbuchHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedtextHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.RefrainHelper;
public class LiedWithLiedtextsRefrainsAndNumbersInBookFixture extends AbstractFixture implements LiedContainingFixture {
    private String titel = "Dummy-Lied";

    public String getTitel() {
        return titel;
    }

    public void setTitel(String liedTitle) {
        this.titel = liedTitle;
    }

    public static LiedWithLiedtextsRefrainsAndNumbersInBookFixture setupAndCreate() {
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture fixture = new LiedWithLiedtextsRefrainsAndNumbersInBookFixture();
        fixture.create();
        return fixture;
    }

    @Override
    public long create() {
        try {
            createLied();
            createRefrains();
            createLiedtexts();
            return id;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while creating " + this.getClass().getSimpleName(), e);
        }
    }

    private void createLied() throws SQLException, ClassNotFoundException {
        id = LiedHelper.createDummyLied(titel);
        addTableIdTuple(Tables.LIED, getId());
    }

    public void addTwoNumberInBookAssociations() {
        long assocId = LiedHelper.addNumberInBookToLied(id, LiederbuchHelper.BOOKID_DIR_SINGEN_WIR2, "12");
        addTableIdTuple(Tables.FK_LIEDERBUCH_LIED, assocId);
        assocId = LiedHelper.addNumberInBookToLied(id, LiederbuchHelper.BOOKID_ADONAI_ZUG, "102");
        addTableIdTuple(Tables.FK_LIEDERBUCH_LIED, assocId);
    }

    private void createRefrains() throws ClassNotFoundException, SQLException {
        for (int i = 0; i < 3; i++) {
            long createdId = RefrainHelper.createRefrain(getId(), i, "Dummy-Refrain Nr " + i);
            addTableIdTuple(Tables.REFRAIN, createdId);
        }
    }

    private void createLiedtexts() throws ClassNotFoundException, SQLException {
        List<Long> refrainIds = getCreatedIdsByTable(Tables.REFRAIN);
        for (int i = 0; i < 5; i++) {
            //2 with a refrain + leave one refrain without any liedtext + 2 without any refrain
            Optional<Long> refrainId = (i < 2 ? Optional.of(refrainIds.get(i)) : Optional.empty());
            long createdId = LiedtextHelper.createLiedtext(getId(), i, "Dummy-Strophe Nr " + i, refrainId);
            addTableIdTuple(Tables.LIEDTEXT, createdId);
        }
    }

    @Override
    public long getLiedId() {
        return id;
    }
}
