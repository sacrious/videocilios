package videos.domicilios.com.videocilios.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by proximate on 3/10/17.
 */

public class Genre {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    private List<Movie> movies = new ArrayList<>();

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre(int id, String name, List<Movie> movies) {
        this.id = id;
        this.name = name;
        this.movies = movies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
