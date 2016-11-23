package com.elkapw.vod.testapp1;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majka on 2016-10-11.
 */

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;


    public AccountAuthenticator (Context context) {
        super(context);

        // I hate you! Google - set mContext as protected!
        this.mContext = context;
    }



    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {

        final Intent intent = new Intent(mContext, LoginAuthenticatorActivity.class);
        intent.putExtra(LoginAuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(LoginAuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(LoginAuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        System.out.println("AUTHENTICATOR ADD ACCOUNT");
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {

                ServerAuthentication newRequest = new ServerAuthentication();
                newRequest.isTokenReturned = true;
                newRequest.setUserLogin(account.name);
                newRequest.setUserPassword(password);
                newRequest.execute();
                authToken = newRequest.getUserToken();
                while (authToken == null){
                    System.out.println("TOKEN nie zostal jeszcze pobrany bo : " + authToken);
                    authToken = newRequest.getUserToken();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, LoginAuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(LoginAuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(LoginAuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        System.out.println("AUTHENTICATOR GET TOKEN");
        return bundle;
    }

    public Bundle editProperties(AccountAuthenticatorResponse response, String
            accountType) {
        return null;
    };
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                              Account account, Bundle options) {
        return null;
    };
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    };
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                             Account account, String authTokenType, Bundle options) {
        return null;
    };
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account
            account, String[] features){
        return null;
    };






    }


