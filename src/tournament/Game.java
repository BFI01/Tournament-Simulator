package tournament;

import player.Player;

import java.util.HashMap;
import java.util.Random;

/**
 * Abstract class defining the functionality of a single table tennis game.
 * <p>
 * A game is won by being first to 11 points (or higher) with a 2 point lead or by reaching
 * 15 points.
 */
public abstract class Game {
    /**
     * Hash map containing players and how many points they have.
     */
    private final HashMap<Player, Integer> pointsDict = new HashMap<>();
    /**
     * Player 1 of 2 players - there are always two players in a game.
     */
    private Player gamePlayer1;
    /**
     * Player 2 of 2 players - there are always two players in a game.
     */
    private Player gamePlayer2;

    /**
     * Simulate a game of table tennis with two players.
     * <p>
     * The winner is the first to 11 points (or higher) with a 2 point lead.
     * Maximum points of 15 to prevent games lasting indefinitely.
     *
     * @param gamePlayer1 {@link Player} object for one of the players
     * @param gamePlayer2 {@link Player} object for the other player
     *
     * @return Returns {@link Player} object of the game winner
     */
    public Player playGame(Player gamePlayer1, Player gamePlayer2) {
        this.gamePlayer1 = gamePlayer1;
        this.gamePlayer2 = gamePlayer2;
        pointsDict.put(gamePlayer1, 0);
        pointsDict.put(gamePlayer2, 0);
        do {
            Player winner = playPoint();
            Integer winnerPoints = pointsDict.get(winner);
            pointsDict.put(winner, winnerPoints + 1);
            if ((winnerPoints + 1) == 15) {
                break;
            }
        }
        while (pointsDict.get(gamePlayer1) < 11
               && pointsDict.get(gamePlayer2) < 11
               || Math.abs(pointsDict.get(gamePlayer1) - pointsDict.get(gamePlayer2)) < 2);

        return (pointsDict.get(gamePlayer1) > pointsDict.get(gamePlayer2)) ? gamePlayer1 : gamePlayer2;
    }

    /**
     * Simulate playing a point between two players.
     * <p>
     * Use random number considering ELO to determine winner.
     *
     * @return Point winner as {@link Player}
     */
    private Player playPoint() {
        // Calculate weighting based on ELO difference
        double eloDifference = gamePlayer1.getElo() - gamePlayer2.getElo();
        double player1WinChance = getWinChance(eloDifference);

        // Generate random value between 0 and 1
        Random random = new Random();
        double randomDouble = random.nextDouble();

        // Decide winner and return player.Player obj
        return (randomDouble <= player1WinChance) ? gamePlayer1 : gamePlayer2;
    }

    /**
     * Use logistic function to calculate chance of winning any given point.
     * <p>
     * Source: <a href="https://nicidob.github.io/nba_elo/">FiveThirtyEight's Elo Ratings and Logistic Regression</a>
     *
     * @param eloDifference Difference between both players' ELO
     * @return Win chance, double between 0 and 1
     */
    public static double getWinChance(double eloDifference) {
        return 1 / (1 + Math.pow(10, -eloDifference/400));
    }
}