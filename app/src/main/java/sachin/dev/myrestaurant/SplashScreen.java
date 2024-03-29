package sachin.dev.myrestaurant;

import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import sachin.dev.myrestaurant.Common.Common;
import sachin.dev.myrestaurant.Retrofit.IMyRestaurantAPI;
import sachin.dev.myrestaurant.Retrofit.RetrofitClient;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashScreen extends AppCompatActivity {

    private IMyRestaurantAPI iMyRestaurantAPI;
    CompositeDisposable compositeDisposable=new CompositeDisposable();

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                dialog.show();
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        //Toast.makeText(SplashScreen.this, "Already Signed In", Toast.LENGTH_SHORT).show();
                        compositeDisposable.add(iMyRestaurantAPI.getUser(Common.API_KEY,account.getId()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userModel -> {
                            if(userModel.isSuccess()){
                                Common.currentUser = userModel.getResult().get(0);
                                Intent intent = new Intent(SplashScreen.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                dialog.dismiss();
                                Log.d("sachin","It cannot find usermodel in database");
                                Intent intent = new Intent(SplashScreen.this,UpdateInfoActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },throwable -> {
                            dialog.dismiss();
                            Toast.makeText(SplashScreen.this, "[GET USER API] "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        dialog.dismiss();
                        Toast.makeText(SplashScreen.this, "Not Signed In", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashScreen.this,MainActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(SplashScreen.this, "You must grant permission to use this app", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
        printKeyHash();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SplashScreen.this,MainActivity.class));
//                finish();
//            }
//        },3000);
    }

    private void init() {
        dialog=new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        //dialog.show();
        iMyRestaurantAPI= RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    private void printKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for(Signature signature :info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HASH_KEY", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
