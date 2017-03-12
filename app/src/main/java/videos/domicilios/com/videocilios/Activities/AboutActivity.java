package videos.domicilios.com.videocilios.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import videos.domicilios.com.videocilios.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    FloatingActionButton fabPlus, fabEmail, fabCvPdf;
    Animation fabOpen, fabClose, fabRotClockwise, fabRotAnticlockwise;
    boolean isOpen = false;

    private final static String CV_SERGIO = "https://drive.google.com/open?id=0B3xkrprEEqYWeHFKNzZRNXNQVDA";

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

        fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fabEmail = (FloatingActionButton) findViewById(R.id.fab_email);
        fabCvPdf = (FloatingActionButton) findViewById(R.id.fab_cv_pdf);
        fabPlus.setOnClickListener(this);
        fabEmail.setOnClickListener(this);
        fabCvPdf.setOnClickListener(this);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRotAnticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
    }

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
            sendEmail();
        } else if (view.getId() == R.id.fab_cv_pdf) {
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (isOpen)
            fabPlus.performClick();
    }
}
