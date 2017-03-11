package videos.domicilios.com.videocilios.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by proximate on 3/10/17.
 */

public class GenreResponse {
    @SerializedName("genres")
    private List<Genre> genres;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
