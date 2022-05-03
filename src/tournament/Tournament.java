package tournament;

import player.Player;

import java.util.ArrayList;
import java.util.Comparator;

public class Tournament extends SeededBracket {
    public ArrayList<Player> players = new ArrayList<>();

    public void addPlayer(String firstName, String lastName, int elo) {
        Player newPlayer = new Player(firstName, lastName, elo);
        players.add(newPlayer);
    }

    public void createPairings() {
    }

    public void createPairings(int byes) {
        // Sort players from the lowest Elo to the highest Elo
        players.sort(new SortByElo());

        // Pairings should be a high Elo player versus a low Elo player
        Match[] matches = createMatches();
        for (int i = 0; i < byes; i++) {
            matches[i] = new Match(players.get(i));
        }
        for (int j = 0; j < (players.size()-byes)/2; j++) {
            matches[byes+j] = new Match(players.get(byes+j), players.get(players.size()-1-byes-j));
        }

        for (Match match:matches) {
            System.out.println(match.toString());
        }

    }

    private Match[] createMatches() {
        // Create an array of matches based on how many players are in the tournament
         return new Match[(int) Math.ceil(players.size()/2.0)];
    }

    public int neededByes() {
        return (int) Math.pow(2, Math.ceil( Math.log(players.size()) / Math.log(2) )) - players.size();
    }
}

class SortByElo implements Comparator<Player> {
    // Sorts in order from highest to lowest Elo
    public int compare(Player player1, Player player2) {
        return player2.getElo() - player1.getElo();
    }
}