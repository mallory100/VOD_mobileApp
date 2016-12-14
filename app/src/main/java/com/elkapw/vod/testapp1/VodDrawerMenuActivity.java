package com.elkapw.vod.testapp1;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static String url_getAuthVideosData = "http://192.168.0.14:8080/VOD_servlet/AndroidReturnVideosForUserServlet";
    private static String url_getBuyVideo = "http://192.168.0.14:8080/VOD_servlet/AndroidBuyVideoServlet";
    private static String url_getVideosCategory = "http://192.168.0.14:8080/VOD_servlet/AndroidReturnListOfVideoCategoriesServlet";

    private static String url_baseImageUrl = "http://192.168.0.14:8080/VOD_servlet/";

    String currentAccountLogin, currentAccountToken;
    Menu menu;
    Toolbar toolbar;
    //Button buyButton,watchButton;
    MenuItem userInfo;
    ImageView videoImageView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        currentCategoryID = 1;

        //pobranie LOGINU zalogowanego uzytkownika
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                currentAccountLogin= null;
            } else {
                currentAccountLogin= extras.getString("Login");
            }
        } else {
            currentAccountLogin = (String) savedInstanceState.getSerializable("Login");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_drawer_menu);


        //Stworzenie TOOLbara
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Stworzenie DRAWERLAYOUT >???
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();


        // PRZY uruchomieniu wyswietlamy zalogowane konta:
        AccountObject accountObject = new AccountObject(this);
        Account[] acc = accountObject.returnAccountList(getString(R.string.accountTypePremium), this);

        // ISTNIEJA przynajmniej 2 konta - popup z mozliwoscia wyboru konta:
        if (acc.length >1 && isUserLogged !=true) {
       //     Toast.makeText(this, "Accounts exists", Toast.LENGTH_SHORT).show();
            this.selectAccountsPopup(acc);
        }
        // istnieje jedno konto wiec nim sie logujemy
        if (acc.length ==1 && isUserLogged !=true){
            currentAccountLogin = acc[0].name;
            accountObject.getExistingAccountAuthToken(acc[0], "TOKEN", getBaseContext(), true);

            while (accountObject.getThreadStatus() != Thread.State.TERMINATED) {
                System.out.println("WATEK NIE ZOSTAL UKONCZONY = " + accountObject.getThreadStatus());
                System.out.println("TOKEN Uzytkownika TO : " + accountObject.accountToken);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }

           currentAccountToken = accountObject.accountToken;
           findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            System.out.println("*****TOKEN : " + currentAccountToken);

        }

        // ustawienie flagi ze uzytkownik jest zalogowany
        if (currentAccountLogin != null){
            isUserLogged = true;
        }


        //Stworzenie ListView z filmami pobranymi z serwera
        listView = (ListView) findViewById(R.id.listViewMovies);
        listView.setFocusable(true);
        videoList = new ArrayList<VideoObject>();
        loadVideosFromServer();

        // Listener Listview
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new Toast(VodDrawerMenuActivity.this).setText("Kliknales pozycje "+ position);

            }

        }); */

        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if (swipeDetector.swipeDetected()) {
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {

                    }
                    if (swipeDetector.getAction() == SwipeDetector.Action.RL) {

                    }else {

                    }
                }
            }});





        //Stworzenie NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        userInfo = this.menu.findItem(R.id.userInfo);

        // gdy uzytkownik jest zalogowany to Item w Menu zmienia sie na "Wyloguj"
        if (isUserLogged == true){
            this.menu.findItem(R.id.logout).setTitle("Wyloguj");
            userInfo.setTitle("Witaj użytkowniku " + currentAccountLogin);
        }
        if (isUserLogged == false){

            userInfo.setTitle("Witaj niezalogowany użytkowniku!");
            this.menu.findItem(R.id.logout).setTitle("Zaloguj");

        }

        //pobranie kategorii filmów z serwera:
        returnCategories();

        System.out.println("Aktualnie zalogowany: " + currentAccountLogin);

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
      //  getMenuInflater().inflate(R.menu.vod_drawer_menu, menu);

        super.onCreateOptionsMenu(menu);

        final ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        if (categoryList!= null) {
            Menu submenu = this.menu.addSubMenu(0,0,30,"Kategorie");

            //pobrane kategorie dodajemy do menu
             for (int i = 0; i < categoryList.size(); i++) {
                 String currentID = "cat" + i;
                int resId = getResources().getIdentifier(currentID,"id", "com.elkapw.vod.testapp1");
                menuItemList.add(submenu.add(0, resId, 0, categoryList.get(i).getCategoryName()));
            };

            // kategoria darmowa ma ID 1
            if (currentCategoryID==1|| categoryList.size()!=0){
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
                loadVideosFromServer();
                setVodCategoryDescription(categoryObject.getCategoryName());
                setVodContentDescription(categoryObject.getCategoryDescription());
            }
            else {
                if (currentCategoryID!=1){
                    showNoPermissionPopup();
                }
            }

        } else if (id == R.id.help) {


        } else if (id == R.id.appSettings) {

            AccountObject accountObject = new AccountObject(this);
            Account[] acc = accountObject.returnAccountList(getString(R.string.accountTypePremium), this);

            // BRAK KONT - popup Toast
            if( acc.length == 0 ) {
                Log.e(null, "No accounts of type " + R.string.accountTypePremium + " found");
                Toast.makeText(this, "Brak kont na urządzeniu", Toast.LENGTH_SHORT).show();
            }

            // ISTNIEJA KONTA - popup z mozliwoscia wyboru konta:
            if (acc.length != 0) {

               // Toast.makeText(this, "Accounts exists", Toast.LENGTH_SHORT).show();

                this.selectAccountsPopup(acc);
            }

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
                setVodCategoryDescription("PHIII");
                setVodContentDescription("Witaj! Zaloguj sie by zobaczysz wszystkie materiały!");
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
/*

    private void setPositionClicked(int[] mTable, int mPosition, int mWasClicked){
       mTable[mPosition] = mWasClicked;
    }
    private int getPositionClicked(int[] mTable, int mPosition){
       int wasClicked =   mTable[mPosition];
        return wasClicked;
    }*/

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
            //tabla z aktualnie kliknietymi pozycjami:
            //final int[] table =  new int[20];


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
                    buyVideos(Integer.toString(video.getVideoID()));
                }
            });


            buyButton.setVisibility(View.INVISIBLE);
            watchButton.setVisibility(View.INVISIBLE);

            /*
            //W kategorii Free nie kupujemy filmow, sa one od razu dostepne
            if (currentCategoryID ==1){
                buyButton.setVisibility(View.INVISIBLE);
                watchButton.setVisibility(View.VISIBLE);
                watchButton.setClickable(true);
            }*/

            /*
            if (video.getIsBought()==true && currentCategoryID!=1){
                watchButton.setVisibility(View.VISIBLE);
                watchButton.setClickable(true);
                watchButton.setBackgroundColor(Color.parseColor("#ff7b7a80"));

                //buyButton.setBackgroundColor(Color.WHITE);
                buyButton.setClickable(false);
                buyButton.setText("Zakupiono");
                buyButton.setVisibility(View.VISIBLE);
            }

            if  (video.getIsBought()==false && currentCategoryID!=1){
                watchButton.setVisibility(View.INVISIBLE);
                buyButton.setVisibility(View.VISIBLE);
                buyButton.setBackgroundColor(Color.parseColor("#ff7b7a80"));
                buyButton.setClickable(true);
                buyButton.setText("Kup");
            }
*/

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
                            watchButton.setBackgroundColor(Color.parseColor("#ff7b7a80"));

                            //buyButton.setBackgroundColor(Color.WHITE);
                            buyButton.setClickable(false);
                            buyButton.setText("Zakupiono");
                            buyButton.setVisibility(View.VISIBLE);
                        }

                        if  (video.getIsBought()==false && currentCategoryID!=1){
                            watchButton.setVisibility(View.INVISIBLE);
                            buyButton.setVisibility(View.VISIBLE);
                            buyButton.setBackgroundColor(Color.parseColor("#ff7b7a80"));
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

    private void selectAccountsPopup(Account[] acc){

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
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Klikniety " + which);
                accountObject.setAccountID(which);

                Account mAccount = accountObject.acc[which];
                accountObject.accountName = accountObject.acc[which].name;
                currentAccountLogin = accountObject.accountName;
                accountObject.getExistingAccountAuthToken(mAccount, "TOKEN", getBaseContext(), true);

                while (accountObject.getThreadStatus() != Thread.State.TERMINATED) {
                    System.out.println("WATEK NIE ZOSTAL UKONCZONY = " + accountObject.getThreadStatus());
                    System.out.println("TOKEN Uzytkownika TO : " + accountObject.accountToken);

                }

                System.out.println("TOKEN Uzytkownika TO : " + accountObject.accountToken);

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


    private void showNoPermissionPopup(){

        AlertDialog.Builder builder = new AlertDialog.Builder(VodDrawerMenuActivity.this);
        builder.setTitle("BRAK DOSTĘPU!");
        builder.setMessage("Zaloguj się, aby zobaczyć pełną listę filmów");
        builder.create();
        builder.show();
    }

//TO DO???? uzywany przy popupie z filmami
    class ServerAuthLogin extends AsyncTask<String, String, String> {
        String s = null;
        JSONParser jParser = new JSONParser();
        JSONObject json;

        String accountToken;
        String accountLogin;

        private String url_login = "http://192.168.0.14:8080/red56/AndroidLoginServlet";

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

            try {
                //informacja zwrotna czy powodzenie
                s = json.getString("info");
                if (s.equals("success")) {
                    System.out.println("ServerAuth - DoInBackground - SUCCESS!!");
                    isUserLogged = true;
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
        vodContent.putExtra("Login", currentAccountLogin);
        vodContent.putExtra("isUserLogged", isUserLogged);
        startActivity(vodContent);
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

    private void buyVideos(String mVideo_Id){
        PremiumVideoBuyer request = new PremiumVideoBuyer();
        request.execute(mVideo_Id);
    };

    private void returnCategories(){
        VideoCategoryReader request = new VideoCategoryReader();
        request.execute();
    }

    // Synchronizacja - Pobranie listy filmow z uwierzytelnieniem zalogowanego uzytkownika
    private class AuthVideoDataReader extends AsyncTask<String, String, String> {
        String s = null;

        String category = String.valueOf(currentCategoryID);
        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("token", currentAccountToken));
            params.add(new BasicNameValuePair("category", category));
            jsonVideo = jParserVideo.makeHttpRequest(url_getAuthVideosData, "GET", params);

            try {
                JSONArray json_array = jsonVideo.getJSONArray("Video");
                int size = json_array.length();
                videoList.clear();

                for (int i = 0; i < size; i++) {
                    JSONObject video = json_array.getJSONObject(i);
                    videoObject = new VideoObject(video.getString("videoname"), video.getString("videourl"), video.getString("videodescription"), video.getBoolean("isBought"), video.getInt("video_ID"), video.getString("videoimageurl"));
                    videoList.add(videoObject);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            System.out.println("Rozpoczynamy pobieranie filmów z serwera");
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            System.out.println("Filmy pobrane pomyślnie z serwera!");
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            // Create the adapter to convert the array to views and attach the adapter to a ListView
            VideosAdapter adapter = new VideosAdapter(VodDrawerMenuActivity.this, videoList);
            listView.setAdapter(adapter);
        }
    }


    // Synchronizacja - Zakupienie danego filmu
    private class PremiumVideoBuyer extends AsyncTask<String, String, String> {

        String s = null;
        JSONParser jParser = new JSONParser();
        JSONObject json;
        String videoID;

        @Override
        protected String doInBackground(String... args) {
            videoID = args[0];
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("login", currentAccountLogin));
            params.add(new BasicNameValuePair("videoID", videoID));
            json = jParser.makeHttpRequest(url_getBuyVideo, "GET", params);

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
            try {
                //informacja zwrotna czy powodzenie
                s = json.getString("info");
                if (s.equals("success")) {

                    System.out.println("FILM pomyslnie zakupiony");
                    loadVideosFromServer();
                } else {

                    System.out.println("Blad podczas zakupu filmu");
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();

            }
        }
    }

    // Synchronizacja - Pobranie kategorii filmow
    private class VideoCategoryReader extends AsyncTask<String, String, String> {
        String s = null;
        JSONObject jsonCategory = new JSONObject();

        @Override
        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("videoID", "ok"));
                jsonCategory = jParserVideo.makeHttpRequest(url_getVideosCategory, "GET", params);
            }


            catch (Exception e) {
                e.printStackTrace();
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //Ponowne wywolanie OnCreate funkcji dla menu (pojawily sie kategorie)
            VodDrawerMenuActivity.this.invalidateOptionsMenu();
            System.out.println("SUCCESS - Pobrano kategorie!");
        }
    }

}
