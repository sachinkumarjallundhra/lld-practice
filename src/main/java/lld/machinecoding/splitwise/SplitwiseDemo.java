package lld.machinecoding.splitwise;

import java.util.*;

class User {
    final String id;
    final String name;
    User(String id, String name) { this.id = id; this.name = name; }
}

enum ExpenseType { EQUAL, EXACT, PERCENT }

class ExpenseService {
    private final Map<String, User> users = new HashMap<>();
    // balances[u1][u2] = amount u1 owes u2 (positive)
    private final Map<String, Map<String, Double>> balances = new HashMap<>();

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
        balances.putIfAbsent(id, new HashMap<>());
    }

    public void addExpense(String paidBy, double amount, List<String> involved,
                           ExpenseType type, List<Double> splits) {
        int n = involved.size();
        if (type == ExpenseType.EQUAL) {
            double share = Math.round(amount * 100.0 / n) / 100.0;
            for (String u : involved) {
                if (u.equals(paidBy)) continue;
                addBalance(u, paidBy, share);
            }
        } else if (type == ExpenseType.EXACT) {
            for (int i = 0; i < n; i++) {
                String u = involved.get(i);
                if (u.equals(paidBy)) continue;
                addBalance(u, paidBy, splits.get(i));
            }
        } else { // percent
            for (int i = 0; i < n; i++) {
                String u = involved.get(i);
                double share = amount * splits.get(i) / 100.0;
                if (u.equals(paidBy)) continue;
                addBalance(u, paidBy, share);
            }
        }
    }

    private void addBalance(String from, String to, double amt) {
        balances.putIfAbsent(from, new HashMap<>());
        balances.putIfAbsent(to, new HashMap<>());
        Map<String, Double> fromMap = balances.get(from);
        Map<String, Double> toMap = balances.get(to);
        double existing = fromMap.getOrDefault(to, 0.0);
        existing += amt;
        fromMap.put(to, existing);
        // normalize opposite direction
        double opp = toMap.getOrDefault(from, 0.0);
        if (opp > 0) {
            if (opp >= existing) {
                toMap.put(from, opp - existing);
                fromMap.put(to, 0.0);
            } else {
                fromMap.put(to, existing - opp);
                toMap.put(from, 0.0);
            }
        }
    }

    public void showBalances() {
        for (String u : balances.keySet()) {
            for (Map.Entry<String, Double> e : balances.get(u).entrySet()) {
                if (e.getValue() > 0.0) {
                    System.out.printf("%s owes %s : %.2f\n", u, e.getKey(), e.getValue());
                }
            }
        }
    }

    // convenience
    public static void main(String[] args) {
        ExpenseService s = new ExpenseService();
        s.addUser("u1","Alice"); s.addUser("u2","Bob"); s.addUser("u3","Carol");
        s.addExpense("u1", 120.0, Arrays.asList("u1","u2","u3"), ExpenseType.EQUAL, null);
        s.showBalances();
    }
}
