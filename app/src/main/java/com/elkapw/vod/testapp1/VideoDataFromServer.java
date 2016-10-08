package com.elkapw.vod.testapp1;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majka on 2016-09-27.
 */
public class VideoDataFromServer {

    JSONParser jParser = new JSONParser();
    JSONObject json;
    JSONArray json_array;
    public ArrayList<VideoObject> video_array;
    VideoObject videoObject;

    private static String url_getVideosData = "http://192.168.0.14:5080/red56/AndroidVideosDataServlet";


    public class getVideosData extends AsyncTask<String, String, String> {
        String s = null;
        AlertDialog.Builder popup;


        @Override
        protected String doInBackground(String... args) {
            // Getting username and password from user input
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            json = jParser.makeHttpRequest(url_getVideosData, "GET", params);

            try {

                JSONArray json_array = json.getJSONArray("Video");
                Log.d("Msg", json.getString("Video"));

                int size = json_array.length();
                video_array = new ArrayList<VideoObject>();

                for (int i = 0; i < size; i++) {
                    JSONObject video  = json_array.getJSONObject(i);
                    videoObject =  new VideoObject(video.getString("videoname"),video.getString("videourl"),video.getString("videodescription"));
                    video_array.add(videoObject);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }















}
