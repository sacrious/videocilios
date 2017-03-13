package videos.domicilios.com.videocilios.Activities;

//region Imports

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.airbnb.lottie.LottieAnimationView;

import videos.domicilios.com.videocilios.R;
//endregion

public class AboutActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    //region Variables
    private FloatingActionButton fabPlus, fabEmail, fabCvPdf;
    private Animation fabOpen, fabClose, fabRotClockwise, fabRotAnticlockwise;
    private LottieAnimationView animationView;
    boolean isOpen = false;

    private final static String CV_SERGIO = "https://drive.google.com/open?id=0B3xkrprEEqYWeHFKNzZRNXNQVDA";
    private boolean isResumeAfterViewFiles = false;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        appBar.addOnOffsetChangedListener(this);

        this.fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        this.fabEmail = (FloatingActionButton) findViewById(R.id.fab_email);
        this.fabCvPdf = (FloatingActionButton) findViewById(R.id.fab_cv_pdf);
        this.fabPlus.setOnClickListener(this);
        this.fabEmail.setOnClickListener(this);
        this.fabCvPdf.setOnClickListener(this);

        this.fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        this.fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        this.fabRotClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        this.fabRotAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        this.animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        this.animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animationView.cancelAnimation();
                animationView.animate().translationY(animationView.getHeight()).alpha(0.0f);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isResumeAfterViewFiles) {
            this.animationView.animate().alpha(1.0f).translationY(0);
            this.animationView.playAnimation();
            isResumeAfterViewFiles = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            animHide();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region OnClickListener
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_plus) {
            if (view.getVisibility() != View.INVISIBLE) {
                if (isOpen) {
                    fabEmail.startAnimation(fabClose);
                    fabCvPdf.startAnimation(fabClose);
                    fabPlus.startAnimation(fabRotAnticlockwise);
                    fabEmail.setClickable(false);
                    fabCvPdf.setClickable(false);
                    isOpen = false;
                } else {
                    fabEmail.startAnimation(fabOpen);
                    fabCvPdf.startAnimation(fabOpen);
                    fabPlus.startAnimation(fabRotClockwise);
                    fabEmail.setClickable(true);
                    fabCvPdf.setClickable(true);
                    isOpen = true;
                }
            }
        } else if (view.getId() == R.id.fab_email) {
            isResumeAfterViewFiles = true;
            sendEmail();
        } else if (view.getId() == R.id.fab_cv_pdf) {
            isResumeAfterViewFiles = true;
            openPdf();
        }
    }

    private void sendEmail() {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:sergiofierro_93@hotmail.com" + "?subject=" + getString(R.string.email_subject) + "&body=" + getString(R.string.email_body);
        uriText = uriText.replace(" ", "%20");
        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Envíame un correo acerca de está app"));
    }

    private void openPdf() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(CV_SERGIO)));
    }



    //endregion

    private void animHide(){
        overridePendingTransition(R.anim.slide_back_in_right, R.anim.slide_back_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        animHide();
    }

    //region OnOffsetChangedListener

    /**
     * Hide fabButtons when scrolling
     *
     * @param appBarLayout   appBarLayout
     * @param verticalOffset verticalOffset
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (isOpen)
            fabPlus.performClick();
    }
    //endregion
}
