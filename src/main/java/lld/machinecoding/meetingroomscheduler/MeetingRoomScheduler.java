package lld.machinecoding.meetingroomscheduler;

import java.util.*;

public class MeetingRoomScheduler {

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

    private Map<String, Booking> bookingById = new HashMap<>();
    private Map<Integer, List<Booking>> roomBookings = new HashMap<>();
    private Map<Integer, List<Booking>> employeeBookings = new HashMap<>();

    private int roomsCount;
    private int employeesCount;

    public MeetingRoomScheduler(int roomsCount, int employeesCount) {
        this.roomsCount = roomsCount;
        this.employeesCount = employeesCount;

        for (int i = 0; i < roomsCount; i++) {
            roomBookings.put(i, new ArrayList<>());
        }
        for (int i = 0; i < employeesCount; i++) {
            employeeBookings.put(i, new ArrayList<>());
        }
    }

    public boolean bookRoom(String bookingId, int employeeId, int roomId, int startTime, int endTime) {
        if (startTime < 0 || startTime > endTime) return false;

        List<Booking> bookings = roomBookings.get(roomId);

        // check overlap in SAME room
        for (Booking b : bookings) {
            if (overlaps(b.start, b.end, startTime, endTime)) {
                return false;
            }
        }

        Booking booking = new Booking(bookingId, employeeId, roomId, startTime, endTime);
        bookingById.put(bookingId, booking);
        bookings.add(booking);
        employeeBookings.get(employeeId).add(booking);

        return true;
    }

    public List<Integer> getAvailableRooms(int startTime, int endTime) {
        List<Integer> result = new ArrayList<>();
        if (startTime > endTime) return result;

        for (int roomId = 0; roomId < roomsCount; roomId++) {
            boolean free = true;
            for (Booking b : roomBookings.get(roomId)) {
                if (overlaps(b.start, b.end, startTime, endTime)) {
                    free = false;
                    break;
                }
            }
            if (free) result.add(roomId);
        }
        return result;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = bookingById.remove(bookingId);
        if (booking == null) return false;

        roomBookings.get(booking.roomId).remove(booking);
        employeeBookings.get(booking.employeeId).remove(booking);
        return true;
    }

    public List<String> listBookingsForRoom(int roomId) {
        List<Booking> list = new ArrayList<>(roomBookings.get(roomId));
        sortBookings(list);

        List<String> res = new ArrayList<>();
        for (Booking b : list) res.add(b.bookingId);
        return res;
    }

    public List<String> listBookingsForEmployee(int employeeId) {
        List<Booking> list = new ArrayList<>(employeeBookings.get(employeeId));
        sortBookings(list);

        List<String> res = new ArrayList<>();
        for (Booking b : list) res.add(b.bookingId);
        return res;
    }

    // ---------------- HELPERS ----------------
    private boolean overlaps(int s1, int e1, int s2, int e2) {
        return Math.max(s1, s2) <= Math.min(e1, e2);
    }

    private void sortBookings(List<Booking> list) {
        Collections.sort(list, (a, b) -> {
            if (a.start != b.start) return a.start - b.start;
            return a.bookingId.compareTo(b.bookingId);
        });
    }
}
