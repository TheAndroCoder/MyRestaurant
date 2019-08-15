package sachin.dev.myrestaurant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import sachin.dev.myrestaurant.Model.Restaurant;
import sachin.dev.myrestaurant.R;

public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.MyViewHolder> {

    private List<Restaurant> restaurantList;
    private Context context;
    public MyRestaurantAdapter(Context context,List<Restaurant> restaurantList){
        this.context=context;
        this.restaurantList=restaurantList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_restaurant,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(restaurantList.get(position).getName());
        holder.address.setText(restaurantList.get(position).getAddress());
        Picasso.get().load(restaurantList.get(position).getImage()).placeholder(R.drawable.app_icon).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,address;
        ImageView image;
        public MyViewHolder(View view){
            super(view);
            name=view.findViewById(R.id.restaurant_name);
            address=view.findViewById(R.id.restaurant_address);
            image=view.findViewById(R.id.restaurant_image);
        }
    }
}
