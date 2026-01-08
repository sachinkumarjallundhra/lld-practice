package lld.machinecoding.ratelimiter;
// RateLimiter.java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// FixedWindowRateLimiter.java
class FixedWindowRateLimiter implements RateLimiter {
    private final long windowMillis;
    private final int maxRequests;
    private final ConcurrentHashMap<String, AtomicInteger> counts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> windowStart = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    @Override
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis();
        windowStart.compute(userId, (k, start) -> {
            if (start == null || now - start >= windowMillis) {
                counts.put(userId, new AtomicInteger(0));
                return now;
            }
            return start;
        });
        AtomicInteger cnt = counts.computeIfAbsent(userId, k -> new AtomicInteger(0));
        int cur = cnt.incrementAndGet();
        return cur <= maxRequests;
    }
}

// SlidingWindowRateLimiter.java (approximation using buckets)
class SlidingWindowRateLimiter implements RateLimiter {
    private final int maxRequests;
    private final long windowMillis;
    private final int numBuckets;
    private final long bucketSizeMillis;
    // userId -> buckets array + lastAccessTime
    private final ConcurrentHashMap<String, UserBuckets> map = new ConcurrentHashMap<>();

    static class UserBuckets {
        final long[] buckets;
        long startTime;
        UserBuckets(int n, long startTime) { buckets = new long[n]; this.startTime = startTime; }
    }

    public SlidingWindowRateLimiter(int maxRequests, long windowMillis, int numBuckets) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.numBuckets = numBuckets;
        this.bucketSizeMillis = windowMillis / numBuckets;
    }

    @Override
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis();
        UserBuckets ub = map.computeIfAbsent(userId, k -> new UserBuckets(numBuckets, now));
        synchronized (ub) {
            // advance buckets if needed
            long elapsed = now - ub.startTime;
            if (elapsed >= windowMillis) {
                Arrays.fill(ub.buckets, 0);
                ub.startTime = now;
            } else if (elapsed >= bucketSizeMillis) {
                int shift = (int)(elapsed / bucketSizeMillis);
                shiftBuckets(ub.buckets, shift);
                ub.startTime += shift * bucketSizeMillis;
            }
            // add to current bucket
            int idx = (int)((now - ub.startTime) / bucketSizeMillis) % numBuckets;
            ub.buckets[idx]++;
            long sum = 0;
            for (long v : ub.buckets) sum += v;
            return sum <= maxRequests;
        }
    }

    private void shiftBuckets(long[] buckets, int shift) {
        if (shift >= buckets.length) {
            Arrays.fill(buckets, 0);
            return;
        }
        long[] temp = new long[buckets.length];
        for (int i = 0; i < buckets.length; i++) {
            int newIdx = (i - shift + buckets.length) % buckets.length;
            temp[newIdx] = buckets[i];
        }
        System.arraycopy(temp, 0, buckets, 0, buckets.length);
        // zero out newly exposed buckets
        for (int i = 0; i < shift; i++) {
            int idx = (buckets.length - 1 - i + buckets.length) % buckets.length;
            buckets[idx] = 0;
        }
    }
}

// Quick main to demo
class RateLimiterDemo {
    public static void main(String[] args) throws Exception {
        RateLimiter rl = new SlidingWindowRateLimiter(5, 10_000, 5);
        String u = "user1";
        for (int i = 0; i < 8; i++) {
            System.out.println(i + " -> " + rl.allowRequest(u));
            Thread.sleep(600);
        }
    }
}
