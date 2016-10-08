package com.elkapw.vod.testapp1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.elkapw.vod.testapp1.R.layout.activity_main;

public class MainActivity extends Activity {
    EditText uname, password; // pola na login i haslo
    Button submit; //przycisk submit
    AlertDialog.Builder popup;

    JSONParser jParser = new JSONParser();
    JSONObject json;

    String username, pass;

//    private static String url_login = "http://192.168.0.14:5080/red56/AndroidLoginServlet";
    private static String url_login = "http://192.168.1.21:5080/red56/AndroidLoginServlet";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(activity_main);



        findViewsById();
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // execute method invokes doInBackground() where we open a Http URL connection using the given Servlet URL
                //and get output response from InputStream and return it.
                username = uname.getText().toString();
                pass = password.getText().toString();

                Login newLogin = new Login();
                newLogin.execute(username, pass);
              //  goToVodContentActivity();

              //  goToVodContentActivity();
                finish();


            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void findViewsById() {

        uname = (EditText) findViewById(R.id.txUtser);
        password = (EditText) findViewById(R.id.txtPass);
        submit = (Button) findViewById(R.id.button1);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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

    private class Login extends AsyncTask<String, String, String> {
        String s = null;
        AlertDialog.Builder popup;

        @Override
        protected String doInBackground(String... args) {
            // Getting username and password from user input
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u", username));
            params.add(new BasicNameValuePair("p", pass));
            json = jParser.makeHttpRequest(url_login, "GET", params);

            try {
                s = json.getString("info");
                Log.d("Msg", json.getString("info"));

                if (s.equals("fail")) {
                    goToVodContentActivity();
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
            popup = new AlertDialog.Builder(MainActivity.this);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

           /* if (s.equals("fail")) {
              //  createPopup(popup);
            } */
        }


    }



    public void goToVideoViewActivity() {

        Intent videoViewWindow = new Intent(getApplicationContext(), VideoViewActivity.class);
        videoViewWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(videoViewWindow);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //  if (id == R.id action_settings) {
        //     return true;
        // }
        return super.onOptionsItemSelected(item);
    }


    public void createPopup(AlertDialog.Builder newpopup) {

        newpopup.setTitle("Niepoprawne dane logowania");
        newpopup.setMessage("Wprowadz poprawny login/haslo!!");
        newpopup.show();
    }

    public void goToVodContentActivity(){



        Intent vodContent = new Intent(getApplicationContext(), VodDrawerMenuActivity.class);
        vodContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(vodContent);


    }


}