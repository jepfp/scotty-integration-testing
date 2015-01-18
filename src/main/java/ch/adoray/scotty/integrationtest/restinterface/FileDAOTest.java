package ch.adoray.scotty.integrationtest.restinterface;

import ch.adoray.scotty.integrationtest.common.ExtRestGETInteractor;

public class FileDAOTest {
    
    public void select_1_pdfContentCorrect(){
        // arrange
        ExtRestGETInteractor interactor = new ExtRestGETInteractor("file", (long) 1);
        // act
        // assert
    }
}
