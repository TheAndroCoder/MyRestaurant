package sachin.dev.myrestaurant.Service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

import sachin.dev.myrestaurant.R;
import ss.com.bannerslider.ImageLoadingService;

public class PicassoImageLoadingService implements ImageLoadingService {


    @Override
    public void loadImage(String url1, ImageView imageView) {
        Picasso.get().load(url1).placeholder(R.drawable.app_icon).into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        Picasso.get().load(resource).into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        Picasso.get().load(url).error(errorDrawable).placeholder(placeHolder).into(imageView);
    }
}
