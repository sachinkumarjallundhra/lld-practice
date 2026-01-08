package lld.machinecoding.customhashmap;

class Entry {
    String key;
    String value;
    Entry next;

    Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
