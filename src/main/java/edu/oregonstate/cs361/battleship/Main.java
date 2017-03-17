package edu.oregonstate.cs361.battleship;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.io.UnsupportedEncodingException;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Main {

    public static void main(String[] args) {
        //This is for debugging purposes
//        if (true) {
//            String projectDir = System.getProperty("user.dir");
//            String staticDir = "/src/main/resources/public";
//            staticFiles.externalLocation(projectDir + staticDir);
//        } else {
//            staticFiles.location("/public");
//        }

        staticFiles.location("/public");

        //This will listen to GET requests to /model and return a clean new model
        get("/model", (req, res) -> newModel());
        //This will listen to POST requests and expects to receive a game model, as well as location to fire to
        post("/fire/:row/:col", (req, res) -> fireAt(req, res));
        //This will listen to POST requests and expects to receive a game model, as well as location to place the ship
        post("/placeShip/:id/:row/:col/:orientation", (req, res) -> placeShip(req, res));
        post("/scan/:row/:col", (req, res) -> scan(req));
    }

    //This function returns a new model
    private static String newModel() {
        BattleshipModel bm = new BattleshipModel();
        Gson gson = new Gson();
        return gson.toJson(bm);
    }

    //This function accepts an HTTP request and deseralizes it into an actual Java object.
    private static BattleshipModel getModelFromReq(Request req){
        Gson gson = new Gson();
        String result = req.body();
        BattleshipModel modelFromReq = gson.fromJson(result, BattleshipModel.class);
        return modelFromReq;
    }

    //This controller
    private static String placeShip(Request req, Response res) {
        BattleshipModel currModel = getModelFromReq(req);
        String id = req.params("id");
        String row = req.params("row");
        String col = req.params("col");
        String orientation = req.params("orientation");
        String result = currModel.placeShip(id,row,col,orientation);
        if(result != null){
            res.status(400);
            return result;
        }
        Gson gson = new Gson();
        return gson.toJson(currModel);
    }

    private static String fireAt(Request req, Response res) {

        BattleshipModel currModel = getModelFromReq(req);
        String row = req.params("row");
        String col = req.params("col");
        int rowInt = Integer.parseInt(row);
        int colInt = Integer.parseInt(col);
        String result = currModel.shootAtComputer(rowInt,colInt);
        if(result != null) {
            res.status(400);
            return result;
        } else //(result == null)
            //currModel.smartShootAtPlayer();
            currModel.easyComputerFire();
        Gson gson = new Gson();
        return gson.toJson(currModel);
    }

    private static String scan(Request req){
        BattleshipModel currModel = getModelFromReq(req);
        String row = req.params("row");
        String col = req.params("col");
        int rowInt = Integer.parseInt(row);
        int colInt = Integer.parseInt(col);
        currModel.scan(rowInt, colInt);
        currModel.shootAtPlayer();
        Gson gson = new Gson();
        return gson.toJson(currModel);
    }



}