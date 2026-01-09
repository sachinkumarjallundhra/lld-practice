package lld.machinecoding.bankingsystem;

import java.util.*;

public class BankingSystem {

    // ---------- ENUM ----------
    enum PaymentStatus {
        SCHEDULED,
        PROCESSED,
        FAILED
    }

    // ---------- PAYMENT ----------
    class Payment {
        String paymentId;
        String fromId;
        String toId;
        int amount;
        double cashback;
        int timestamp;
        PaymentStatus status;

        Payment(String pid, String from, String to, int amt, double cashback, int ts) {
            this.paymentId = pid;
            this.fromId = from;
            this.toId = to;
            this.amount = amt;
            this.cashback = cashback;
            this.timestamp = ts;
            this.status = PaymentStatus.SCHEDULED;
        }
    }

    // ---------- ACCOUNT ----------
    class Account {
        String id;
        int balance = 0;
        int totalOutgoing = 0;
        List<Payment> scheduledPayments = new ArrayList<>();

        Account(String id) {
            this.id = id;
        }
    }

    // ---------- STORAGE ----------
    private Map<String, Account> accounts = new HashMap<>();
    private Map<String, Payment> payments = new HashMap<>();
    private int paymentCounter = 1;

    // ---------- LEVEL 1 ----------
    public boolean createAccount(String accountId, int timestamp) {
        if (accounts.containsKey(accountId)) return false;
        accounts.put(accountId, new Account(accountId));
        return true;
    }

    public Optional<Integer> deposit(String accountId, int timestamp, int amount) {
        Account acc = accounts.get(accountId);
        if (acc == null) return Optional.empty();
        acc.balance += amount;
        return Optional.of(acc.balance);
    }

    public Optional<Boolean> transfer(String fromId, String toId, int timestamp, int amount) {
        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);
        if (from == null || to == null || from.balance < amount) {
            return Optional.empty();
        }
        from.balance -= amount;
        to.balance += amount;
        from.totalOutgoing += amount;
        return Optional.of(true);
    }

    // ---------- LEVEL 2 ----------
    public List<String> topSpenders(int timestamp, int n) {
        List<Account> list = new ArrayList<>(accounts.values());
        list.sort((a, b) -> {
            if (b.totalOutgoing != a.totalOutgoing)
                return b.totalOutgoing - a.totalOutgoing;
            return a.id.compareTo(b.id);
        });

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, list.size()); i++) {
            result.add(list.get(i).id);
        }
        return result;
    }

    // ---------- LEVEL 3 ----------
    public void schedulePayment(String accountId, String targetId,
                                int timestamp, int amount, double cashbackPercentage) {

        String pid = "P" + paymentCounter++;
        Payment payment = new Payment(
                pid, accountId, targetId, amount, cashbackPercentage, timestamp
        );

        payments.put(pid, payment);
        accounts.get(accountId).scheduledPayments.add(payment);
    }

    public String getPaymentStatus(String accountId, int timestamp, String paymentId) {
        Payment p = payments.get(paymentId);
        if (p == null || !p.fromId.equals(accountId)) return "FAILED";
        return p.status.name();
    }

    public void processScheduledPayments(int currentTimestamp) {
        for (Payment p : payments.values()) {
            if (p.status != PaymentStatus.SCHEDULED) continue;
            if (p.timestamp > currentTimestamp) continue;

            Account from = accounts.get(p.fromId);
            Account to = accounts.get(p.toId);

            if (from == null || to == null || from.balance < p.amount) {
                p.status = PaymentStatus.FAILED;
                continue;
            }

            from.balance -= p.amount;
            to.balance += p.amount;
            from.totalOutgoing += p.amount;

            int cashback = (int) (p.amount * p.cashback);
            from.balance += cashback;

            p.status = PaymentStatus.PROCESSED;
        }
    }

    // ---------- LEVEL 4 ----------
    public void mergeAccounts(String id1, String id2) {
        if (!accounts.containsKey(id1) || !accounts.containsKey(id2)) return;

        Account a = accounts.get(id1);
        Account b = accounts.get(id2);

        a.balance += b.balance;
        a.totalOutgoing += b.totalOutgoing;
        a.scheduledPayments.addAll(b.scheduledPayments);

        // Update payment references
        for (Payment p : payments.values()) {
            if (p.fromId.equals(id2)) p.fromId = id1;
            if (p.toId.equals(id2)) p.toId = id1;
        }

        accounts.remove(id2);
    }
}

