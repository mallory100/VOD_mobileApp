package pl.edu.pwelka.majaskrobisz.vodclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import static pl.edu.pwelka.majaskrobisz.vodclient.LoginAuthenticatorActivity.SERVER_URL;


/**
 * Aktywnosc glowna - odpowiada steruje innymi aktywnosciami, wyswietlane sa w niej filmy, kategorie, belka nawigacyjna, status zalogowania
 */

public class VodDrawerMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<VideoObject> videoList;
    JSONParser jParserVideo = new JSONParser();
    JSONObject jsonVideo;
    VideoObject videoObject;
    boolean isUserLogged = false;
    ListView listView;
    ArrayList<CategoryObject> categoryList;
    int currentCategoryID;
    private static String url_getAuthVideosData;
    private static String url_getBuyVideo ;
    private static String url_getVideosCategory;
    private static String url_baseImageUrl ;
    String pingURL;
    Menu menu;
    Toolbar toolbar;
    MenuItem userInfo;
    ImageView videoImageView;
    int video_postion =1;
    Account[] acc;
    AccountManager mAccountManager;
    Thread watek ;
    AccountObject accountObject;
    MyPreferenceFragment myPreferenceFragment;
    AlertDialog alertDialog;
    String port;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        currentCategoryID = 1;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_drawer_menu);

        //Stworzenie TOOLbara
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Stworzenie DRAWERLAYOUT
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //pobranie Loginu zalogowanego użytkownika:
        // PRZY uruchomieniu wyswietlamy zalogowane konta:
        accountObject = new AccountObject(this);
        acc = accountObject.returnAccountList(getString(R.string.accountTypePremium), this); //lista wszystkich kont (opcja dla uwentualnegoo rozszerzenia aplikacji

        //jezeli istnieje konto zaloguj sie na niego
        if (acc.length ==1){

            getDataAboutExistingAccount(); // pobranie loginu i tokena zalogowanego użytkownika
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            isUserLogged = true;
        }



        //Stworzenie ListView z filmami pobranymi z serwera
        listView = (ListView) findViewById(R.id.listViewMovies);
        listView.setFocusable(true);
        videoList = new ArrayList<VideoObject>();

        //okno ustawien ip serwera

        SERVER_URL = PreferenceManager.getDefaultSharedPreferences(this).getString("ipAddress", null);
        port = PreferenceManager.getDefaultSharedPreferences(this).getString("port", null);
        setIpAddress(SERVER_URL, port);

        myPreferenceFragment = (MyPreferenceFragment) getFragmentManager().findFragmentById(R.id.preferencesFragment);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.settings_menu, null);
        dialogBuilder.setView(dialogView);

        Button checkServerStatus = (Button) dialogView.findViewById(R.id.checkServerStatusButton);
        final TextView status = (TextView) dialogView.findViewById(R.id.status);

        checkServerStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnectedToServer(3000)==true){
                    status.setBackgroundColor(Color.GREEN);
                    status.setText("Uzyskano połączenie z serwerem");

                    finish();
                    startActivity(getIntent());}
                else
                {
                    status.setBackgroundColor(Color.RED);
                    status.setText("Brak połączenia z serwerem");

                }


                }
        });
        alertDialog= dialogBuilder.create();

        //Stworzenie NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        userInfo = this.menu.findItem(R.id.userInfo);

        // gdy uzytkownik jest zalogowany to Item w Menu zmienia sie na "Wyloguj"
        if (isUserLogged == true){
            this.menu.findItem(R.id.logout).setTitle("Wyloguj");
            userInfo.setTitle("Witaj użytkowniku " + accountObject.getAccountName());
        }
        if (isUserLogged == false){

            userInfo.setTitle("Witaj niezalogowany użytkowniku!");
            this.menu.findItem(R.id.logout).setTitle("Zaloguj");

        }

        System.out.println("Aktualnie zalogowany: " + accountObject.getAccountName());





            // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //OPIS TextView - opis aktualnej kategorii
    private void setVodContentDescription(String mDescription){

        TextView vodContentDescription = (TextView) findViewById(R.id.vodContentDescription);
        vodContentDescription.setText(mDescription);

    }
    //OPIS TextView - informacja o aktualnej kategorii
    private void setVodCategoryDescription(String mCategoryDescription){
        TextView vodCategoryDescription = (TextView) findViewById(R.id.vodCategoryDescription);
        vodCategoryDescription.setText("Kategoria: " + mCategoryDescription);
        vodCategoryDescription.setTypeface(null, Typeface.BOLD);
        vodCategoryDescription.setGravity(Gravity.CENTER);

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

        super.onCreateOptionsMenu(menu);

        final ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        if (categoryList!= null) {
            Menu submenu = this.menu.addSubMenu(0,0,30,"Kategorie");

            //pobrane kategorie dodajemy do menu
             for (int i = 0; i < categoryList.size(); i++) {
                 String currentID = "cat" + i;
                int resId = getResources().getIdentifier(currentID,"id", getPackageName());
                menuItemList.add(submenu.add(0, resId, 0, categoryList.get(i).getCategoryName()));
            };

            // kategoria darmowa ma ID 1
            if (currentCategoryID==1 && categoryList.size()!=0){
                setVodCategoryDescription(categoryList.get(0).getCategoryName());
                setVodContentDescription(categoryList.get(0).getCategoryDescription());
            }
        }
        System.out.println(menuItemList);

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

            alertDialog.show();

 //           myPreferenceFragment.getView().setVisibility(View.VISIBLE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();


    // obsluga dynamicznie zaladowanych kategorii filmow z serwera (max 10 kategorii)
        if (id == R.id.cat0 || id == R.id.cat1 || id == R.id.cat2 || id == R.id.cat3 || id == R.id.cat4
                || id == R.id.cat5 || id == R.id.cat6|| id == R.id.cat7|| id == R.id.cat8|| id == R.id.cat9){

            String videoCategory = item.getTitle().toString();
            CategoryObject categoryObject = new CategoryObject();

            for (int i=0; i<categoryList.size();i++){
                if (videoCategory.equals(categoryList.get(i).getCategoryName())){
                    categoryObject = categoryList.get(i);
                }
            }
            currentCategoryID = categoryObject.getCategoryID();
            System.out.println("kliknieta kategoria to " + currentCategoryID);

            //jezeli zalogowany uzytkownik uzyskuje dostep, jezeli nie - popup o braku dostepu
            if (isUserLogged==true) {
                setVodCategoryDescription("Jeszcze chwilka...");
                setVodContentDescription("Trwa ładowanie");
                videoList.clear();
                loadVideosFromServer();
                setVodCategoryDescription(categoryObject.getCategoryName());
                setVodContentDescription(categoryObject.getCategoryDescription());
            }
            else {
                if (currentCategoryID!=1){
                    showNoPermissionPopup();
                }
                else{
                    setVodCategoryDescription("Jeszcze chwilka...");
                    setVodContentDescription("Trwa ładowanie");
                    videoList.clear();
                    loadVideosFromServer();
                    setVodCategoryDescription(categoryObject.getCategoryName());
                    setVodContentDescription(categoryObject.getCategoryDescription());
                }
            }


        } else if (id == R.id.about) {

            showAboutAppPopup();

        } else if (id == R.id.logout) {

            System.out.println("ITEM TYTUL" + item.getTitle());
            if (item.getTitle().toString().equals("Zaloguj")){

                goToLoginAuthenticatorActivity();
            }
            if (item.getTitle().toString().equals("Wyloguj")){

                isUserLogged = false;
                AccountObject accountObject = new AccountObject(this);
                accountObject.removeAccount(this);
                currentCategoryID = 1;
                loadVideosFromServer();
                // kategoria darmowa ma ID 1
                if (currentCategoryID==1|| categoryList.size()!=0){
                    setVodCategoryDescription(categoryList.get(0).getCategoryName());
                    setVodContentDescription(categoryList.get(0).getCategoryDescription());
                }
                item.setTitle("Zaloguj");
                Toast.makeText(this, "Pomyślnie wylogowano", Toast.LENGTH_LONG).show();
                userInfo.setTitle("Witaj niezalogowany!");

            }
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
                Uri.parse("android-app://pl.edu.pwelka.majaskrobisz.vodclient/http/host/path")
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
                Uri.parse("android-app://pl.edu.pwelka.majaskrobisz.vodclient/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class VideosAdapter extends ArrayAdapter<VideoObject> {
        public VideosAdapter(Context context, ArrayList<VideoObject> videos) {
            super(context, 0, videos);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // Pobranie odpowiedniego obiektu VideoObject
            final VideoObject video = getItem(position);
            // Sprawdzenie czy istniejacy widok jest ponownie uzywany, jesli nie opcja inflate
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_item, parent, false);
            }

            //Podlaczenie elementow widoku do xmla
            final TextView videoName = (TextView) convertView.findViewById(R.id.videoName);
            final Button buyButton = (Button) convertView.findViewById(R.id.buyButton);
            final TextView videoDescription = (TextView) convertView.findViewById(R.id.videoDescription);
            final Button watchButton = (Button) convertView.findViewById(R.id.watchButton);
            videoImageView = (ImageView) convertView.findViewById(R.id.videoImageView);

            // Pobranie obrazkow biblioteka Picasso!!!
            Picasso.with(VodDrawerMenuActivity.this).load(url_baseImageUrl + video.getImageUrl()).fit().centerCrop().into(videoImageView);

            // Uzupelnienie danych w widoku
            videoName.setText(video.getVideoName());
            videoName.setVisibility(View.VISIBLE);
            videoDescription.setText(video.getVideoDescription());
            videoDescription.setBackgroundColor(Color.parseColor("#b07b7a80"));
            videoDescription.setVisibility(View.INVISIBLE);
            videoImageView.setClickable(true);

            watchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    System.out.println("Klikniety ogladaj!!! pozycja nr " + position);
                    goToSpecificVideoContentActivity(video);
                }
            });

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    System.out.println("Klikniety KUP!!! pozycja nr + " + position);
                    buyVideos(Integer.toString(video.getVideoID()), position);
                    ;
                }
            });


            buyButton.setVisibility(View.INVISIBLE);
            watchButton.setVisibility(View.INVISIBLE);

            videoImageView.setOnClickListener(new View.OnClickListener() {
                boolean wasClicked=false;

                @Override
                public void onClick(View arg0) {



                    if (wasClicked==false) {

                        System.out.println("Klikniety obrazek pozycja nr " + position);
                        videoDescription.setVisibility(View.VISIBLE);
                        videoName.setVisibility(View.INVISIBLE);
                        watchButton.setVisibility(View.VISIBLE);
                        buyButton.setVisibility(View.VISIBLE);


                        if (currentCategoryID ==1){
                            buyButton.setVisibility(View.INVISIBLE);
                            watchButton.setVisibility(View.VISIBLE);
                            watchButton.setClickable(true);
                        }


                        if (video.getIsBought()==true && currentCategoryID!=1){
                            watchButton.setVisibility(View.VISIBLE);
                            watchButton.setClickable(true);
                            watchButton.setBackgroundColor(Color.parseColor("#ff368596"));

                            //buyButton.setBackgroundColor(Color.WHITE);
                            buyButton.setClickable(false);
                            buyButton.setText("Zakupiono");
                            buyButton.setVisibility(View.VISIBLE);
                        }

                        if  (video.getIsBought()==false && currentCategoryID!=1){
                            watchButton.setVisibility(View.INVISIBLE);
                            buyButton.setVisibility(View.VISIBLE);
                            buyButton.setBackgroundColor(Color.parseColor("#ff368596"));
                            buyButton.setClickable(true);
                            buyButton.setText("Kup");
                        }

                        wasClicked=true;
                    } else {

                        videoName.setVisibility(View.VISIBLE);
                        videoDescription.setVisibility(View.INVISIBLE);
                        watchButton.setVisibility(View.INVISIBLE);
                        buyButton.setVisibility(View.INVISIBLE);
                        wasClicked=false;
                    }


                }
            });

            // Return the completed view to render on screen
            return convertView;
        }

    }

       private void showNoPermissionPopup(){

        AlertDialog.Builder builder = new AlertDialog.Builder(VodDrawerMenuActivity.this);
           builder.setTitle("BRAK DOSTĘPU!");
        builder.setMessage("Zaloguj się, aby zobaczyć pełną listę filmów");
        builder.create();
        builder.show();
    }

    // Funkcje obslugujace przejscia miedzy Activity
    public void goToLoginAuthenticatorActivity() {
        Intent authenticatorActivityWindow = new Intent(getApplicationContext(), LoginAuthenticatorActivity.class);
        authenticatorActivityWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(authenticatorActivityWindow);
    }

    public void goToSpecificVideoContentActivity(VideoObject video){

        Intent specificVideoContent = new Intent(getApplicationContext(), VideoViewActivity.class);
        specificVideoContent.putExtra("videoObject", video);
        specificVideoContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(specificVideoContent);
    }

    //Funkcje wywolujace pobranie danych z serwera za pomoca klasy AsyncTask
    public void loadVideosFromServer(){
        AuthVideoDataReader vd = new AuthVideoDataReader();
        vd.execute();
    }

    private void buyVideos(String mVideo_Id, int mPosition){
        PremiumVideoBuyer request = new PremiumVideoBuyer();
        video_postion=mPosition+1; //kupiony film, pozycja do powrotu po zladowaniu listy
        System.out.println("pozycja w buyvideo" + video_postion);
        request.execute(mVideo_Id);
    };

    private void returnCategories(){
        VideoCategoryReader request = new VideoCategoryReader();
        request.execute();
    }

    // Synchronizacja - Pobranie listy filmow z uwierzytelnieniem zalogowanego uzytkownika
    private class AuthVideoDataReader extends AsyncTask<String, String, String> {
        String s = null;
        boolean isExceptionCatched;


        String category = String.valueOf(currentCategoryID);
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", accountObject.getAccountToken()));
            params.add(new BasicNameValuePair("category", category));

                try {
                    jsonVideo = jParserVideo.makeHttpRequest(url_getAuthVideosData, "GET", params);
                    JSONArray json_array = jsonVideo.getJSONArray("Video");
                    int size = json_array.length();

                    for (int i = 0; i < size; i++) {
                        JSONObject video = json_array.getJSONObject(i);
                        videoObject = new VideoObject(video.getString("videoname"), video.getString("videourl"), video.getString("videodescription"), video.getBoolean("isBought"), video.getInt("video_ID"), video.getString("videoimageurl"));
                        videoList.add(videoObject);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    isExceptionCatched=true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    isExceptionCatched=true;
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            videoList.clear();
            System.out.println("Rozpoczynamy pobieranie filmów z serwera");
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            if (isExceptionCatched == true){
                setVodCategoryDescription("Brak kategorii");
                setVodContentDescription("Błąd podczas połączenia z serwerem"); //blad pobierania filmow
                Toast.makeText(getApplication().getBaseContext(), "Błąd podczas pobierania listy filmów!", Toast.LENGTH_SHORT).show();

            }

            super.onPostExecute(result);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            // Create the adapter to convert the array to views and attach the adapter to a ListView
            VideosAdapter adapter = new VideosAdapter(VodDrawerMenuActivity.this, videoList);
            listView.setAdapter(adapter);

            if (video_postion!=1){
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Odswiezenie do pozycji" + video_postion);
                        listView.smoothScrollToPosition(video_postion);
                        video_postion=1;
                    }
                });

            }
        }}


    // Synchronizacja - Zakupienie danego filmu
    private class PremiumVideoBuyer extends AsyncTask<String, String, String> {

        String s = null;
        JSONParser jParser = new JSONParser();
        JSONObject json;
        String videoID;
        boolean isExceptionCatched = false;
        boolean isJsonParserExceptionCatched = false;

        @Override
        protected String doInBackground(String... args) {
            videoID = args[0];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", accountObject.getAccountToken()));
            params.add(new BasicNameValuePair("videoID", videoID));
            try {
                json = jParser.makeHttpRequest(url_getBuyVideo, "GET", params);
                s = json.getString("info");

            } catch (JSONException e) {
                // TODO Auto-generated catch block

                isExceptionCatched = true;
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
                isJsonParserExceptionCatched = true;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            System.out.println("Rozpoczynamy zakup filmu z serwera");
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            if (isExceptionCatched == true) {
                Toast.makeText(getApplication().getBaseContext(), "Błąd podczas kupowania filmu", Toast.LENGTH_SHORT).show();

            }
            if (isJsonParserExceptionCatched == true) {
                Toast.makeText(getApplication().getBaseContext(), "Brak połączenia z siecią", Toast.LENGTH_SHORT).show();
            }
            else{
                //informacja zwrotna czy powodzenie
                if (s.equals("success")) {

                    System.out.println("FILM pomyslnie zakupiony");
                    loadVideosFromServer();

                } else {

                    System.out.println("Blad podczas zakupu filmu");
                    video_postion = 1;
                }
            };
        }

    }
    // Synchronizacja - Pobranie kategorii filmow
    private class VideoCategoryReader extends AsyncTask<String, String, String> {
        String s = null;
        JSONObject jsonCategory = new JSONObject();
        boolean isExceptionCatched = false;

        @Override
        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("videoID", "ok"));
                jsonCategory = jParserVideo.makeHttpRequest(url_getVideosCategory, "GET", params);
            }
            catch (Exception e) {
                e.printStackTrace();
                isExceptionCatched = true;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            System.out.println("Rozpoczynamy pobieranie kategorii filmow z serwera");
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);


        }

        @Override
        protected void onPostExecute(String result) {
            //informacja zwrotna czy powodzenie
            System.out.println("Json z kategoriami to " + jsonCategory);
            super.onPostExecute(result);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            try {
                categoryList = new ArrayList<CategoryObject>();
                JSONArray json_array = jsonCategory.getJSONArray("Category");
                System.out.println(json_array);
                    for (int i = 0 ;i < json_array.length(); i++) {
                        JSONObject category = json_array.getJSONObject(i);
                        categoryList.add(i, new CategoryObject(category.getInt("category"),category.getString("categoryname"),category.getString("categorydescription")));
                        System.out.println(categoryList);
                    }
            }
            catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isExceptionCatched = true;

            }
            catch (Exception e) {
                e.printStackTrace();
                isExceptionCatched = true;
            }

            if (isExceptionCatched==true){
                Toast.makeText(getApplication().getBaseContext(), "Błąd podczas pobierania kategorii", Toast.LENGTH_SHORT).show();
            }

            //Ponowne wywolanie OnCreate funkcji dla menu (pojawily sie kategorie)
            VodDrawerMenuActivity.this.invalidateOptionsMenu();
            System.out.println("SUCCESS - Pobrano kategorie!");
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isConnectedToServer(int timeout) {
        try{
            URL myUrl = new URL(pingURL);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        }
        catch (NetworkOnMainThreadException e){
            e.printStackTrace();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();

            // Handle your exceptions
            return false;
        }

    }


    private void showAboutAppPopup(){

        AlertDialog.Builder builder = new AlertDialog.Builder(VodDrawerMenuActivity.this);
        builder.setTitle("O aplikacji");
        builder.setMessage("Aplikacja stworzona na potrzeby pracy inżynierskiej. \nTemat: System wideo na żądanie na urządzenia mobilne" +
                "\nAutor: Maja Skrobisz \nUczelnia: Politechnika Warszawska \nWydział: Elektroniki i Technik Informacyjnych");
        builder.setIcon(R.drawable.elka);
        final AlertDialog dialog = builder.create();
        dialog.show();

    }


    // Funkcja pobierajaca dane na temat zalogowanego konta (glownie token) z AccountManagera
    private void getDataAboutExistingAccount(){

        // lista zalogowanych kont, domyslnie w aplikacji zawsze tylko jedno
        Account mAccount =  acc[0];
        mAccountManager = AccountManager.get(this);
        getExistingAccountAuthToken(mAccount, "TOKEN");

        while ( watek.getState() != Thread.State.TERMINATED){
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        System.out.println("AccountObject LOGIN : " + accountObject.getAccountName());
        System.out.println("AccountObject TOKEN : " + accountObject.getAccountToken());


    };

    private void updateCurrentUserData(String mtoken){
        accountObject.setAccountName(acc[0].name);
        accountObject.setAccountToken(mtoken);

    };


    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        watek = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    updateCurrentUserData(authtoken);
                    System.out.println("TOKEN POBRANY Z AccountManagera to : " + authtoken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        watek.start();
    }

    public void setIpAddress(String ipAddress, String port){
        pingURL = "http://" + ipAddress;
        SERVER_URL = "http://" + ipAddress + ":"+  port + "/VOD_servlet";
        url_getAuthVideosData = SERVER_URL + "/AndroidReturnVideosForUserServlet";
        url_getBuyVideo = SERVER_URL +"/AndroidBuyVideoServlet";
        url_getVideosCategory = SERVER_URL + "/AndroidReturnListOfVideoCategoriesServlet";
        url_baseImageUrl = SERVER_URL ;
        //pobranie kategorii filmów z serwera:
        returnCategories();
        //pobranie filmów
        loadVideosFromServer();
    };


    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }


        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if (key.equals("ipAddress"))
            {
                // get preference by key
                Preference pref = findPreference(key);
            }
            if (key.equals("port"))
            {
                // get preference by key
                Preference pref = findPreference(key);
            }
        }

    }


}
