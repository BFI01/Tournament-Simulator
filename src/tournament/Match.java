package tournament;

import player.Player;

import java.util.HashMap;

/**
 * Defines a match of table tennis, which is the best of 7 games.
 *
 * @see tournament.Game
 */
public class Match extends Game {
    /**
     * Using a hashmap rather than storing wins in the {@link Player} object enforces
     * single responsibility and
     * <a href="https://alvinalexander.com/java/java-law-of-demeter-java-examples/">LoD</a>
     * by not relying on calling a {@code Match.getWinner().getGameWins()} in classes that
     * create {@link Match} objects for example.
     */
    private final HashMap<Player, Integer> gamesDict = new HashMap<>();
    /**
     * Player 1 of 2 players - there may not always be a {@code matchPlayer1 on object initialisation.
     */
    private Player matchPlayer1;
    /**
     * Player 1 of 2 players - there may not always be a {@code matchPlayer2} in cases of bye matches.
     */
    private Player matchPlayer2;
    /**
     * Used to differentiate {@link Match} objects between tournament rounds.
     */
    private int matchNumber;
    /**
     * Status of whether {@link Match} is in progress or finished/not started.
     */
    private boolean onGoing = false;

    /**
     * {@link Player} who won the match.
     */
    private Player winner = null;

    /**
     * Initialises a {@link Match} with two players.
     *
     * @param matchPlayer1 Player 1 of 2 players in the match
     * @param matchPlayer2 Player 2 of 2 players in the match
     */
    public Match(Player matchPlayer1, Player matchPlayer2) {
        this.matchPlayer1 = matchPlayer1;
        this.matchPlayer2 = matchPlayer2;
    }

    /**
     * Initialises a {@link Match} with a single player.
     * <p>
     * Used for bye matches.
     *
     * @param matchPlayer Binds to {@link Match#matchPlayer1}, {@link Match#matchPlayer2} is null.
     */
    public Match(Player matchPlayer) {
        this.matchPlayer1 = matchPlayer;
        this.matchPlayer2 = null;
    }

    /**
     * Initialises a {@link Match} with no players.
     * <p>
     * Used when you don't know what {@link Player} objects will be playing in the match.
     */
    public Match() {
        this.matchPlayer1 = null;
        this.matchPlayer2 = null;
    }

    /**
     * Used to update a {@link Player} object without breaking
     * <a href="https://alvinalexander.com/java/java-law-of-demeter-java-examples/">LoD</a>.
     * <p>
     * Calls {@link Player#addWin()} if {@code wonMatch} is true and adjusts Elo.
     *
     * @param player {@link Player} object that needs updating.
     * @param wonMatch Boolean whether that player won the match.
     * @param expectedGameWins How many games they were expected to win.
     * @param gameWins How many games they actually won.
     */
    private void updatePlayer(Player player, boolean wonMatch, int expectedGameWins, int gameWins) {
        if (wonMatch) {
            player.addWin();
        }
        player.adjustElo(expectedGameWins, gameWins);
    }

    /**
     * Adds a {@link Player} to the match.
     * <p>
     * If no players are in the match then the {@code matchPlayer} is bound to {@link Match#matchPlayer1}.
     * <p>
     * If {@link Match#matchPlayer1} is set, {@code matchPlayer} is bound to {@link Match#matchPlayer2} instead.
     *
     * @param matchPlayer {@link Player} to be added.
     *
     * @throws IndexOutOfBoundsException Thrown when {@code addPlayer()} is called when there are
     * already 2 players in the match.
     */
    public void addPlayer(Player matchPlayer) {
        // Adding first player
        if (matchPlayer1 == null) {
            matchPlayer1 = matchPlayer;
        // Adding second player
        } else if (matchPlayer2 == null) {
            matchPlayer2 = matchPlayer;
        // Handling in case of third addPlayer call per object
        } else {
            throw new IndexOutOfBoundsException("This match already has two players!");
        }
    }

    /**
     * Simulates playing a {@link Match}.
     *
     * @return {@link Player} object of the player who lost the match.
     */
    public Player playMatch() {
        // Prevent playMatch from running with zero players
        if (matchPlayer1 == null) {
            System.out.println("Cannot play a Match with no players. Use Match.addPlayer() first!");
            return null;
        }
        // Catch bye case
        if (matchPlayer2 == null) {
            // When a match contains a bye, the player immediately moves on to the next round
            System.out.printf("[TOURNEY] %s %s gets a bye and moves on to the next round%n",
                    matchPlayer1.getFirstName(),
                    matchPlayer1.getLastName());
            setWinner(matchPlayer1);
            matchPlayer1.addWin();
            return null;
        }
        // Set each players' game wins to 0
        gamesDict.put(matchPlayer1, 0);
        gamesDict.put(matchPlayer2, 0);

        // Max games to be played, in this case it is best of 7
        int maxGames = 7;
        int winsNeeded = (maxGames + 1) / 2;

        // Calculate expected wins so Elo can be adjusted after each match
        double eloDifference = matchPlayer1.getElo() - matchPlayer2.getElo();
        int player1ExpectedWins = (int) Math.round(getWinChance(eloDifference) * winsNeeded);

        do {
            // Play a game
            Player gameWinner = playGame(matchPlayer1, matchPlayer2);
            // Update the game winner's game wins
            Integer winnerPoints = gamesDict.get(gameWinner);
            gamesDict.put(gameWinner, winnerPoints + 1);
            System.out.printf("[GAME] %s vs %s: %d-%d%n",
                    matchPlayer1.getLastName(),
                    matchPlayer2.getLastName(),
                    gamesDict.get(matchPlayer1),
                    gamesDict.get(matchPlayer2));
        }
        // Repeat until one player reaches the required game wins to win the match
        while (gamesDict.get(matchPlayer1) < winsNeeded &&
               gamesDict.get(matchPlayer2) < winsNeeded);

        // Handle updating Match and Player properties according to who won and lost the match
        Player loser;
        if ((gamesDict.get(matchPlayer1) > gamesDict.get(matchPlayer2))) {
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
        updatePlayer(matchPlayer1, (matchPlayer2 == loser), player1ExpectedWins, gamesDict.get(matchPlayer1));
        updatePlayer(matchPlayer2, (matchPlayer1 == loser), winsNeeded-player1ExpectedWins, gamesDict.get(matchPlayer2));
        setOnGoing(false);
        return loser;
    }

    /**
     * Gets an array of the players in the match.
     * <p>
     * The length of the array will be how many players are in the match.
     *
     * @return Array of {@link Player} objects
     */
    public Player[] getPlayers() {
        // Two players in the match
        if (matchPlayer2 != null) {
            return new Player[]{matchPlayer1, matchPlayer2};
        // One player in the match
        } else if (matchPlayer1 != null) {
            return new Player[]{matchPlayer1};
        // No players in the match (returns empty array)
        } else {
            return new Player[0];
        }
    }

    /**
     * Getter for {@link Match#matchNumber}.
     *
     * @return {@link Match#matchNumber}
     */
    public int getMatchNumber() {
        return matchNumber;
    }

    /**
     * Setter for {@link Match#matchNumber}.
     *
     * @param number Integer to set {@link Match#matchNumber} to.
     */
    public void setMatchNumber(int number) {
        matchNumber = number;
    }

    /**
     * Getter for {@link Match#onGoing}.
     *
     * @return {@link Match#onGoing}
     */
    public boolean isOnGoing() {
        return onGoing;
    }

    /**
     * Setter for {@link Match#onGoing}.
     *
     * @param onGoing Boolean to set {@link Match#onGoing} to.
     */
    public void setOnGoing(boolean onGoing) {
        this.onGoing = onGoing;
    }

    /**
     * Getter for {@link Match#winner}.
     *
     * @return {@link Match#winner}
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Setter for {@link Match#winner}.
     *
     * @param winner {@link Player} object to set {@link Match#winner} to.
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * {@code toString()} override for {@link Match}.
     *
     * @return Readable {@code String} showing details about {@link Match} object
     */
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