package videos.domicilios.com.videocilios.Fragments;


//region Imports

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import videos.domicilios.com.videocilios.Activities.MainActivity;
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
import videos.domicilios.com.videocilios.Utils.DetailsTransition;
import videos.domicilios.com.videocilios.Utils.IRowSelected;
import videos.domicilios.com.videocilios.Utils.ISnackBarHidding;
//endregion

public class MovieListFragment extends Fragment implements ISnackBarHidding, IRowSelected, SearchView.OnQueryTextListener {

    //region Variables
    public static final String TAG = MovieListFragment.class.getSimpleName();

    private MainActivity activity;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private final static String API_KEY = "ae6059597b6805b4d06e9a87af2fca40";
    private final static String LANGUAGE = "es-ES";
    private ApiInterface apiService;
    private GenreAdapter adapter;

    private List<Genre> genres;
    private List<Movie> movies;
    private View rootView;

    private boolean shouldRecreate = true;
    //endregion

    //region Constructor
    public MovieListFragment() {

    }
    //endregion

    //region LifeCycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = ((MainActivity) getActivity());
        activity.hideLoading();

        rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));

        apiService =
                ApiClient.getClient().create(ApiInterface.class);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (shouldRecreate) {

            this.performRequest();
        } else {
            recyclerView.setAdapter(adapter);
        }
    }
    //endregion

    //region Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_list, menu);

        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setOnQueryTextListener(this);
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

    //region Services Request

    private void performRequest() {
        activity.showLoading();
        movies = new ArrayList<>();
        this.genreRequest();
        shouldRecreate = false;
    }

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
                if (page == 3) {
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
        activity.hideLoading();
        this.setupView(genres);
    }

    /***
     * Handle the failure on the request
     */
    private void errorRequest() {
        activity.hideLoading();
        CustomSnackBar.show(getResources().getString(R.string.connection_problems), R.drawable.icon_error, ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimaryDark), activity.getContainerView(), activity.getApplicationContext(), this);
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

    //region Implementation ISnackBarHidding, IRowSelected
    @Override
    public void onRowClick(RecyclerView.ViewHolder viewHolder, Object object) {
        Fragment movieDetails = new MovieDetailFragment();
        Bundle bundle = new Bundle();
        String movieGson = new Gson().toJson(object);
        bundle.putString("movie", movieGson);
        movieDetails.setArguments(bundle);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            movieDetails.setSharedElementEnterTransition(new DetailsTransition());
            movieDetails.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            movieDetails.setSharedElementReturnTransition(new DetailsTransition());
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(((GenreAdapter.ContentViewHolder) viewHolder).imgMovie, getString(R.string.activity_img_trans))
                .addSharedElement(((GenreAdapter.ContentViewHolder) viewHolder).txtTitleMovie, getString(R.string.activity_text_trans))
                .addSharedElement(((GenreAdapter.ContentViewHolder) viewHolder).txtDate, getString(R.string.activity_text_date_trans))
                .replace(R.id.frame_container, movieDetails)
                .addToBackStack(null)
                .commit();
    }

    @Override
    @SuppressWarnings("all")
    public void onRowLongClick(RecyclerView.ViewHolder viewHolder, Object object) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_alert, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        ((TextView) dialogView.findViewById(R.id.txt_movie_overview)).setText(((Movie) object).getOverview());
        dialogView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.hide();
            }
        });
        alertDialog.show();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
    }

    @Override
    public void snackBarHiding() {
        if (genres == null) {
            Snackbar.make(rootView, getResources().getString(R.string.connection_problems), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry_connect), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            performRequest();
                        }
                    }).show();
        }
    }
    //endregion

    //region Setup View

    /***
     * Populate recyclerView
     *
     * @param mGenres list of genres to display
     */
    private void setupView(List<Genre> mGenres) {
        adapter = new GenreAdapter(activity.getApplication(), mGenres, this);
        adapter.setMode(ExpandableRecyclerAdapter.MODE_ACCORDION);
        recyclerView.setAdapter(adapter);
    }
    //endregion
}
