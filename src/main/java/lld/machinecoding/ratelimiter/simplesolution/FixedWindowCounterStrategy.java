package lld.machinecoding.ratelimiter.simplesolution;

class FixedWindowCounterStrategy implements RateLimitStrategy {

    private int maxRequests;
    private int windowSize;

    private int currentWindowStart = -1;
    private int count = 0;

    FixedWindowCounterStrategy(int maxRequests, int windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
    }

    @Override
    public boolean isAllowed(int timestamp) {

        int windowStart = (timestamp / windowSize) * windowSize;

        if (windowStart != currentWindowStart) {
            currentWindowStart = windowStart;
            count = 0;
        }

        if (count < maxRequests) {
            count++;
            return true;
        }
        return false;
    }
}

