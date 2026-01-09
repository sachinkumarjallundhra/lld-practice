# Design a Complete Banking System

## 📜 Problem Statement

Design an in-memory banking system that supports:

### Level 1 – Basic Operations
- Create accounts
- Deposit money
- Transfer money between accounts

### Level 2 – Ranking
- Return top spenders based on outgoing transactions

### Level 3 – Scheduled Payments
- Schedule payments with cashback
- Track payment status
- Process scheduled payments

### Level 4 – Account Merging
- Merge two accounts into one
- Preserve balances, transaction history, and scheduled payments

All operations are timestamp-based and executed in memory.

---

## 🧠 Design Overview

The system models real-world banking behavior:
- Each **Account** owns its balance, outgoing total, and scheduled payments
- **Payments** transition through lifecycle states
- Rankings are computed using outgoing totals
- Merging accounts preserves financial correctness

---

## 🏗️ Core Entities

### Account
```java
class Account {
    String accountId;
    int balance;
    int totalOutgoing;
    List<Payment> payments = new ArrayList<>();

    Account(String accountId) {
        this.accountId = accountId;
        this.balance = 0;
        this.totalOutgoing = 0;
    }
}
```

---

### Payment

```java
class Payment {
    String paymentId;
    String fromAccount;
    String toAccount;
    int amount;
    double cashbackPercentage;
    int scheduledTime;
    PaymentStatus status;

    Payment(String paymentId, String from, String to, int amount,
            double cashback, int time) {
        this.paymentId = paymentId;
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
        this.cashbackPercentage = cashback;
        this.scheduledTime = time;
        this.status = PaymentStatus.SCHEDULED;
    }
}
```

---

### PaymentStatus

```java
enum PaymentStatus {
    SCHEDULED,
    PROCESSED,
    FAILED
}
```

---

## 💻 Full Java Implementation

```java
import java.util.*;

public class BankingSystem {

    private Map<String, Account> accounts = new HashMap<>();
    private int paymentCounter = 1;

    // ---------------- LEVEL 1 ----------------

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

    public Optional<Integer> transfer(String fromId, String toId, int timestamp, int amount) {
        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);

        if (from == null || to == null || from.balance < amount) {
            return Optional.empty();
        }

        from.balance -= amount;
        to.balance += amount;
        from.totalOutgoing += amount;

        return Optional.of(from.balance);
    }

    // ---------------- LEVEL 2 ----------------

    public List<String> topSpenders(int timestamp, int n) {
        List<Account> list = new ArrayList<>(accounts.values());

        list.sort((a, b) -> {
            if (b.totalOutgoing != a.totalOutgoing) {
                return b.totalOutgoing - a.totalOutgoing;
            }
            return a.accountId.compareTo(b.accountId);
        });

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(n, list.size()); i++) {
            result.add(list.get(i).accountId);
        }
        return result;
    }

    // ---------------- LEVEL 3 ----------------

    public void schedulePayment(String accountId, String targetAccId,
                                int timestamp, int amount, double cashbackPercentage) {
        Account acc = accounts.get(accountId);
        if (acc == null) return;

        String paymentId = "PAY-" + paymentCounter++;
        Payment payment = new Payment(
                paymentId, accountId, targetAccId,
                amount, cashbackPercentage, timestamp
        );

        acc.payments.add(payment);
    }

    public void processScheduledPayments(int currentTimestamp) {
        for (Account acc : accounts.values()) {
            for (Payment p : acc.payments) {
                if (p.status != PaymentStatus.SCHEDULED) continue;
                if (p.scheduledTime > currentTimestamp) continue;

                Account from = accounts.get(p.fromAccount);
                Account to = accounts.get(p.toAccount);

                if (from == null || to == null || from.balance < p.amount) {
                    p.status = PaymentStatus.FAILED;
                    continue;
                }

                from.balance -= p.amount;
                to.balance += p.amount;
                from.totalOutgoing += p.amount;

                int cashback = (int) (p.amount * p.cashbackPercentage / 100.0);
                from.balance += cashback;

                p.status = PaymentStatus.PROCESSED;
            }
        }
    }

    public String getPaymentStatus(String accountId, int timestamp, String paymentId) {
        Account acc = accounts.get(accountId);
        if (acc == null) return "NOT_FOUND";

        for (Payment p : acc.payments) {
            if (p.paymentId.equals(paymentId)) {
                return p.status.name();
            }
        }
        return "NOT_FOUND";
    }

    // ---------------- LEVEL 4 ----------------

    public void mergeAccounts(String accountId1, String accountId2) {
        if (!accounts.containsKey(accountId1) || !accounts.containsKey(accountId2)) {
            return;
        }

        Account a1 = accounts.get(accountId1);
        Account a2 = accounts.get(accountId2);

        a1.balance += a2.balance;
        a1.totalOutgoing += a2.totalOutgoing;
        a1.payments.addAll(a2.payments);

        accounts.remove(accountId2);
    }
}
```

---

## 🧩 Design Patterns Used

* **Single Responsibility Principle**
* **Encapsulation**
* **Aggregation (Account owns Payments)**
* **Domain Driven Design**
* **Command-style processing (timestamp-based)**

---

## ⚠️ Edge Cases Covered

* Duplicate account creation
* Insufficient balance transfers
* Payment failures
* Cashback calculation
* Ranking ties
* Merging accounts with active payments

---

## ⏱️ Time Complexity

| Operation        | Complexity |
| ---------------- | ---------- |
| Create Account   | O(1)       |
| Deposit          | O(1)       |
| Transfer         | O(1)       |
| Top Spenders     | O(N log N) |
| Schedule Payment | O(1)       |
| Process Payments | O(P)       |
| Merge Accounts   | O(P)       |

---

## ✅ Interview Notes

**Why store `List<Payment>` inside Account?**

> Payments are owned by the account that initiates them.
> This simplifies lookup, merging, validation, and lifecycle management.

---

## 🚀 Possible Extensions

* Transaction history
* Interest calculation
* Account limits
* Fraud detection
* Persistent storage

