package com.dexter.flex.activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.FragmentTransaction;

import com.dexter.flex.R;
import com.dexter.flex.interfaces.ShowInternetDialog;
import com.dexter.flex.receiver.ConnectivityReceiver;
import com.dexter.flex.utility.BaseActivity;
import com.dexter.flex.utility.SessionManager;
import com.dexter.flex.utility.SharePreference;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.dexter.flex.utility.Constants.NOT_SHOW;
import static com.dexter.flex.utility.Constants.PASSWORD;
import static com.dexter.flex.utility.Constants.SHOW;
import static com.dexter.flex.utility.Constants.USER_EXIST;
public class UserOrWasherActivity extends BaseActivity implements View.OnClickListener, ShowInternetDialog {

    private ImageView logout;
    private Button userButton, vehicleWasherButton;
    private EditText washerKeyEditText;
    private final com.dexter.flex.receiver.ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private static final String SELECT_SERVICE = "SelectServiceActivity";
    private static final String DASHBOARD = "DashboardActivity";
    private DatabaseReference mDatabase;

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
        if (ifUserExist != null && !ifUserExist.equalsIgnoreCase( "" )) {
            goToSelectService( SELECT_SERVICE );
        }
        if (washerKey != null && !washerKey.equalsIgnoreCase( "" )) {
            goToSelectService( DASHBOARD );
        }
    }

    private void findView() {
        logout = findViewById( R.id.moreOptions );
        userButton = findViewById( R.id.userButton );
        washerKeyEditText = findViewById( R.id.washerKeyEditText );
        vehicleWasherButton = findViewById( R.id.vehicleWasherButton );
        logout.setOnClickListener( this );
        userButton.setOnClickListener( this );
        vehicleWasherButton.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == logout) {
            logout( UserOrWasherActivity.this );
        } else if (v == userButton) {
            SharePreference.removeWasherKeyUserExit( this );
            SharePreference.setUserExit( this, USER_EXIST );
            goToSelectService( SELECT_SERVICE );
        } else if (v == vehicleWasherButton) {
            String key = washerKeyEditText.getText().toString().trim();
            if (key.equalsIgnoreCase( "" )) {
                washerKeyEditText.setError( "Please provide vehicle washer key" );
            } else {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child( PASSWORD ).child( "washer" ).get().addOnCompleteListener( task -> {
                    if (!task.isSuccessful()) {
                        Log.e( "firebase", "Error getting data", task.getException() );
                    } else {
                        Log.d( "firebase", String.valueOf( task.getResult().getValue() ) );
                        SharePreference.setWasherKey( UserOrWasherActivity.this,
                                                      String.valueOf( task.getResult().getValue() ) );
                        String keySharePreference = String.valueOf( task.getResult().getValue() );
                        if (!keySharePreference.equalsIgnoreCase( "" )) {
                            if (key.equalsIgnoreCase( keySharePreference )) {
                                goToSelectService( DASHBOARD );
                            } else {
                                showSnackBar( "Key not matched", Snackbar.LENGTH_SHORT );
                            }
                        }
                    }
                } );
                
                hideSoftKeyboard( this );
            }
        }
    }

    private void goToSelectService(String whichActivity) {
        Intent intent;
        if (whichActivity.equalsIgnoreCase( SELECT_SERVICE )) {
            intent = new Intent( UserOrWasherActivity.this, SelectServiceActivity.class );
        } else {
            intent = new Intent( UserOrWasherActivity.this, DashboardActivity.class );
        }
        startActivity( intent );
        finish();
    }

    @Override
    public void showInternetLostFragment(String showOrNot) {
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