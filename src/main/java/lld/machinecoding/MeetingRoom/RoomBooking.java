package lld.machinecoding.MeetingRoom;

import java.util.*;

public class RoomBooking {

    class Meeting{
        String meetingId;
        String roomId;
        int startTime;
        int endTime;
        Meeting(String meetingId,String roomId,int startTime,int endTime){
            this.meetingId= meetingId;
            this.roomId = roomId;
            this.startTime = startTime;
            this.endTime = endTime;

        }
    }
    class Room{
        String roomId;
        TreeMap<Integer,Integer> intervals = new TreeMap<>();
        Room(String roomId){
            this. roomId = roomId;

        }
    }

    Map<String,Room> rooms = new HashMap<>();
    Map<String,Meeting> meetingMap = new HashMap<>();
    List<String> sortedRoomIds;

    public RoomBooking(List<String> roomIds) {
        sortedRoomIds = new ArrayList<>(roomIds);
        Collections.sort(sortedRoomIds);

        for(String id : roomIds){
            rooms.put(id,new Room(id));

        }


    }

    public String bookMeeting(String meetingId, int startTime, int endTime) {

        if (meetingMap.containsKey(meetingId)) {
            return "";
        }

        for(String roomId : sortedRoomIds){
            Room room = rooms.get(roomId);
            if(isAvailable(room,startTime,endTime)){
                room.intervals.put(startTime,endTime);
                Meeting m = new Meeting(meetingId,roomId,startTime,endTime);
                meetingMap.put(meetingId,m);
                return roomId;

            }
        }
        return "";
    }

    private boolean isAvailable(Room room,int startTime,int endTime){

        Map.Entry<Integer,Integer> floor = room.intervals.floorEntry(startTime);
        if(floor!= null && floor.getValue() >= startTime) return false;

        Map.Entry<Integer,Integer> ceil = room.intervals.ceilingEntry(startTime);
        if(ceil != null && ceil.getKey() <= endTime) return false;

        return true;
    }

    public boolean cancelMeeting(String meetingId) {
        Meeting m = meetingMap.get(meetingId);
        if(m== null) return false;

        Room room = rooms.get(m.roomId);
        room.intervals.remove(m.startTime);
        meetingMap.remove(meetingId);
        return true;
    }
}
