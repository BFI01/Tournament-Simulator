package player;

/**
 * Contains player data and ability to change their Elo rating.
 */
public class Player {
    /**
     * First name of the {@link Player}.
     */
    private final String firstName;
    /**
     * Last name of the {@link Player}.
     */
    private final String lastName;
    /**
     * Gender is an integer to allow for non-binary genders if necessary.
     */
    private final int gender;
    /**
     * Player skill rating based on the Elo Rating System
     * <p>
     * Source: <a href="https://en.wikipedia.org/wiki/Elo_rating_system">Elo Rating System</a>
     */
    private int elo;
    /**
     * Indicates the number of matches a {@link Player} has won.
     */
    private int wins = 0;

    /**
     * Initialises {@link Player} object.
     *
     * @param firstName First name of player.
     * @param lastName Last name of player.
     * @param gender 0: Male, 1: Female, >1: Non-binary (other).
     * @param elo Elo rating of the player.
     */
    public Player(String firstName, String lastName, int gender, int elo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.elo = elo;
    }

    /**
     * Changes Elo rating based on the expected games a player will win during a match compared
     * to the actual games they won.
     * <p>
     * Source: <a href="https://en.wikipedia.org/wiki/Elo_rating_system#cite_note-27">Elo Rating System</a>
     *
     * @param expectedWins Number of games a player is expected to win based on their Elo.
     * @param wins Number of games the player won during the match.
     */
    public void adjustElo(double expectedWins, double wins) {
        // K-factor is max change in Elo per adjustment. Formally set to 16 for pros and 32 for weaker players
        int K = 32;
        int newElo = (int) Math.round(getElo() + (K * (wins - expectedWins)));
        System.out.printf("[ELO] Updated %s %s's Elo from %d to %d (%d)%n",
                getFirstName(),
                getLastName(),
                getElo(),
                newElo,
                newElo-getElo());
        setElo(newElo);
    }

    /**
     * Getter for {@link Player#elo}.
     *
     * @return {@link Player#elo}
     */
    public int getElo() {
        return elo;
    }

    /**
     * Setter for {@link Player#elo}
     *
     * @param elo int value
     */
    public void setElo(int elo) {
        this.elo = elo;
    }

    /**
     * Getter for {@link Player#firstName}.
     *
     * @return {@link Player#firstName}
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Getter for {@link Player#lastName}.
     *
     * @return {@link Player#lastName}
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Getter for {@link Player#gender}.
     *
     * @return {@link Player#gender}
     */
    public int getGender() {
        return gender;
    }

    /**
     * Getter for {@link Player#wins}.
     *
     * @return {@link Player#wins}
     */
    public int getWins() {
        return wins;
    }

    /**
     * Increments {@link Player#wins} by 1.
     */
    public void addWin() {
        wins++;
    }

    /**
     * {@code toString()} override for {@link Player}.
     *
     * @return Readable {@code String} showing details about {@link Player} object
     */
    public String toString() {
        return String.format("Player(firstName: %s, lastName: %s, elo: %d, wins: %d)",
                firstName,
                lastName,
                elo,
                wins);
    }
}

