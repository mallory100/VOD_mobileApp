package com.elkapw.vod.testapp1;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majka on 2016-11-13.
 */
class ServerAuthentication extends AsyncTask<String, String, String> {
        String s = null;

        JSONParser jParser = new JSONParser();
        JSONObject json;


        String accountToken;
        String accountLogin, accountPassword;
        boolean isTokenReturned = false;
        boolean isAccountCreated = false;
        boolean isUserLogged = false;

        private static String url_returnToken = "http://192.168.0.14:5080/red56/AndroidReturnTokenServlet";
        private static String url_login = "http://192.168.0.14:5080/red56/AndroidLoginServlet";
        private static String url_createAccount = "http://192.168.0.14:5080/red56/AndroidCreateAccountServlet";

    ServerAuthentication() {
    }


    protected void setUserLogin(String username) {
            accountLogin = username;
        };

    protected void setUserPassword(String pass) {
        accountPassword = pass;
    };

    protected void setUserToken(String token) {
        accountToken = token;
    };


    protected String getUserLogin() {
        return accountLogin;
    };

    protected String getUserPassword() {
        return accountPassword;
    };

    protected String getUserToken() {
        return accountToken;
    };


    protected void authenticate(){

        if (isTokenReturned==true){

            // Getting username and password from user input
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u", accountLogin));
            params.add(new BasicNameValuePair("p", accountPassword));
            json = jParser.makeHttpRequest(url_returnToken, "GET", params);
            System.out.println("OPERACJA : RETURN TOKEN");

        }

        if (isAccountCreated ==true){

            // Getting username and password from user input
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u", accountLogin));
            params.add(new BasicNameValuePair("p", accountPassword));
            json = jParser.makeHttpRequest(url_createAccount, "GET", params);
            System.out.println("OPERACJA : CREATE ACCOUNT");

        }

        if (isUserLogged ==true){

            // Getting username and password from user input
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("u", accountLogin));
            params.add(new BasicNameValuePair("p", accountToken));
            json = jParser.makeHttpRequest(url_login, "GET", params);
            System.out.println("OPERACJA : LOGIN USER");

        }

    }


        @Override
        protected String doInBackground(String... args) {

            authenticate();

            try {
                //informacja zwrotna czy powodzenie
                s = json.getString("info");

                if (isTokenReturned == true ){
                    accountToken = json.getString("token");

                    System.out.println("TOKEN TO " + accountToken);
                }

                if (s.equals("success")) {

                    System.out.println("Operacja pomyślnie wykonana!!");

                }

                System.out.println("TOKEN TO " + accountToken);

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
            System.out.println("TOKEN TO " + accountToken);

        }


    }

