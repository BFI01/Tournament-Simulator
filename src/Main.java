import com.opencsv.exceptions.CsvException;
import player.CSVParser;
import player.Player;
import tournament.Tournament;

import java.io.IOException;
import java.util.*;

public class Main {
    private static int getPlayerCount() {
        // Get user input for number of players in the tournament
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of players in the tournament:");
        System.out.print("> ");
        int playerCount = -1;
        try {
            playerCount = scanner.nextInt();
            if (playerCount < 2) {
                System.out.println("You need at least two players! Please try again...");
                getPlayerCount();
            } else if (playerCount > 100_000) {
                System.out.println("You can have at most 100,000 players! Please try again...");
                getPlayerCount();
            }
        }
        catch (java.util.InputMismatchException e) {
            System.out.println("Input must be an integer! Please try again...");
            scanner.next();
            getPlayerCount();
        }
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
        }
        System.out.printf("------------------------------------------------------------%n");
    }
}