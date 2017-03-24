package videos.domicilios.com.videocilios.Base;

//region Imports

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import videos.domicilios.com.videocilios.Activities.AboutActivity;
import videos.domicilios.com.videocilios.R;
//endregion

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //region Variables
    private DrawerLayout fullView;
    private LottieAnimationView animationView;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCache(new WeakMemoryCache()).build();
        ImageLoader.getInstance().init(config);
    }
    //endregion

    /**
     * Overrides for showing Navigation Menu on all activities
     *
     * @param layoutResID layout id
     */
    @Override
    @SuppressWarnings("all")
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        this.fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        this.animationView = (LottieAnimationView) fullView.findViewById(R.id.animation_view);
        this.hideLoading();
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.fullView, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.fullView.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) this.fullView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //region LoadingView

    /**
     * Know if loading is currently showing
     */
    public boolean isLoadingShowing() {
        return this.animationView.getVisibility() == View.VISIBLE;
    }

    /**
     * Show animation for loading
     */
    public void showLoading() {
        this.animationView.setVisibility(View.VISIBLE);
        this.animationView.playAnimation();
    }

    /**
     * Hides animation for loading
     */
    public void hideLoading() {
        this.animationView.cancelAnimation();
        this.animationView.setVisibility(View.GONE);
    }
    //endregion

    /**
     * Get the container view
     *
     * @return container view
     */
    public View getContainerView() {
        return fullView;
    }


    //region OnNavigationItemSelectedListener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_share) {
            startActivity(new Intent(this, AboutActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
        fullView.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region BackPressed

    /**
     * Handles when DrawerOpen close it
     */
    @Override
    public void onBackPressed() {
        if (this.fullView.isDrawerOpen(GravityCompat.START)) {
            this.fullView.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //endregion
}
