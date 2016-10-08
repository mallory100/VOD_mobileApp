package com.elkapw.vod.testapp1;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoViewActivity extends Activity {

    protected VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<VideoObject> videoToDisplay = (ArrayList<VideoObject>) getIntent().getSerializableExtra("video_array");
        setContentView(R.layout.activity_video_view);



        // OBSLUGA WYSWIETLENIA POJEDYNCZEGO FILMIKU Z SERWERA
        videoView = (VideoView) findViewById(R.id.videoView1); //przypisanie do elementu layoutu

        videoView.setMediaController(new MediaController(this));
        System.out.print("NOWE ACTIVITY");

        // http://localhost:5080/red56/movies/video2.mp4

      //  String videoUri = videoToDisplay.get(0).getVideoURL();
        String uri = "http://192.168.0.14:5080/red56/movies/video2.mp4"; // lokalne wideo
        String uri1 = "http://192.168.42.193:5080/vodmovies/vid1.mp4"; // lokalne usb tethering wideo
        String uri2 = "http://download.wavetlan.com/SVV/Media/HTTP/BlackBerry.mp4";
        String uri3 = "res/video/video2.mp4";
        videoView.setVideoURI(Uri.parse(uri));
        videoView.requestFocus();
        videoView.start();

    }



}
