package videos.domicilios.com.videocilios.Rest;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import videos.domicilios.com.videocilios.Model.GenreResponse;
import videos.domicilios.com.videocilios.Model.MovieResponse;

/**
 * Created by Sergio on 3/10/17.
 */

public interface ApiInterface {
    @GET("genre/movie/list")
    Observable<GenreResponse> getGenres(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/top_rated")
    Observable<MovieResponse> getMovieSortByDate(@Query("api_key") String apiKey, @Query("language") String language, @Query("sort_by") String sortBy, @Query("page") Integer page);
}
