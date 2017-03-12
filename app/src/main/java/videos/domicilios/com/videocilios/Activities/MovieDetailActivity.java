package videos.domicilios.com.videocilios.Activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


import org.w3c.dom.Text;

import videos.domicilios.com.videocilios.Model.Movie;
import videos.domicilios.com.videocilios.R;
import videos.domicilios.com.videocilios.Rest.ConstantsUrl;
import videos.domicilios.com.videocilios.Utils.LatoFontTextView;

public class MovieDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        hideLoading();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.img_background)
                .showImageForEmptyUri(R.drawable.img_background)
                .showImageOnFail(R.drawable.img_background_error)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(options)
                .memoryCache(new WeakMemoryCache()).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();

        ImageView headerImg = (ImageView) findViewById(R.id.img_header);
        ImageView posterImg = (ImageView) findViewById(R.id.img_poster);
        ImageView ageRestrictionImg = (ImageView) findViewById(R.id.img_age_restriction);
        LatoFontTextView txtTitle = (LatoFontTextView) findViewById(R.id.txt_title_movie);
        LatoFontTextView txtRating = (LatoFontTextView) findViewById(R.id.txt_movie_rating);
        LatoFontTextView txtDate = (LatoFontTextView) findViewById(R.id.txt_movie_release_date);
        LatoFontTextView txtVoteCount = (LatoFontTextView) findViewById(R.id.txt_movie_vote_count);
        LatoFontTextView txtOverview = (LatoFontTextView) findViewById(R.id.txt_movie_overview);

        String listSerializedToJson = getIntent().getExtras().getString("movie");
        Movie movie = new Gson().fromJson(listSerializedToJson, Movie.class);

        if (movie != null) {
            txtTitle.setText(movie.getTitle());
            txtRating.setText(String.valueOf(movie.getVoteAverage()));
            txtDate.setText(getString(R.string.release_date, movie.getReleaseDate()));
            txtOverview.setText(movie.getOverview());
            txtVoteCount.setText(getString(R.string.vote_count, String.valueOf(movie.getVoteCount())));
            if (!movie.isAdult()) {
                ageRestrictionImg.setImageResource(R.drawable.everyone);
            }
            imageLoader.displayImage(ConstantsUrl.URL_IMG_300W + movie.getBackdropPath(), headerImg, options);
            imageLoader.displayImage(ConstantsUrl.URL_IMG_185W + movie.getPosterPath(), posterImg, options);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            posterImg.setTransitionName(getString(R.string.activity_img_trans));
            txtTitle.setTransitionName(getString(R.string.activity_text_trans));
            txtDate.setTransitionName(getString(R.string.activity_text_date_trans));
        }


    }
}
