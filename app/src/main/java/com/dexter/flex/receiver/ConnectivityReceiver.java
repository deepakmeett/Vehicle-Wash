package com.dexter.flex.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.dexter.flex.interfaces.ShowInternetDialog;

import static com.dexter.flex.utility.Constants.NOT_SHOW;
import static com.dexter.flex.utility.Constants.SHOW;
public class ConnectivityReceiver extends BroadcastReceiver {

    public final ShowInternetDialog showInternetDialog;

    public ConnectivityReceiver(ShowInternetDialog showInternetDialog) {
        this.showInternetDialog = showInternetDialog;
    }

    private static final String TAG = "ConnectivityReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        checkConnection( context );
    }

    private void checkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getApplicationContext().getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network != null) {
            if (network.getType() == ConnectivityManager.TYPE_WIFI) {
//                Toast.makeText( context, "Wi-fi Internet Available", Toast.LENGTH_SHORT ).show();
                Log.e( TAG, "checkConnection: " + "Wi-fi Internet Available" );
            } else if (network.getType() == ConnectivityManager.TYPE_MOBILE) {
//                Toast.makeText( context, "Internet Available", Toast.LENGTH_SHORT ).show();
                Log.e( TAG, "checkConnection: " + "Internet Available" );
            }
            showInternetDialog.showInternetLostFragment( NOT_SHOW );
        } else {
            showInternetDialog.showInternetLostFragment( SHOW );
        }
    }
}