package lld.machinecoding.ratelimiter.simplesolution;

import java.util.*;

public class RateLimiter {

    private Map<String, RateLimitStrategy> resourceStrategies = new HashMap<>();

    // ---------------- addResource ----------------
    public void addResource(String resourceId, String strategy, String limits) {

        String[] parts = limits.split(",");
        int maxRequests = Integer.parseInt(parts[0]);
        int timePeriod = Integer.parseInt(parts[1]);

        RateLimitStrategy rateLimitStrategy;

        if (strategy.equals("fixed-window-counter")) {
            rateLimitStrategy =
                    new FixedWindowCounterStrategy(maxRequests, timePeriod);

        } else if (strategy.equals("sliding-window-counter")) {
            rateLimitStrategy =
                    new SlidingWindowCounterStrategy(maxRequests, timePeriod);

        } else {
            throw new IllegalArgumentException("Unknown strategy");
        }

        // Replace old strategy if exists
        resourceStrategies.put(resourceId, rateLimitStrategy);
    }

    // ---------------- isAllowed ----------------
    public boolean isAllowed(String resourceId, int timestamp) {
        RateLimitStrategy strategy = resourceStrategies.get(resourceId);
        return strategy.isAllowed(timestamp);
    }
}

