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
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa autentykatora niezbedna do stworzenia konta uzytkownika na urzadzeniu. Implementacja abstrakcyjnej klasy.
 */

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private final Context mContext;

    public AccountAuthenticator (Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        return Bundle.EMPTY ;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
    return Bundle.EMPTY;
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


