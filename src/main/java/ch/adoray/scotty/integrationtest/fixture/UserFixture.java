package ch.adoray.scotty.integrationtest.fixture;

import java.sql.SQLException;

import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.UserHelper;
public class UserFixture extends AbstractFixture {
    private long id;

    public static UserFixture setupAndCreate(){
        UserFixture fixture = new UserFixture();
        fixture.create();
        return fixture;
    }

    private void createUser() throws SQLException, ClassNotFoundException {
        id = UserHelper.createDummyUser();
        addTableIdTuple(Tables.USER, id);
    }

    public long getId() {
        return id;
    }

    @Override
    public long create() {
        try {
            createUser();
            return id;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error while creating " + this.getClass().getSimpleName(), e);
        }
    }
}
