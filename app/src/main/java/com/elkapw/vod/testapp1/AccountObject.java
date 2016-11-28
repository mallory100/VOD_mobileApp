package com.elkapw.vod.testapp1;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by Majka on 2016-09-25.
 */
public class AccountObject implements Serializable {

    String accountName, accountPassword, accountToken;

    int accountID;
    Account[] acc;
    Thread watek;
    String accountType = "com.elkapw.vod.full";

    AccountObject(Context mContext){

        AccountManager mAccountManager = AccountManager.get(mContext);
        acc = mAccountManager.getAccountsByType(accountType);

    }

    public Account[] returnAccountList(String mAccountType, Context mContext) {

        accountType = mAccountType;
        AccountManager mAccountManager = AccountManager.get(mContext);
        acc = mAccountManager.getAccountsByType(mAccountType);
        return acc;
    };


    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String mAccountName) {
        this.accountName = mAccountName;
    }

    public AccountObject(String videoName, String videoURL, String videoDescription) {

    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    private void setNewToken(String mtoken){

        this.accountToken = mtoken;

    };

    public Thread.State getThreadStatus(){
        return  watek.getState();
    };



    public void getExistingAccountAuthToken(Account account, String authTokenType, Context mContext, boolean bool) {
        AccountManager mAccountManager = AccountManager.get(mContext);
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, bool, null, null);

        watek = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    setNewToken(authtoken);
                    System.out.println("TOKEN POBRANY Z AccountManagera to : " + authtoken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        watek.start();


    }

}
