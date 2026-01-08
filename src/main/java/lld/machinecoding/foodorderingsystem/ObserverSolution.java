package lld.machinecoding.foodorderingsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Order {
    String orderId;
    String restaurantId;
    String foodItemId;

    Order(String orderId, String restaurantId, String foodItemId) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.foodItemId = foodItemId;
    }
}

class RatingStats {
    int totalRating = 0;
    int count = 0;

    void addRating(int rating) {
        totalRating += rating;
        count++;
    }

    double getAverage() {
        if (count == 0) return 0.0;
        double avg = (double) totalRating / count;
        return (double)((int)((avg + 0.05) * 10)) / 10.0;
    }
}

/* ===================== OBSERVER ===================== */

interface OrderRatingObserver {
    void onOrderRated(Order order, int rating);
}

class OrderRatingPublisher {
    private List<OrderRatingObserver> observers = new ArrayList<>();

    void register(OrderRatingObserver observer) {
        observers.add(observer);
    }

    void notifyObservers(Order order, int rating) {
        for (OrderRatingObserver o : observers) {
            o.onOrderRated(order, rating);
        }
    }
}

/* ===================== OBSERVERS ===================== */

class RestaurantRatingObserver implements OrderRatingObserver {

    private Map<String, RatingStats> restaurantRatings;

    RestaurantRatingObserver(Map<String, RatingStats> restaurantRatings) {
        this.restaurantRatings = restaurantRatings;
    }

    @Override
    public void onOrderRated(Order order, int rating) {
        restaurantRatings
                .computeIfAbsent(order.restaurantId, k -> new RatingStats())
                .addRating(rating);
    }
}

class FoodItemRatingObserver implements OrderRatingObserver {

    private Map<String, Map<String, RatingStats>> foodItemRatings;

    FoodItemRatingObserver(Map<String, Map<String, RatingStats>> foodItemRatings) {
        this.foodItemRatings = foodItemRatings;
    }

    @Override
    public void onOrderRated(Order order, int rating) {
        foodItemRatings
                .computeIfAbsent(order.foodItemId, k -> new HashMap<>())
                .computeIfAbsent(order.restaurantId, k -> new RatingStats())
                .addRating(rating);
    }
}

/* ===================== MAIN SYSTEM ===================== */

public class ObserverSolution {
    private Map<String, Order> orders = new HashMap<>();

    // RestaurantId -> RatingStats
    private Map<String, RatingStats> restaurantRatings = new HashMap<>();

    // FoodItemId -> (RestaurantId -> RatingStats)
    private Map<String, Map<String, RatingStats>> foodItemRatings = new HashMap<>();

    private OrderRatingPublisher publisher = new OrderRatingPublisher();

    /* ===================== INIT ===================== */

    public void init() {
        publisher.register(new RestaurantRatingObserver(restaurantRatings));
        publisher.register(new FoodItemRatingObserver(foodItemRatings));
    }

    /* ===================== APIs ===================== */

    public void orderFood(String orderId, String restaurantId, String foodItemId) {
        orders.put(orderId, new Order(orderId, restaurantId, foodItemId));
    }

    public void rateOrder(String orderId, int rating) {
        Order order = orders.get(orderId);
        publisher.notifyObservers(order, rating);
    }

    public List<String> getTopRestaurantsByFood(String foodItemId) {
        Map<String, RatingStats> map = foodItemRatings.getOrDefault(foodItemId, new HashMap<>());

        List<String> restaurants = new ArrayList<>(map.keySet());

        restaurants.sort((a, b) -> {
            double r1 = map.get(a).getAverage();
            double r2 = map.get(b).getAverage();
            if (Double.compare(r2, r1) != 0) {
                return Double.compare(r2, r1);
            }
            return a.compareTo(b);
        });

        return restaurants.size() > 20 ? restaurants.subList(0, 20) : restaurants;
    }

    public List<String> getTopRatedRestaurants() {
        List<String> restaurants = new ArrayList<>(restaurantRatings.keySet());

        restaurants.sort((a, b) -> {
            double r1 = restaurantRatings.get(a).getAverage();
            double r2 = restaurantRatings.get(b).getAverage();
            if (Double.compare(r2, r1) != 0) {
                return Double.compare(r2, r1);
            }
            return a.compareTo(b);
        });

        return restaurants.size() > 20 ? restaurants.subList(0, 20) : restaurants;
    }
}
