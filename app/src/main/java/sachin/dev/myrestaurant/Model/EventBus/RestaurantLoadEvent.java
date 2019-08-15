package sachin.dev.myrestaurant.Model.EventBus;

import java.util.List;

import sachin.dev.myrestaurant.Model.Restaurant;

public class RestaurantLoadEvent {
    public boolean success;
    public String message;
    public List<Restaurant> restaurantList;

    public RestaurantLoadEvent(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RestaurantLoadEvent(boolean success, List<Restaurant> restaurantList) {
        this.success = success;
        this.restaurantList = restaurantList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }
}
