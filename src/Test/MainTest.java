/**
 * Created by tnoelcke on 3/1/2017.
 */
package edu.oregonstate.cs361.battleship;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static spark.Spark.awaitInitialization;

class MainTest {

    @BeforeAll

    public static void beforeClass() {
        Main.main(null);
        awaitInitialization();
    }

    @AfterAll
    public static void afterClass() {
        Spark.stop();
    }

    @Test
    public void testGetModel() {
        TestResponse res = request("GET", "http://localhost:4567/model/true", null);
        assertEquals(200, res.status);
        //assertEquals("MODEL",res.body);
    }

    @Test
    public void testPlaceShip() {
        BattleshipModel test = new BattleshipModel();
        Gson gson = new Gson();
        String model = gson.toJson(test);
        TestResponse res = request("POST", "http://localhost:4567/placeShip/battleship/1/1/horizontal", model);
        assertEquals(res.status, 200);
    }

    @Test
    public void testBadPlacement() {
        BattleshipModel test = new BattleshipModel();
        Gson gson = new Gson();
        String model = gson.toJson(test);
        TestResponse res = request("POST", "http://localhost:4567/placeShip/battleship/11/11/horizontal", model);
        assertEquals(res, null);
    }

    @Test
    public void testValidFire() {
        BattleshipModel test = new BattleshipModel();
        placeShipsUser(test);
        Gson gson = new Gson();
        String model = gson.toJson(test);

        TestResponse res = request("POST", "http://localhost:4567/fire/4/1", model);
        assertEquals(200, res.status);
    }

    @Test
    public void testInvalidFire(){
        BattleshipModel test = new BattleshipModel();
        placeShipsUser(test);
        Gson gson = new Gson();
        String model = gson.toJson(test);

        TestResponse res = request("POST", "http://localhost:4567/fire/11/11", model);
        assertEquals(null, res);

    }

    @Test
    public void testScan(){
        BattleshipModel test = new BattleshipModel();
        placeShipsUser(test);
        Gson gson = new Gson();
        String model = gson.toJson(test);

        TestResponse res = request("POST", "http://localhost:4567/scan/4/1", model);
        assertEquals(200, res.status);
    }

    private TestResponse request(String method, String path, String body) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            if (body != null) {
                connection.setDoInput(true);
                byte[] outputInBytes = body.getBytes("UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(outputInBytes);
            }
            connection.connect();
            String returnBody = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), returnBody);
        } catch (IOException e) {
            return null;
        }
    }

    private static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public Map<String, String> json() {
            return new Gson().fromJson(body, HashMap.class);
        }
    }

    // have to place user ships for fire or scan trests to work
    //else we get a 500 error.
    private void placeShipsUser(BattleshipModel bm){
        bm.placeShip("aircraftCarrier", "1", "1" ,"horizontal" );
        bm.placeShip("battleship", "2", "2" ,"horizontal" );
        bm.placeShip("submarine", "3", "3" ,"horizontal" );
        bm.placeShip("clipper", "4", "4" ,"horizontal" );
        bm.placeShip("dinghy", "5", "5" ,"horizontal" );
    }
}
