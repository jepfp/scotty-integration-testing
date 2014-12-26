package ch.adoray.scotty.integrationtest.song;

import static ch.adoray.scotty.integrationtest.common.Configuration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONParser;

import ch.adoray.scotty.integrationtest.common.DatabaseAccess;
import ch.adoray.scotty.integrationtest.common.Interactor;
import ch.adoray.scotty.integrationtest.common.Interactor.InteractorConfigurationWithParams;
import ch.adoray.scotty.integrationtest.common.Interactor.RpcInteractorConfiguration;
import ch.adoray.scotty.integrationtest.common.Tables;
import ch.adoray.scotty.integrationtest.common.entityhelper.LiedHelper;
import ch.adoray.scotty.integrationtest.fixture.LiedWithLiedtextsRefrainsAndNumbersInBookFixture;
import ch.adoray.scotty.integrationtest.restinterface.Helper;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.google.common.collect.Maps;
public class ChangeOrder {
    private static final String CHANGE_ORDER_ACTION = "ChangeOrder";
    private static final String LIEDTEXT_TABLE = "liedtext";
    private static final String REFRAIN_TABLE = "refrain";

    @Test
    public void moveUpLiedtext_2times_originalOrderAndSongUpdatedAtChanged() throws Exception {
        //arrange 1a
        int liedId = 6;
        int liedTextId = 4;
        //arrange 1b: updated at part
        LiedHelper.setUpdatedAtToFarBehind(liedId);
        Date dateUpdatedAtBefore = LiedHelper.getDateUpdatedAtOf(liedId);
        //act 1
        boolean methodResult = moveUp(LIEDTEXT_TABLE, String.valueOf(liedTextId));
        //assert 1
        assertTrue("RPC function returned false!", methodResult);
        Helper.assertIdsInOrder(loadLiedtexts(liedId), 2, 1, 4, 3);
        //assert 1b
        Date dateUpdatedAtAfter = LiedHelper.getDateUpdatedAtOf(liedId);
        assertFalse(dateUpdatedAtBefore.equals(dateUpdatedAtAfter));
        //arrange 2
        int liedTextId2nd = 3;
        //act 2
        methodResult = moveUp(LIEDTEXT_TABLE, String.valueOf(liedTextId2nd));
        //assert 2
        assertTrue("RPC function returned false!", methodResult);
        Helper.assertIdsInOrder(loadLiedtexts(liedId), 2, 1, 3, 4);
    }

    private boolean moveUp(String table, String id) throws JSONException {
        RpcInteractorConfiguration config = new RpcInteractorConfiguration(CHANGE_ORDER_ACTION, "moveUp");
        config.addMethodParam(table);
        config.addMethodParam(id);
        JavaScriptPage result = Interactor.performRawRequest(config);
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        return json.getBoolean("result");
    }

    private boolean moveDown(String table, long id) throws JSONException {
        RpcInteractorConfiguration config = new RpcInteractorConfiguration(CHANGE_ORDER_ACTION, "moveDown");
        config.addMethodParam(table);
        config.addMethodParam(String.valueOf(id));
        JavaScriptPage result = Interactor.performRawRequest(config);
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        return json.getBoolean("result");
    }

    private JSONArray loadLiedtexts(long liedId) throws JSONException {
        JSONObject json = Helper.readWithFkAttributeFilter("liedtext", "lied_id", String.valueOf(liedId));
        JSONArray data = (JSONArray) json.get("data");
        return data;
    }

    private JSONArray loadRefrains(long liedId) throws JSONException {
        JSONObject json = Helper.readWithFkAttributeFilter("refrain", "lied_id", String.valueOf(liedId));
        JSONArray data = (JSONArray) json.get("data");
        return data;
    }

    @Test
    public void moveUpLiedtext_alreadyAtTop_returnsFalse() throws Exception {
        //arrange
        int liedTextId = 2;
        //act
        boolean methodResult = moveUp(LIEDTEXT_TABLE, String.valueOf(liedTextId));
        //assert
        assertFalse("RPC function must return false!", methodResult);
    }

    @Test
    public void moveUpLiedtext_notExistingId_exception() throws Exception {
        //arrange
        int liedtextId = 999999;
        RpcInteractorConfiguration config = new RpcInteractorConfiguration(CHANGE_ORDER_ACTION, "moveUp");
        config//
            .addMethodParam(LIEDTEXT_TABLE)//
            .addMethodParam(String.valueOf(liedtextId))//
            .disableFailOnJsonSuccessFalse();
        //act
        JavaScriptPage result = Interactor.performRawRequest(config);
        //assert
        JSONObject json = (JSONObject) JSONParser.parseJSON(result.getContent());
        String type = json.getString("type");
        assertEquals("RPC function must return an exception!", "exception", type);
    }

    @Test
    public void moveDownLiedtext_2times_originalOrder() throws Exception {
        //arrange 1a
        int liedId = 6;
        int liedTextId = 2;
        //arrange 1b: updated at part
        LiedHelper.setUpdatedAtToFarBehind(liedId);
        Date dateUpdatedAtBefore = LiedHelper.getDateUpdatedAtOf(liedId);
        //act 1
        boolean methodResult = moveDown(LIEDTEXT_TABLE, liedTextId);
        //assert 1
        assertTrue("RPC function returned false!", methodResult);
        Helper.assertIdsInOrder(loadLiedtexts(liedId), 1, 2, 3, 4);
        //assert 1b
        Date dateUpdatedAtAfter = LiedHelper.getDateUpdatedAtOf(liedId);
        assertFalse(dateUpdatedAtBefore.equals(dateUpdatedAtAfter));
        //arrange 2
        int liedTextId2nd = 1;
        //act 2
        methodResult = moveDown(LIEDTEXT_TABLE, liedTextId2nd);
        //assert 2
        assertTrue("RPC function returned false!", methodResult);
        Helper.assertIdsInOrder(loadLiedtexts(liedId), 2, 1, 3, 4);
    }

    @Test
    public void moveUpLiedtext_alreadyAtBottom_returnsFalse() throws Exception {
        //arrange
        int liedTextId = 4;
        //act
        boolean methodResult = moveDown(LIEDTEXT_TABLE, liedTextId);
        //assert
        assertFalse("RPC function must return false!", methodResult);
    }

    @Test
    public void moveDown_moveDownRefrain_ReihenfolgeChanged() throws Exception {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        List<Long> refrainIds = liedFixture.getCreatedIdsByTable(REFRAIN_TABLE);
        // act
        boolean methodResult = moveDown(Tables.REFRAIN, refrainIds.get(0));
        // assert
        assertTrue("RPC function returned false!", methodResult);
        Helper.assertIdsInOrder(loadRefrains(liedFixture.getLiedId()), refrainIds.get(1), refrainIds.get(0));
        // clean up
        liedFixture.cleanUp();
    }

    @Test
    public void moveDown_moveDownRefrain_lastEditUserIdCorrect() throws Exception {
        // arrange
        LiedWithLiedtextsRefrainsAndNumbersInBookFixture liedFixture = LiedWithLiedtextsRefrainsAndNumbersInBookFixture.setupAndCreate();;
        List<Long> refrainIds = liedFixture.getCreatedIdsByTable(REFRAIN_TABLE);
        long lastEditUserIdBefore = loadLastEditUserId(liedFixture.getLiedId());
        assertTrue("The fixture should have set lastEditUser_id to 1. Fix this to have the test working properly!", lastEditUserIdBefore != 3);
        // act
        boolean methodResult = moveDown(Tables.REFRAIN, refrainIds.get(0));
        // assert
        assertTrue("RPC function returned false!", methodResult);
        assertTrue("After moving down the int test user (id 3) has to be set!", loadLastEditUserId(liedFixture.getLiedId()) == 3);
        // clean up
        liedFixture.cleanUp();
    }

    private long loadLastEditUserId(long liedId) throws ClassNotFoundException, SQLException {
        Map<String, String> record = DatabaseAccess.getRecordById(Tables.LIED, liedId);
        return Long.parseLong(record.get("lastEditUser_id"));
    }
}
