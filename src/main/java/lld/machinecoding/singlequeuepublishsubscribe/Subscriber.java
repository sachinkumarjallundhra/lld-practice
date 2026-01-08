package lld.machinecoding.singlequeuepublishsubscribe;

interface Subscriber {
    void onMessage(Message message);
}
