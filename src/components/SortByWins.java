package components;

import player.Player;

import java.util.Comparator;

/**
 * Sorts an array of {@link Player} objects in order of their match {@link Player#getWins()}, from
 * highest to lowest.
 */
public class SortByWins implements Comparator<Player> {
    // Sorts in order from highest to lowest wins
    public int compare(Player player1, Player player2) {
        return player2.getWins() - player1.getWins();
    }
}
