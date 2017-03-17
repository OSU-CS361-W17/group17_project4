package edu.oregonstate.cs361.battleship;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Samuel on 3/2/2017.
 */
public class BattleshipModelTest {
    BattleshipModel model = new BattleshipModel();
    Ship currentShip;

    @Test
    public void getShip() throws Exception {
        String[] ships = {"aircraftCarrier","battleship","submarine","clipper","dinghy"};

        //Attempts to get a ship object, checks if a ship object is returned
        for (String ship : ships)
        {
            currentShip = model.getShip(ship);
            assertTrue(currentShip instanceof Ship);
        }

        //Attempts to get an invalid ship, checks if null is returned
        currentShip = model.getShip("ShipDoesntExist");
        assertNull(currentShip);
    }

    @Test
    public void placeEnemyShip() throws Exception {
        //Places enemy battleship
        currentShip = model.placeEnemyShip("battleship",4, true);
        assertTrue(currentShip instanceof Ship);
    }

    @Test
    public void checkShipOverlap(){


    }

    @Test
    public void placeShip() throws Exception {
        String result = "";
        String outBounds = "Ship Placement out of bounds";
        String overlap = "Placement overlaps another ship";

        //Places a ship at 1,1 should return null
        result = model.placeShip("battleship","1","1","horizontal");
        assertNull(result);

        //Places a ship overlapping at the same point
        result = model.placeShip("submarine","1","1","vertical");
        assertEquals(overlap,result);

        //Place a submarine at 2,1 vertical
        result = model.placeShip("submarine","2","1","vertical");
        assertNull(result);

        //Place civilian ship clipper at 4,4
        result = model.placeShip("clipper","4","4","horizontal");
        assertNull(result);

        //Place civilian ship dinghy at 5,4
        result = model.placeShip("dinghy","5","4","vertical");
        assertNull(result);

        //Places a ship out of bounds vertical
        result = model.placeShip("aircraftCarrier","20","20","vertical");
        assertEquals(outBounds,result);

        //Place a ship in bounds with its endpoint out of bounds vertical
        result = model.placeShip("aircraftCarrier","9","9","vertical");
        assertEquals(outBounds,result);

        //Places a ship out of bounds horizontal
        result = model.placeShip("aircraftCarrier","11","10","horizontal");
        assertEquals(outBounds,result);

        //Place a ship in bounds with its endpoint out of bounds horizontal
        result = model.placeShip("aircraftCarrier","10","11","horizontal");
        assertEquals(outBounds,result);

        //Place a ship in bounds with its endpoint out of bounds horizontal
        result = model.placeShip("aircraftCarrier","9","20","horizontal");
        assertEquals(outBounds,result);
    }

    @Test
    public void overlappingShip(){

        String overlap = "Placement overlaps another ship";
        String result = model.placeShip("aircraftCarrier","1","1","horizontal");
        String resultOverlap = model.placeShip("submarine","1","1","horizontal");
        assertEquals(resultOverlap, overlap);

    }


    @Test
    public void sinkAllEnemyShips(){
        MilitaryShip[] myMilitary = model.getEnemyMilitaryShips();
        CivilianShip[] myCivilian = model.getEnemyCivilianShips();

        for(int row = 1; row < 11; row++){
            for(int col = 1; col < 11; col++){
                model.shootAtComputer(row, col);
            }

        }

        ArrayList<Ship> sunkenShips = model.getEnemySunkShips();
        assertEquals(sunkenShips.contains(myMilitary[0]), true);
        assertEquals(sunkenShips.contains(myMilitary[1]), true);
        assertEquals(sunkenShips.contains(myMilitary[2]), true);
        assertEquals(sunkenShips.contains(myCivilian[0]), true);
        assertEquals(sunkenShips.contains(myCivilian[1]), true);


    }

    @Test
    public void testInBounds(){
        //fake horizontal aircraft carrier
        int orientation = 2;
        int length = 5;
        Coordinate start = new Coordinate(1, 6);
        assertEquals(model.shipInBounds(start, orientation, length), true);
        start = new Coordinate(1, 7);
        assertEquals(model.shipInBounds(start, orientation, length), false);

        //fake vertical aircraft carrier
        orientation = 1;
        length = 5;
        start = new Coordinate(6, 1);
        assertEquals(model.shipInBounds(start, orientation, length), true);
        start = new Coordinate(7, 1);
        assertEquals(model.shipInBounds(start, orientation, length), false);


    }


    @Test
    public void shootAtComputer() throws Exception {
        String result = "";
        String offBoard = "That Shot is off the board!";
        String alreadyFired = "You have already fired there!";
        int i,j;

        //Attempts a shot at 1,1. Should return null
        result = model.shootAtComputer(1,1);
        assertNull(result);

        //Attempts a shot at 1,1 again. Should return already fired message
        result = model.shootAtComputer(1,1);
        assertEquals(alreadyFired,result);

        //Attempts a shot off board. Should return off board message
        result = model.shootAtComputer(20,20);
        assertEquals(offBoard,result);

        //A test for hitting a computer ship? Placement is random unfortunately
    }

    @Test
    public void shootAtPlayer() throws Exception {
        int i;

        //Places a player ship
        model.placeShip("battleship","1","1","horizontal");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        //Fire randomly 50 times to hit player ships, likely need something to test here but variables are unaccessible
        for(i=0;i<50;i++)
            model.shootAtPlayer();

    }


    //this makes sure that smart fire adds a hit or miss every time its called
    //this method asserts that smart fire will sink all ships
    @Test
    public void smartShootAtPlayer(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","2","horizontal");
        model.placeShip("submarine","3","2","horizontal");
        model.placeShip("clipper","4","2","horizontal");
        model.placeShip("dinghy","5","2","horizontal");

        int count = 0;
        boolean clipper = false;

        while(model.getPlayerSunkShips().size() != 5){
            model.smartShootAtPlayer();
            count++;
        }
        assertEquals(model.getShip("aircraftCarrier").isSunk(), true);
        assertEquals(model.getShip("battleship").isSunk(), true);
        assertEquals(model.getShip("submarine").isSunk(), true);
        assertEquals(model.getShip("clipper").isSunk(), true);
        assertEquals(model.getShip("dinghy").isSunk(), true);
        assertEquals(count + 2, model.getMissArraySize() + model.getHitArraySize());

    }

    @Test
    public void easyComputerFire(){
        //Places a ship on each row and attempts to fire 3 times
        int i = 0;
        model = new BattleshipModel();
        model.placeShip("clipper","1","1","horizontal");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("battleship","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        //Fire once, computer should hit and sink clipper
        model.easyComputerFire();
        assertEquals(3,model.getHitArraySize() );

        //Fire 2 more times, computer should have 3 hits and 2 misses
        model.easyComputerFire();
        model.easyComputerFire();
        assertEquals(3,model.getHitArraySize() ); //hits
        assertEquals(2,model.getMissArraySize() ); //misses

        //Fire 50 times, should have 45 misses and 10 hits with this ship positioning
        for(i=0;i<50;i++)
            model.easyComputerFire();

        assertEquals(10,model.getHitArraySize());
        assertEquals(45,model.getMissArraySize());
    }

    //this test fires at both civilian ships
    //build a new model and shoots at the other end of the clipper
    @Test
    public void smartShootAtCivilian(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(4,1);
        assertEquals(3, model.getHitArraySize());
        assertEquals(0, model.getMissArraySize());
        assertEquals(null, model.firstFireHit);
        model.shootAtPlayer(5,1);
        assertEquals(4, model.getHitArraySize());
        assertEquals(0, model.getMissArraySize());
        assertEquals(null, model.firstFireHit);

        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(4,3);
        assertEquals(3, model.getHitArraySize());
        assertEquals(0, model.getMissArraySize());
        assertEquals(null, model.firstFireHit);
        model.shootAtPlayer(5,1);
        assertEquals(4, model.getHitArraySize());
        assertEquals(0, model.getMissArraySize());
        assertEquals(null, model.firstFireHit);

    }

    //this test shoots at the middle of the battleship and asserts that the AI will
    //go to the top and then turn around once it misses
    @Test
    public void smartShootAtPlayerBattleshipMiddleStart(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","2","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(3,8);
        assertEquals(1, model.getMissArraySize()+ model.getHitArraySize());
        assertEquals(3, model.firstFireHit.getAcross());
        assertEquals(8, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(1, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(2, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals("up", model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(8, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals("down", model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(3, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals("down", model.fireDirection);
        assertEquals(3, model.getHitArraySize());
        assertEquals(3, model.getMissArraySize());
        assertEquals(4, model.lastFired.getAcross());
        assertEquals(8, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(4, model.getHitArraySize());
        assertEquals(null, model.lastFired);
    }
    //this test shoots at the top of the battleship and asserts that the AI will
    //miss and then turn around and fire in opposite direction
    @Test
    public void smartShootAtPlayerBattlshipTopStart(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(1,8);
        assertEquals(1, model.getHitArraySize());
        assertEquals(1, model.firstFireHit.getAcross());
        assertEquals(8, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(1, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(2, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals("down", model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(8, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals("down", model.fireDirection);
        assertEquals(3, model.getHitArraySize());
        assertEquals(3, model.lastFired.getAcross());
        assertEquals(8, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(4, model.getHitArraySize());
        assertEquals(null, model.lastFired);
    }

    //this test shoots at the bottom of the battleship and asserts that the AI will
    //fire up all the way to the top
    @Test
    public void smartShootAtPlayerBattlshipBottomStart(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(4,8);
        assertEquals(1, model.getHitArraySize());
        assertEquals(4, model.firstFireHit.getAcross());
        assertEquals(8, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(1, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(2, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals("up", model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(3, model.lastFired.getAcross());
        assertEquals(8, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals("up", model.fireDirection);
        assertEquals(3, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(8, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(4, model.getHitArraySize());
        assertEquals(null, model.lastFired);
    }

    //this test shoots at the middle of the aircraftcarrier and asserts that the AI will
    //go to the left and then turn around once it misses
    @Test
    public void smartShootAtPlayerAircraftCarrierMiddleStart(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","2","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(2,1);
        assertEquals(1, model.getMissArraySize());
        model.shootAtPlayer(2,3);
        assertEquals(1, model.getHitArraySize());
        assertEquals(2, model.firstFireHit.getAcross());
        assertEquals(3, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals("left", model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(2, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals(1, model.getMissArraySize());
        assertEquals("right", model.fireDirection);
        assertEquals(3, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(4, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals("right", model.fireDirection);
        assertEquals(4, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(5, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(5, model.getHitArraySize());
        assertEquals(null, model.lastFired);
    }
    //this test shoots at the left of the aircraftcarrier and asserts that the AI will
    //miss and then turn around and fire in the opposite direction
    @Test
    public void smartShootAtPlayerAircraftCarrierBeginningStart(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(2,1);
        assertEquals(1, model.getHitArraySize());
        assertEquals(2, model.firstFireHit.getAcross());
        assertEquals(1, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals("right", model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(2, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals("right", model.fireDirection);
        assertEquals(3, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(3, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals("right", model.fireDirection);
        assertEquals(4, model.getHitArraySize());
        assertEquals(2, model.lastFired.getAcross());
        assertEquals(4, model.lastFired.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(5, model.getHitArraySize());
        assertEquals(null, model.lastFired);
    }
    //this test shoots at the middle of the submarine and asserts that the AI will
    //go to the left and then turn around once it misses
    @Test
    public void smartShootAtPlayerSubEndStart(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(3,2);
        assertEquals(1, model.getHitArraySize());
        assertEquals(3, model.firstFireHit.getAcross());
        assertEquals(2, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(null, model.lastFired);
        assertEquals(0, model.getMissArraySize());

    }
    //this test shoots at the middle of the aircraftcarrier and asserts that the AI will
    //try left, miss and then turn around and hit the last coord
    @Test
    public void smartShootAtPlayerSubStart(){
        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","1","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(3,1);
        assertEquals(1, model.getHitArraySize());
        assertEquals(3, model.firstFireHit.getAcross());
        assertEquals(1, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(null, model.lastFired);
        assertEquals(0, model.getMissArraySize());

        model = new BattleshipModel();
        //Places a player ship
        model.placeShip("battleship","1","8","vertical");
        model.placeShip("aircraftCarrier","2","1","horizontal");
        model.placeShip("submarine","3","4","horizontal");
        model.placeShip("clipper","4","1","horizontal");
        model.placeShip("dinghy","5","1","horizontal");

        model.shootAtPlayer(3,4);
        assertEquals(1, model.getHitArraySize());
        assertEquals(3, model.firstFireHit.getAcross());
        assertEquals(4, model.firstFireHit.getDown());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(1, model.getMissArraySize());
        model.smartShootAtPlayer();
        assertEquals(null, model.fireDirection);
        assertEquals(2, model.getHitArraySize());
        assertEquals(null, model.lastFired);
        assertEquals(1, model.getMissArraySize());

    }

    //test getScanresult function
    @Test
    public void getScanResult() throws Exception {
        boolean result;
        //Should return false by default
        result = model.getScanResult();
        assertFalse(result);
    }
    //test scan function
    @Test
    public void scan() throws Exception {
        boolean result;

        //Scan main board, may return true or false
        model.scan(1,1);
        result = model.getScanResult();
        assertNotNull(result);
    }

}