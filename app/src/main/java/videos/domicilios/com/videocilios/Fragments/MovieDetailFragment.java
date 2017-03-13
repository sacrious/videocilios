package videos.domicilios.com.videocilios.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import videos.domicilios.com.videocilios.Model.Movie;
import videos.domicilios.com.videocilios.R;
import videos.domicilios.com.videocilios.Rest.ConstantsUrl;
import videos.domicilios.com.videocilios.Utils.LatoFontTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

    //region Constructor
    public MovieDetailFragment() {

    }
    //endregion

    //region LifeCycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.img_background)
                .showImageForEmptyUri(R.drawable.img_background)
                .showImageOnFail(R.drawable.img_background_error)
                .cacheOnDisk(true)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();

        ImageView headerImg = (ImageView) view.findViewById(R.id.img_header);
        ImageView posterImg = (ImageView) view.findViewById(R.id.img_movie_poster);
        ImageView ageRestrictionImg = (ImageView) view.findViewById(R.id.img_age_restriction);
        LatoFontTextView txtTitle = (LatoFontTextView) view.findViewById(R.id.txt_title_movie);
        LatoFontTextView txtRating = (LatoFontTextView) view.findViewById(R.id.txt_movie_rating);
        LatoFontTextView txtDate = (LatoFontTextView) view.findViewById(R.id.txt_movie_release_date);
        LatoFontTextView txtVoteCount = (LatoFontTextView) view.findViewById(R.id.txt_movie_vote_count);
        LatoFontTextView txtOverview = (LatoFontTextView) view.findViewById(R.id.txt_movie_overview);

        String movieGson = this.getArguments().getString("movie");
        Movie movie = new Gson().fromJson(movieGson, Movie.class);

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
            imageLoader.displayImage(ConstantsUrl.URL_IMG_92W + movie.getPosterPath(), posterImg, options);
        }
    }
    //endregion
}
