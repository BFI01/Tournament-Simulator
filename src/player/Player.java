package player;

public class Player {
    private String firstName;
    private String lastName;
    private int elo;

    public void adjustElo(double expectedWins, double wins) {
        // K-factor is max change in Elo per adjustment. Formally set to 16 for pros and 32 for weaker players
        int K = 32;
        double newElo = this.getElo() + (K * (wins - expectedWins));
        this.setElo((int) Math.round(newElo));
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

