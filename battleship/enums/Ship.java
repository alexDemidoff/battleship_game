package battleship.enums;

public enum Ship {
    AIRCRAFT_CARRIER ("Aircraft Carrier"),
    BATTLESHIP ("Battleship"),
    SUBMARINE ("Submarine"),
    CRUISER ("Cruiser"),
    DESTROYER ("Destroyer");

    String name;

    Ship(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
