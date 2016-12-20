package com.elkapw.vod.testapp1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VideoViewActivity extends Activity {

    protected VideoView videoView;
    protected String baseURL = "http://192.168.0.14:8080/VOD_servlet";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String uri1 = "http://192.168.42.193:5080/vodmovies/vid1.mp4"; // lokalne usb tethering wideo

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
