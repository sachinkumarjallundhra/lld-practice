import java.util.*;

public class CarRentalService {

    class Car {
        String licensePlate;
        int costPerDay;
        int freeKmsPerDay;
        int costPerKm;
        List<Order> listOfOrder = new ArrayList<>();

        Car(String licensePlate, int costPerDay, int freeKmsPerDay, int costPerKm) {
            this.licensePlate = licensePlate;
            this.costPerDay = costPerDay;
            this.freeKmsPerDay = freeKmsPerDay;
            this.costPerKm = costPerKm;
        }
    }

    class Order {
        String orderId;
        String carLicensePlate;
        int fromDate;
        int tillDate;
        Trip trip;

        Order(String orderId, String carLicensePlate, int fromDate, int tillDate) {
            this.orderId = orderId;
            this.carLicensePlate = carLicensePlate;
            this.fromDate = fromDate;
            this.tillDate = tillDate;
        }
    }

    class Trip {
        int startOdometer;
        Integer endOdometer;
        Integer endDay;

        Trip(int startOdometer) {
            this.startOdometer = startOdometer;
        }
    }

    Map<String, Car> cars = new HashMap<>();
    Map<String, Order> orders = new HashMap<>();

    public void addCar(String licensePlate, int costPerDay, int freeKmsPerDay, int costPerKm) {
        if (cars.containsKey(licensePlate)) return;
        cars.put(licensePlate, new Car(licensePlate, costPerDay, freeKmsPerDay, costPerKm));
    }

    public boolean bookCar(String orderId, String carLicensePlate, String fromDate, String tillDate) {
        if (orders.containsKey(orderId)) return false;

        Car car = cars.get(carLicensePlate);
        if (car == null) return false;

        int from = parseDay(fromDate);
        int till = parseDay(tillDate);
        if (from > till) return false;

        for (Order order : car.listOfOrder) {
            int effectiveTill = order.trip != null && order.trip.endDay != null
                    ? order.trip.endDay
                    : order.tillDate;
            if (from <= effectiveTill && order.fromDate <= till) {
                return false;
            }
        }

        Order newOrder = new Order(orderId, carLicensePlate, from, till);
        orders.put(orderId, newOrder);
        car.listOfOrder.add(newOrder);
        return true;
    }

    public void startTrip(String orderId, int odometerReading) {
        Order order = orders.get(orderId);
        if (order == null || order.trip != null) return;
        order.trip = new Trip(odometerReading);
    }

    public int endTrip(String orderId, int finalOdometerReading, String endDate) {
        Order order = orders.get(orderId);
        if (order == null || order.trip == null) return 0;

        Trip trip = order.trip;
        if (trip.endDay != null) return 0;

        int endDay = parseDay(endDate);
        trip.endDay = endDay;
        trip.endOdometer = finalOdometerReading;

        int effectiveEndDay = Math.max(order.tillDate, endDay);
        int days = 1 + (effectiveEndDay - order.fromDate);

        int tripKms = finalOdometerReading - trip.startOdometer;
        Car car = cars.get(order.carLicensePlate);

        int freeKms = days * car.freeKmsPerDay;
        int extraKms = Math.max(0, tripKms - freeKms);

        return days * car.costPerDay + extraKms * car.costPerKm;
    }

    private int parseDay(String date) {
        return Integer.parseInt(date.substring(8));
    }
}
