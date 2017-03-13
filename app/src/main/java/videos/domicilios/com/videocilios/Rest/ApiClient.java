package videos.domicilios.com.videocilios.Rest;




import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by proximate on 3/10/17.
 */

public class ApiClient {

//    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
