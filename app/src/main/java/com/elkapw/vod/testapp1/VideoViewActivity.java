package com.elkapw.vod.testapp1;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VideoViewActivity extends Activity {

    TextView videoDescription;
    protected VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VideoObject videoToDisplay = (VideoObject) getIntent().getSerializableExtra("videoObject");
        setContentView(R.layout.activity_video_view);


        // OBSLUGA WYSWIETLENIA POJEDYNCZEGO FILMIKU Z SERWERA
        videoView = (VideoView) findViewById(R.id.videoView1); //przypisanie do elementu layoutu

        videoDescription = (TextView) findViewById(R.id.videoDescription);

        videoView.setMediaController(new MediaController(this));
        System.out.print("NOWE ACTIVITY");

        videoDescription.setText(videoToDisplay.getVideoDescription());
        // http://localhost:5080/red56/movies/video2.mp4

      //  String videoUri = videoToDisplay.get(0).getVideoURL();
        String uri = "http://192.168.1.21:5080/red56/movies/video2.mp4"; // lokalne wideo
        String uri1 = "http://192.168.42.193:5080/vodmovies/vid1.mp4"; // lokalne usb tethering wideo
        String uri2 = "http://download.wavetlan.com/SVV/Media/HTTP/BlackBerry.mp4";
        String uri3 = "res/video/video2.mp4";
        videoView.setVideoURI(Uri.parse(uri));
        videoView.requestFocus();
        videoView.start();

    }



}
