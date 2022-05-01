package tournament;

import player.Player;

import java.util.HashMap;

public class Match extends Game {
    // Using a hashmap rather than storing wins in the player.Player object enforces single responsibility and LoD(?)
    // But makes it so you can't track how many wins each player has after the tournament ends
    private final HashMap<String, Integer> gamesDict = new HashMap<>();

    private void updatePlayerWins(Player player) {
        player.setWins(player.getWins() + 1);
    }

    public Player playMatch(Player matchPlayer1, Player matchPlayer2) {
        gamesDict.put("player1", 0);
        gamesDict.put("player2", 0);

        int maxGames = 7;
        int winsNeeded = (maxGames + 1) / 2;

        do {
            String gameWinner = playGame(matchPlayer1, matchPlayer2);
            Integer winnerPoints = gamesDict.get(gameWinner);
            gamesDict.put(gameWinner, winnerPoints + 1);
        }
        while (gamesDict.get("player1") < winsNeeded &&
               gamesDict.get("player2") < winsNeeded);

        if (gamesDict.get("player1") > gamesDict.get("player2")) {
            updatePlayerWins(matchPlayer1);
            return matchPlayer1;
        } else {
            updatePlayerWins(matchPlayer2);
            return matchPlayer2;
        }
    }

    public Player playMatch(Player matchPlayer, boolean bye) {
        // When a match contains a Bye, the player immediately moves on to the next round
        return matchPlayer;
    }
}