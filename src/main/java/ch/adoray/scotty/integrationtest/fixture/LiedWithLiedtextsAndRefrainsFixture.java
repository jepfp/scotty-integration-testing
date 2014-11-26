package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedtextHelper;
import ch.adoray.scotty.integrationtest.common.entityhelper.RefrainHelper;
public class LiedWithLiedtextsAndRefrainsFixture extends AbstractFixture {
    private long liedId;

    public LiedWithLiedtextsAndRefrainsFixture() {
        try {
            createLied();
            createRefrains();
            createLiedtexts();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while creating " + this.getClass().getSimpleName(), e);
        }
    }

    private void createLied() throws SQLException, ClassNotFoundException {
        liedId = LiedHelper.createDummyLied();
        addTableIdTuple(Tables.LIED, getLiedId());
    }

    private void createRefrains() throws ClassNotFoundException, SQLException {
        for (int i = 0; i < 3; i++) {
            long createdId = RefrainHelper.createRefrain(getLiedId(), i, "Dummy-Refrain Nr " + i);
            addTableIdTuple(Tables.REFRAIN, createdId);
        }
    }

    private void createLiedtexts() throws ClassNotFoundException, SQLException {
        List<Long> refrainIds = getCreatedIdsByTable(Tables.REFRAIN);
        for (int i = 0; i < 5; i++) {
            //2 with a refrain + leave one refrain without any liedtext + 2 without any refrain
            Optional<Long> refrainId = (i < 2 ? Optional.of(refrainIds.get(i)) : Optional.empty());
            long createdId = LiedtextHelper.createLiedtext(getLiedId(), i, "Dummy-Strophe Nr " + i, refrainId);
            addTableIdTuple(Tables.LIEDTEXT, createdId);
        }
    }

    public long getLiedId() {
        return liedId;
    }
}
