package pl.edu.pwelka.majaskrobisz.vodclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import static pl.edu.pwelka.majaskrobisz.vodclient.R.layout.activity_login_authenticator;

/**
 * Akywnosc odpowiadajaca za proces logowania uzytkownika
 */
public class LoginAuthenticatorActivity extends Activity {
    public static String SERVER_URL = null;

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;

    EditText accountNameEditText, accountPassEditText; // pola na login i haslo
    Button signIn; //przycisk signIn
    TextView signUp;
    String username, pass, token, currentUserToken;
    String accountType = "pl.edu.pwelka.vodclient";

    String mAccountType;
    Account[] acc;
    private  String url_login = SERVER_URL + "/AndroidReturnTokenServlet";


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        super.onCreate(savedInstanceState);
        setContentView(activity_login_authenticator);
        mAccountManager = AccountManager.get(getBaseContext());

        //AUTHENTICATOR PART:
        mAccountType = getString(R.string.accountTypePremium);
        AccountManager mAccountManager = AccountManager.get(this);
        acc = mAccountManager.getAccountsByType(mAccountType);

        findViewsById();
        // PRZYCISK LOGOWANIA:
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // klikniecie powoduje pobranie danych wprowadzonych przez uzytkownika i wywolanie metody doInBackground, w ktorej wysylane
                // jest zadanie do serwera w celu uwierzytelnienia i zwrocenia tokenu
                username = accountNameEditText.getText().toString();
                pass = accountPassEditText.getText().toString();
                token = null;

                ServerAuthReturnTokenAndLogin newRequest = new ServerAuthReturnTokenAndLogin();
                newRequest.execute(username, pass);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                goToSignUpActivity();
            }
        });
    }

    private void findViewsById() {
        accountNameEditText = (EditText) findViewById(R.id.accountName);
        accountPassEditText = (EditText) findViewById(R.id.accountPassword);
        signIn = (Button) findViewById(R.id.submit);
        signUp = (TextView) findViewById(R.id.signUpTextView);
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
                "Main Page", // TODO: Define a title for the content shown.
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

    private void finishLogin(Intent intent) {

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));


        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)) {

            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = "TOKEN"; //                    mAuthTokenType;

            //Stworzenie konta i wpisanie do niego pobranego tokenu
            boolean isCorrectlyCreated =  mAccountManager.addAccountExplicitly(account, accountPassword, null);
            System.out.println("CZY KONTO ZOSTALO POPRAWNIE STWORZONE: " + isCorrectlyCreated );

            mAccountManager.setAuthToken(account, authtokenType, authtoken);

        } else {

               mAccountManager.setPassword(account, accountPassword);
        }
    }

    public void goToSignUpActivity() {
        Intent signUpActivityWindow = new Intent(getApplicationContext(), SignUpActivity.class);
        signUpActivityWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signUpActivityWindow);
    }

    public void goToVodContentActivity(){

        Intent vodContent = new Intent(getApplicationContext(), VodDrawerMenuActivity.class);
        vodContent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(vodContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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


        class ServerAuthReturnTokenAndLogin extends AsyncTask<String, String, String> {
            String s = null;
            JSONParser jParser = new JSONParser();
            JSONObject json;
            String accountToken, accountLogin;
            boolean isExceptionCatched=false;
            boolean isJsonParserExceptionCatched= false;
            @Override
            protected String doInBackground(String... args) {
                accountLogin = args[0] ;
                accountToken = args[1];

                    try{
                        // Pobranie loginu i tokenu
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("u", accountLogin));
                        params.add(new BasicNameValuePair("p", accountToken));
                        json = jParser.makeHttpRequest(url_login, "GET", params);
                        System.out.println("OPERACJA : Return token and LOGIN USER");
                        s = json.getString("info");
                        accountToken = json.getString("token");;
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        isExceptionCatched=true;
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        isJsonParserExceptionCatched = true;
                    }

                return "Wykonano doinBackground";
            }

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                System.out.println("ServerAuth RTL- ON PRE EXECUTE - DONE!!");
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);
                if (isExceptionCatched==true){
                    Toast.makeText(getApplication().getBaseContext(), "Nieprawidłowy login bądź hasło! Spróbuj ponownie.", Toast.LENGTH_SHORT).show();
                }
                if (isJsonParserExceptionCatched==true){
                    Toast.makeText(getApplication().getBaseContext(), "Brak połączenia z siecią!", Toast.LENGTH_SHORT).show();
                }

                else {
                    //informacja zwrotna czy powodzenie
                    Bundle data = new Bundle();
                    if (s.equals("success")) {

                        System.out.println("ServerAuth RTL - DoInBackground - SUCCESS!!");
                        currentUserToken = accountToken;

                        //wrzucenie danych w Bundle i wyslanie w inna czesc programu
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, currentUserToken);
                        data.putString(PARAM_USER_PASS, pass);

                        final Intent res = new Intent();
                        res.putExtras(data);
                        finishLogin(res);
                        goToVodContentActivity();
                        Toast.makeText(getApplication().getBaseContext(), "Pomyślnie zalogowano!", Toast.LENGTH_LONG).show();
                    }
                }

                System.out.println("ServerAuth RTL - ON POST EXECUTE - DONE!!");
            }

    }

    // funkcja sprawdzająca stan połączenia urządzenia do sieci
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

};




