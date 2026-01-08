package lld.machinecoding.ratelimiter;

public interface RateLimiter {
    boolean allowRequest(String userId);
}