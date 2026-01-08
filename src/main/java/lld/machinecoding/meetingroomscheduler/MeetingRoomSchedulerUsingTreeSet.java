package lld.machinecoding.meetingroomscheduler;

import java.util.*;

public class MeetingRoomSchedulerUsingTreeSet {

    static class Booking {
        String bookingId;
        int employeeId;
        int roomId;
        int start;
        int end;

        Booking(String bookingId, int employeeId, int roomId, int start, int end) {
            this.bookingId = bookingId;
            this.employeeId = employeeId;
            this.roomId = roomId;
            this.start = start;
            this.end = end;
        }
    }

    private Comparator<Booking> BOOKING_COMPARATOR = (a, b) -> {
        if (a.start != b.start) return a.start - b.start;
        return a.bookingId.compareTo(b.bookingId);
    };

    private Map<Integer, TreeSet<Booking>> roomBookings = new HashMap<>();
    private Map<Integer, TreeSet<Booking>> employeeBookings = new HashMap<>();
    private Map<String, Booking> bookingById = new HashMap<>();

    private int roomsCount;

    public MeetingRoomSchedulerUsingTreeSet(int roomsCount, int employeesCount) {
        this.roomsCount = roomsCount;

        for (int i = 0; i < roomsCount; i++) {
            roomBookings.put(i, new TreeSet<>(BOOKING_COMPARATOR));
        }
        for (int i = 0; i < employeesCount; i++) {
            employeeBookings.put(i, new TreeSet<>(BOOKING_COMPARATOR));
        }
    }

    // ---------------- BOOK ROOM ----------------
    public boolean bookRoom(String bookingId, int employeeId, int roomId, int start, int end) {
        if (start < 0 || start > end) return false;

        TreeSet<Booking> set = roomBookings.get(roomId);
        Booking newBooking = new Booking(bookingId, employeeId, roomId, start, end);

        Booking floor = set.floor(newBooking);
        if (floor != null && overlaps(floor, newBooking)) return false;

        Booking ceil = set.ceiling(newBooking);
        if (ceil != null && overlaps(ceil, newBooking)) return false;

        set.add(newBooking);
        employeeBookings.get(employeeId).add(newBooking);
        bookingById.put(bookingId, newBooking);

        return true;
    }

    // ---------------- GET AVAILABLE ROOMS ----------------
    public List<Integer> getAvailableRooms(int start, int end) {
        List<Integer> result = new ArrayList<>();
        if (start > end) return result;

        for (int roomId = 0; roomId < roomsCount; roomId++) {
            TreeSet<Booking> set = roomBookings.get(roomId);
            Booking probe = new Booking("", -1, roomId, start, end);

            Booking floor = set.floor(probe);
            if (floor != null && overlaps(floor, probe)) continue;

            Booking ceil = set.ceiling(probe);
            if (ceil != null && overlaps(ceil, probe)) continue;

            result.add(roomId);
        }
        return result;
    }

    // ---------------- CANCEL BOOKING ----------------
    public boolean cancelBooking(String bookingId) {
        Booking b = bookingById.remove(bookingId);
        if (b == null) return false;

        roomBookings.get(b.roomId).remove(b);
        employeeBookings.get(b.employeeId).remove(b);
        return true;
    }

    // ---------------- LIST BOOKINGS FOR ROOM ----------------
    public List<String> listBookingsForRoom(int roomId) {
        List<String> res = new ArrayList<>();
        for (Booking b : roomBookings.get(roomId)) {
            res.add(b.bookingId);
        }
        return res;
    }

    // ---------------- LIST BOOKINGS FOR EMPLOYEE ----------------
    public List<String> listBookingsForEmployee(int employeeId) {
        List<String> res = new ArrayList<>();
        for (Booking b : employeeBookings.get(employeeId)) {
            res.add(b.bookingId);
        }
        return res;
    }

    // ---------------- HELPER ----------------
    private boolean overlaps(Booking a, Booking b) {
        return Math.max(a.start, b.start) <= Math.min(a.end, b.end);
    }
}

