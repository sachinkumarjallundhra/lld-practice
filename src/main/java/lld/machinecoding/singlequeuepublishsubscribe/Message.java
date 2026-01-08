package lld.machinecoding.singlequeuepublishsubscribe;

class Message {
    String eventType;
    String payload;

    Message(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
    }
}
