package lld.machinecoding.ratelimiter.simplesolution;

import java.util.ArrayDeque;
import java.util.Deque;

class SlidingWindowCounterStrategy implements RateLimitStrategy {

    private int maxRequests;
    private int windowSize;
    private Deque<Integer> timestamps = new ArrayDeque<>();

    SlidingWindowCounterStrategy(int maxRequests, int windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }

    @Override
    public boolean isAllowed(int timestamp) {

        int windowStart = timestamp - windowSize + 1;

        while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < maxRequests) {
            timestamps.addLast(timestamp);
            return true;
        }
        return false;
    }
}