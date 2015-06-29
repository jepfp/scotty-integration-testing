package ch.adoray.scotty.integrationtest.restinterface;


import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.junit.Test;

import ch.adoray.scotty.integrationtest.common.ExtRestDeleteInteractor;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.fixture.AbstractFixture;
import ch.adoray.scotty.integrationtest.fixture.LiedContainingFixture;
public abstract class AbstractSongBelongingDAOTest<F extends AbstractFixture & LiedContainingFixture> extends AbstractDAOTest<F> {
    @Test
    public void destroy_object_updatedAtAndLastEditUserIdOfLiedChanged() throws JSONException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        //arrange
        Class<F> defaultFixture = getDefaultFixture();
        F fixture = defaultFixture.newInstance();
        fixture.create();
        long liedId = fixture.getLiedId();
        LiedHelper.setUpdatedAtToFarBehind(liedId);
        LocalDateTime updatedAtBefore = LiedHelper.determineUpdatedAtOfLiedById(liedId);
        String lastEditUserIdBefore = LiedHelper.determineLastEditUserId(liedId);
        ExtRestDeleteInteractor interactor = new ExtRestDeleteInteractor(getController(), getFixtureCreatedId(fixture));
        // act
        interactor.performRequestAsRestResponse();
        // assert
        LocalDateTime updatedAtAfter = LiedHelper.determineUpdatedAtOfLiedById(liedId);
        assertThat(updatedAtBefore, not(updatedAtAfter));
        LiedHelper.assertLastUserHasChangedToCurrentTestUser(liedId, lastEditUserIdBefore);
        // clean up
        fixture.cleanUp();
    }
}
