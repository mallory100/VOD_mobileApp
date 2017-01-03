package pl.edu.pwelka.majaskrobisz.vodclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import static pl.edu.pwelka.majaskrobisz.vodclient.LoginAuthenticatorActivity.SERVER_URL;

/**
 * Aktywnosc przedstawiajaca material wideo
 */
public class VideoViewActivity extends Activity {

    protected VideoView videoView;
    protected String baseURL = SERVER_URL;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        VideoObject videoToDisplay = (VideoObject) getIntent().getSerializableExtra("videoObject");
        setContentView(R.layout.activity_video_view);

        // OBSLUGA WYSWIETLENIA POJEDYNCZEGO FILMIKU Z SERWERA
        videoView = (VideoView) findViewById(R.id.videoView);  //przypisanie do elementu layoutu
        videoView.setMediaController(new MediaController(this)); //dodanie przyciskow

        String uri = baseURL + videoToDisplay.getVideoURL(); //pobrany z serwera URL

        videoView.setVideoURI(Uri.parse(uri));
        videoView.requestFocus();
        videoView.start();

        //stworzenie komunikatu o ladowaniu materialu
        progressDialog = ProgressDialog.show(this, "Proszę czekać ....", "Trwa ładowanie ...", true);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
            }
        });
        progressDialog.setCancelable(true);

    }



}
