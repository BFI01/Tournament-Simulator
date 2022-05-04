import com.opencsv.exceptions.CsvException;
import player.CSVParser;
import player.Player;
import tournament.Match;
import tournament.Tournament;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Main {
    private static void output(final String text, boolean logsOnly) throws FileNotFoundException {
        FileOutputStream outputFile = new FileOutputStream("logs.txt");
        PrintStream logs = new PrintStream(outputFile);
        logs.println(text);
        if (!logsOnly) {
            System.out.println(text);
        }
    }

    private static int getPlayerCount() {
        // Get user input for number of players in the tournament
        Scanner scanner = new Scanner(System.in);
        int playerCount = -1;
        do {
            System.out.println("Enter the number of players in the tournament:");
            System.out.print("> ");
            try {
                playerCount = scanner.nextInt();
                if (playerCount < 2 || playerCount > 100000) {
                    System.out.println("You need at least two players! Please try again...");
                }
            }
            catch (java.util.InputMismatchException e) {
                System.out.println("Input must be an integer! Please try again...");
                scanner.next();
            }
        }
        while (playerCount < 2 || playerCount > 100000);
        return playerCount;
    }

    public static void main(String[] args) throws IOException, CsvException {
        System.out.printf("---------------------------------------------%n");
        System.out.println("     Table Tennis Tournament Simulator");
        System.out.printf("---------------------------------------------%n");

        // Initialise Tournament object
        Tournament tournament = new Tournament();

        // Get user input for player count
        int playerCount = getPlayerCount();

        // Generate array of players
        System.out.println("[SYSTEM] Loading player names...");
        CSVParser parser = new CSVParser();
        List<String[]> playerFirstNames = parser.readFromFile("data/firstnames.csv");
        List<String[]> playerLastNames = parser.readFromFile("data/lastnames.csv");

        Random random = new Random();
        System.out.println("[SYSTEM] Generating players...");
        for (int i = 0; i < playerCount; i++) {
            // Get first name
            int firstNameIndex = random.nextInt(playerFirstNames.size());
            String firstName = playerFirstNames.get(firstNameIndex)[0];
            // Get gender, easily modifiable for non-binary genders if required
            int gender = 0;
            if (Objects.equals(playerFirstNames.get(firstNameIndex)[1], "female")) {
                gender = 1;
            }
            // Get last name and Elo
            String lastName = playerLastNames.get(random.nextInt(playerLastNames.size()))[0];
            int elo = 1000 + random.nextInt(400); // Arbitrary range of 1000 to 1400 Elo for any player
            // Add player to the tournament
            tournament.addPlayer(firstName, lastName, gender, elo);
        }

        // Order by ELO and create initial pairings
        System.out.println("[TOURNEY] Creating initial match draws...");
        int byes = tournament.getNeededByes();
        System.out.println("[TOURNEY] Byes needed for this tournament: " + byes);
        tournament.assignMatches(byes);

        // Perform matches
        System.out.println("[TOURNEY] Simulating matches...");
        int round = 0;
        while (tournament.getNumberOfMatches(round) > 1) {
            System.out.printf("------------------------------%n");
            System.out.println("[TOURNEY] Round " + (round+1));
            System.out.printf("------------------------------%n");
            tournament.playMatches(round);
            tournament.assignMatches();
            round++;
        }

        // Final match
        System.out.printf("------------------------------%n");
        System.out.println("[TOURNEY] Finals");
        System.out.printf("------------------------------%n");
        tournament.playMatches(round);

        // Output leaderboard
        int position = 1;
        System.out.printf("%n------------------------------------------------------------%n");
        System.out.println("                        Leaderboards:");
        for (Player player : tournament.getLeaderboard()) {
            String gender = switch (player.getGender()) {
                case 0 -> "M";
                case 1 -> "F";
                default -> "O";
            };
            System.out.printf("%d) %s %s (%s) [Elo: %d, Wins: %d]%n",
                    position,
                    player.getFirstName(),
                    player.getLastName(),
                    gender,
                    player.getElo(),
                    player.getWins());
            if (position % 10 == 0 && position < playerCount) {
                System.out.println("---");
            }
            position++;
            // If the leaderboard has more than 30 players, display
//            if (position == 11) {
//                System.setOut();
//            }
        }
        System.out.printf("------------------------------------------------------------%n");
    }
}