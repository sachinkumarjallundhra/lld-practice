package lld.machinecoding.singlequeuepublishsubscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ConcreteSubscriber implements Subscriber {

    String subscriberId;
    Set<String> eventTypes;
    int processedCount = 0;

    ConcreteSubscriber(String subscriberId, List<String> eventTypes) {
        this.subscriberId = subscriberId;
        this.eventTypes = new HashSet<>(eventTypes);
    }

    @Override
    public void onMessage(Message message) {
        if (eventTypes.contains(message.eventType)) {
            processedCount++;
            // processing simulated
        }
    }
}
