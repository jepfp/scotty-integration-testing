package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.junit.Test;

import ch.adoray.scotty.integrationtest.common.ExtRestPOSTInteractor;
import ch.adoray.scotty.integrationtest.common.response.RestResponse;
public class DTOValidatorTest {
    @Test
    public void notNullOrEmpty_titelIsNull_exception() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("lied");
        interactor.setFailOnJsonSuccessFalse(false);
        // act
        RestResponse response = RestResponse.createFromResponse(interactor.performRequest().getContent());
        // assert
        assertValidationError(response, "Fehler im Feld Titel: Das Feld darf nicht leer sein.");
    }

    private void assertValidationError(RestResponse response, String expectedMessage) {
        assertEquals(expectedMessage, response.getMessage());
        assertEquals("exception", response.getType());
    }
    
    @Test
    public void notNullOrEmpty_titelEmpty_errorMessage() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("lied");
        // act
        interactor.setField("Titel", "")//
            .setField("rubrik_id", "3")//
            .setField("tonality", "E");
        interactor.setFailOnJsonSuccessFalse(false);
        RestResponse response = RestResponse.createFromResponse(interactor.performRequest().getContent());
        assertValidationError(response, "Fehler im Feld Titel: Das Feld darf nicht leer sein.");
    }
    
    @Test
    public void notNullOrEmpty_titelEmptyButWithSpace_errorMessage() throws JSONException, ClassNotFoundException, SQLException, IOException {
        //arrange
        ExtRestPOSTInteractor interactor = new ExtRestPOSTInteractor("lied");
        // act
        interactor.setField("Titel", "          ")//
            .setField("rubrik_id", "3")//
            .setField("tonality", "E");
        interactor.setFailOnJsonSuccessFalse(false);
        RestResponse response = RestResponse.createFromResponse(interactor.performRequest().getContent());
        assertValidationError(response, "Fehler im Feld Titel: Das Feld darf nicht leer sein.");
    }
}
