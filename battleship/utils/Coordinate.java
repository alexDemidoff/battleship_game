package battleship.utils;

import battleship.exceptions.CoordinateFormatException;

public class Coordinate {

    public int i;
    public int j;

    public Coordinate(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public static Coordinate parse(String coordinate) throws CoordinateFormatException, NumberFormatException {
        String[] split = coordinate.split("", 2);

        if (split.length < 2) {
            throw new CoordinateFormatException();
        }
        
        // Throws NumberFormatException
        int j = Integer.parseInt(split[1]);
        int i;

        if (split[0].length() > 1) {
            throw new CoordinateFormatException();
        }

        i =  split[0].charAt(0) - 'A' + 1;

        if (j < 1 || j > 10 || i < 1 || i > 10) {
            throw new CoordinateFormatException();
        }

        return new Coordinate(i, j);
    }
}
