package com.pipam.skripsi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver{

    public SMSReceiver(){

    }
    @Override
    public void onReceive(Context context,Intent intent){
        Toast.makeText(context, "Action : "+intent.getAction(), Toast.LENGTH_SHORT).show();

    }
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}
