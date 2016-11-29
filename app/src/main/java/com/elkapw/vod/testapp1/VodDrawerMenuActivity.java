package com.elkapw.vod.testapp1;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;

public class VodDrawerMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<VideoObject> videoList;
    JSONParser jParserVideo = new JSONParser();
    JSONObject jsonVideo;
    VideoObject videoObject;
    boolean isUserLogged = false;
    String videoCategory;
    ListView listView;
    private static String url_getVideosData = "http://192.168.0.14:5080/red56/AndroidVideosDataServlet";
  //  private static String url_getVideosData = "http://192.168.1.21:5080/red56/AndroidVideosDataServlet";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        videoCategory = "Free";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_drawer_menu);

        //Create ListView with movies
        listView = (ListView) findViewById(R.id.listViewMovies);
        videoList = new ArrayList<VideoObject>();
        loadVideosFromServer();

        //Create listVIEW LISTENER
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), VideoViewActivity.class);
                intent.putExtra("videoObject", videoList.get(position));
                startActivity(intent);
            }

        });

        //Create TOOLbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create FLOATINGACTIONBUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TO DO", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Create DRAWERLAYOUT
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Create NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Create test TEXTVIEW
       TextView movieElement = (TextView) findViewById(R.id.testView);
          movieElement.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent videoViewWindow = new Intent(getApplicationContext(), VideoViewActivity.class);
                videoViewWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(videoViewWindow);
            }});

        // Create ListVIEW!!!
        /*
        View videoItemView  = getLayoutInflater().inflate(R.layout.video_item, null);
        TextView videoElementName = (TextView) videoItemView.findViewById(R.id.videoName);

*/







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

        if (id == R.id.userAccount) {
            // Handle the camera action
        } else if (id == R.id.accounts) {

            AccountObject accountObject = new AccountObject(this);
            Account[] acc = accountObject.returnAccountList(getString(R.string.accountTypePremium), this);

            // BRAK KONT - popup Toast
            if( acc.length == 0 ) {
                Log.e(null, "No accounts of type " + R.string.accountTypePremium + " found");
                Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
            }

            // ISTNIEJA KONTA - popup z mozliwoscia wyboru konta:
            if (acc.length != 0) {

                Toast.makeText(this, "Accounts exists", Toast.LENGTH_SHORT).show();

                this.selectAccountsPopup(acc);
            }

        } else if (id == R.id.freeMovies) {

            videoCategory = "Free";
            loadVideosFromServer();

        } else if (id == R.id.cityMovies) {

            videoCategory = "City";
            loadVideosFromServer();


        } else if (id == R.id.natureMovies) {

            videoCategory = "Nature";
            loadVideosFromServer();

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

// POBRANIE LISTY FILMoW Z SERWERA
    private class VideoDataReader extends AsyncTask<String, String, String> {
        String s = null;

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair("category", videoCategory));
            jsonVideo = jParserVideo.makeHttpRequest(url_getVideosData, "GET", params);

            try {

                JSONArray json_array = jsonVideo.getJSONArray("Video");
                Log.d("Msg", jsonVideo.getString("Video"));

                int size = json_array.length();
                // videoList = new ArrayList<VideoObject>();

                videoList.clear();

                for (int i = 0; i < size; i++) {
                    JSONObject video = json_array.getJSONObject(i);
                    videoObject = new VideoObject(video.getString("videoname"), video.getString("videourl"), video.getString("videodescription"));
                    videoList.add(videoObject);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            System.out.println("pobranie filmow - ON PRE EXECUTE - DONE!!");



        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            System.out.println("pobranie filmow - ON PRE EXECUTE - DONE!!");




        }

    }



    public void goToSpecificVideoContentActivity(){

        Intent specificVideoContent = new Intent(getApplicationContext(), VodDrawerMenuActivity.class);
        specificVideoContent.putExtra("videoList", videoList);
        specificVideoContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(specificVideoContent);


    }


    private void selectAccountsPopup(Account[] acc){
        View inflatedView = getLayoutInflater().inflate(R.layout.accounts_popup, null);
        ListView listViewAccounts = (ListView) inflatedView.findViewById(R.id.listViewAccounts);

        final AccountObject accountObject = new AccountObject(this);

        List<String> list = new ArrayList<String>() ;

        for (int j =0; j <acc.length; j++){
            list.add(acc[j].name);
            System.out.println(list);
        }

        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        System.out.println("CS TO JEST: " + Arrays.toString(cs));

        AlertDialog.Builder builder = new AlertDialog.Builder(VodDrawerMenuActivity.this);
        builder.setTitle("Jestes zalogowany na urzadzeniu! ");

        // WYBRANIE ISTNIEJACEGO KONTA:
        //   builder.setMessage("Wybierz jedno z ponizszych kont badz utworz nowe");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Klikniety " + which);
                accountObject.setAccountID(which);

                Account mAccount = accountObject.acc[which];
                accountObject.accountName = accountObject.acc[which].name;
                accountObject.getExistingAccountAuthToken(mAccount, "TOKEN", getBaseContext(), true);

                while (accountObject.getThreadStatus() != Thread.State.TERMINATED) {
                    System.out.println("WATEK NIE ZOSTAL UKONCZONY = " + accountObject.getThreadStatus());
                    System.out.println("TOKEN Uzytkownika TO : " + accountObject.accountToken);

                }

                System.out.println("TOKEN Uzytkownika TO : " +  accountObject.accountToken);

                ServerAuthLogin newRequestLogin = new ServerAuthLogin();
                newRequestLogin.execute(accountObject.accountName, accountObject.accountToken);

                System.out.println("STATUS REQUESTA " + newRequestLogin.getStatus());

            }

        });

        final AlertDialog dialog = builder.create();

        new android.os.Handler().postDelayed(new Runnable() {

            public void run() {
                dialog.show();
            }

        }, 1000L);

    }




    class ServerAuthLogin extends AsyncTask<String, String, String> {
        String s = null;
        JSONParser jParser = new JSONParser();
        JSONObject json;

        String accountToken;
        String accountLogin;

        private String url_login = "http://192.168.0.14:5080/red56/AndroidLoginServlet";


        @Override
        protected String doInBackground(String... args) {
            accountLogin = args[0];
            accountToken = args[1];

            // Getting username and password from user input
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u", accountLogin));
            params.add(new BasicNameValuePair("p", accountToken));
            json = jParser.makeHttpRequest(url_login, "GET", params);
            System.out.println("OPERACJA : LOGIN USER");

            return "Wykonano doinBackground";
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            System.out.println("ServerAuth - ON PRE EXECUTE - DONE!!");


        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            try {
                //informacja zwrotna czy powodzenie
                s = json.getString("info");
                if (s.equals("success")) {
                    System.out.println("ServerAuth - DoInBackground - SUCCESS!!");
                    goToVodContentActivity();

                } else {

                    System.out.println("ServerAuth - DoInBackground - FAIL!!");
                    Toast.makeText(VodDrawerMenuActivity.this, "Token jest nieaktualny", Toast.LENGTH_LONG).show();
                    goToLoginAuthenticatorActivity(); //powrot do ekranu logowania


                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();

            }


            System.out.println("ServerAuth - ON POST EXECUTE - DONE!!");

        }

    }



    // Funkcje obslugujace przejscia miedzy Activity
    public void goToLoginAuthenticatorActivity() {
        Intent authenticatorActivityWindow = new Intent(getApplicationContext(), LoginAuthenticatorActivity.class);
        authenticatorActivityWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(authenticatorActivityWindow);
    }

    public void goToVodContentActivity(){

        Intent vodContent = new Intent(getApplicationContext(), VodDrawerMenuActivity.class);
        vodContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(vodContent);
    }

    public void loadVideosFromServer(){

        //JSON array to ArrayList
        VideoDataReader vd = new VideoDataReader();
        vd.execute();

        // Create the adapter to convert the array to views
        VideosAdapter adapter = new VideosAdapter(this, videoList);
          // Attach the adapter to a ListView
        listView.setAdapter(adapter);


    }


}
