package com.elkapw.vod.testapp1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.Toast;

public class PopupActivity extends Activity {
    PopupWindow popUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        popUp = new PopupWindow(this);


    }


    @Override
    protected void onStart() {
        super.onStart();
        this.createLoginFailedPopup();


    }

    public void createLoginFailedPopup() {
        this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // The code below assumes that the root container has an id called 'main'



        //Let this be the code in your n'th level thread from main UI thread
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            public void run() {
//                popUp.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);
            }
        });

    }
}