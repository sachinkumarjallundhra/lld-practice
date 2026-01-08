package lld.machinecoding.inmemorydatabase;

public class ValueEntry {
    int timestamp;
    String value;
    int expiry;

    ValueEntry(int timestamp,String value,int expiry){
        this.timestamp =timestamp;
        this.value = value;
        this.expiry = expiry;

    }
}
