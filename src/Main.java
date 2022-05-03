import com.opencsv.exceptions.CsvException;
import player.CSVParser;
import player.Player;
import tournament.Match;
import tournament.Tournament;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, CsvException {
        System.out.println("Table Tennis Tournament Simulator");

        // Get user input for number of players in the tournament
        Scanner scanner = new Scanner(System.in);
        int playerCount = -1;
        do {
            System.out.println("Enter the number of players in the tournament:");
            System.out.print("> ");
            try {
                playerCount = scanner.nextInt();
                if (playerCount < 2) {
                    System.out.println("You need at least two players! Please try again...");
                }
            }
            catch (java.util.InputMismatchException e) {
                System.out.println("Input must be an integer! Please try again...");
                scanner.next();
            }
        }
        while (playerCount < 2);

        // Initialise Tournament object
        Tournament tournament = new Tournament();

        // Generate array of players
        CSVParser parser = new CSVParser();
        List<String[]> playerFirstNames = parser.readFromFile("data/firstnames.csv");
        List<String[]> playerLastNames = parser.readFromFile("data/lastnames.csv");

        Random random = new Random();
        for (int i = 0; i < playerCount; i++) {
            String firstName = playerFirstNames.get(random.nextInt(playerFirstNames.size()))[0];
            String lastName = playerLastNames.get(random.nextInt(playerLastNames.size()))[0];
            int elo = 1000 + random.nextInt(500); // Arbitrary range of 1000 to 1500 Elo for any player
            tournament.addPlayer(firstName, lastName, elo);
        }



        // Order by ELO and create pairings
        int byes = tournament.neededByes();
        System.out.println("Byes needed for this tournament: " + byes);
        tournament.createPairings(byes);

        /* Perform matches
        Match match = new Match();
        Player winner = match.playMatch(players.get(0), players.get(1));
        System.out.println(winner.getFirstName() + winner.getLastName());
        */
        // Rounds

        // Create Tournament class to handle all this stuff
    }
}