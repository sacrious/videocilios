package videos.domicilios.com.videocilios.Activities;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

import videos.domicilios.com.videocilios.R;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout fullView;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        this.fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        this.animationView = (LottieAnimationView) fullView.findViewById(R.id.animation_view);
        NestedScrollView activityContainer = (NestedScrollView) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, this.fullView, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.fullView.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) this.fullView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void showLoading() {
        this.animationView.setVisibility(View.VISIBLE);
        this.animationView.playAnimation();
    }

    protected void hideLoading() {
        this.animationView.cancelAnimation();
        this.animationView.setVisibility(View.GONE);
    }

    protected View getContainerView() {
        return fullView;
    }

    protected void showRetry() {
        Snackbar.make(fullView, getResources().getString(R.string.connection_problems), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.retry_connect), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRequest();
            }
        }).show();
    }

    protected void performRequest() {
        return;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        fullView.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.fullView.isDrawerOpen(GravityCompat.START)) {
            this.fullView.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
