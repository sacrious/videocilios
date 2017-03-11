package videos.domicilios.com.videocilios.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import videos.domicilios.com.videocilios.Model.Genre;
import videos.domicilios.com.videocilios.Model.Movie;
import videos.domicilios.com.videocilios.R;

/**
 * Created by proximate on 3/10/17.
 */

public class GenreAdapter extends ExpandableRecyclerAdapter<GenreAdapter.GenreItems> {

    private static final String URL_IMG = "http://image.tmdb.org/t/p/w185/";

    private List<Genre> genres;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public GenreAdapter(Context context, List<Genre> genres) {
        super(context);

        this.genres = genres;


//        options = new DisplayImageOptions.Builder()
//                // Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .cacheInMemory(false)
//                .displayer(new CircleBitmapDisplayer())
//                .showImageOnLoading(R.mipmap.ic_launcher)
//                .showImageForEmptyUri(R.mipmap.ic_launcher)
//                .showImageOnFail(R.mipmap.ic_launcher)
//                .cacheOnDisk(true)
//                .build();
        options = new DisplayImageOptions.Builder()
                // Bitmaps in RGB_565 consume 2 times less memory than in ARGB_8888.
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .defaultDisplayImageOptions(options)
                .memoryCache(new WeakMemoryCache()).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        setItems(getItems(genres));
    }

    public static class GenreItems extends ExpandableRecyclerAdapter.ListItem {
        public String name;
        public Movie movie;

        public GenreItems(String section) {
            super(TYPE_HEADER);

            this.name = section;
        }

        public GenreItems(Movie movie) {
            super(TYPE_CONTENT);
            this.movie = movie;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        TextView name;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.img_arrow));

            name = (TextView) view.findViewById(R.id.txt_header_name);
        }

        public void bind(int position) {
            super.bind(position);

            name.setText(visibleItems.get(position).name);
        }
    }

    public class ContentViewHolder extends ExpandableRecyclerAdapter.ViewHolder {

        private final RatingBar ratingBar;
        private final TextView txtRatingNumber, txtTitleMovie, txtDate;
        private final ImageView imgMovie;

        public ContentViewHolder(View view) {
            super(view);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_movie);
            txtRatingNumber = (TextView) view.findViewById(R.id.rating_number);
            txtTitleMovie = (TextView) view.findViewById(R.id.txt_title_movie);
            txtDate = (TextView) view.findViewById(R.id.txt_release_date_movie);
            imgMovie = (ImageView) view.findViewById(R.id.img_movie);
        }

        public void bind(int position) {
            txtRatingNumber.setText(String.valueOf(visibleItems.get(position).movie.getVoteAverage()));
            txtTitleMovie.setText(visibleItems.get(position).movie.getTitle());
            txtDate.setText(visibleItems.get(position).movie.getReleaseDate());
            ratingBar.setRating(Float.parseFloat(txtRatingNumber.getText().toString()));
            imageLoader.displayImage(URL_IMG + visibleItems.get(position).movie.getBackdropPath(), imgMovie, options);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.row_section, parent));
            case TYPE_CONTENT:
            default:
                return new ContentViewHolder(inflate(R.layout.row_content, parent));
        }
    }


    @Override
    public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case TYPE_CONTENT:
            default:
                ((ContentViewHolder) holder).bind(position);
                break;
        }
    }

    private List<GenreItems> getItems(List<Genre> genres) {
        List<GenreItems> items = new ArrayList<>();
        for (Genre genre : genres) {
            if (genre.getMovies().size() > 0) {
                items.add(new GenreItems(genre.getName() + " " + genre.getMovies().size()));
                for (Movie movie : genre.getMovies()) {
                    items.add(new GenreItems(movie));
                }
            }
        }

        return items;
    }
}