package lld.machinecoding.leaderboard;

import java.util.*;

public class FantasyLeaderboard {

    private static class User {
        String userId;
        int score;

        User(String userId) {
            this.userId = userId;
            this.score = 0;
        }
    }

    private Map<String, Integer> playerScore = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private Map<String, Set<User>> playerToUsers = new HashMap<>();

    private TreeSet<User> leaderboard;


    public FantasyLeaderboard() {
        leaderboard = new TreeSet<>((a, b) -> {
            if (a.score != b.score) {
                return Integer.compare(b.score, a.score); // desc
            }
            return a.userId.compareTo(b.userId); // asc
        });
    }

    public void addUser(String userId, List<String> playerIds) {
        User user = new User(userId);

        // Compute initial score based on existing player scores
        int initialScore = 0;
        for (String playerId : playerIds) {
            int ps = playerScore.getOrDefault(playerId, 0);
            initialScore += ps;

            playerToUsers
                    .computeIfAbsent(playerId, k -> new HashSet<>())
                    .add(user);
        }

        user.score = initialScore;

        users.put(userId, user);
        leaderboard.add(user);
    }

    public void addScore(String playerId, int score) {
        int newScore = playerScore.getOrDefault(playerId, 0) + score;
        playerScore.put(playerId, newScore);

        Set<User> affectedUsers = playerToUsers.get(playerId);
        if (affectedUsers == null) return;

        for (User user : affectedUsers) {
            // Re-balance TreeSet: remove -> update -> reinsert
            leaderboard.remove(user);
            user.score += score;
            leaderboard.add(user);
        }
    }

    public List<String> getTopK(int k) {
        List<String> result = new ArrayList<>();
        int count = 0;

        for (User user : leaderboard) {
            result.add(user.userId);
            count++;
            if (count == k) break;
        }
        return result;
    }
}
