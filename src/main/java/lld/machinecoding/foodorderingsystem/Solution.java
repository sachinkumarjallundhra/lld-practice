package lld.machinecoding.foodorderingsystem;

import java.util.*;

public class Solution {

    static class Order {
        String orderId;
        String restaurantId;
        String foodItemId;

        Order(String o, String r, String f) {
            orderId = o;
            restaurantId = r;
            foodItemId = f;
        }
    }
    static class RatingStats {
        int total;
        int count;

        void add(int rating) {
            total += rating;
            count++;
        }

        double avg() {
            if (count == 0) return 0.0;
            double raw = (double) total / count;
            return (double)((int)((raw + 0.05) * 10)) / 10.0;
        }
    }

    private Map<String, Order> orders = new HashMap<>();

    private Map<String, RatingStats> restaurantRatings = new HashMap<>();

    // foodItemId → (restaurantId → RatingStats)
    private Map<String, Map<String, RatingStats>> foodRatings = new HashMap<>();

    public Solution(){}


    public void orderFood(String orderId, String restaurantId, String foodItemId) {
        orders.put(orderId, new Order(orderId, restaurantId, foodItemId));

    }

    /**
     * when you(customer) are rating an order e.g giving 4 stars to an orders
     * then it means you are assigning 4 stars to both the food item
     * in that restaurant as well as 4 stars to the overall restaurant rating.
     * - rating ranges from 1 to 5, 5 is best, 1 is worst
     */
    public void rateOrder(String orderId, int rating) {
        Order o = orders.get(orderId);

        // restaurant overall rating
        restaurantRatings
                .computeIfAbsent(o.restaurantId, k -> new RatingStats())
                .add(rating);

        // food-specific rating per restaurant
        foodRatings
                .computeIfAbsent(o.foodItemId, k -> new HashMap<>())
                .computeIfAbsent(o.restaurantId, k -> new RatingStats())
                .add(rating);

    }

    /**
     * - Fetches a list of top 20 restaurants
     * - unrated restaurants will be at the bottom of list.
     * - restaurants are sorted in descending order on average ratings
     * of the food item and then based on restaurant id lexicographically
     * - ratings are rounded down to 1 decimal point,
     *  i.e. 4.05, 4.08, 4.11, 4.12, 4.14 all become 4.1,
     *    4.15, 4.19, 4.22, 4.24 all become 4.2
     * - e.g. 'food-item-1':  veg burger is rated 4.3 in restaurant-4
     * and 4.6 in restaurant-6 then we will return ['restaurant-6', 'restaurant-4']
     */
    public List<String> getTopRestaurantsByFood(String foodItemId) {
        List<String> result = new ArrayList<>();

        Map<String, RatingStats> map = foodRatings.get(foodItemId);
        if (map == null) return result;

        List<String> restaurants = new ArrayList<>(map.keySet());

        Collections.sort(restaurants, (a, b) -> {
            double ra = map.get(a).avg();
            double rb = map.get(b).avg();

            if (ra != rb) return Double.compare(rb, ra);
            return a.compareTo(b);
        });

        for (int i = 0; i < restaurants.size() && i < 20; i++) {
            result.add(restaurants.get(i));
        }
        return result;
    }

    /**
     * - Here we are talking about restaurant's overall rating and NOT food item's rating.
     */
    public List<String> getTopRatedRestaurants() {
        List<String> result = new ArrayList<>();
        List<String> restaurants = new ArrayList<>(restaurantRatings.keySet());

        Collections.sort(restaurants, (a, b) -> {
            double ra = restaurantRatings.get(a).avg();
            double rb = restaurantRatings.get(b).avg();

            if (ra != rb) return Double.compare(rb, ra);
            return a.compareTo(b);
        });

        for (int i = 0; i < restaurants.size() && i < 20; i++) {
            result.add(restaurants.get(i));
        }
        return result;
    }
}

// uncomment below code in case you are using your local ide like intellij, eclipse etc and
// comment it back again back when you are pasting completed solution in the online CodeZym editor.
// if you don't comment it back, you will get "java.lang.AssertionError: java.lang.LinkageError"
// This will help avoid unwanted compilation errors and get method autocomplete in your local code editor.
/**
 interface Q05RestaurantRatingInterface {
 void init(Helper05 helper);
 void orderFood(String orderId, String restaurantId, String foodItemId);
 void rateOrder(String orderId, int rating);
 List<String> getTopRestaurantsByFood(String foodItemId);
 List<String> getTopRatedRestaurants();
 }

 class Helper05 {
 void print(String s){System.out.print(s);}
 void println(String s){System.out.println(s);}
 }
 */
