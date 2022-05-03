package tournament;

import player.Player;

import java.util.HashMap;

public class Match extends Game {
    // Using a hashmap rather than storing wins in the player.Player object enforces single responsibility and LoD(?)
    // But makes it so you can't track how many wins each player has after the tournament ends
    private final HashMap<String, Integer> gamesDict = new HashMap<>();
    private final Player matchPlayer1;
    private final Player matchPlayer2;
    private int matchNumber;
    private boolean onGoing = false;
    private Player winner = null;

    public Match(Player matchPlayer1, Player matchPlayer2) {
        this.matchPlayer1 = matchPlayer1;
        this.matchPlayer2 = matchPlayer2;
    }

    public Match(Player matchPlayer) {
        this.matchPlayer1 = matchPlayer;
        this.matchPlayer2 = null;
    }

    private void updatePlayer(Player player, int expectedWins, int wins) {
        player.setWins(player.getWins() + 1);
        player.adjustElo(expectedWins, wins);
    }

    public Player playMatch() {
        if (matchPlayer2 == null) {
            // When a match contains a Bye, the player immediately moves on to the next round
            System.out.printf("[GAME] %s %s gets a bye and moves on to the next round%n",
                    matchPlayer1.getFirstName(),
                    matchPlayer1.getLastName());
            return matchPlayer1;
        }

        gamesDict.put("player1", 0);
        gamesDict.put("player2", 0);

        int maxGames = 7;
        int winsNeeded = (maxGames + 1) / 2;

        double eloDifference = matchPlayer1.getElo() - matchPlayer2.getElo();
        int player1ExpectedWins = (int) Math.floor(getWinChance(eloDifference) * winsNeeded);

        do {
            String gameWinner = playGame(matchPlayer1, matchPlayer2);
            Integer winnerPoints = gamesDict.get(gameWinner);
            gamesDict.put(gameWinner, winnerPoints + 1);
            System.out.printf("[GAME] %s vs %s: %d-%d%n",
                    matchPlayer1.getLastName(),
                    matchPlayer2.getLastName(),
                    gamesDict.get("player1"),
                    gamesDict.get("player2"));
        }
        while (gamesDict.get("player1") < winsNeeded &&
               gamesDict.get("player2") < winsNeeded);

        if (gamesDict.get("player1") > gamesDict.get("player2")) {
            System.out.printf("[GAME] %s vs %s: %s %s won!%n",
                    matchPlayer1.getLastName(),
                    matchPlayer2.getLastName(),
                    matchPlayer1.getFirstName(),
                    matchPlayer1.getLastName());
            updatePlayer(matchPlayer1, player1ExpectedWins, gamesDict.get("player1"));
            return matchPlayer1;
        } else {
            System.out.printf("[GAME] %s vs %s: %s %s won!%n",
                    matchPlayer1.getLastName(),
                    matchPlayer2.getLastName(),
                    matchPlayer2.getFirstName(),
                    matchPlayer2.getLastName());
            updatePlayer(matchPlayer2, winsNeeded-player1ExpectedWins, gamesDict.get("player2"));
            return matchPlayer2;
        }
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int number) {
        matchNumber = number;
    }

    public String toString() {
        String output;
        if (matchPlayer2 != null) {
            output = String.format("Match(Player(firstName: %s, lastName: %s, elo: %d), Player(firstName: %s, lastName: %s, elo: %d)",
                    matchPlayer1.getFirstName(),
                    matchPlayer1.getLastName(),
                    matchPlayer1.getElo(),
                    matchPlayer2.getFirstName(),
                    matchPlayer2.getLastName(),
                    matchPlayer2.getElo());
            if (winner == null) {
                output += String.format(", matchNumber: %d, onGoing: %b, winner: null)",
                        matchNumber,
                        onGoing);
            } else {
                output += String.format(", matchNumber: %d, onGoing: %b, winner: Player(firstName: %s, lastName: %s, elo: %d))",
                        matchNumber,
                        onGoing,
                        winner.getFirstName(),
                        winner.getLastName(),
                        winner.getElo());
            }
        } else {
            output = String.format("Match(Player(firstName: %s, lastName: %s, elo: %d), Bye",
                    matchPlayer1.getFirstName(),
                    matchPlayer1.getLastName(),
                    matchPlayer1.getElo());
            output += String.format(", matchNumber: %d, onGoing: %b, winner: null)",
                    matchNumber,
                    onGoing);
        }
        return output;
    }
}