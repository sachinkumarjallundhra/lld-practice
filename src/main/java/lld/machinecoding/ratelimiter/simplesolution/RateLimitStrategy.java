package lld.machinecoding.ratelimiter.simplesolution;

interface RateLimitStrategy {
    boolean isAllowed(int timestamp);
}
