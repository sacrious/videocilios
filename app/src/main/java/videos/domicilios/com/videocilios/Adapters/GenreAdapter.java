package videos.domicilios.com.videocilios.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import videos.domicilios.com.videocilios.Model.Genre;
import videos.domicilios.com.videocilios.Model.Movie;
import videos.domicilios.com.videocilios.R;
import videos.domicilios.com.videocilios.Rest.ConstantsUrl;
import videos.domicilios.com.videocilios.Interfaces.IRowSelected;
import videos.domicilios.com.videocilios.Utils.LatoFontTextView;

/**
 * Created by Sergio on 3/10/17.
 */

@SuppressWarnings("all")
public class GenreAdapter extends ExpandableRecyclerAdapter<GenreAdapter.GenreItems> {

    private List<Genre> genres;
    private IRowSelected listener;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public GenreAdapter(Context context, List<Genre> genres, IRowSelected listener) {
        super(context);

        this.listener = listener;
        this.genres = genres;
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.img_background)
                .showImageForEmptyUri(R.drawable.img_background)
                .showImageOnFail(R.drawable.img_background_error)
                .cacheOnDisk(true)
                .build();
        imageLoader = ImageLoader.getInstance();
        setItems(getItems(genres));
    }

    public static class GenreItems extends ExpandableRecyclerAdapter.ListItem {
        public String name;
        public int movieQuantity;
        public Movie movie;

        public GenreItems(String section, int movieQuantity) {
            super(TYPE_HEADER);

            this.name = section;
            this.movieQuantity = movieQuantity;
        }

        public GenreItems(Movie movie) {
            super(TYPE_CONTENT);
            this.movie = movie;
        }
    }

    public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder {
        private final LatoFontTextView headerName;
        private final LatoFontTextView headerMovieQuantity;
        private final View viewContainer;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.img_arrow));
            this.headerName = (LatoFontTextView) view.findViewById(R.id.txt_header_name);
            this.headerMovieQuantity = (LatoFontTextView) view.findViewById(R.id.txt_movie_count);
            this.viewContainer = view;

        }

        public void bind(int position) {
            super.bind(position);

            this.headerName.setText(visibleItems.get(position).name);
            this.headerMovieQuantity.setText(String.valueOf(visibleItems.get(position).movieQuantity));
        }
    }

    public class ContentViewHolder extends ExpandableRecyclerAdapter.ViewHolder {

        private final RatingBar ratingBar;
        LatoFontTextView txtRatingNumber;
        public final LatoFontTextView txtTitleMovie, txtDate;
        public final ImageView imgMovie;
        private final View containerView;

        public ContentViewHolder(View view) {
            super(view);
            this.ratingBar = (RatingBar) view.findViewById(R.id.rating_movie);
            this.txtRatingNumber = (LatoFontTextView) view.findViewById(R.id.rating_number);
            this.txtTitleMovie = (LatoFontTextView) view.findViewById(R.id.txt_title_movie);
            this.txtDate = (LatoFontTextView) view.findViewById(R.id.txt_release_date_movie);
            this.imgMovie = (ImageView) view.findViewById(R.id.img_movie);
            this.containerView = view;
        }

        public void bind(final int position, final ViewHolder holder) {
            this.txtRatingNumber.setText(String.valueOf(visibleItems.get(position).movie.getVoteAverage()));
            this.txtTitleMovie.setText(visibleItems.get(position).movie.getTitle());
            this.txtDate.setText(visibleItems.get(position).movie.getReleaseDate());
            this.ratingBar.setRating(Float.parseFloat(txtRatingNumber.getText().toString()));
            imageLoader.displayImage(ConstantsUrl.URL_IMG_92W + visibleItems.get(position).movie.getPosterPath(), imgMovie, options);
            ViewCompat.setTransitionName(imgMovie, String.valueOf(position) + "_image");
            ViewCompat.setTransitionName(txtTitleMovie, String.valueOf(position) + "_title");
            ViewCompat.setTransitionName(txtDate, String.valueOf(position) + "_date");
            this.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRowClick(holder, visibleItems.get(position).movie);
                }
            });
            this.containerView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onRowLongClick(holder, visibleItems.get(position).movie);
                    return true;
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.row_section, parent));
            case TYPE_CONTENT:
                return new ContentViewHolder(inflate(R.layout.row_content, parent));
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
                ((ContentViewHolder) holder).bind(position, holder);
                break;
            default:
                ((ContentViewHolder) holder).bind(position, holder);
                break;
        }
    }

    private List<GenreItems> getItems(List<Genre> genres) {
        List<GenreItems> items = new ArrayList<>();
        for (Genre genre : genres) {
            if (genre.getMovies().size() > 0) {
                items.add(new GenreItems(genre.getName(), genre.getMovies().size()));
                for (Movie movie : genre.getMovies()) {
                    items.add(new GenreItems(movie));
                }
            }
        }
        return items;
    }
}