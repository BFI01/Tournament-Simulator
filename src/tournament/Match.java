package tournament;

import player.Player;

import java.util.HashMap;

public class Match extends Game {
    // Using a hashmap rather than storing wins in the player.Player object enforces single responsibility and LoD(?)
    // But makes it so you can't track how many wins each player has after the tournament ends
    private final HashMap<String, Integer> gamesDict = new HashMap<>();
    private Player matchPlayer1;
    private Player matchPlayer2;
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

    public Match() {
        this.matchPlayer1 = null;
        this.matchPlayer2 = null;
    }

    private void updatePlayer(Player player, boolean wonMatch, int expectedGameWins, int gameWins) {
        if (wonMatch) {
            player.setWins(player.getWins() + 1);
        }
        player.adjustElo(expectedGameWins, gameWins);
    }

    public void addPlayer(Player matchPlayer) {
        if (matchPlayer1 == null) {
            matchPlayer1 = matchPlayer;
        } else if (matchPlayer2 == null) {
            matchPlayer2 = matchPlayer;
        } else {
            throw new IndexOutOfBoundsException("This match already has two players!");
        }
    }

    public Player playMatch() {
        // Catch bye case
        if (matchPlayer2 == null) {
            // When a match contains a bye, the player immediately moves on to the next round
            System.out.printf("[TOURNEY] %s %s gets a bye and moves on to the next round%n",
                    matchPlayer1.getFirstName(),
                    matchPlayer1.getLastName());
            setWinner(matchPlayer1);
            matchPlayer1.setWins(matchPlayer1.getWins() + 1);
            return null;
        }

        gamesDict.put("player1", 0);
        gamesDict.put("player2", 0);

        int maxGames = 7;
        int winsNeeded = (maxGames + 1) / 2;

        double eloDifference = matchPlayer1.getElo() - matchPlayer2.getElo();
        int player1ExpectedWins = (int) Math.round(getWinChance(eloDifference) * winsNeeded);

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

        Player loser;
        if ((gamesDict.get("player1") > gamesDict.get("player2"))) {
            setWinner(matchPlayer1);
            loser = matchPlayer2;
        } else {
            setWinner(matchPlayer2);
            loser = matchPlayer1;
        }

        System.out.printf("[GAME] %s vs %s: %s %s won!%n",
                matchPlayer1.getLastName(),
                matchPlayer2.getLastName(),
                winner.getFirstName(),
                winner.getLastName());
        updatePlayer(matchPlayer1, (matchPlayer2 == loser), player1ExpectedWins, gamesDict.get("player1"));
        updatePlayer(matchPlayer2, (matchPlayer1 == loser), winsNeeded-player1ExpectedWins, gamesDict.get("player2"));
        setOnGoing(false);
        return loser;
    }

    public Player[] getPlayers() {
        if (matchPlayer2 != null) {
            return new Player[]{matchPlayer1, matchPlayer2};
        } else if (matchPlayer1 != null) {
            return new Player[]{matchPlayer1};
        } else {
            return new Player[0];
        }
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(int number) {
        matchNumber = number;
    }

    public boolean isOnGoing() {
        return onGoing;
    }

    public void setOnGoing(boolean onGoing) {
        this.onGoing = onGoing;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
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
            if (getWinner() == null) {
                output += String.format(", matchNumber: %d, onGoing: %b, winner: null)",
                        matchNumber,
                        isOnGoing());
            } else {
                output += String.format(", matchNumber: %d, onGoing: %b, winner: Player(firstName: %s, lastName: %s, elo: %d))",
                        matchNumber,
                        isOnGoing(),
                        getWinner().getFirstName(),
                        getWinner().getLastName(),
                        getWinner().getElo());
            }
        } else {
            output = String.format("Match(Player(firstName: %s, lastName: %s, elo: %d), Bye",
                    matchPlayer1.getFirstName(),
                    matchPlayer1.getLastName(),
                    matchPlayer1.getElo());
            output += String.format(", matchNumber: %d, onGoing: %b, winner: null)",
                    matchNumber,
                    isOnGoing());
        }
        return output;
    }
}