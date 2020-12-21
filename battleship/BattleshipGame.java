package battleship;

import battleship.battlefield.Battlefield;

import java.util.Scanner;

public class BattleshipGame {

    public static void run() {
        Battlefield firstPlayerField = Battlefield.createBattlefieldWithName("Player 1");
        Battlefield secondPlayerField = Battlefield.createBattlefieldWithName("Player 2");

        initializeField(firstPlayerField);
        pressEnter();
        initializeField(secondPlayerField);

        while (firstPlayerField.isOnWater() && secondPlayerField.isOnWater()) {
            pressEnter();
            makeMove(firstPlayerField, secondPlayerField);

            if (secondPlayerField.isOnWater() && firstPlayerField.isOnWater()) {
                pressEnter();
                makeMove(secondPlayerField, firstPlayerField);
            }
        }
    }

    private static void makeMove(Battlefield currentPlayer, Battlefield opponentPlayer) {
        opponentPlayer.drawField(false);
        System.out.println("---------------------");
        currentPlayer.drawField(true);
        System.out.printf("\n%s, it's your turn:\n", currentPlayer.getName());
        opponentPlayer.takeShot();
        System.out.println(opponentPlayer.getStateMessage());
    }

    private static void pressEnter() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();
    }

    private static void initializeField(Battlefield field) {
        System.out.printf("%s, place your ships on the game field\n", field.getName());
        field.drawField(false);
        field.placeShips();
    }
}
