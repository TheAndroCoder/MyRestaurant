package sachin.dev.myrestaurant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

public class MainActivity extends AppCompatActivity {
    private IMyRestaurantAPI iMyRestaurantAPI;
    CompositeDisposable compositeDisposable=new CompositeDisposable();

    private AlertDialog dialog;
    private static final int APP_REQUEST_CODE=1234;
    @BindView(R.id.btn_sign_in)
    Button btn_sign_in;

    @OnClick(R.id.btn_sign_in)
    void loginUser(){
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder=new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,builder.build());
        startActivityForResult(intent,APP_REQUEST_CODE);
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }
    private void init(){
        dialog=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        iMyRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==APP_REQUEST_CODE){
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(loginResult.getError()!=null){
                dialog.dismiss();
                Toast.makeText(this, loginResult.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }else if(loginResult.wasCancelled()){
                dialog.dismiss();
                Toast.makeText(this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        compositeDisposable.add(iMyRestaurantAPI.getUser(Common.API_KEY,account.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userModel -> {
                            if(userModel.isSuccess()){
                                Common.currentUser=userModel.getResult().get(0);
                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                finish();
                            }else{
                                dialog.dismiss();
                                startActivity(new Intent(MainActivity.this,UpdateInfoActivity.class));
                                finish();
                            }
                        },throwable -> {
                            dialog.dismiss();
                            Log.d("sachin","failed to get user info "+throwable.getMessage());
                        }));
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        dialog.dismiss();
                        Log.d("sachin","Account kit error "+accountKitError.getErrorType().getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
