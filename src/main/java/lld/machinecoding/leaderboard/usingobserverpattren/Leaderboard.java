package lld.machinecoding.leaderboard.usingobserverpattren;

import java.util.*;

public class Leaderboard {

    private Map<String, Player> players = new HashMap<>();
    private Map<String, User> users = new HashMap<>();

    private TreeSet<User> leaderboard;

    public Leaderboard() {
        leaderboard = new TreeSet<>((a, b) -> {
            if (a.totalScore != b.totalScore) {
                return b.totalScore - a.totalScore; // score DESC
            }
            return a.userId.compareTo(b.userId); // tie-break
        });
    }

    // ---------------- addUser ----------------
    public void addUser(String userId, List<String> playerIds) {

        User user = new User(userId);
        users.put(userId, user);

        for (String pid : playerIds) {
            Player p = players.computeIfAbsent(pid, k -> new Player(pid));
            p.addObserver(user);
            user.totalScore += p.score; // reflect existing score
        }

        leaderboard.add(user);
    }

    // ---------------- addScore ----------------
    public void addScore(String playerId, int delta) {

        Player p = players.computeIfAbsent(playerId, k -> new Player(playerId));

        // Before updating users, remove them from leaderboard
        for (PlayerObserver obs : p.observers) {
            leaderboard.remove((User) obs);
        }

        p.addScore(delta); // notifies users

        // Reinsert updated users
        for (PlayerObserver obs : p.observers) {
            leaderboard.add((User) obs);
        }
    }

    // ---------------- getTopK ----------------
    public List<String> getTopK(int k) {
        List<String> result = new ArrayList<>();
        int count = 0;

        for (User u : leaderboard) {
            result.add(u.userId);
            count++;
            if (count == k) break;
        }
        return result;
    }
}
