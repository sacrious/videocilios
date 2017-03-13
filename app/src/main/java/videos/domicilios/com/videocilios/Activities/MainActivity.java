package videos.domicilios.com.videocilios.Activities;



import android.os.Bundle;
import videos.domicilios.com.videocilios.Fragments.MovieListFragment;
import videos.domicilios.com.videocilios.R;

public class MainActivity extends BaseActivity {

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_container, new MovieListFragment())
                    .commit();
        }
    }
    //endregion
}
