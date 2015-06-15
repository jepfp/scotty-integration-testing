package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.junit.Test;

import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
import ch.adoray.scotty.integrationtest.fixture.AbstractFixture;
public abstract class AbstractDAOTest<F extends AbstractFixture> {
    abstract String getController();

    abstract Class<F> getDefaultFixture();

    abstract long getFixtureCreatedId(F fixture);

    @Test
    public void read_withId_idCorrect() throws Exception {
        // arrange
        Class<F> defaultFixture = getDefaultFixture();
        F fixture = defaultFixture.newInstance();
        fixture.create();
        ExtRestGETInteractor interactor = new ExtRestGETInteractor(getController(), getFixtureCreatedId(fixture));
        // act
        RestResponse result = interactor.performRequestAsRestResponse();
        // assert
        assertEquals(getFixtureCreatedId(fixture), result.getFirstId());
        // clean up
        fixture.cleanUp();
    }

    @Test
    public void read_all_correctSize() throws Exception {
        // arrange
        List<F> fixtures = createFixtures(3);
        ExtRestGETInteractor interactor = new ExtRestGETInteractor(getController());
        // act
        RestResponse restResponse = interactor.performRequestAsRestResponse();
        // assert
        restResponse.assertIdsInOrder(getFixtureCreatedId(fixtures.get(0)), //
            getFixtureCreatedId(fixtures.get(1)), //
            getFixtureCreatedId(fixtures.get(2)));
        // clean up
        fixtures.stream().forEach(f -> {
            f.cleanUp();
        });
    }

    private List<F> createFixtures(int howMany) throws InstantiationException, IllegalAccessException {
        Validate.isTrue(howMany > 0);
        List<F> fixtures = new ArrayList<F>();
        Class<F> defaultFixture = getDefaultFixture();
        for (int i = 0; i < howMany; i++) {
            F fixture = defaultFixture.newInstance();
            fixture.create();
            fixtures.add(fixture);
        }
        return fixtures;
    }
}
