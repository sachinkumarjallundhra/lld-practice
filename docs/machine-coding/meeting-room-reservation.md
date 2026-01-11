# Design a Meeting Room Reservation System

## Problem Statement

Design a **Meeting Room Reservation System** for a fixed set of conference rooms.

The system must:
- Book meetings without overlap
- Cancel existing meetings
- Always choose the **lexicographically smallest room ID** when multiple rooms are available
- Treat time intervals as **inclusive** (`[startTime, endTime]`)

---

## Key Requirements

- Each room can host **only one meeting at a time**
- Meetings **overlap if times intersect inclusively**
- Cancel only active meetings
- Meeting IDs are unique for active bookings

---

## 🏗️ Class Design

### Class Name
`RoomBooking`

### Constructor
```java
RoomBooking(List<String> roomIds);
```

### Methods

```java
String bookMeeting(String meetingId, int startTime, int endTime);
boolean cancelMeeting(String meetingId);
```

---

## 🧩 Design Overview

### Core Data Structures

| Structure                   | Purpose                   |
| --------------------------- | ------------------------- |
| `TreeMap<Integer, Integer>` | Stores intervals per room |
| `Map<String, Room>`         | Room registry             |
| `Map<String, Meeting>`      | Active meetings lookup    |
| `List<String>`              | Sorted room IDs           |

---

## 🏛️ Entity Models

### Meeting

* meetingId
* roomId
* startTime
* endTime

### Room

* roomId
* TreeMap of booked intervals (`start → end`)

---

## 🔍 Booking Logic

To check if a room is free:

1. Find the **closest previous interval** using `floorEntry`
2. Find the **closest next interval** using `ceilingEntry`
3. Reject booking if either overlaps (inclusive rules)

---

## 🧠 Design Patterns Used

* **Single Responsibility Principle**
* **Encapsulation**
* **Ordered Resource Selection**
* **Interval Scheduling using TreeMap**

---

## 💻 Java Implementation

```java
import java.util.*;

public class RoomBooking {

    class Meeting {
        String meetingId;
        String roomId;
        int startTime;
        int endTime;

        Meeting(String meetingId, String roomId, int startTime, int endTime) {
            this.meetingId = meetingId;
            this.roomId = roomId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    class Room {
        String roomId;
        TreeMap<Integer, Integer> intervals = new TreeMap<>();

        Room(String roomId) {
            this.roomId = roomId;
        }
    }

    private Map<String, Room> rooms = new HashMap<>();
    private Map<String, Meeting> meetingMap = new HashMap<>();
    private List<String> sortedRoomIds;

    public RoomBooking(List<String> roomIds) {
        sortedRoomIds = new ArrayList<>(roomIds);
        Collections.sort(sortedRoomIds);

        for (String id : roomIds) {
            rooms.put(id, new Room(id));
        }
    }

    public String bookMeeting(String meetingId, int startTime, int endTime) {

        if (meetingMap.containsKey(meetingId)) {
            return "";
        }

        for (String roomId : sortedRoomIds) {
            Room room = rooms.get(roomId);
            if (isAvailable(room, startTime, endTime)) {
                room.intervals.put(startTime, endTime);
                Meeting meeting = new Meeting(meetingId, roomId, startTime, endTime);
                meetingMap.put(meetingId, meeting);
                return roomId;
            }
        }
        return "";
    }

    private boolean isAvailable(Room room, int startTime, int endTime) {

        Map.Entry<Integer, Integer> floor = room.intervals.floorEntry(startTime);
        if (floor != null && floor.getValue() >= startTime) {
            return false;
        }

        Map.Entry<Integer, Integer> ceil = room.intervals.ceilingEntry(startTime);
        if (ceil != null && ceil.getKey() <= endTime) {
            return false;
        }

        return true;
    }

    public boolean cancelMeeting(String meetingId) {
        Meeting meeting = meetingMap.get(meetingId);
        if (meeting == null) {
            return false;
        }

        Room room = rooms.get(meeting.roomId);
        room.intervals.remove(meeting.startTime);
        meetingMap.remove(meetingId);
        return true;
    }
}
```

---

## ⚠️ Edge Cases Handled

* Inclusive overlap (`end == start` is conflict)
* Duplicate meeting IDs rejected
* Cancelling non-existent meetings
* Correct reuse of rooms after cancellation
* Lexicographically smallest room chosen

---

## ⏱️ Time Complexity

| Operation      | Complexity     |
| -------------- | -------------- |
| Book Meeting   | `O(R × log M)` |
| Cancel Meeting | `O(log M)`     |
| Overlap Check  | `O(log M)`     |

> R = number of rooms
> M = number of meetings per room

---

## 🧪 Example

```java
RoomBooking rb = new RoomBooking(Arrays.asList("roomA", "roomB"));

rb.bookMeeting("m1", 10, 20); // roomA
rb.bookMeeting("m2", 15, 25); // roomB
rb.bookMeeting("m3", 20, 30); // ""
rb.cancelMeeting("m1");
rb.bookMeeting("m4", 20, 30); // roomA
```

---

## 🚀 Possible Enhancements

* PriorityQueue optimization
* Support recurring meetings
* Room capacity handling
* Time window queries
* Concurrency control
