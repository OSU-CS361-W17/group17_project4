package edu.oregonstate.cs361.battleship;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by michaelhilton on 1/4/17.
 */
public class BattleshipModel {

    private MilitaryShip aircraftCarrier = new MilitaryShip("AircraftCarrier",5, new Coordinate(0,0),new Coordinate(0,0),false);
    private MilitaryShip battleship = new MilitaryShip("Battleship",4, new Coordinate(0,0),new Coordinate(0,0),true);
    private MilitaryShip submarine = new MilitaryShip("Submarine",2, new Coordinate(0,0),new Coordinate(0,0),true);
    private CivilianShip clipper = new CivilianShip("Clipper",3, new Coordinate(0,0),new Coordinate(0,0),false);
    private CivilianShip dinghy = new CivilianShip("Dinghy",1, new Coordinate(0,0),new Coordinate(0,0),false);


    private MilitaryShip computer_aircraftCarrier;
    private MilitaryShip computer_battleship;
    private MilitaryShip computer_submarine;
    private CivilianShip computer_clipper;
    private CivilianShip computer_dinghy;


    private MilitaryShip[] playerMilitaryShips;
    private CivilianShip[] playerCivilianShips;
    private MilitaryShip[] enemyMilitaryShips;
    private CivilianShip[] enemyCivilianShips;
    private int militaryPlaceIndex = 0;
    private int civilianPlaceIndex = 0;

    private ArrayList<Coordinate> playerHits;
    private ArrayList<Coordinate> playerMisses;
    private ArrayList<Coordinate> computerHits;
    private ArrayList<Coordinate> computerMisses;

    //these arrays are for which ships have been sunk
    private ArrayList<Ship> computerShipsSunk;
    private ArrayList<Ship> playerShipsSunk;

    //private ArrayList<Coordinate> computerScore;
    private ArrayList<Coordinate> playerShipPoints;
    private ArrayList<Coordinate> computerShipPoints;
    private boolean scanResult;
    // will be used to store the names of sunk ships.
    private String mySunkShip;
    private String enemySunkShip;

    //fields for Smart AI fire
    private boolean smart_AI_Fire;
    Coordinate lastFired;
    Coordinate firstFireHit;
    String fireDirection;

    //Easy AI Fire vars
    private int computerFireRow = 1;
    private int computerFireCol = 1;
    private boolean computerAltFire = false;

    //Maximum board size
    private static final int BOARD_SIZE = 10;
    private boolean easyMode;

    public BattleshipModel() {
        playerHits = new ArrayList<>();
        playerMisses= new ArrayList<>();
        computerHits = new ArrayList<>();
        computerMisses= new ArrayList<>();
        playerShipPoints = new ArrayList<>();
        computerShipPoints = new ArrayList<>();

        computerShipsSunk = new ArrayList<>();
        playerShipsSunk = new ArrayList<>();

        enemyMilitaryShips = new MilitaryShip[3];
        enemyCivilianShips = new CivilianShip[2];
        playerMilitaryShips = new MilitaryShip[3];
        playerCivilianShips = new CivilianShip[2];


        computer_aircraftCarrier = (MilitaryShip) placeEnemyShip("Computer_AircraftCarrier", 5,true);
        computer_battleship = (MilitaryShip) placeEnemyShip("Computer_Battleship",4,true);
        computer_submarine = (MilitaryShip) placeEnemyShip("Computer_Submarine",2,true);
        computer_clipper = (CivilianShip) placeEnemyShip("Computer_Clipper",3,true);
        computer_dinghy = (CivilianShip) placeEnemyShip("Computer_Dinghy",1,true);

        enemyMilitaryShips[0] = (computer_aircraftCarrier);
        enemyMilitaryShips[1] = (computer_battleship);
        enemyMilitaryShips[2] = (computer_submarine);
        enemyCivilianShips[0] = (computer_clipper);
        enemyCivilianShips[1] = (computer_dinghy);

        mySunkShip = null;
        enemySunkShip = null;

        scanResult = false;

        smart_AI_Fire = false;
        lastFired = null;
        firstFireHit = null;
        fireDirection = null;
        easyMode = false;
    }

    public BattleshipModel(boolean gameMode) {
        playerHits = new ArrayList<>();
        playerMisses= new ArrayList<>();
        computerHits = new ArrayList<>();
        computerMisses= new ArrayList<>();
        playerShipPoints = new ArrayList<>();
        computerShipPoints = new ArrayList<>();

        computerShipsSunk = new ArrayList<>();
        playerShipsSunk = new ArrayList<>();

        enemyMilitaryShips = new MilitaryShip[3];
        enemyCivilianShips = new CivilianShip[2];
        playerMilitaryShips = new MilitaryShip[3];
        playerCivilianShips = new CivilianShip[2];


        computer_aircraftCarrier = (MilitaryShip) placeEnemyShip("Computer_AircraftCarrier", 5,gameMode);
        computer_battleship = (MilitaryShip) placeEnemyShip("Computer_Battleship",4,gameMode);
        computer_submarine = (MilitaryShip) placeEnemyShip("Computer_Submarine",2,gameMode);
        computer_clipper = (CivilianShip) placeEnemyShip("Computer_Clipper",3,gameMode);
        computer_dinghy = (CivilianShip) placeEnemyShip("Computer_Dinghy",1,gameMode);

        enemyMilitaryShips[0] = (computer_aircraftCarrier);
        enemyMilitaryShips[1] = (computer_battleship);
        enemyMilitaryShips[2] = (computer_submarine);
        enemyCivilianShips[0] = (computer_clipper);
        enemyCivilianShips[1] = (computer_dinghy);

        mySunkShip = null;
        enemySunkShip = null;

        scanResult = false;

        smart_AI_Fire = false;
        lastFired = null;
        firstFireHit = null;
        fireDirection = null;
        easyMode = gameMode;

    }

    //replaces fireAtComputer and chooses correct
    // computer fireing mode based on game mode.
    public String fire(int row, int col) {
        String result = shootAtComputer(row, col);
        if(result == null){
            chooseFire();
        }
        return result;
    }

    //chooses the correct fire function based on if the game mode.
    public void chooseFire() {
        if(easyMode) {
            easyComputerFire();
        } else {
            smartShootAtPlayer();
        }
    }

// Refactored getShip into a case statement instead of a mass of cluttered if statements. -GH
    public Ship getShip(String shipName) {
        switch(shipName) {
            case "aircraftCarrier":
                return aircraftCarrier;
            case "battleship":
                return battleship;
            case "submarine":
                return submarine;
            case "clipper":
                return clipper;
            case "dinghy":
                return dinghy;
            default:
                return null;
        }
    }
public int enemyShipOrientation(boolean isEasy, int orientation){// This function returns the proper enemy ship orientation depending on game difficulty.
        if(isEasy == true) {
            return 1;
        }

        else{
            orientation = (Math.random() <= 0.5) ? 1 : 2;
            return orientation;
        }
}
public int enemyShipColumn(boolean isEasy, int col){// This function returns the proper enemy ship column depending on game difficulty.
    if(isEasy == true){
        return col = col+1;
    }
    else{
        return col = (int) (Math.random() * BOARD_SIZE) + 1;
    }

}
public int enemyShipRow(boolean isEasy, int row){ // This function returns the proper enemy ship row depending on game difficulty.
    if(isEasy == true){
        return 5;
    }
    else{
        return row = (int) (Math.random() * BOARD_SIZE) + 1;
    }
}

    public Ship placeEnemyShip(String name, int length, boolean isEasy){//Difficulty decides if the ships are to be placed randomly or statically.

        boolean valid = false;
        boolean visibleShip = true;

        int orientation = 0;
        int row = 0;
        int col = 0;
        Coordinate test;
        while(!valid) {
            //select a random orientation; 1 == vertical; 2 == horizontal
            //random Coordinate for start point
            orientation = enemyShipOrientation(isEasy,orientation);
            row = enemyShipRow(isEasy,row); // sets row using enemyShipRow function
            col = enemyShipColumn(isEasy,col); // sets col using enemyShipColumn function
            test = new Coordinate(row, col);
            valid = isValidMove(length, test, orientation);
        }
        Coordinate startCoordinate;
        Coordinate endCoordinate;

        //vertical, change row
        if(orientation == 1){
            startCoordinate = new Coordinate(row, col);
            endCoordinate = new Coordinate(row+length-1, col);
        }
        else { //horizontal, change col
            startCoordinate = new Coordinate(row, col);
            endCoordinate = new Coordinate(row, col+length-1);
        }

        Coordinate[] myPoints = new Coordinate[length];
        myPoints = addPointsToArray(startCoordinate, myPoints, length, orientation);
        addPointsToArrayList(myPoints, computerShipPoints);

        Ship currentShip;
        //Give stealth to Computer_Battleship and Computer_Submarine
        if(name == "Computer_Battleship" || name == "Computer_Submarine")
            visibleShip = false;
        if(name == "Computer_Battleship" || name == "Computer_Submarine" || name == "Computer_AircraftCarrier"){

            currentShip = new MilitaryShip(name, length, startCoordinate, endCoordinate, visibleShip);
            currentShip.setPoints(myPoints);
        }
        else{

            currentShip = new CivilianShip(name, length, startCoordinate, endCoordinate, visibleShip);
            currentShip.setPoints(myPoints);

        }

           return currentShip;
    }

    //This function checks for a point in bounds and if it is not a duplicate point
    private boolean isValidMove(int length, Coordinate test, int orientation){
        //1 for vertical
        //2 for horizontal

        //check for point out of bounds
        Coordinate startCoor = test;
        if(!shipInBounds(startCoor, orientation, length)){
            return false; //if the point is out of bounds, return false;
        }

        //fill array with valid points and check for overlap
        Coordinate[] myPoints = new Coordinate[length];
        myPoints = addPointsToArray(startCoor, myPoints, length, orientation);
        if(checkForDuplicatePoints(myPoints, computerShipPoints))
            return false;
        else {

            //if the ship is in bounds and does not overlap, return true
            return true;
        }
    }

    //this checks if there is already a ship at the given point
    private boolean checkForDuplicatePoints(Coordinate[] testPoints, ArrayList<Coordinate> points){
        for(int i = 0; i < testPoints.length; i++){
            for(int j = 0; j < points.size(); j++){
                if(points.get(j).equals(testPoints[i])){
                    return true;
                }
            }

        }
        return false;
    }

    //this adds a Coordinate[] of points to an Arraylist
    private void addPointsToArrayList(Coordinate[] points, ArrayList target){
        for (int i = 0; i < points.length; i++) {
            target.add(points[i]);
        }
    }

    //this adds a the correct points to a Coordinate[] based on start coordinate, orientation, and length
    private Coordinate[] addPointsToArray(Coordinate start, Coordinate[] array, int shipLength, int orientation){
        int across = start.getAcross();
        int down = start.getDown();
        for (int i = 0; i < shipLength; i++) {
            if (orientation == 1) {
                array[i] = new Coordinate((across+i), (down));
            } else if (orientation == 2) {
                array[i] = new Coordinate((across), (down+i));
            }
        }
        return array;
    }

    //this function checks to see if the ship being placed will stay in bounds
    public boolean shipInBounds(Coordinate c, int orientation, int length){
        //vertical
        if(orientation == 1){
            if(c.getAcross() + length-1 > 10)
                return false;
            else
                return true;
        }
        else{//horizontal
            if(c.getDown() + length-1 > 10)
                return false;
            else
                return true;
        }
    }


/*Checks if ship placement at specified location would be inbounds, not overlap any other ships, and then
updates the cooresponding arrays with the ships points */
    public String placeShip(String shipName, String AcrossS, String DownS, String orientation) {
        //Initialize vars
        int Across = Integer.parseInt(AcrossS);
        int Down = Integer.parseInt(DownS);

        int size, endDown, endAcross;
        endDown = endAcross = 0;

        Ship placingShip;
        MilitaryShip testShip;

        Coordinate start, end, toAdd;
        toAdd = new Coordinate(0,0);
        Coordinate[] myPoints;

        //Get the ship and assign it to placingShip
        placingShip = getShip(shipName);

        //Get size of ship and use it to create myPoints Coordinate array
        size = getShip(shipName).getLength();
        myPoints = new Coordinate[size];

        //Return error string if coordinate is off board
        if(Down > BOARD_SIZE || Across > BOARD_SIZE)
            return "Ship Placement out of bounds";

        //Set endDown and endAcross based on orientation
        if(orientation.equals("vertical")) {
            endDown = Down;
            endAcross = Across + size - 1;
        }
        else if(orientation.equals("horizontal")) {
            endDown = Down + size -1;
            endAcross = Across;
        }

        //Return error string if ship would go off board
        if(endAcross > BOARD_SIZE || endDown > BOARD_SIZE)
            return "Ship Placement out of bounds";

        //Create new start and end coordinates for ship
        start = new Coordinate(Across, Down);
        end = new Coordinate(endAcross, endDown);
        testShip = new MilitaryShip("test", size, start, end, false);

        //Test if ship position would overlap any other ship
        for(int i = 0; i < playerShipPoints.size(); i++){
            if(testShip.covers(playerShipPoints.get(i)))
                return "Placement overlaps another ship";
        }

        //Add ship coordinates to playerShipPoints
        for(int i = 0; i < size; i++){
            //If vertical, move along across or row
            if (orientation.equals("vertical"))
                toAdd = new Coordinate(Across + i, Down);
            //If horizontal, move along Down or column
            else if (orientation.equals("horizontal"))
                toAdd = new Coordinate(Across,Down+i);

            playerShipPoints.add(toAdd);
            myPoints[i] = toAdd;
        }

        //Set location of ship, assign points, and then add to corresponding player ship array
        placingShip.setLocation(start,end);
        placingShip.setPoints(myPoints);

        //Military
        if (placingShip instanceof MilitaryShip) {
            playerMilitaryShips[militaryPlaceIndex] = (MilitaryShip)placingShip;
            militaryPlaceIndex++;
        }
        //Civilian
        else if (placingShip instanceof CivilianShip) {
            playerCivilianShips[civilianPlaceIndex] = (CivilianShip)placingShip;
            civilianPlaceIndex++;
        }

        //Ship was placed successfully, return null
        return null;

    }

    public String shootAtComputer(int row, int col) {

        enemySunkShip = null;
        //Note: Reversed order for checking computerHits and computerMisses
        if(row > BOARD_SIZE || col > BOARD_SIZE)
            return "That Shot is off the board!";
        for(int i = 0; i < computerMisses.size(); i++){
            if(row == computerMisses.get(i).getAcross() && col == computerMisses.get(i).getDown())
                return "You have already fired there!";
        }
        for(int i = 0; i < computerHits.size(); i ++){
            if(row == computerHits.get(i).getAcross() && col == computerHits.get(i).getDown())
                return "You have already fired there!";
        }
        Coordinate coor = new Coordinate(row,col);

        boolean hit = false;

        for(int i = 0; i < 3; i++){
            if(enemyMilitaryShips[i].covers(coor)){
                if(!enemyMilitaryShips[i].isSunk()) {
                    computerHits.add(coor);
                    enemyMilitaryShips[i].addHit();

                    if (enemyMilitaryShips[i].isSunk()) {
                        computerShipsSunk.add(enemyMilitaryShips[i]);
                        enemySunkShip = enemyMilitaryShips[i].getName();
                    }

                    hit = true;

                }
            }
        }
        for(int i = 0; i < 2; i++){
            if(enemyCivilianShips[i].covers(coor)){
                if(!enemyCivilianShips[i].isSunk()) {
                    computerHits.add(coor);
                    enemyCivilianShips[i].addHit();

                    if (enemyCivilianShips[i].isSunk()) {
                        computerShipsSunk.add(enemyCivilianShips[i]);
                        enemySunkShip = enemyCivilianShips[i].getName();
                        Coordinate addpoints[] = enemyCivilianShips[i].getPoints();
                        for(int j = 0; j < addpoints.length; j++){

                            if(addpoints[j].equals(coor) == false){
                                computerHits.add(addpoints[j]);
                            }

                        }

                    }

                    hit = true;
                }
            }
        }
        if(!hit){
            computerMisses.add(coor);
        }
        return null;

    }

    private Coordinate getRandomCoordinate(){
        int max = BOARD_SIZE;
        int min = 1;
        Random random = new Random();
        int randRow = random.nextInt(max - min + 1) + min;
        int randCol = random.nextInt(max - min + 1) + min;
        return new Coordinate(randRow,randCol);

    }
    public void smartShootAtPlayer() {
        mySunkShip = null;

        //last fired will only be set if there was a hit on military ship
        if(firstFireHit == null){
            shootAtPlayer();
        }
        //smart fire on military ship begin
        else {
            //if there is no direction yet found test surrounding coordinates
            if(fireDirection == null){
                Coordinate surrounding[] = getSurroundingCoordinates(firstFireHit);
                //check points in order, if duplicate, skip.
                for (int i = 0; i < surrounding.length; i++) {
                    //check if coordinate is in bounds
                    Coordinate[] test = new Coordinate[1];
                    test[0] = surrounding[i];

                        //check if coordinate has already been fired at
                        if (!checkForDuplicatePoints(test, playerMisses) && !checkForDuplicatePoints(test, playerHits)) {
                            //This point is in bounds, adjacent to the last fired hit, and has not already been fired at
                            //fire here
                            Ship military = checkMilitaryHit(test[0]);
                            if (military == null) {
                                playerMisses.add(test[0]);
                            }
                            //set the direction that the computer should take
                            else{
                                //unless the ship was sunk
                                if(!playerShipsSunk.contains(military)) {
                                    fireDirection = getDirection(firstFireHit, test[0]);
                                }
                            }
                            break;
                        }
                }//end for loop
            }// end if no direction found
            else {
                //if direction has been set
                Coordinate[] test = new Coordinate[1];
                test[0] = getNextPoint(lastFired, fireDirection);

                if (pointInBounds(test[0])) {
                    //check if coordinate has already been fired at
                    if (!checkForDuplicatePoints(test, playerMisses) && !checkForDuplicatePoints(test, playerHits)) {
                        //This point is in bounds, adjacent to the last fired hit, and has not already been fired at
                        //fire here
                        Ship military = checkMilitaryHit(test[0]);
                        if (military == null) {
                            //if this happens, the ship has reached the end of the ship but has not sunk it yet
                            //so go back to the first hit and switch directions
                            playerMisses.add(test[0]);
                            switch (fireDirection) {
                                case "up":
                                    fireDirection = "down";
                                    break;
                                case "down":
                                    fireDirection = "up";
                                    break;
                                case "left":
                                    fireDirection = "right";
                                    break;
                                case "right":
                                    fireDirection = "left";
                                    break;
                            }
                            lastFired = firstFireHit;
                        }
                    }
                    else {
                        switchDirsFromFirstHit(fireDirection);
                        test[0] = getNextPoint(lastFired, fireDirection);
                        Ship military = checkMilitaryHit(test[0]);
                    }
                }
                //if the point was out of bounds, go back to first hit and switch dirs
                else{
                    switchDirsFromFirstHit(fireDirection);
                    test[0] = getNextPoint(lastFired, fireDirection);
                    Ship military = checkMilitaryHit(test[0]);
                }
            }

        }//end else
    }//end method
    public void shootAtPlayer() {
        mySunkShip = null;
        Coordinate coor [] = new Coordinate[1];
        coor[0] =  getRandomCoordinate();


        while(checkForDuplicatePoints(coor, playerMisses) || checkForDuplicatePoints(coor, playerHits)){
                coor[0] = getRandomCoordinate();
        }
        Ship civilianHit = checkCivilianHit(coor[0]);
        Ship miliatryHit = checkMilitaryHit(coor[0]);

        if(civilianHit == null && miliatryHit == null)
            playerMisses.add(coor[0]);

    }
    public void shootAtPlayer(int across, int down) {
        mySunkShip = null;
        Coordinate coor [] = new Coordinate[1];
        coor[0] =  new Coordinate(across, down);

        Ship civilianHit = checkCivilianHit(coor[0]);
        Ship miliatryHit = checkMilitaryHit(coor[0]);

        if(civilianHit == null && miliatryHit == null)
            playerMisses.add(coor[0]);

    }

    public void easyComputerFire() {
        //Uses computerFireRow and computerFireCol to fire at every other space
        //If this function encounters a space that has already been hit in its pattern, it will call itself to fire again
        boolean alreadyHit = false;
        Coordinate[] fireLocation = new Coordinate[1];
        fireLocation[0] = new Coordinate(computerFireRow,computerFireCol);

        //Check if firing location is already in playerHits, fire if it is not a duplicate
        if (checkForDuplicatePoints(fireLocation,playerHits) )
            alreadyHit = true;
        else
            //Fire if not already a sunk position
            shootAtPlayer(computerFireRow, computerFireCol);


        //Increment firing location column
        computerFireCol += 2;

        //Move to next row if end of a column, start at column index 1 of new row
        if (computerFireCol > (BOARD_SIZE - 1) && !computerAltFire) {
            computerFireCol = 1;
            computerFireRow++;
        }
        //Start at column index 2 if in alternate firing mode
        else if (computerFireCol > BOARD_SIZE && computerAltFire) {
            computerFireCol = 2;
            computerFireRow++;
        }

        //If last row, change firing mode to target skipped spaces
        if (computerFireRow > BOARD_SIZE)
        {
            computerAltFire = true;
            //Start new firing at (1,2)
            computerFireRow = 1;
            computerFireCol = 2;
        }

        //If already hit, call this function again to attempt to shoot again
        if (alreadyHit)
            easyComputerFire();
    }

    public boolean getScanResult(){

        return this.scanResult;
    }

    private Ship checkCivilianHit(Coordinate fire){
        //iterate through the civilianShips
        for(int i = 0; i < playerCivilianShips.length; i++){
            //if the coordinate is a civilian ship
            if(playerCivilianShips[i].covers(fire)){
                //add a hit to the playerHits
                playerHits.add(fire);
                //add a hit to the ship object
                playerCivilianShips[i].addHit();
                //sink the civilian ship
                playerShipsSunk.add(playerCivilianShips[i]);
                //pass in ship name to sunkShip String for message box
                mySunkShip = playerCivilianShips[i].getName();
                //add remaining points to hit to color red on front end
                Coordinate addpoints[] = playerCivilianShips[i].getPoints();
                for(int j = 0; j < addpoints.length; j++){
                    if(addpoints[j].equals(fire) == false){ //do not add the point we added already
                        playerHits.add(addpoints[j]);
                    }
                }
                return playerCivilianShips[i];
            }
        }
        return null;
    }

    private Ship checkMilitaryHit(Coordinate fire){

        //iterate through all military ships
        for(int i = 0; i < playerMilitaryShips.length; i++){
            //if the coordinate contains a military ship
            if(playerMilitaryShips[i].covers(fire)){
                //add hit to playerHits
                playerHits.add(fire);
                if(firstFireHit == null)
                    firstFireHit = fire;
                else
                    lastFired = fire;
                //add hits to ship object
                playerMilitaryShips[i].addHit();

                //if that was the last hit needed to sink the ship, add ship to sunkships
                if (playerMilitaryShips[i].isSunk()) {
                    playerShipsSunk.add(playerMilitaryShips[i]);
                    //pass in ship name to sunkShip String for message box
                    mySunkShip = playerMilitaryShips[i].getName();
                    firstFireHit = null;
                    lastFired = null;
                    fireDirection = null;
                }
                return playerMilitaryShips[i];
            }

        }
        return null;
    }



    public void scan(int row, int col){
        scanResult = false;
        Coordinate up = new Coordinate(row-1, col);
        Coordinate down = new Coordinate(row+1, col);
        Coordinate left = new Coordinate(row, col-1);
        Coordinate right = new Coordinate(row, col+1);

        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
        Ship shipToCheck;

        //Add Coordinates to arraylist coords
        coords.add(up);
        coords.add(down);
        coords.add(left);
        coords.add(right);

        //For each coordinate, get the ship that is at that coordinate and then check if it has stealth
        for(Coordinate coord : coords)
        {
            shipToCheck = getShipFromCoordinate(coord);
            if (shipToCheck != null)
                if(shipToCheck.isVisible())
                    scanResult = true;
        }
        chooseFire(); //fires at the player ships


    }

    //this will return the ship object that is on the cooordinate parameter
    //null otherwise
    private Ship getShipFromCoordinate(Coordinate c){
        for(int i = 0; i < 3; i++){
            if(enemyMilitaryShips[i].containsPoint(c)){
                return enemyMilitaryShips[i];
            }
        }
        for(int i = 0; i < 2; i++){
            if(enemyCivilianShips[i].containsPoint(c)){
                return enemyCivilianShips[i];
            }
        }
        return null;
    }

    public ArrayList<Ship> getEnemySunkShips(){
        return computerShipsSunk;
    }
    public ArrayList<Ship> getPlayerSunkShips(){
        return playerShipsSunk;
    }
    public MilitaryShip[] getEnemyMilitaryShips(){
        return this.enemyMilitaryShips;
    }
    public CivilianShip[] getEnemyCivilianShips(){
        return this.enemyCivilianShips;
    }
    public int getMissArraySize(){return playerMisses.size();}
    public int getHitArraySize(){return playerHits.size();}
    private boolean pointInBounds(Coordinate c){
        if(c.getDown() < 1 || c.getDown() > 10)
            return false;
        if(c.getAcross() < 1 || c.getAcross() > 10)
            return false;
        return true;
    }

    //this function returns an array of all surrounding points in bounds
    public Coordinate [] getSurroundingCoordinates(Coordinate c){
        ArrayList<Coordinate> toReturn = new ArrayList<>();
        int length = 0;
        if(pointInBounds(new Coordinate(c.getAcross(), c.getDown() - 1))){
            length++;
            toReturn.add(new Coordinate(c.getAcross(), c.getDown() - 1));
        }
        if(pointInBounds(new Coordinate(c.getAcross(), c.getDown() + 1))){
            length++;
            toReturn.add(new Coordinate(c.getAcross(), c.getDown() + 1));
        }
        if(pointInBounds(new Coordinate(c.getAcross() - 1, c.getDown()))){
            length++;
            toReturn.add(new Coordinate(c.getAcross() - 1, c.getDown()));
        }
        if(pointInBounds(new Coordinate(c.getAcross() + 1, c.getDown()))){
            length++;
            toReturn.add(new Coordinate(c.getAcross() + 1, c.getDown()));
        }
        Coordinate [] myarray = new Coordinate[length];
        for(int i = 0; i < length; i++){
            myarray[i] = toReturn.get(i);
        }

        return myarray;
    }


    //given the direction, this function returns the next point adjacent to Coordinate c
    private Coordinate getNextPoint(Coordinate c, String direction){
        Coordinate toReturn = null;
        switch (direction) {
            case "up":
                toReturn = new Coordinate(c.getAcross()  - 1, c.getDown());
                break;
            case "down":
                toReturn = new Coordinate(c.getAcross() + 1, c.getDown() );
                break;
            case "left":
                toReturn = new Coordinate(c.getAcross(), c.getDown() - 1);
                break;
            case "right":
                toReturn = new Coordinate(c.getAcross(), c.getDown() + 1);
                break;
        }
        return toReturn;
    }

    private void switchDirsFromFirstHit(String direction){
        switch (direction) {
            case "up":
                fireDirection = "down";
                break;
            case "down":
                fireDirection = "up";
                break;
            case "left":
                fireDirection = "right";
                break;
            case "right":
                fireDirection = "left";
                break;
        }
        lastFired = firstFireHit;
    }

    private String getDirection(Coordinate start, Coordinate end){
        if(start.getDown() - end.getDown() < 0)
            return "right";
        else if(start.getDown() - end.getDown() > 0)
            return "left";
        else if(start.getAcross() - end.getAcross() < 0)
            return "down";
        else
            return "up";
    }

}//end class