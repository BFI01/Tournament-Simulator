package tournament;

import components.SortByElo;
import components.SortByWins;
import player.Player;

import java.util.ArrayList;

/**
 * Defines a bracket-style tournament that can handle any number of players by making use of
 * <a href="https://www.printyourbrackets.com/how-byes-work-in-a-tournament.html">byes</a>.
 */
public class Tournament {
    /**
     * {@link ArrayList} of {@link Player} objects representing all players who are not
     * out of the tournament.
     */
    private final ArrayList<Player> players = new ArrayList<>();
    /**
     * {@link ArrayList} of {@link Player} objects representing all players who
     * lost a {@link Match} and are out of the tournament.
     */
    private final ArrayList<Player> out = new ArrayList<>();
    /**
     * 2-dimensional {@link ArrayList} of {@link Match} objects representing all matches in each
     * round.
     */
    private final ArrayList<Match[]> rounds = new ArrayList<>();

    /**
     * Add an empty array of {@link Match} objects to {@link Tournament#rounds} with the
     * appropriate number of bye matches and normal matches.
     * <p>
     * This is a {@link Match} for each bye plus the ceiling division of half (the number of players
     * - the number of byes).
     *
     * @param byes integer number of byes to include in the round.
     */
    private void addRound(int byes) {
        // Create an array of matches based on how many players are in the tournament
        rounds.add(new Match[byes + (int) Math.ceil((getPlayers().size()-byes)/2.0)]);
    }

    /**
     * Add an array of {@link Match} objects to {@link Tournament#rounds} with the appropriate
     * number of matches based on the last round.
     * <p>
     * This is half the number of matches is the last round.
     */
    private void addRound() {
        if (rounds.size()-1 < 0) {
            System.out.println("The first round of the tournament must include a byes argument!");
            return;
        }
        rounds.add(new Match[getNumberOfMatches(rounds.size()-1)/2]);
    }

    /**
     * Adds a player to the tournament.
     *
     * @param newPlayer {@link Player} object
     */
    public void addPlayer(Player newPlayer) {
        getPlayers().add(newPlayer);
    }

    /**
     * Add a new round to {@link Tournament#rounds} and assign all {@link Tournament#players}
     * to a {@link Match} in correspondence to the last round and the structure of a
     * <a href="https://www.interbasket.net/brackets/">bracket tournament</a>.
     */
    public void assignMatches() {
        addRound();
        // Iterate up to half the number of players still in the tournament as each match
        // has two players
        for (int i = 0; i < getPlayers().size()/2; i++) {
            Match match = new Match();
            match.setMatchNumber(i);
            // Iterate through all matches in the last round
            for (Match lastRoundMatch : rounds.get(rounds.size()-2)) {
                /* Add player to the latest created match if they are a subset of the
                 * bracket tournament tree structure. This is done by taking the match's
                 * matchNumber and halving it, as after the first round each subsequent
                 * round will contain half the number of matches.
                 *
                 * Player 1 ------------┐     Round 2     ┌------------ Player 3
                 *     Round 1, Match 0 |---- Match 0 ----| Round 1, Match 1
                 * Player 2 ------------┘       |         └------------ Player 4
                 *                              |
                 *                        Round 3, Match 0
                 *                              |
                 * Player 1 ------------┐       |         ┌------------ Player 3
                 *     Round 1, Match 2 |---- Match 1 ----| Round 1, Match 3
                 * Player 2 ------------┘     Round 2     └------------ Player 4
                 *
                 * Following the diagram, the floor division of (Match:0 / 2.0) and (Match:1 / 2.0)
                 * are both 0, and the floor division of (Match:2 / 2.0) and (Match:3 / 2.0) are
                 * both 1, et ce tera. This means the match numbers follow the list indexing
                 * exactly.
                 */
                if (i == Math.floor(lastRoundMatch.getMatchNumber()/2.0)) {
                    match.addPlayer(lastRoundMatch.getWinner());
                }
                // Break out the loop if a match has two players to prevent an IndexOutOfBoundsException
                // being thrown
                if (match.getPlayers().length == 2) {
                    break;
                }
            }
            rounds.get(rounds.size()-1)[i] = match;
        }
    }

    /**
     * Add a new round to {@link Tournament#rounds} and assign all {@link Tournament#players}
     * to a {@link Match} in correspondence to their Elo rating.
     * <p>
     * The top-rated players will receive a bye whilst the rest of the players will be paired
     * together as: the highest rated player with the lowest rated player, the second-highest rated
     * player with the second-lowest rated player, et ce tera.
     *
     * @param byes Number of byes to include in the round.
     */
    public void assignMatches(int byes) {
        // Sort players from the lowest Elo to the highest Elo
        getPlayers().sort(new SortByElo());

        // Pairings should be a high Elo player versus a low Elo player
        addRound(getNeededByes());
        // Bye matches
        for (int i = 0; i < byes; i++) {
            rounds.get(0)[i] = new Match(getPlayers().get(i));
            // Giving each match a unique number will allow the structure of the bracket tournament
            // to be upheld throughout the rounds
            rounds.get(0)[i].setMatchNumber(i);
        }
        // Regular matches
        for (int j = 0; j < (getPlayers().size()-byes)/2; j++) {
            /*
             * To pair players with high and low Elo's, take an ordered list of players in terms of their Elo
             * and create matches starting with the first & last player in the list, moving inwards:
             * --> [Pl1, Pl2, Pl3, Pl4, Pl5, Pl6]
             *       ^                        ^     Match A
             * --> [Pl1, Pl2, Pl3, Pl4, Pl5, Pl6]
             *            ^              ^          Match B
             * --> [Pl1, Pl2, Pl3, Pl4, Pl5, Pl6]
             *                 ^    ^               Match C
             */
            rounds.get(0)[byes+j] = new Match(getPlayers().get(byes+j), getPlayers().get(getPlayers().size()-(j+1)));
            rounds.get(0)[byes+j].setMatchNumber(byes+j);
        }
    }

    /**
     * Simulate all matches in a given round.
     *
     * @param round Round number to simulate (starting at 0).
     */
    public void playMatches(int round) {
        for (Match match : rounds.get(round)) {
            Player loser = match.playMatch();

            // Loser will be null for bye matches
            if (loser != null) {
                players.remove(loser);
                out.add(loser);
            }
        }
    }

    /**
     * Calculates the number of needed byes for the first round of a tournament based on how many
     * {@link Tournament#players} are in said tournament.
     *
     * Source: <a href="https://www.printyourbrackets.com/how-byes-work-in-a-tournament.html">Tournament Byes</a>
     *
     * @return integer number of byes needed.
     */
    public int getNeededByes() {
        // Number of byes needed is 2^n - (number of players)
        // Where n = ceiling(log_2(number of players))
        return (int) Math.pow(2, Math.ceil( Math.log(getPlayers().size()) / Math.log(2) )) - getPlayers().size();
    }

    /**
     * Getter for {@link Tournament#players}.
     *
     * @return {@link Tournament#players}
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets number of matches in a given round.
     *
     * @param round The round to lookup (starts from 0).
     * @return integer value representing the number of matches.
     */
    public int getNumberOfMatches(int round) {
        return rounds.get(round).length;
    }

    /**
     * Creates a leaderboard from the tournament based on number of wins.
     *
     * @return Sorted array of {@link Player} objects, the first element being
     * the current first place player of the tournament and the last element
     * being the last place player.
     */
    public ArrayList<Player> getLeaderboard() {
        // Concatenate out and players into a new allPlayers array
        ArrayList<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(out);
        allPlayers.addAll(players);

        // Order by wins
        allPlayers.sort(new SortByWins());

        return allPlayers;
    }
}