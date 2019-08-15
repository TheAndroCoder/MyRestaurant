package sachin.dev.myrestaurant;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.accountkit.AccountKit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import sachin.dev.myrestaurant.Adapter.MyRestaurantAdapter;
import sachin.dev.myrestaurant.Adapter.RestaurantSliderAdapter;
import sachin.dev.myrestaurant.Common.Common;
import sachin.dev.myrestaurant.Model.EventBus.RestaurantLoadEvent;
import sachin.dev.myrestaurant.Model.Restaurant;
import sachin.dev.myrestaurant.Retrofit.IMyRestaurantAPI;
import sachin.dev.myrestaurant.Retrofit.RetrofitClient;
import sachin.dev.myrestaurant.Service.PicassoImageLoadingService;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView txt_username,txt_userphone;
    private boolean isBig=true;

    @BindView(R.id.recycler_header)
    TextView recycler_header;
    @BindView(R.id.bannerSlider)
    Slider banner_slider;
    @BindView(R.id.restaurant_recycler)
    RecyclerView restaurants_recycler;

    IMyRestaurantAPI iMyRestaurantAPI;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        txt_username=headerView.findViewById(R.id.txt_user_name);
        txt_userphone=headerView.findViewById(R.id.txt_user_phone);
        txt_username.setText(Common.currentUser.getName());
        txt_userphone.setText(Common.currentUser.getUserPhone());
        init();
        restaurants_recycler.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        //restaurants_recycler.addItemDecoration(new DividerItemDecoration(this,RecyclerView.VERTICAL));
        loadRestaurants();
        restaurants_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0 && isBig){
                    //scrolling up so reduce text size
                    ValueAnimator animator=ValueAnimator.ofFloat(25,20);
                    animator.setDuration(100);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float AnimatedValue =(float)valueAnimator.getAnimatedValue();
                            recycler_header.setTextSize(AnimatedValue);
                        }
                    });
                    animator.start();
                    isBig=false;
                }else if(dy<0 && !isBig){
                    //scrolling down so increase text size
                    ValueAnimator animator=ValueAnimator.ofFloat(20,25);
                    animator.setDuration(100);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float AnimatedValue=(float)valueAnimator.getAnimatedValue();
                            recycler_header.setTextSize(AnimatedValue);
                        }
                    });
                    animator.start();
                    isBig=true;
                }
            }
        });
        Slider.init(new PicassoImageLoadingService());
    }
    private void init(){
        dialog=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        iMyRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    private void loadRestaurants(){
        dialog.show();

        compositeDisposable.add(iMyRestaurantAPI.getRestaurants(Common.API_KEY)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(restaurantModel -> {
            if(restaurantModel.isSuccess()){
                Log.d("sachin","success");
                EventBus.getDefault().post(new RestaurantLoadEvent(true,restaurantModel.getResult()));
            }else{
                Log.d("sachin","failed");
                EventBus.getDefault().post(new RestaurantLoadEvent(false,restaurantModel.getResult()));
            }
            dialog.dismiss();
        },throwable -> {
            dialog.dismiss();
            EventBus.getDefault().post(new RestaurantLoadEvent(false,throwable.getMessage()));
        }));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Handle the logout action
            Common.currentUser=null;
            AccountKit.logOut();
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else if(id==R.id.nav_history){

        }else if(id==R.id.nav_nearby){

        }else if(id==R.id.nav_update_info){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    /*
    Registering Event Bus
     */

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }
    /*
    Listen to EventBus for events
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processRestaurantLoadEvent(RestaurantLoadEvent event){
        if(event.isSuccess()){
            displayBanner(event.getRestaurantList());
            displayRestaurantList(event.getRestaurantList());
        }else{
            Log.d("sachin","restaurant load failed "+event.getMessage());
        }
    }

    private void displayBanner(List<Restaurant> restaurantList) {
        Log.d("sachin","trying to set banner adapter");
        banner_slider.setAdapter(new RestaurantSliderAdapter(restaurantList));
    }
    private void displayRestaurantList(List<Restaurant> restaurantList){
        MyRestaurantAdapter adapter = new MyRestaurantAdapter(this,restaurantList);
        restaurants_recycler.setAdapter(adapter);
    }
}
