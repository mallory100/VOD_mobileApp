package pl.edu.pwelka.majaskrobisz.vodclient;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

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


