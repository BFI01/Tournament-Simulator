public class Player {
    private int elo;
    private int points;

    public Player(int elo) {
        this.setElo(elo);
    }

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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

