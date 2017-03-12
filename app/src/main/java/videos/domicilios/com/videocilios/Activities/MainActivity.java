package videos.domicilios.com.videocilios.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import videos.domicilios.com.videocilios.Adapters.ExpandableRecyclerAdapter;
import videos.domicilios.com.videocilios.Adapters.GenreAdapter;
import videos.domicilios.com.videocilios.Model.Genre;
import videos.domicilios.com.videocilios.Model.GenreResponse;
import videos.domicilios.com.videocilios.Model.Movie;
import videos.domicilios.com.videocilios.Model.MovieResponse;
import videos.domicilios.com.videocilios.R;
import videos.domicilios.com.videocilios.Rest.ApiClient;
import videos.domicilios.com.videocilios.Rest.ApiInterface;
import videos.domicilios.com.videocilios.Utils.CustomSnackBar;
import videos.domicilios.com.videocilios.Utils.IRowSelected;
import videos.domicilios.com.videocilios.Utils.ISnackBarHidding;

public class MainActivity extends BaseActivity implements ISnackBarHidding, IRowSelected, SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static String API_KEY = "ae6059597b6805b4d06e9a87af2fca40";
    private final static String LANGUAGE = "es-ES";

    private ApiInterface apiService;

    private List<Genre> genres;
    private List<Movie> movies;
    private RecyclerView recyclerView;
    private SearchView searchView;

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.main_activity_title));
        }

        movies = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        showLoading();
        this.genreRequest();
    }
    //endregion

    //region Handling Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        this.searchView.setQuery("", false);
        this.searchView.clearFocus();

        if (id == R.id.action_order_by_date) {
            List<Genre> genreOrderByDate = genres;
            for (Genre genre : genreOrderByDate) {
                Collections.sort(genre.getMovies());
            }
            setupView(genreOrderByDate);
            return true;
        } else if (id == R.id.action_order_by_name) {
            List<Genre> genreOrderByName = genres;
            for (Genre genre : genreOrderByName) {
                Collections.sort(genre.getMovies(), new Comparator<Movie>() {
                    public int compare(Movie o1, Movie o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
            }
            setupView(genreOrderByName);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Setup View

    /***
     * Populate recyclerView
     *
     * @param mGenres list of genres to display
     */
    private void setupView(List<Genre> mGenres) {
        GenreAdapter adapter = new GenreAdapter(this, mGenres, this);
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recyclerView.setAdapter(adapter);
    }
    //endregion

    //region Services Request

    /***
     * Perform first request, get all genres
     */
    private void genreRequest() {
        Call<GenreResponse> call = apiService.getGenres(API_KEY, LANGUAGE);
        call.enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                genres = response.body().getGenres();
                movieRequest(1);
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                errorRequest();
            }
        });
    }

    /***
     * When the genreRequest has already finish, perform this request to get all movies
     *
     * @param page the api does not allow us to bring many movies at once,
     *             so we must make this request to get pages until page 5 to populate recyclerView with many movies
     */
    private void movieRequest(final int page) {
        Call<MovieResponse> call = apiService.getMovieSortByDate(API_KEY, LANGUAGE, "primary_release_date.desc", page);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                movies.addAll(response.body().getResults());
                if (page == 5) {
                    addMoviesToGenres(movies);
                } else {
                    movieRequest(page + 1);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                errorRequest();
            }
        });
    }

    /***
     * The api does not allow us to bring the name of the genres along with the films, here we are relating them
     *
     * @param movies list of movies generated by movieRequest
     */
    private void addMoviesToGenres(List<Movie> movies) {
        for (Genre genre : genres) {
            if (genre.getMovies() == null)
                genre.setMovies(new ArrayList<Movie>());
            for (Movie movie : movies) {
                if (movie.getGenreIds().contains(genre.getId()))
                    genre.getMovies().add(movie);
            }
        }
        hideLoading();
        this.setupView(genres);
    }

    /***
     * Handle the failure on the request
     */
    private void errorRequest() {
        hideLoading();
        CustomSnackBar.show(getResources().getString(R.string.connection_problems), R.drawable.icon_error, ContextCompat.getColor(this, R.color.colorAccent), getContainerView(), this, this);
    }

    /***
     * Snackbar action retry request
     */
    @Override
    protected void performRequest() {
        this.genreRequest();
    }
    //endregion

    //region Filter list
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (genres != null) {
            filter(newText);
            recyclerView.scrollToPosition(0);
        }
        return true;
    }

    /***
     * Filter list by average movie rating or movie title
     *
     * @param query text string that the searchView is typing,
     *              this will be the text by which the list will filter
     */
    private void filter(String query) {
        query = query.toLowerCase();
        List<Genre> filteredModelList = new ArrayList<>();
        for (Genre model : genres) {
            boolean nullModel = true;
            for (Movie movie : model.getMovies()) {
                final String movieName = movie.getTitle().toLowerCase();
                final Double movieRating = movie.getVoteAverage();
                if (movieName.contains(query) || (isDouble(query) && movieRating >= Double.parseDouble(query))) {
                    if (nullModel) {
                        filteredModelList.add(new Genre(model.getId(), model.getName(), new ArrayList<Movie>()));
                    }
                    filteredModelList.get(filteredModelList.size() - 1).getMovies().add(movie);
                    nullModel = false;
                }
            }

        }
        this.setupView(filteredModelList);
    }

    /***
     * Validate if a String can be converted to double
     *
     * @param str string text to be converted
     * @return is double?
     */
    @SuppressWarnings("all")
    public boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    //endregion

    //region ISnackBarHidden, IRowSelected

    /**
     * Has the tap handler over a row of movies
     *
     * @param object       movie object
     * @param moviePicture movie URL picture
     * @param movieTitle   movie title
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onRowSelected(Object object, ImageView moviePicture, TextView movieTitle, TextView ratingNumber, TextView dateRelease) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        String serializedMovie = new Gson().toJson(object);
        intent.putExtra("movie", serializedMovie);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            moviePicture.setTransitionName(getString(R.string.activity_img_trans));
            movieTitle.setTransitionName(getString(R.string.activity_text_trans));
            ratingNumber.setTransitionName(getString(R.string.activity_text_rating_trans));
            dateRelease.setTransitionName(getString(R.string.activity_text_date_trans));

            Pair<View, String> pair1 = Pair.create((View) moviePicture, moviePicture.getTransitionName());
            Pair<View, String> pair2 = Pair.create((View) movieTitle, movieTitle.getTransitionName());
            Pair<View, String> pair3 = Pair.create((View) ratingNumber, ratingNumber.getTransitionName());
            Pair<View, String> pair4 = Pair.create((View) dateRelease, dateRelease.getTransitionName());
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, pair1, pair2, pair3, pair4);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /***
     * It runs immediately after the CustomSnackBar is hidden
     */
    @Override
    public void snackBarHiding() {
        showRetry();
    }
    //endregion
}
