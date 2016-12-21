package pl.edu.pwelka.majaskrobisz.vodclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import java.io.Serializable;

/**
 * Klasa obiektu aktualnie zalogowanego uzytkownika na urzadzeniu
 */
public class AccountObject implements Serializable {

    String accountName = "niezalogowany";
    String accountToken = "brak";
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

    public void setAccountToken(String mtoken){         this.accountToken = mtoken;     };
    public String getAccountToken() {
        return accountToken;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public void removeAccount(Context mContext){

        AccountManager mAccountManager = AccountManager.get(mContext);

        mAccountManager.removeAccount(acc[accountID], new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> future) {
                try {
                    if (future.getResult()) {
                        // do something
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, null);




    }

}
