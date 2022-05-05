package player;

public class Player {
    private final String firstName;
    private final String lastName;
    private final int gender;
    private int elo;
    private int wins = 0;

    public Player (String firstName, String lastName, int gender, int elo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.elo = elo;
    }

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

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getGender() {
        return gender;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        wins++;
    }

    public String toString() {
        return String.format("Player(firstName: %s, lastName: %s, elo: %d, wins: %d)",
                firstName,
                lastName,
                elo,
                wins);
    }
}

