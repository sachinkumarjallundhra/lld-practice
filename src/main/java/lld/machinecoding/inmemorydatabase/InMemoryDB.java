package lld.machinecoding.inmemorydatabase;

import java.util.*;

public class InMemoryDB {

    static class ValueEntry {
        int timestamp;
        String value;
        int expiry;

        ValueEntry(int timestamp, String value, int expiry) {
            this.timestamp = timestamp;
            this.value = value;
            this.expiry = expiry;
        }
    }

    private final Map<String, Map<String, List<ValueEntry>>> store = new HashMap<>();

    /* ---------------- Level 1 ---------------- */

    public void set(int timestamp, String key, String field, String value) {
        setInternal(timestamp, key, field, value, Integer.MAX_VALUE);
    }

    public boolean compareAndSet(int timestamp, String key, String field, int expectedValue) {
        String cur = get(timestamp, key, field);
        if (cur == null || Integer.parseInt(cur) != expectedValue) return false;
        set(timestamp, key, field, cur);
        return true;
    }

    public boolean compareAndDelete(int timestamp, String key, String field, int expectedValue) {
        String cur = get(timestamp, key, field);
        if (cur == null || Integer.parseInt(cur) != expectedValue) return false;
        setInternal(timestamp, key, field, null, Integer.MAX_VALUE);
        return true;
    }

    public String get(int timestamp, String key, String field) {
        if (!store.containsKey(key)) return null;
        if (!store.get(key).containsKey(field)) return null;

        List<ValueEntry> history = store.get(key).get(field);
        for (int i = history.size() - 1; i >= 0; i--) {
            ValueEntry e = history.get(i);
            if (e.timestamp <= timestamp && timestamp < e.expiry) {
                return e.value;
            }
        }
        return null;
    }

    /* ---------------- Level 2 ---------------- */

    public List<String> scan(int timestamp, String key) {
        List<String> res = new ArrayList<>();
        if (!store.containsKey(key)) return res;

        for (String field : store.get(key).keySet()) {
            String val = get(timestamp, key, field);
            if (val != null) {
                res.add(field + "=" + val);
            }
        }

        Collections.sort(res);
        return res;
    }

    public List<String> scanByPrefix(int timestamp, String key, String prefix) {
        List<String> res = new ArrayList<>();
        if (!store.containsKey(key)) return res;

        for (String field : store.get(key).keySet()) {
            if (field.startsWith(prefix)) {
                String val = get(timestamp, key, field);
                if (val != null) {
                    res.add(field + "=" + val);
                }
            }
        }

        Collections.sort(res);
        return res;
    }

    /* ---------------- Level 3 ---------------- */

    public void setWithTTL(int timestamp, String key, String field, String value, int ttl) {
        setInternal(timestamp, key, field, value, timestamp + ttl);
    }

    public boolean compareAndSetWithTTL(
            int timestamp,
            String key,
            String field,
            int expectedValue,
            int newValue,
            int ttl
    ) {
        String cur = get(timestamp, key, field);
        if (cur == null || Integer.parseInt(cur) != expectedValue) return false;
        setInternal(timestamp, key, field, String.valueOf(newValue), timestamp + ttl);
        return true;
    }


    /* ---------------- Helpers ---------------- */
    private void setInternal(int timestamp, String key, String field, String value, int expiry) {
        store
                .computeIfAbsent(key, k -> new HashMap<>())
                .computeIfAbsent(field, f -> new ArrayList<>())
                .add(new ValueEntry(timestamp, value, expiry));
    }
}
