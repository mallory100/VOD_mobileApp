package com.elkapw.vod.testapp1;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import static com.elkapw.vod.testapp1.R.layout.activity_login_authenticator;

public class LoginAuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    public static final String KEY_ACCOUNT_TYPE = "KEY_ACCOUNT_TYPE";
    private final int REQ_SIGNUP = 1;

    private AccountManager mAccountManager;


    EditText accountName, accountPass; // pola na login i haslo
    Button signIn, signUp, showAccounts; //przycisk signIn

    JSONParser jParser = new JSONParser();
    JSONObject json;

    String username, pass, token;
    String accountType = "com.elkapw.vod.full";

    String mAccountType;
    Account[] acc;
    LinearLayout mainLayout;
    PopupWindow pw;
    ListView listViewAccounts;
    ArrayAdapter<String> arrayAdapter;
    List<String> accountsArrayList, list;
    int accountID;
        /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(activity_login_authenticator);

        mAccountManager = AccountManager.get(getBaseContext());

        //AUTHENTICATOR PART:

        mainLayout = new LinearLayout(this);
        mAccountType = getString(R.string.accountTypePremium);
        AccountManager mAccountManager = AccountManager.get(this);

        // TODO: UI to pick account, for now we'll just take the first
        acc = mAccountManager.getAccountsByType(mAccountType);

        // Jeśli brak kont krotki komunikat, że nie ma zalogowanych użytkowników:
        if( acc.length == 0 ) {
            Log.e(null, "No accounts of type " + mAccountType + " found");
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
            return;
        }

       // Jeśli występują konta dla aplikacji VOD wyświetla się popup z mozliwoscia wyboru konta:
        if (acc.length != 0) {

            this.selectAccountsPopup();

      //      mAccountManager.getAuthToken(acc[accountID], "TOKEN", options, this, this, new Handler(new OnError()));
        }










        findViewsById();
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // execute method invokes doInBackground() where we open a Http URL connection using the given Servlet URL
                //and get output response from InputStream and return it.
                username = accountName.getText().toString();
                pass = accountPass.getText().toString();

                token = null;
                Bundle data = new Bundle();

                ServerAuthentication newRequest = new ServerAuthentication();
                newRequest.isTokenReturned = true;
                newRequest.setUserLogin(username);
                newRequest.setUserPassword(pass);
                newRequest.execute();
                token = newRequest.getUserToken();

                // WORKAROUND dla zbyt pozno pobranego tokena
                while (token==null){
                    System.out.println("TOKEN nie zostal jeszcze pobrany bo : " + token);
                    token = newRequest.getUserToken();
                }

                data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                data.putString(AccountManager.KEY_AUTHTOKEN, token);
                data.putString(PARAM_USER_PASS, pass);

                final Intent res = new Intent();
                res.putExtras(data);
                finishLogin(res);
                goToVodContentActivity();

               finish();


            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                goToSignUpActivity();
                finish();


            }
        });

        showAccounts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                selectAccountsPopup();



            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    private void findViewsById() {

        accountName = (EditText) findViewById(R.id.accountName);
        accountPass = (EditText) findViewById(R.id.accountPassword);
        signIn = (Button) findViewById(R.id.submit);
        signUp = (Button) findViewById(R.id.signUp);
        showAccounts = (Button) findViewById(R.id.showAccountsButton);


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

    private void finishLogin(Intent intent) {

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        System.out.println(mAccountManager.getAccounts());

           if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)) {

            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = "TOKEN"; //                    mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            boolean isCorrectlyCreated =  mAccountManager.addAccountExplicitly(account, accountPassword, null);
               System.out.println("CZY KONTO ZOSTALO POPRAWNIE STWORZONE: " + isCorrectlyCreated );

            mAccountManager.setAuthToken(account, authtokenType, authtoken);

        } else {
               //
               mAccountManager.setPassword(account, accountPassword);

        }

           final Intent intentAccount = new Intent();
           intentAccount.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
           intentAccount.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
           setAccountAuthenticatorResult(intentAccount.getExtras());
           setResult(RESULT_OK, intentAccount);
           finish();

           /*

        setAccountAuthenticatorResult(intent.getExtras());
        mAccountManager.getAccounts();
        setResult(RESULT_OK, intent);
        finish();*/
    }



    public void goToVideoViewActivity() {

        Intent videoViewWindow = new Intent(getApplicationContext(), VideoViewActivity.class);
        videoViewWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(videoViewWindow);

    }

    public void goToAuthenticatorActivity() {
        Intent authenticatorActivityWindow = new Intent(getApplicationContext(), LoginAuthenticatorActivity.class);
        authenticatorActivityWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(authenticatorActivityWindow);
    }


    public void goToSignUpActivity() {
        Intent signUpActivityWindow = new Intent(getApplicationContext(), SignUpActivity.class);
        signUpActivityWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signUpActivityWindow);

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

    // Popup wyświetlający aktualnie zalogowane konta dla aplikacji VOD na urzadzeniu
    private void selectAccountsPopup(){
        View inflatedView = getLayoutInflater().inflate(R.layout.accounts_popup, null);
        listViewAccounts = (ListView) inflatedView.findViewById(R.id.listViewAccounts);

        list = new ArrayList<String>() ;

        for (int j =0; j <acc.length; j++){
            list.add(acc[j].name);
            System.out.println(list);
        }

        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        System.out.println("CS TO JEST: " + Arrays.toString(cs));

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAuthenticatorActivity.this);
        builder.setTitle("Jesteś zalogowany na urządzeniu! ");

        //   builder.setMessage("Wybierz jedno z poniższych kont bądź utwórz nowe");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Kliknięty " + which);
                accountID = which;

                Account mAccount =  acc[which];
                getExistingAccountAuthToken(mAccount, "TOKEN");

                goToVodContentActivity();
            }
        });

        final AlertDialog dialog = builder.create();

        new Handler().postDelayed(new Runnable(){

            public void run() {
                dialog.show();
            }

        }, 1000L);



    }



    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    System.out.println(authtoken);

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }


    private void createAccountPopup2(){

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.

        View inflatedView1 = getLayoutInflater().inflate(R.layout.accounts_popup, null);
        listViewAccounts = (ListView) inflatedView1.findViewById(R.id.listViewAccounts);

        for (int j = 0; j < acc.length; j++) {
            accountsArrayList.add(acc[j].name);
            System.out.println(accountsArrayList);
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accountsArrayList);
        listViewAccounts.setAdapter(arrayAdapter);

        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pw = new PopupWindow(
                inflatedView1,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                false);

        // The code below assumes that the root container has an id called 'main'
        //  pw.showAtLocation(findViewById(R.id.loginActivity), Gravity.CENTER, 0, 0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pw.showAtLocation(findViewById(R.id.loginActivity), Gravity.CENTER, 0, 0);
            }
        }, 1000L);
    }};



