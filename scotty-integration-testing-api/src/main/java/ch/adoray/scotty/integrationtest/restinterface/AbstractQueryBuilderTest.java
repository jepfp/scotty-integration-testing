package ch.adoray.scotty.integrationtest.restinterface;

import static ch.adoray.scotty.integrationtest.base.Configuration.config;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Map;

import org.json.JSONException;
import org.junit.Test;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
public class AbstractQueryBuilderTest {
    @Test
    public void read_logStatementInDb_formatCorrect() throws JSONException, ClassNotFoundException, SQLException {
        // act
        InteractorConfigurationWithParams config = new InteractorConfigurationWithParams(config().getRestInterfaceUrl() + "/user");
        Interactor.performRequest(config);
        // assert
        Map<String, String> record = DatabaseAccess.getLastRecord("logging");
        String message = record.get("message");
        String expectedMessage = "3 ## correct@login.ch ## user ## SELECT * FROM user ## ";
        assertEquals("Format correct?", expectedMessage, message);
    }
}
