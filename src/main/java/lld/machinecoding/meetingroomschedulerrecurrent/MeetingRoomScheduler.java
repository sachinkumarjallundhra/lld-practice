package lld.machinecoding.meetingroomschedulerrecurrent;

import java.util.*;

public class MeetingRoomScheduler {

    static class Booking {
        String bookingId;
        int employeeId;
        int roomId;
        int start;
        int end;

        Booking(String id, int emp, int room, int s, int e) {
            bookingId = id;
            employeeId = emp;
            roomId = room;
            start = s;
            end = e;
        }
    }

    private Comparator<Booking> COMP = (a, b) -> {
        if (a.start != b.start) return a.start - b.start;
        return a.bookingId.compareTo(b.bookingId);
    };

    private Map<Integer, TreeSet<Booking>> roomMap = new HashMap<>();
    private Map<Integer, TreeSet<Booking>> empMap = new HashMap<>();
    private Map<String, List<Booking>> bookingMap = new HashMap<>();

    private int rooms;

    public MeetingRoomScheduler(int roomsCount, int employeesCount) {
        rooms = roomsCount;
        for (int i = 0; i < roomsCount; i++)
            roomMap.put(i, new TreeSet<>(COMP));
        for (int i = 0; i < employeesCount; i++)
            empMap.put(i, new TreeSet<>(COMP));
    }

    // ---------------- BOOK ROOM ----------------
    public boolean bookRoom(String bookingId, int employeeId, int roomId,
                            int startTime, int duration, int repeatDuration) {

        if (startTime < 0 || duration <= 0 || duration >= repeatDuration)
            return false;

        TreeSet<Booking> roomSet = roomMap.get(roomId);
        List<Booking> instances = new ArrayList<>();

        // 1️⃣ Pre-check ALL 20 occurrences
        for (int i = 0; i < 20; i++) {
            int s = startTime + i * repeatDuration;
            int e = s + duration - 1;
            Booking probe = new Booking(bookingId, employeeId, roomId, s, e);

            Booking floor = roomSet.floor(probe);
            if (floor != null && overlaps(floor, probe)) return false;

            Booking ceil = roomSet.ceiling(probe);
            if (ceil != null && overlaps(ceil, probe)) return false;

            instances.add(probe);
        }

        // 2️⃣ Commit all
        for (Booking b : instances) {
            roomSet.add(b);
            empMap.get(employeeId).add(b);
        }
        bookingMap.put(bookingId, instances);
        return true;
    }

    // ---------------- AVAILABLE ROOMS ----------------
    public List<Integer> getAvailableRooms(int start, int end) {
        List<Integer> res = new ArrayList<>();
        if (start > end) return res;

        for (int r = 0; r < rooms; r++) {
            TreeSet<Booking> set = roomMap.get(r);
            Booking probe = new Booking("", -1, r, start, end);

            Booking floor = set.floor(probe);
            if (floor != null && overlaps(floor, probe)) continue;

            Booking ceil = set.ceiling(probe);
            if (ceil != null && overlaps(ceil, probe)) continue;

            res.add(r);
        }
        return res;
    }

    // ---------------- CANCEL BOOKING ----------------
    public boolean cancelBooking(String bookingId) {
        List<Booking> list = bookingMap.remove(bookingId);
        if (list == null) return false;

        for (Booking b : list) {
            roomMap.get(b.roomId).remove(b);
            empMap.get(b.employeeId).remove(b);
        }
        return true;
    }

    // ---------------- LIST ROOM BOOKINGS ----------------
    public List<String> listBookingsForRoom(int roomId, int n) {
        List<String> res = new ArrayList<>();
        int count = 0;
        for (Booking b : roomMap.get(roomId)) {
            if (count++ == n) break;
            res.add(b.bookingId + "-" + b.start + "-" + b.end);
        }
        return res;
    }

    // ---------------- LIST EMP BOOKINGS ----------------
    public List<String> listBookingsForEmployee(int empId, int n) {
        List<String> res = new ArrayList<>();
        int count = 0;
        for (Booking b : empMap.get(empId)) {
            if (count++ == n) break;
            res.add(b.bookingId + "-" + b.start + "-" + b.end);
        }
        return res;
    }

    private boolean overlaps(Booking a, Booking b) {
        return Math.max(a.start, b.start) <= Math.min(a.end, b.end);
    }
}

