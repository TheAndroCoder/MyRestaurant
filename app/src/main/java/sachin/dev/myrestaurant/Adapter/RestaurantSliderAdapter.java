package sachin.dev.myrestaurant.Adapter;

import android.util.Log;

import java.util.List;

import sachin.dev.myrestaurant.Model.Restaurant;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class RestaurantSliderAdapter extends SliderAdapter {
    private List<Restaurant> restaurantList;
    public RestaurantSliderAdapter(List<Restaurant> restaurantList){
        this.restaurantList=restaurantList;
    }

    @Override
    public int getItemCount() {
        if(restaurantList.size()>6){
            return 6;
        }
        return restaurantList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        Log.d("sachin","Value here is "+restaurantList.get(position).getImage());
        imageSlideViewHolder.bindImageSlide(restaurantList.get(position).getImage());
    }
}
