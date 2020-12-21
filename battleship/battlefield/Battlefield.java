package battleship.battlefield;

import battleship.enums.Cell;
import battleship.enums.Ship;
import battleship.exceptions.BattleshipLengthException;
import battleship.exceptions.CoordinateFormatException;
import battleship.exceptions.LocationException;
import battleship.exceptions.ShipIsTooCloseToAnotherException;
import battleship.utils.Coordinate;
import battleship.utils.ShipLocation;

import java.util.Scanner;

public class Battlefield {

    private static final int AIRCRAFT_CARRIER_LENGTH = 5;
    private static final int BATTLESHIP_LENGTH = 4;
    private static final int SUBMARINE_LENGTH = 3;
    private static final int CRUISER_LENGTH = 3;
    private static final int DESTROYER_LENGTH = 2;

    private static final int SIZE = 12;

    public boolean alive = true;

    private final Cell[][] cells = new Cell[SIZE][SIZE];
    private int[] shipCurrentLengths;
    private Cell currentMove;
    private String name;

    private Battlefield(String name) {
        initialize(name);
    }

    public static Battlefield createBattlefieldWithName(String name) {
        return new Battlefield(name);
    }

    private void initialize(String name) {
        this.name = name;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = Cell.FOG;
            }
        }

        shipCurrentLengths = new int[5];
        shipCurrentLengths[0] = AIRCRAFT_CARRIER_LENGTH;
        shipCurrentLengths[1] = BATTLESHIP_LENGTH;
        shipCurrentLengths[2] = CRUISER_LENGTH;
        shipCurrentLengths[3] = SUBMARINE_LENGTH;
        shipCurrentLengths[4] = DESTROYER_LENGTH;
    }

    public void drawField(boolean showShips) {
        System.out.print(" ");
        for (int i = 1; i < SIZE - 1; i++) {
            System.out.print(" " + i);
        }
        System.out.println();

        for (int i = 1; i < SIZE - 1; i++) {
            System.out.print((char) (i - 1 + 'A'));
            for (int j = 1; j < SIZE - 1; j++) {
                System.out.print(" " + parseCell(cells[i][j], showShips));
            }
            System.out.println();
        }
    }

    private String parseCell(Cell cell, boolean showShips) {
        switch (cell) {
            case FOG:
            case RESTRICTED:
                return "~";
            case AIRCRAFT_CARRIER_CELL:
            case BATTLESHIP_CELL:
            case SUBMARINE_CELL:
            case CRUISER_CELL:
            case DESTROYER_CELL:
                if (showShips) {
                    return "O";
                } else {
                    return "~";
                }
            case HIT:
                return "X";
            case MISS:
                return "M";
            default:
                return null;
        }
    }

    public void placeShips() {
        for (Ship ship : Ship.values()) {
            System.out.printf("\nEnter the coordinates of the %s (%d cells):\n", ship.getName(), getShipLength(ship));
            putShipOnField(getShipLocationFromPlayer(ship), ship);
            drawField(true);
        }
    }

    private int getShipLength(Ship ship) {
        switch (ship) {
            case AIRCRAFT_CARRIER:
                return AIRCRAFT_CARRIER_LENGTH;
            case BATTLESHIP:
                return BATTLESHIP_LENGTH;
            case CRUISER:
                return CRUISER_LENGTH;
            case SUBMARINE:
                return SUBMARINE_LENGTH;
            case DESTROYER:
                return DESTROYER_LENGTH;
            default:
                return -1;
        }
    }

    private void putShipOnField(ShipLocation shipLocation, Ship ship) {
        for (int i = shipLocation.begin.i; i <= shipLocation.end.i; i++) {
            for (int j = shipLocation.begin.j; j <= shipLocation.end.j; j++) {
                cells[i][j] = getCellMarkerFor(ship);
            }
        }

        markRestrictedAreaAroundShip(shipLocation);
    }

    private Cell getCellMarkerFor(Ship ship) {
        switch (ship) {
            case AIRCRAFT_CARRIER:
                return Cell.AIRCRAFT_CARRIER_CELL;
            case BATTLESHIP:
                return Cell.BATTLESHIP_CELL;
            case CRUISER:
                return Cell.CRUISER_CELL;
            case SUBMARINE:
                return Cell.SUBMARINE_CELL;
            case DESTROYER:
                return Cell.DESTROYER_CELL;
            default:
                return null;
        }
    }

    private void markRestrictedAreaAroundShip(ShipLocation shipLocation) {
        for (int i = shipLocation.begin.i - 1; i <= shipLocation.end.i + 1; i++) {
            cells[i][shipLocation.begin.j - 1] = Cell.RESTRICTED;
            cells[i][shipLocation.end.j + 1] = Cell.RESTRICTED;
        }

        for (int j = shipLocation.begin.j; j <= shipLocation.end.j; j++) {
            cells[shipLocation.begin.i - 1][j] = Cell.RESTRICTED;
            cells[shipLocation.end.i + 1][j] = Cell.RESTRICTED;
        }
    }

    private ShipLocation getShipLocationFromPlayer(Ship ship) {
        boolean isCorrect = false;
        ShipLocation shipLocation = new ShipLocation();

        do {
            String[] playerInput = getPlayerInput().split(" ");

            try {
                if (playerInput.length != 2) {
                    throw new CoordinateFormatException();
                }

                // Throws NumberFormatException or CoordinateFormatException
                shipLocation.begin = Coordinate.parse(playerInput[0]);
                shipLocation.end = Coordinate.parse(playerInput[1]);

                swap(shipLocation);

                // Throws LocationException
                checkShipLocation(shipLocation);
                // Throws BattleshipLengthException
                checkShipLength(ship, shipLocation);
                // Throws ShipIsTooCloseToAnotherException
                checkIfShipIsTooCloseToAnother(shipLocation);

                isCorrect = true;

            } catch (CoordinateFormatException | NumberFormatException e) {
                System.out.println("\nError! You entered the wrong coordinates! Try again:");
            } catch (LocationException e) {
                System.out.println("\nError! Wrong ship location! Try again:");
            } catch (BattleshipLengthException e) {
                System.out.printf("\nError! Wrong length of the %s! Try again:\n", ship.getName());
            } catch (ShipIsTooCloseToAnotherException e) {
                System.out.println("\nError! You placed it too close to another one. Try again:");
            }
        } while (!isCorrect);

        return shipLocation;
    }

    private void swap(ShipLocation shipLocation) {
        if (shipLocation.begin.i > shipLocation.end.i ||
                shipLocation.begin.j > shipLocation.end.j) {
            Coordinate buf = shipLocation.begin;
            shipLocation.begin = shipLocation.end;
            shipLocation.end = buf;
        }
    }

    private void checkIfShipIsTooCloseToAnother(ShipLocation shipLocation) throws ShipIsTooCloseToAnotherException {
        for (int i = shipLocation.begin.i; i <= shipLocation.end.i; i++) {
            for (int j = shipLocation.begin.j; j <= shipLocation.end.j; j++) {
                if (cells[i][j] == Cell.RESTRICTED || isShipCell(cells[i][j])) {
                    throw new ShipIsTooCloseToAnotherException();
                }
            }
        }
    }

    private void checkShipLocation(ShipLocation shipLocation) throws LocationException {
        if (shipLocation.begin.i != shipLocation.end.i && shipLocation.begin.j != shipLocation.end.j) {
            throw new LocationException();
        }
    }

    private void checkShipLength(Ship ship, ShipLocation shipLocation) throws BattleshipLengthException {
        int shipLength = Math.abs(shipLocation.begin.i - shipLocation.end.i + shipLocation.begin.j - shipLocation.end.j) + 1;

        switch (ship) {
            case AIRCRAFT_CARRIER:
                if (shipLength != AIRCRAFT_CARRIER_LENGTH) {
                    throw new BattleshipLengthException();
                }
                break;
            case BATTLESHIP:
                if (shipLength != BATTLESHIP_LENGTH) {
                    throw new BattleshipLengthException();
                }
                break;
            case CRUISER:
                if (shipLength != CRUISER_LENGTH) {
                    throw new BattleshipLengthException();
                }
                break;
            case SUBMARINE:
                if (shipLength != SUBMARINE_LENGTH) {
                    throw new BattleshipLengthException();
                }
                break;
            case DESTROYER:
                if (shipLength != DESTROYER_LENGTH) {
                    throw new BattleshipLengthException();
                }
                break;
        }
    }

    private String getPlayerInput() {
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine().toUpperCase();
    }

    public void takeShot() {
        Coordinate shotCoordinates = getShotCoordinatesFromPlayer();

        if (isShipCell(cells[shotCoordinates.i][shotCoordinates.j])) {
            calculateShipLengths(shotCoordinates);
            cells[shotCoordinates.i][shotCoordinates.j] = Cell.HIT;
            currentMove = Cell.HIT;
        } else {
            cells[shotCoordinates.i][shotCoordinates.j] = Cell.MISS;
            currentMove = Cell.MISS;
        }
    }

    private void calculateShipLengths(Coordinate shot) {
        switch (cells[shot.i][shot.j]) {
            case AIRCRAFT_CARRIER_CELL:
                shipCurrentLengths[0]--;
                break;
            case BATTLESHIP_CELL:
                shipCurrentLengths[1]--;
                break;
            case SUBMARINE_CELL:
                shipCurrentLengths[2]--;
                break;
            case CRUISER_CELL:
                shipCurrentLengths[3]--;
                break;
            case DESTROYER_CELL:
                shipCurrentLengths[4]--;
                break;
        }
    }

    private Coordinate getShotCoordinatesFromPlayer() {
        boolean isCorrect = false;
        Coordinate shotCoordinates = null;

        do {
            try {
                shotCoordinates = Coordinate.parse(getPlayerInput());

                isCorrect = true;
            } catch (CoordinateFormatException | NumberFormatException e) {
                System.out.println("\nError! You entered the wrong coordinates! Try again:");
            }
        } while (!isCorrect);

        return shotCoordinates;
    }

    private boolean isShipCell(Cell cell) {
        return cell == Cell.AIRCRAFT_CARRIER_CELL ||
                cell == Cell.BATTLESHIP_CELL ||
                cell == Cell.SUBMARINE_CELL ||
                cell == Cell.CRUISER_CELL ||
                cell == Cell.DESTROYER_CELL ||
                cell == Cell.HIT;
    }

    public String getStateMessage() {
        // Checking if all ships are sank
        if (checkIfAllShipsAreSank()) {
            alive = false;
            return "\nYou sank the last ship. You won. Congratulations!";
        }

        // Otherwise checking current ship lengths
        for (int i = 0; i < shipCurrentLengths.length; i++) {
            if (shipCurrentLengths[i] == 0) {
                shipCurrentLengths[i] = -1;
                return  "\nYou sank a ship!\n";
            }
        }

        // Otherwise checking if player hit ship a missed
        if (currentMove == Cell.HIT) {
            return "\nYou hit a ship!\n";
        }

        if (currentMove == Cell.MISS) {
            return "\nYou missed!\n";
        }

        return null;
    }

    private boolean checkIfAllShipsAreSank() {
        return shipCurrentLengths[0] <= 0 &&
                shipCurrentLengths[1] <= 0 &&
                shipCurrentLengths[2] <= 0 &&
                shipCurrentLengths[3] <= 0 &&
                shipCurrentLengths[4] <= 0;
    }

    public boolean isOnWater() {
        return alive;
    }

    public String getName() {
        return name;
    }
}
