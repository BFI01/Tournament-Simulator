import player.Player;
import tournament.Match;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Table Tennis Tournament Simulator");

        // Get user input for number of players in the tournament
        Scanner scanner = new Scanner(System.in);
        int playerCount = -1;
        do {
            System.out.println("Enter the number of players in the tournament:");
            try {
                playerCount = scanner.nextInt();
                if (playerCount < 2 || playerCount > 1000) {
                    System.out.println("Input must be between 2 and 1000! Please try again...");
                }
            }
            catch (java.util.InputMismatchException e) {
                System.out.println("Input must be an integer! Please try again...");
                scanner.next();
            }
        }
        while (playerCount < 2 || playerCount > 1000);

        // Read player information from JSON file and create array of players
        JSONParser jsonParser = new JSONParser();
        ArrayList<Player> players = new ArrayList<>();
        Player[] data = jsonParser.read("data/players.json");

        // Filter players array down randomly to the specified player count
        Random random = new Random();
        for (int i = 0; i < playerCount - 1; i++) {
            players.add(data[random.nextInt(data.length)]);
        }

        // Order by ELO and create pairings

        // Perform matches
        Match match = new Match();
        Player winner = match.playMatch(players.get(0), players.get(1));
        System.out.println(winner.getFirstName() + winner.getLastName());

        // Rounds

        // Create Tournament class to handle all this stuff
    }
}