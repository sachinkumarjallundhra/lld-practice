# Design a Complete In-Memory Database

## 📜 Problem Statement

Design an **in-memory database** that stores records identified by a `key`.
Each record contains multiple **fields**, and each field stores a **value**.

The system evolves across 4 levels:

---

## 🧩 Levels Breakdown

### ✅ Level 1 – Basic Operations
- Set values
- Conditional updates
- Conditional deletes
- Get values

### ✅ Level 2 – Scanning & Filtering
- Scan all fields for a key
- Scan fields by prefix

### ✅ Level 3 – TTL (Time-To-Live)
- Fields can expire after a given TTL
- Expired fields are invisible

### ✅ Level 4 – Look-Back Queries
- Retrieve field values **as of a given timestamp**

---

## 🧠 Design Overview

### Key Ideas
- **Multi-version storage** per field
- **Timestamp-aware writes**
- **Lazy expiration using TTL**
- **Sorted history for look-back**

---

## 🏗️ Core Data Model

### VersionedValue
Stores historical versions of a field.

```java
class VersionedValue {
    int timestamp;
    String value;
    Integer expiryTime; // null means no TTL

    VersionedValue(int timestamp, String value, Integer expiryTime) {
        this.timestamp = timestamp;
        this.value = value;
        this.expiryTime = expiryTime;
    }

    boolean isExpired(int currentTime) {
        return expiryTime != null && currentTime >= expiryTime;
    }
}
```

---

### Database Structure

```text
Map<key,
  Map<
    field,
    List<VersionedValue>
  >
>
```

---

## 💻 Full Java Implementation (All Levels)

```java
import java.util.*;

public class InMemoryDatabase {

    private Map<String, Map<String, List<VersionedValue>>> db = new HashMap<>();

    // ---------- LEVEL 1 ----------

    public void set(int timestamp, String key, String field, String value) {
        db.computeIfAbsent(key, k -> new HashMap<>())
          .computeIfAbsent(field, f -> new ArrayList<>())
          .add(new VersionedValue(timestamp, value, null));
    }

    public boolean compareAndSet(int timestamp, String key, String field, int expectedValue) {
        String curr = get(timestamp, key, field);
        if (curr == null || !curr.equals(String.valueOf(expectedValue))) {
            return false;
        }
        set(timestamp, key, field, curr);
        return true;
    }

    public boolean compareAndDelete(int timestamp, String key, String field, int expectedValue) {
        String curr = get(timestamp, key, field);
        if (curr == null || !curr.equals(String.valueOf(expectedValue))) {
            return false;
        }
        db.get(key).get(field)
          .add(new VersionedValue(timestamp, null, null));
        return true;
    }

    public String get(int timestamp, String key, String field) {
        if (!db.containsKey(key) || !db.get(key).containsKey(field)) {
            return null;
        }
        return getLatestValue(db.get(key).get(field), timestamp);
    }

    // ---------- LEVEL 2 ----------

    public List<String> scan(int timestamp, String key) {
        List<String> result = new ArrayList<>();
        if (!db.containsKey(key)) return result;

        for (String field : db.get(key).keySet()) {
            String val = get(timestamp, key, field);
            if (val != null) {
                result.add(field + "=" + val);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<String> scanByPrefix(int timestamp, String key, String prefix) {
        List<String> result = new ArrayList<>();
        if (!db.containsKey(key)) return result;

        for (String field : db.get(key).keySet()) {
            if (!field.startsWith(prefix)) continue;
            String val = get(timestamp, key, field);
            if (val != null) {
                result.add(field + "=" + val);
            }
        }
        Collections.sort(result);
        return result;
    }

    // ---------- LEVEL 3 ----------

    public void setWithTTL(int timestamp, String key, String field, String value, int ttl) {
        int expiry = timestamp + ttl;
        db.computeIfAbsent(key, k -> new HashMap<>())
          .computeIfAbsent(field, f -> new ArrayList<>())
          .add(new VersionedValue(timestamp, value, expiry));
    }

    public boolean compareAndSetWithTTL(int timestamp, String key, String field,
                                        int expectedValue, int newValue, int ttl) {
        String curr = get(timestamp, key, field);
        if (curr == null || !curr.equals(String.valueOf(expectedValue))) {
            return false;
        }
        setWithTTL(timestamp, key, field, String.valueOf(newValue), ttl);
        return true;
    }

    // ---------- HELPERS ----------

    private String getLatestValue(List<VersionedValue> history, int timestamp) {
        String result = null;
        for (VersionedValue vv : history) {
            if (vv.timestamp > timestamp) break;
            if (vv.isExpired(timestamp)) continue;
            result = vv.value;
        }
        return result;
    }
}
```

---

## ⚠️ Edge Cases Handled

* Field deletion using tombstones
* Expired values hidden automatically
* Prefix scans skip expired fields
* Look-back reads ignore future writes
* Multiple updates to same field

---

## 🧩 Design Patterns Used

* **Multi-Version Concurrency Control (MVCC)**
* **Encapsulation**
* **Single Responsibility Principle**
* **Lazy Expiration**
* **Time-based Snapshot Reads**

---

## ⏱️ Time Complexity

| Operation      | Complexity |
| -------------- | ---------- |
| set / get      | O(V)       |
| scan           | O(F × V)   |
| scanByPrefix   | O(F × V)   |
| TTL operations | O(1)       |
| Look-back      | O(V)       |

> F = fields per key, V = versions per field

---

## 🎯 Interview Notes

**Why versioned values instead of overwriting?**

> Enables:

* Look-back queries
* Safe TTL handling
* Historical correctness

**Why lazy TTL cleanup?**

> Avoids background threads and keeps implementation deterministic.

---

## 🚀 Possible Extensions

* Background compaction
* Range queries
* Indexing for prefix scans
* Persistent WAL
* Concurrent access handling

```