package sachin.dev.myrestaurant.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sachin.dev.myrestaurant.Model.UpdateUserModel;
import sachin.dev.myrestaurant.Model.UserModel;

public interface IMyRestaurantAPI {
    @GET("user")
    Observable<UserModel> getUser(@Query("key") String apikey, @Query("fbid")String fbid);

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key")String apikey, @Field("userPhone")String userPhone, @Field("userName")String userName, @Field("userAddress")String userAddress, @Field("fbid")String fbid);
}
