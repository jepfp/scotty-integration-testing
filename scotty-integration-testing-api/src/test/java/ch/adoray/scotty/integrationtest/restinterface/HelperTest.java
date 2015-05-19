package ch.adoray.scotty.integrationtest.restinterface;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import ch.adoray.scotty.integrationtest.common.ResourceLoader;


public class HelperTest {
    @Test
    public void removeTimestampNodes_bothPresent_bothRemoved() throws Exception {
        // arrange
        String testData = ResourceLoader.loadTestData();
        // act
        String processedString = Helper.removeTimestampNodes(testData);
        // assert
        assertFalse(processedString.contains("updated_at"));
        assertFalse(processedString.contains("created_at"));
    }
    
    @Test
    public void removeTimestampNodes_createdAtPresent_removed() throws Exception {
        // arrange
        String testData = ResourceLoader.loadTestData();
        // act
        String processedString = Helper.removeTimestampNodes(testData);
        // assert
        assertFalse(processedString.contains("updated_at"));
    }
    
    @Test
    public void removeTimestampNodes_neitherUpdatedAtOrCreatedAtArePresent_nothingChanged() throws Exception {
        // arrange
        String testData = ResourceLoader.loadTestData();
        // act
        String processedString = Helper.removeTimestampNodes(testData);
        // assert
        JSONAssert.assertEquals(testData, processedString, false);
    }
}
