import java.util.HashMap;
import java.util.Random;

/**
 * <p>Abstract class </p>
 */
public abstract class Game {
    private Player gamePlayer1;
    private Player gamePlayer2;
    private final HashMap<String, Integer> pointsDict = new HashMap<>();

    /**
     * <p>Simulate a game of table tennis with two players</p>
     * <br>
     * <p>The winner is the first to 11 points (or higher) with a 2 point lead</p>
     * @param gamePlayer1 Player object for one of the players
     * @param gamePlayer2 Player object for the other player
     * @return Returns Player object of the game winner
     */
    private Player playGame(Player gamePlayer1, Player gamePlayer2) {
        this.gamePlayer1 = gamePlayer1;
        this.gamePlayer2 = gamePlayer2;
        pointsDict.put("player1", 0);
        pointsDict.put("player2", 0);
        do {
            String winner = playPoint();
            Integer winnerPoints = pointsDict.get(winner);
            pointsDict.put(winner, winnerPoints++);
            System.out.println(winner + "won the point and is now on " + winnerPoints + "points");
        }
        while (pointsDict.get("player1") < 11 &&
               pointsDict.get("player2") < 11 ||
               Math.abs(pointsDict.get("player1") - pointsDict.get("player2")) < 2);

        System.out.println("player1: " + pointsDict.get("player1"));
        System.out.println("player2: " + pointsDict.get("player2"));
        return (pointsDict.get("player1") > pointsDict.get("player1")) ? gamePlayer1 : gamePlayer2;
    }

    /**
     * <p>Simulate playing a point between two players.
     * Use random number considering ELO to determine winner.</p>
     * @return Point winner as String
     */
    private String playPoint() {
        // Calculate weighting based on ELO difference
        double eloDifference = this.gamePlayer1.getElo() - this.gamePlayer2.getElo();
        double player1WinChance = getWinChance(eloDifference);

        // Generate random value between 0 and 1
        Random random = new Random();
        double randomDouble = random.nextDouble();

        // Decide winner and return Player obj
        return (randomDouble <= player1WinChance) ? "player1" : "player2";
    }

    /**
     * <p>Use logistic function to calculate chance of winning any given point.</p>
     * @param eloDifference Difference between both players' ELO
     * @return Win chance, double between 0 and 1
     */
    public static double getWinChance(double eloDifference) {
        return 1 / (1 + Math.pow(10, -eloDifference/400));
    }
}
