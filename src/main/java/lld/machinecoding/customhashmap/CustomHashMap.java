package lld.machinecoding.customhashmap;

import java.util.ArrayList;
import java.util.List;

public class CustomHashMap {

    private Entry[] buckets;
    private int size;
    private double minLoadFactor;
    private double maxLoadFactor;

    public CustomHashMap(double minLoadFactor, double maxLoadFactor) {
        this.minLoadFactor = round2(minLoadFactor);
        this.maxLoadFactor = round2(maxLoadFactor);
        this.buckets = new Entry[2];   // initial bucketsCount = 2
        this.size = 0;
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    public void put(String key, String value) {
        int index = bucketIndex(key, buckets.length);
        Entry head = buckets[index];

        // check if key exists → update
        Entry curr = head;
        while (curr != null) {
            if (curr.key.equals(key)) {
                curr.value = value;
                return;
            }
            curr = curr.next;
        }

        // insert new entry
        Entry newEntry = new Entry(key, value);
        newEntry.next = head;
        buckets[index] = newEntry;
        size++;

        rehashIfNeeded();

    }

    public String get(String key) {
        int index = bucketIndex(key, buckets.length);
        Entry curr = buckets[index];

        while (curr != null) {
            if (curr.key.equals(key)) {
                return curr.value;
            }
            curr = curr.next;
        }
        return "";
    }

    public String remove(String key) {
        int index = bucketIndex(key, buckets.length);
        Entry curr = buckets[index];
        Entry prev = null;

        while (curr != null) {
            if (curr.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                rehashIfNeeded();
                return curr.value;
            }
            prev = curr;
            curr = curr.next;
        }
        return "";
    }

    public List<String> getBucketKeys(int bucketIndex) {
        List<String> result = new ArrayList<>();

        if (bucketIndex < 0 || bucketIndex >= buckets.length) {
            return result;
        }

        Entry curr = buckets[bucketIndex];
        while (curr != null) {
            result.add(curr.key);
            curr = curr.next;
        }

        // manual lexicographic sort
        for (int i = 0; i < result.size(); i++) {
            for (int j = i + 1; j < result.size(); j++) {
                if (result.get(i).compareTo(result.get(j)) > 0) {
                    String tmp = result.get(i);
                    result.set(i, result.get(j));
                    result.set(j, tmp);
                }
            }
        }

        return result;
    }

    public int size() {
        return size;
    }

    public int bucketsCount() {
        return buckets.length;
    }

    private int bucketIndex(String key, int bucketsCount) {
        return hash(key) % bucketsCount;
    }

    private int hash(String key) {
        int sum = 0;
        for (char c : key.toCharArray()) {
            sum += (c - 'a' + 1);
        }
        int len = key.length();
        return len * len + sum;
    }

    private void rehashIfNeeded() {
        double lf = loadFactor();

        // GROW
        while (lf > maxLoadFactor) {
            rehash(buckets.length * 2);
            lf = loadFactor();   // recompute after resize
        }

        // SHRINK
        while (lf < minLoadFactor && buckets.length > 2) {
            rehash(buckets.length / 2);
            lf = loadFactor();
        }
    }
    private double loadFactor() {
        double lf = (double) size / buckets.length;
        return round2(lf);
    }

    private void rehash(int newBucketCount) {
        if (newBucketCount < 2) return;

        Entry[] oldBuckets = buckets;
        buckets = new Entry[newBucketCount];
        size = 0;  // reset and recount

        for (Entry head : oldBuckets) {
            Entry curr = head;
            while (curr != null) {
                insertWithoutRehash(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }

    private void insertWithoutRehash(String key, String value) {
        int index = bucketIndex(key, buckets.length);
        Entry newEntry = new Entry(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
    }









}
