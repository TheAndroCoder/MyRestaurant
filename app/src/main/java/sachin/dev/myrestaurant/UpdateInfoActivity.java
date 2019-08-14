package sachin.dev.myrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import sachin.dev.myrestaurant.Common.Common;
import sachin.dev.myrestaurant.Retrofit.IMyRestaurantAPI;
import sachin.dev.myrestaurant.Retrofit.RetrofitClient;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;

public class UpdateInfoActivity extends AppCompatActivity {
    private IMyRestaurantAPI iMyRestaurantAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.btn_update)
    Button update_btn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        ButterKnife.bind(this);
        init();
    }
    private void init(){
        dialog=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        iMyRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
        toolbar.setTitle("Update Information");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        compositeDisposable.add(iMyRestaurantAPI.updateUserInfo(Common.API_KEY,account.getPhoneNumber().toString(),username.getText().toString(),address.getText().toString(),account.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updateUserModel -> {
                            if(updateUserModel.isSuccess()){
                                compositeDisposable.add(iMyRestaurantAPI.getUser(Common.API_KEY,account.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(userModel -> {
                                        Common.currentUser=userModel.getResult().get(0);
                                        startActivity(new Intent(UpdateInfoActivity.this,HomeActivity.class));
                                        finish();
                                        },throwable -> {
                                            dialog.dismiss();
                                            Log.d("sachin","failed to get userInfo "+throwable.getMessage());
                                        }));
                            }else{
                                Log.d("sachin","failed to update "+updateUserModel.getMessage());
                            }
                        },throwable -> {
                            dialog.dismiss();
                            Log.d("sachin",throwable.getMessage());
                        }));
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Log.d("sachin",accountKitError.getErrorType().getMessage());
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
