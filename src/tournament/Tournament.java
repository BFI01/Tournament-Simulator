package tournament;

import player.Player;

import java.util.ArrayList;
import java.util.Comparator;

public class Tournament {
    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Player> out = new ArrayList<>();
    private final ArrayList<Match[]> rounds = new ArrayList<>();

    private void addRound(int byes) {
        // Create an array of matches based on how many players are in the tournament
        rounds.add(new Match[byes + (int) Math.ceil((getPlayers().size()-byes)/2.0)]);
    }

    private void addRound() {
        rounds.add(new Match[getNumberOfMatches(rounds.size()-1)/2]);
    }

    public void addPlayer(String firstName, String lastName, int gender, int elo) {
        Player newPlayer = new Player(firstName, lastName, gender, elo);
        getPlayers().add(newPlayer);
    }

    public void assignMatches() {
        addRound();
        for (int i = 0; i < getPlayers().size()/2; i++) {
            Match match = new Match();
            match.setMatchNumber(i);
            for (Match lastRoundMatch : rounds.get(rounds.size()-2)) {
                if (i == Math.floor(lastRoundMatch.getMatchNumber()/2.0)) {
                    match.addPlayer(lastRoundMatch.getWinner());
                }
                if (match.getPlayers().length == 2) {
                    break;
                }
            }
            rounds.get(rounds.size()-1)[i] = match;
        }
    }

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

    public int getNeededByes() {
        return (int) Math.pow(2, Math.ceil( Math.log(getPlayers().size()) / Math.log(2) )) - getPlayers().size();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getNumberOfMatches(int round) {
        return rounds.get(round).length;
    }

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

class SortByElo implements Comparator<Player> {
    // Sorts in order from highest to lowest Elo
    public int compare(Player player1, Player player2) {
        return player2.getElo() - player1.getElo();
    }
}

class SortByWins implements Comparator<Player> {
    // Sorts in order from highest to lowest wins
    public int compare(Player player1, Player player2) {
        return player2.getMatchWins() - player1.getMatchWins();
    }
}