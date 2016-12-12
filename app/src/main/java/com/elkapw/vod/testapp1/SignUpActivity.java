package com.elkapw.vod.testapp1;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.elkapw.vod.testapp1.LoginAuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static com.elkapw.vod.testapp1.LoginAuthenticatorActivity.PARAM_USER_PASS;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * In charge of the Sign up process. Since it's not an AuthenticatorActivity decendent,
 * it returns the result back to the calling activity, which is an AuthenticatorActivity,
 * and it return the result back to the Authenticator
 *
 * User: udinic
 */

public class SignUpActivity extends AppCompatActivity {

    private String mAccountType;

    String accountName, accountPassword, accountToken;

  //  private static String url_login = "http://192.168.0.14:5080/red56/AndroidCreateAccountServlet";
    private static String url_login = "http://192.168.0.14:8080/VOD_servlet/AndroidCreateAccountServlet";



    JSONParser jParser = new JSONParser();
    JSONObject json;
    Toast successMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.alreadyMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewAccount newLogin = new NewAccount();
                newLogin.execute(accountName, accountPassword);

                successMessage.makeText(getApplication().getBaseContext(),"Pomyślnie stworzono konto!", Toast.LENGTH_SHORT);
                goToLoginAuthenticatorActivity();
                successMessage.makeText(getApplication().getBaseContext(),"Pomyślnie stworzono konto!", Toast.LENGTH_LONG);



            }
        });
    }
    private class NewAccount extends AsyncTask<String, String, String> {

        String accountName = ((TextView) findViewById(R.id.accountName)).getText().toString().trim();
        String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();

        String s = null;
        AlertDialog.Builder popup;

        @Override
        protected String doInBackground(String... args) {

            // Getting username and password from user input
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u", accountName));
            params.add(new BasicNameValuePair("p", accountPassword));
            json = jParser.makeHttpRequest(url_login, "GET", params);

            try {
                s = json.getString("info");
                Log.d("Msg", json.getString("info"));

                accountToken = json.getString("token");
                if (s.equals("success")) {

                    //goToAuthenticatorActivity();
                    //  goToVodContentActivity();
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

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

        @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public void goToLoginAuthenticatorActivity() {
        Intent loginAuthenticatorActivityWindow = new Intent(getApplicationContext(), LoginAuthenticatorActivity.class);
        loginAuthenticatorActivityWindow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginAuthenticatorActivityWindow);
    }



}