package com.elkapw.vod.testapp1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Serwis niezbedny do procesu logowania przy uzyciu autentykatora
 */

public class VodAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}