package lld.machinecoding.leaderboard.usingobserverpattren;

import java.util.ArrayList;
import java.util.List;

class Player {
    String playerId;
    int score = 0;
    List<PlayerObserver> observers = new ArrayList<>();

    Player(String playerId) {
        this.playerId = playerId;
    }

    void addObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    void addScore(int delta) {
        score += delta;
        notifyObservers(delta);
    }

    private void notifyObservers(int delta) {
        for (PlayerObserver obs : observers) {
            obs.onPlayerScoreChanged(delta);
        }
    }
}
