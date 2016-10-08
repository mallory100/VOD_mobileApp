package com.elkapw.vod.testapp1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VodDrawerMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<VideoObject> video_array;
    JSONParser jParserVideo = new JSONParser();
    JSONObject jsonVideo;
    JSONArray json_arrayVideo;
    VideoObject videoObject;
   // private static String url_getVideosData = "http://192.168.0.14:5080/red56/AndroidVideosDataServlet";
    private static String url_getVideosData = "http://192.168.1.21:5080/red56/AndroidVideosDataServlet";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_drawer_menu);

        //JSON array to ArrayList
        video_array = new ArrayList<VideoObject>();
        VideoDataReader vd = new VideoDataReader();
        vd.execute();

/*      video_array.add(new VideoObject("name1", "url1", "desc1"));
        video_array.add(new VideoObject("name2","url1","desc1"));
        video_array.add(new VideoObject("name3","url1","desc1"));
*/

        // Create the adapter to convert the array to views
        VideosAdapter adapter = new VideosAdapter(this, video_array);
        ListView listView = (ListView) findViewById(R.id.listViewMovies);   // Attach the adapter to a ListView
        listView.setAdapter(adapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


     //   TextView videoName = (TextView) findViewById(R.id.videoName) ;
       TextView movieElement = (TextView) findViewById(R.id.textViewVideo);
      //TextView movieElement1 = (TextView) listView.findViewById(R.id.videoName);

////// POPRAWA null we view, zakomentowant onclick dzialaa!!
     //   LinearLayout movieElement1 = (LinearLayout) findViewById(R.id.movieElement);
       // View movieElementView = new View(this.getBaseContext());
      //  movieElementView  = getLayoutInflater().inflate(R.layout.video_item, null);
//        movieElement1.addView(movieElementView);

          movieElement.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent videoViewWindow = new Intent(getApplicationContext(), VideoViewActivity.class);
                videoViewWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(videoViewWindow);
            }});


    /*   videoName.setOnClickListener(new View.OnClickListener() {TextView videoName = (TextView) findViewById(R.id.videoName) ;
            @Override
            public void onClick(View arg0) {
                Intent videoViewWindow = new Intent(getApplicationContext(), VideoViewActivity.class);
                videoViewWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(videoViewWindow);
            }
        }); */


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vod_drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.newMovies) {

        } else if (id == R.id.comedyMovies) {

        } else if (id == R.id.horrorMovies) {

        } else if (id == R.id.documentMovies) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "VodDrawerMenu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.elkapw.vod.testapp1/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "VodDrawerMenu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.elkapw.vod.testapp1/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class VideosAdapter extends ArrayAdapter<VideoObject> {
        public VideosAdapter(Context context, ArrayList<VideoObject> videos) {
            super(context, 0, videos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            VideoObject video = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_item, parent, false);
            }
            // Lookup view for data population
            TextView videoName = (TextView) convertView.findViewById(R.id.videoName);
        //    TextView videoHome = (TextView) convertView.findViewById(R.id.videoHome);
            // Populate the data into the template view using the data object
            videoName.setText(video.getVideoName());
          //  videoHome.setText(video.getVideoDescription());
            // Return the completed view to render on screen
            return convertView;
        }
    }


    private class VideoDataReader extends AsyncTask<String, String, String> {
        String s = null;
        AlertDialog.Builder popup;

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            jsonVideo = jParserVideo.makeHttpRequest(url_getVideosData, "GET", params);

            try {

                JSONArray json_array = jsonVideo.getJSONArray("Video");
                Log.d("Msg", jsonVideo.getString("Video"));

                int size = json_array.length();
                // video_array = new ArrayList<VideoObject>();

                for (int i = 0; i < size; i++) {
                    JSONObject video = json_array.getJSONObject(i);
                    videoObject = new VideoObject(video.getString("videoname"), video.getString("videourl"), video.getString("videodescription"));
                    video_array.add(videoObject);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }



    public void goToSpecificVideoContentActivity(){

        Intent specificVideoContent = new Intent(getApplicationContext(), VodDrawerMenuActivity.class);
        specificVideoContent.putExtra("video_array", video_array);
        specificVideoContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(specificVideoContent);


    }


}
