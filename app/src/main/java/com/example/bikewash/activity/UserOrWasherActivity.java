package com.example.bikewash.activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentTransaction;

import com.example.bikewash.R;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.ConnectivityReceiver;
import com.example.bikewash.utility.SessionManager;
import com.example.bikewash.utility.SharePreference;
import com.example.bikewash.utility.ShowInternetDialog;
import com.google.android.material.snackbar.Snackbar;

import static com.example.bikewash.utility.Constants.NOT_SHOW;
import static com.example.bikewash.utility.Constants.SHOW;
import static com.example.bikewash.utility.Constants.USER_EXIST;
public class UserOrWasherActivity extends BaseActivity implements View.OnClickListener, ShowInternetDialog {

    private Button userButton, vehicleWasherButton;
    private EditText washerKeyEditText;
    private final com.example.bikewash.utility.ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private static final String SELECT_SERVICE = "SelectServiceActivity";
    private static final String DASHBOARD = "DashboardActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_or_washer );
        checkUserOrWasher();
        findView();
    }

    private void checkUserOrWasher() {
        String ifUserExist = SharePreference.getUserExit( this );
        String washerKey = SharePreference.getWasherKey( this );
        if (ifUserExist != null && !ifUserExist.equalsIgnoreCase( "" )){
            goToSelectService(SELECT_SERVICE);
        }if (washerKey != null && !washerKey.equalsIgnoreCase( "" )){
            goToSelectService(DASHBOARD);
        }
    }

    private void findView() {
        userButton = findViewById( R.id.userButton );
        washerKeyEditText = findViewById( R.id.washerKeyEditText );
        vehicleWasherButton = findViewById( R.id.vehicleWasherButton );
        userButton.setOnClickListener( this );
        vehicleWasherButton.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == userButton){
            SharePreference.removeWasherKeyUserExit( this );
            SharePreference.setUserExit( this, USER_EXIST );
            goToSelectService(SELECT_SERVICE);
        } else if (v == vehicleWasherButton){
            String key = washerKeyEditText.getText().toString().trim();
            if (key.equalsIgnoreCase( "" )){
                washerKeyEditText.setError( "Please provide vehicle washer key" );
            }else {
                SharePreference.setWasherKey( this, "76@SevenSix" );
                String keySharePreference = SharePreference.getWasherKey( this );
                if (keySharePreference != null && !keySharePreference.equalsIgnoreCase( "" )){
                    if (key.equalsIgnoreCase( keySharePreference )){
                        goToSelectService(DASHBOARD);
                    }else {
                        showSnackBar( "Key not matched", Snackbar.LENGTH_SHORT );
                    }
                }
                hideSoftKeyboard( this );
            }
        }
    }

    private void goToSelectService(String whichActivity) {
        Intent intent;
        if (whichActivity.equalsIgnoreCase( SELECT_SERVICE )){
            intent = new Intent( UserOrWasherActivity.this, SelectServiceActivity.class);
        }else {
            intent = new Intent( UserOrWasherActivity.this, DashboardActivity.class);
        }
        startActivity( intent );
        finish();
    }

    @Override
    public void showInternetLostDialog(String showOrNot) {
        if (showOrNot != null && !showOrNot.equalsIgnoreCase( "" )) {
            if (showOrNot.equalsIgnoreCase( SHOW )) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.userWasherFrameLayout, SessionManager.internetLostFragment );
                transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
                transaction.commit();
                commonProgressbar( false, true );
            } else if (showOrNot.equalsIgnoreCase( NOT_SHOW )) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove( SessionManager.internetLostFragment );
                transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
                transaction.commit();
            }
        }
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION );
        this.registerReceiver( ConnectivityReceiver, filter );
        super.onResume();
    }

    @Override
    public void onPause() {
        this.unregisterReceiver( ConnectivityReceiver );
        super.onPause();

    }
}