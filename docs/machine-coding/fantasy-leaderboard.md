# 🏆 Design a Leaderboard for Fantasy Teams

## 📜 Problem Statement

Design an **in-memory leaderboard** for a fantasy sports application.

* Each user creates **exactly one team**
* A team consists of **one or more players**
* Players gain or lose points during a live match
* A user’s score is the **sum of scores of all players in their team**
* The system must support **Top-K leaderboard queries**

---

## 📌 Rules & Constraints

* Each user has **exactly one team**
* A player can belong to **multiple users’ teams**
* Initial user score is **0**
* Player score updates are **deltas** (positive or negative)
* Leaderboard sorting:

    1. **Total score (descending)**
    2. **UserId (lexicographically ascending)**

---

## 🧠 Key Design Insight

This is a **many-to-many relationship**:

* One player → many users
* One user → many players

Whenever a player’s score changes, **all dependent users must be updated immediately**.

This naturally fits the **Observer Design Pattern**.

---

## 🏗️ Design Overview

### Core Entities

#### 1️⃣ Player

* Maintains current score
* Knows which users depend on it

#### 2️⃣ User

* Represents a fantasy team
* Observes players
* Maintains total team score

#### 3️⃣ Leaderboard

* Maintains sorted users
* Supports Top-K queries efficiently

---

## 🧩 Design Patterns Used

### ✅ Observer Pattern

* `Player` = Subject
* `User` = Observer
* Player score updates notify all users automatically

### ✅ Sorted Set (TreeSet)

* Keeps leaderboard **always sorted**
* Ensures fast Top-K retrieval

---

## 🧱 Data Structures Used

| Structure              | Purpose              |
| ---------------------- | -------------------- |
| `Map<String, Player>`  | Player registry      |
| `Map<String, User>`    | User registry        |
| `TreeSet<User>`        | Sorted leaderboard   |
| `List<PlayerObserver>` | Observers per player |

---

## 💻 Java Implementation


### Player

```java
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
```

---

### PlayerObserver Interface

```java
interface PlayerObserver {
    void onPlayerScoreChanged(int delta);
}
```

---

### User

```java
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
```

---
### Leaderboard
```java
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

        // Remove affected users before update
        for (PlayerObserver obs : p.observers) {
            leaderboard.remove((User) obs);
        }

        p.addScore(delta); // notify observers

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
```

---



## 🧪 Example Walkthrough

```text
addUser("uA", ["p1", "p2"])
addUser("uB", ["p2"])

getTopK(2)
→ ["uA", "uB"]

addScore("p2", 10)
→ uA = 10, uB = 10

addScore("p1", 3)
→ uA = 13, uB = 10

getTopK(1)
→ ["uA"]

addScore("p2", -5)
→ uA = 8, uB = 5
```

---

## ⚠️ Edge Cases Handled

* Player score updates before user registration
* Negative score deltas
* Tie-breaking using lexicographical order
* `k` greater than number of users

---

## ⏱️ Time & Space Complexity

| Operation | Complexity                 |
| --------- | -------------------------- |
| addUser   | O(p log n)                 |
| addScore  | O(u log n)                 |
| getTopK   | O(k)                       |
| Memory    | O(users + players + edges) |

Where:

* `p` = players per team
* `u` = users observing a player
* `n` = total users

---

## ✅ Why This Solution Is Strong

✔ Clean separation of concerns
✔ Real-time leaderboard consistency
✔ Efficient Top-K queries
✔ Observer pattern fits domain perfectly
✔ Interview-grade design clarity

---
