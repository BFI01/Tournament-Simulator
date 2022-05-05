package components;

import player.Player;

import java.util.Comparator;

/**
 * Sorts an array of {@link Player} objects in order of their Elo rating ({@link Player#getElo()}),
 * from highest to lowest.
 */
public class SortByElo implements Comparator<Player> {
    // Sorts in order from highest to lowest Elo
    public int compare(Player player1, Player player2) {
        return player2.getElo() - player1.getElo();
    }
}
