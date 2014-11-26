package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;

import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.UserHelper;
public class UserFixture extends AbstractFixture {
    private long id;

    public UserFixture() {
        try {
            createUser();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while creating " + this.getClass().getSimpleName(), e);
        }
    }

    private void createUser() throws SQLException, ClassNotFoundException {
        id = UserHelper.createDummyUser();
        addTableIdTuple(Tables.USER, id);
    }

    public long getId() {
        return id;
    }
}
