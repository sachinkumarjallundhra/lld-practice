package lld.machinecoding.leaderboard.usingobserverpattren;

class User implements PlayerObserver {
    String userId;
    int totalScore = 0;

    User(String userId) {
        this.userId = userId;
    }

    @Override
    public void onPlayerScoreChanged(int delta) {
        totalScore += delta;
    }
}
