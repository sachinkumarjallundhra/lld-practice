package lld.machinecoding.parkinglot;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

enum VehicleType { CAR, BIKE }
class Vehicle {
    final String regNumber;
    final String color;
    Vehicle(String regNumber, String color) { this.regNumber = regNumber; this.color = color; }
}
class Slot {
    final int slotNo;
    Vehicle vehicle;
    Slot(int slotNo) { this.slotNo = slotNo; }
    boolean isFree() { return vehicle == null; }
}

class ParkingLot {
    private final int capacity;
    private final Slot[] slots;
    private final PriorityQueue<Integer> freeSlots;
    private final Map<String,Integer> regToSlot = new HashMap<>();
    private final Map<String, List<Integer>> colorToSlots = new HashMap<>();

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        slots = new Slot[capacity+1];
        freeSlots = new PriorityQueue<>();
        for (int i = 1; i <= capacity; i++) {
            slots[i] = new Slot(i);
            freeSlots.add(i);
        }
    }

    public int park(Vehicle v) {
        if (freeSlots.isEmpty()) return -1;
        int slotNo = freeSlots.poll();
        slots[slotNo].vehicle = v;
        regToSlot.put(v.regNumber, slotNo);
        colorToSlots.computeIfAbsent(v.color, k -> new ArrayList<>()).add(slotNo);
        return slotNo;
    }

    public boolean leave(int slotNo) {
        if (slotNo <=0 || slotNo > capacity || slots[slotNo].isFree()) return false;
        Vehicle v = slots[slotNo].vehicle;
        slots[slotNo].vehicle = null;
        regToSlot.remove(v.regNumber);
        List<Integer> l = colorToSlots.get(v.color);
        if (l!=null) { l.remove(Integer.valueOf(slotNo)); if (l.isEmpty()) colorToSlots.remove(v.color); }
        freeSlots.add(slotNo);
        return true;
    }

    public void status() {
        System.out.println("Slot No. | Reg No | Colour");
        for (int i = 1; i <= capacity; i++) {
            if (!slots[i].isFree()) {
                System.out.printf("%d        %s     %s\n", i, slots[i].vehicle.regNumber, slots[i].vehicle.color);
            }
        }
    }

    public List<Integer> slotsByColor(String color) {
        return colorToSlots.getOrDefault(color, Collections.emptyList());
    }

    public Integer slotByReg(String reg) {
        return regToSlot.get(reg);
    }

    public static void main(String[] args) {
        ParkingLot pl = new ParkingLot(3);
        System.out.println("Parked at: " + pl.park(new Vehicle("KA-01", "White"))); //1
        System.out.println("Parked at: " + pl.park(new Vehicle("MH-02", "Black"))); //2
        pl.status();
        System.out.println(pl.slotByReg("KA-01"));
        pl.leave(1);
        pl.status();
    }
}
