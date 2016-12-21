package pl.edu.pwelka.majaskrobisz.vodclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import static pl.edu.pwelka.majaskrobisz.vodclient.LoginAuthenticatorActivity.ARG_ACCOUNT_TYPE;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


/**
 * Aktywnosc odpowiadajaca za proces rejestracji nowego uzytkownika.
 */
public class SignUpActivity extends AppCompatActivity {

    private String mAccountType;
    String accountName, accountPassword;
    private static String url_login = "http://192.168.0.14:8080/VOD_servlet/AndroidCreateAccountServlet";
    JSONParser jParser = new JSONParser();
    JSONObject json;

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
            }
        });
    }
    private class NewAccount extends AsyncTask<String, String, String> {

        String accountName = ((TextView) findViewById(R.id.accountName)).getText().toString().trim();
        String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();
        String s = null;
        boolean isErrorCatched = false;

        @Override
        protected String doInBackground(String... args) {

            //jezeli dostep do sieci:
            if (isNetworkConnected()==true) {
                    // Pobranie loginu i hasla od uzytkownika
                try{
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("u", accountName));
                    params.add(new BasicNameValuePair("p", accountPassword));
                    json = jParser.makeHttpRequest(url_login, "GET", params);
                    s = json.getString("info");
                } catch (JSONException e) {
                    isErrorCatched = true;
                    e.printStackTrace();
                }
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

            if (isNetworkConnected()==false){

                Toast.makeText(getApplication().getBaseContext(), "Brak połączenia z siecia!", Toast.LENGTH_SHORT).show();
            }
            else {

                if (isErrorCatched==true){
                    Toast.makeText(getApplication().getBaseContext(), "Błąd podczas tworzenia konta", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (s.equals("success")) {
                        goToLoginAuthenticatorActivity();
                        Toast.makeText(getApplication().getBaseContext(), "Pomyślnie stworzono konto!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplication().getBaseContext(), "Błąd podczas tworzenia konta", Toast.LENGTH_SHORT).show();
                    }
            }
            }}
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


}