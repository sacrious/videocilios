package videos.domicilios.com.videocilios.Presenters;

import java.util.ArrayList;
import java.util.List;

import videos.domicilios.com.videocilios.Base.BasePresenter;
import videos.domicilios.com.videocilios.Interfaces.IObserver;
import videos.domicilios.com.videocilios.Model.Genre;
import videos.domicilios.com.videocilios.Model.GenreResponse;
import videos.domicilios.com.videocilios.Model.Movie;
import videos.domicilios.com.videocilios.Model.MovieResponse;
import videos.domicilios.com.videocilios.Rest.ApiCallback;

/**
 * Created by Sergio on 3/12/17.
 */

public class MoviePresenter extends BasePresenter<IObserver> {

    private final static String API_KEY = "ae6059597b6805b4d06e9a87af2fca40";

    public MoviePresenter(IObserver view) {
        super.attachView(view);
    }

    /***
     * Perform first request, get all genres
     */
    public void loadData(final String language, final String sort_by) {
        view.showLoading();
        addSubscribe(api.getGenres(API_KEY, language), new ApiCallback<GenreResponse>() {
            @Override
            public void onSuccess(GenreResponse model) {
                loadMovieData(language, sort_by, 1, model.getGenres(), new ArrayList<Movie>());
            }

            @Override
            public void onFailure(String message) {
                view.getDataFail(message);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /***
     * When the genreRequest has already finish, perform this request to get all movies until 3 page
     */
    void loadMovieData(final String language, final String sort_by, final int page, final List<Genre> genres, final List<Movie> movies) {
        addSubscribe(api.getMovieSortByDate(API_KEY, language, sort_by, page), new ApiCallback<MovieResponse>() {
            @Override
            public void onSuccess(MovieResponse model) {
                movies.addAll(model.getResults());
                if (page == 3) {
                    view.getDataSuccess(addMoviesToGenres(model.getResults(), genres));
                    view.hideLoading();
                } else {
                    loadMovieData(language, sort_by, page + 1, addMoviesToGenres(model.getResults(), genres), movies);
                }
            }

            @Override
            public void onFailure(String message) {
                view.getDataFail(message);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /***
     * The api does not allow us to bring the name of the genres along with the films, here we are relating them
     *
     * @param movies list of movies generated by movieRequest
     */
    private List<Genre> addMoviesToGenres(List<Movie> movies, List<Genre> genres) {
        for (Genre genre : genres) {
            if (genre.getMovies() == null)
                genre.setMovies(new ArrayList<Movie>());
            for (Movie movie : movies) {
                if (movie.getGenreIds().contains(genre.getId()))
                    genre.getMovies().add(movie);
            }
        }
        return genres;
    }
}