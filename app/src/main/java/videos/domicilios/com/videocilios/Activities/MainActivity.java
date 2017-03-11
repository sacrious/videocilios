package videos.domicilios.com.videocilios.Activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
import videos.domicilios.com.videocilios.Utils.ISnackBarHidding;

public class MainActivity extends BaseActivity implements ISnackBarHidding, SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static String API_KEY = "ae6059597b6805b4d06e9a87af2fca40";
    private final static String LANGUAGE = "es-ES";

    private ApiInterface apiService;

    private List<Genre> genres;
    private List<Movie> movies;
    private RecyclerView recyclerView;
    private SearchView searchView;

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
        this.makeGenderRequest();
    }

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
        this.searchView.setQuery("",false);
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
            for (Genre genre : genreOrderByName){
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

    private void setupView(List<Genre> mGenres) {
        GenreAdapter adapter = new GenreAdapter(this, mGenres);
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recyclerView.setAdapter(adapter);
    }


    private void makeGenderRequest() {
        Call<GenreResponse> call = apiService.getGenres(API_KEY, LANGUAGE);
        call.enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                genres = response.body().getGenres();
                makeMovieRequest(1);
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                errorRequest();
            }
        });
    }

    private void makeMovieRequest(final int page) {
        Call<MovieResponse> call = apiService.getMovieSortByDate(API_KEY, LANGUAGE, "primary_release_date.desc", page);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                movies.addAll(response.body().getResults());
                if (page == 5) {
                    filterMoviesByGenre(movies);
                } else {
                    makeMovieRequest(page + 1);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                errorRequest();
            }
        });
    }

    private void errorRequest() {
        hideLoading();
        CustomSnackBar.show(getResources().getString(R.string.connection_problems), R.drawable.icon_error, ContextCompat.getColor(this, R.color.colorAccent), getContainerView(), this, this);
    }

    @Override
    protected void performRequest() {
        this.makeGenderRequest();
    }

    private void filterMoviesByGenre(List<Movie> movies) {

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

    @Override
    public void snackBarHidding() {
        showRetry();
    }

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

    public boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
