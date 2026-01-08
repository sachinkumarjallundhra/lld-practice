package lld.machinecoding.singlequeuepublishsubscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubSubQueueManager {

    // Global FIFO queue (append-only)
    private List<Message> queue = new ArrayList<>();

    // Active subscribers
    private Map<String, ConcreteSubscriber> activeSubscribers = new HashMap<>();

    // Cumulative processed counts across sessions
    private Map<String, Integer> totalProcessedCounts = new HashMap<>();

    // ---------------- addSubscriber ----------------
    public void addSubscriber(String subscriberId, List<String> eventTypesToProcess) {

        // If re-subscribing, remove old active subscription
        if (activeSubscribers.containsKey(subscriberId)) {
            removeSubscriber(subscriberId);
        }

        ConcreteSubscriber sub =
                new ConcreteSubscriber(subscriberId, eventTypesToProcess);

        activeSubscribers.put(subscriberId, sub);
    }

    // ---------------- removeSubscriber ----------------
    public void removeSubscriber(String subscriberId) {

        ConcreteSubscriber sub = activeSubscribers.remove(subscriberId);
        if (sub == null) return;

        // Accumulate processed count
        int prev = totalProcessedCounts.getOrDefault(subscriberId, 0);
        totalProcessedCounts.put(subscriberId, prev + sub.processedCount);
    }

    // ---------------- sendMessage ----------------
    public void sendMessage(String eventType, String message) {

        Message msg = new Message(eventType, message);
        queue.add(msg); // append to global FIFO queue

        // Notify all currently active subscribers
        for (ConcreteSubscriber sub : activeSubscribers.values()) {
            sub.onMessage(msg);
        }
    }

    // ---------------- countProcessedMessages ----------------
    public int countProcessedMessages(String subscriberId) {

        int total = totalProcessedCounts.getOrDefault(subscriberId, 0);

        ConcreteSubscriber active = activeSubscribers.get(subscriberId);
        if (active != null) {
            total += active.processedCount;
        }

        return total;
    }
}

