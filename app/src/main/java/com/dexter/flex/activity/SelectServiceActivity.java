package com.dexter.flex.activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.dexter.flex.R;
import com.dexter.flex.bottom_sheet.SettingsBottomSheet;
import com.dexter.flex.interfaces.ShowInternetDialog;
import com.dexter.flex.receiver.ConnectivityReceiver;
import com.dexter.flex.utility.BaseActivity;
import com.dexter.flex.utility.SessionManager;
import com.dexter.flex.utility.SharePreference;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.dexter.flex.utility.Constants.ALL;
import static com.dexter.flex.utility.Constants.AUTO_SERVICE;
import static com.dexter.flex.utility.Constants.BIKE_SERVICE;
import static com.dexter.flex.utility.Constants.BOOKING;
import static com.dexter.flex.utility.Constants.CAR_SERVICE;
import static com.dexter.flex.utility.Constants.CLOSE;
import static com.dexter.flex.utility.Constants.FEEDBACK;
import static com.dexter.flex.utility.Constants.FEEDBACK_RESULT;
import static com.dexter.flex.utility.Constants.HOW_TO_USE;
import static com.dexter.flex.utility.Constants.LOGOUT;
import static com.dexter.flex.utility.Constants.NOT_SHOW;
import static com.dexter.flex.utility.Constants.OPEN;
import static com.dexter.flex.utility.Constants.OTHER_SERVICE;
import static com.dexter.flex.utility.Constants.PASSWORD;
import static com.dexter.flex.utility.Constants.PENDING;
import static com.dexter.flex.utility.Constants.RANDOM;
import static com.dexter.flex.utility.Constants.REACH_TIME;
import static com.dexter.flex.utility.Constants.REVIEW;
import static com.dexter.flex.utility.Constants.RUNNING_NUMBER;
import static com.dexter.flex.utility.Constants.SHARE;
import static com.dexter.flex.utility.Constants.SHOW;
import static com.dexter.flex.utility.Constants.TEMPO_SERVICE;
import static com.dexter.flex.utility.Constants.TRACTOR_SERVICE;
import static com.dexter.flex.utility.Constants.TRUCK_SERVICE;
import static com.dexter.flex.utility.Constants.UID;
import static com.dexter.flex.utility.Constants.VEHICLE_MODEL;
import static com.dexter.flex.utility.Constants.VEHICLE_TYPE;
import static com.dexter.flex.utility.Constants.WASHING_STATUS;
public class SelectServiceActivity extends BaseActivity implements View.OnClickListener, ShowInternetDialog
        , SettingsBottomSheet.SettingsBottom {

    private FrameLayout threeDot;
    private CardView bikeCard, carCard, tempoCard, tractorCard, truckCard, autoCard, otherCard;
    private CheckBox bikeCheckBox, carCheckBox, tempoCheckBox, tractorCheckBox, truckCheckBox,
            autoCheckBox, otherCheckBox;
    private EditText timeToReach, vehicleModel;
    private Button submitButton;
    int serviceSelectedIs = BIKE_SERVICE;
    private DatabaseReference dr, dr1;
    private FirebaseAuth firebaseAuth;
    private final ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private static final String TAG = "SelectServiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_select_service );
        checkRunningNumber();
        openSettings();
        findView();
        initialize();
    }

    private void checkRunningNumber() {
        String ifRunningNumber = SharePreference.getRunningNumber( this );
        if (ifRunningNumber != null && !ifRunningNumber.equalsIgnoreCase( "" )) {
            goToDashboard();
        }
    }

    private void openSettings() {
        String howToUse = SharePreference.getHowTo( this );
        if (howToUse.equalsIgnoreCase( "" )) {
            settings();
        }
    }

    private void findView() {
        threeDot = findViewById( R.id.settings );
        bikeCard = findViewById( R.id.bikeCard );
        bikeCheckBox = findViewById( R.id.bikeCheckBox );
        carCard = findViewById( R.id.carCard );
        carCheckBox = findViewById( R.id.carCheckBox );
        tempoCard = findViewById( R.id.tempoCard );
        tempoCheckBox = findViewById( R.id.tempoCheckBox );
        tractorCard = findViewById( R.id.tractorCard );
        tractorCheckBox = findViewById( R.id.tractorCheckBox );
        truckCard = findViewById( R.id.truckCard );
        truckCheckBox = findViewById( R.id.truckCheckBox );
        autoCard = findViewById( R.id.autoCard );
        autoCheckBox = findViewById( R.id.autoCheckBox );
        otherCard = findViewById( R.id.otherCard );
        otherCheckBox = findViewById( R.id.otherCheckBox );
        vehicleModel = findViewById( R.id.vehicleModel );
        timeToReach = findViewById( R.id.timeToReach );
        submitButton = findViewById( R.id.submitButton );
        threeDot.setOnClickListener( this );
        bikeCard.setOnClickListener( this );
        carCard.setOnClickListener( this );
        tempoCard.setOnClickListener( this );
        truckCard.setOnClickListener( this );
        tractorCard.setOnClickListener( this );
        autoCard.setOnClickListener( this );
        otherCard.setOnClickListener( this );
        submitButton.setOnClickListener( this );
    }

    private void initialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference().child( ALL );
        dr1 = FirebaseDatabase.getInstance().getReference().child( ALL );
    }

    @Override
    public void onClick(View v) {
        if (v == threeDot) {
            settings();
        } else if (v == bikeCard) {
            checkBoxChecked( true, false, false, false, false, false, false );
            serviceSelectedIs = BIKE_SERVICE;
            vehicleModel.setText( "" );
        } else if (v == carCard) {
            checkBoxChecked( false, true, false, false, false, false, false );
            serviceSelectedIs = CAR_SERVICE;
            vehicleModel.setText( "" );
        } else if (v == tempoCard) {
            checkBoxChecked( false, false, true, false, false, false, false );
            serviceSelectedIs = TEMPO_SERVICE;
            vehicleModel.setText( "" );
        } else if (v == truckCard) {
            checkBoxChecked( false, false, false, true, false, false, false );
            serviceSelectedIs = TRUCK_SERVICE;
            vehicleModel.setText( "" );
        } else if (v == tractorCard) {
            checkBoxChecked( false, false, false, false, true, false, false );
            serviceSelectedIs = TRACTOR_SERVICE;
            vehicleModel.setText( "" );
        } else if (v == autoCard) {
            checkBoxChecked( false, false, false, false, false, true, false );
            serviceSelectedIs = AUTO_SERVICE;
            vehicleModel.setText( "" );
        } else if (v == otherCard) {
            checkBoxChecked( false, false, false, false, false, false, true );
            serviceSelectedIs = OTHER_SERVICE;
            vehicleModel.setText( "" );
        } else if (v == submitButton) {
            String reachingTime = timeToReach.getText().toString().trim();
            String vehicleModelData = vehicleModel.getText().toString().trim();
            if (vehicleModelData.equalsIgnoreCase( "" )) {
                vehicleModel.setError( "Please provide vehicle model number" );
            } else if (vehicleModelData.length() <= 4) {
                vehicleModel.setError( "Model number should be more than 4 digits" );
            } else if (reachingTime.equalsIgnoreCase( "" )) {
                timeToReach.setError( "Please provide time to reach at service station" );
            } else if (Integer.parseInt( reachingTime ) <= 0) {
                timeToReach.setError( "Please provide correct time" );
            } else {
                hideSoftKeyboard( this );
                DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference();
                mUsersDatabase.child( PASSWORD ).child( BOOKING ).get()
                        .addOnCompleteListener( task -> {
                            if (!task.isSuccessful()) {
                                Log.e( "firebase", "Error getting data", task.getException() );
                            } else {
                                Log.d( "firebase", String.valueOf( Objects.requireNonNull( task.getResult() ).getValue() ) );
                                String value = (String) task.getResult().getValue();
                                if (value == null || value.equalsIgnoreCase( OPEN )) {
                                    sendDataToFireBase( serviceSelectedIs, vehicleModelData, reachingTime );
                                } else if (value.equalsIgnoreCase( CLOSE )) {
                                    showSnackBar( "Booking closed", Snackbar.LENGTH_LONG );
                                }
                            }
                        } );
            }
        }
    }

    private void settings() {
        SettingsBottomSheet settingsBottomSheet = new SettingsBottomSheet( this );
        settingsBottomSheet.show( getSupportFragmentManager(), "Settings" );
    }

    private void checkBoxChecked(boolean bike, boolean car, boolean tempo, boolean truck, boolean tractor, boolean auto, boolean other) {
        bikeCheckBox.setChecked( bike );
        carCheckBox.setChecked( car );
        tempoCheckBox.setChecked( tempo );
        truckCheckBox.setChecked( truck );
        tractorCheckBox.setChecked( tractor );
        autoCheckBox.setChecked( auto );
        otherCheckBox.setChecked( other );
    }

    private void sendDataToFireBase(int serviceSelectedIs, String vehicle_Model, String reachTime) {
        commonProgressbar( true, false );
        dr1.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                String uid = Objects.requireNonNull( firebaseAuth.getCurrentUser() ).getUid();
                String random = UUID.randomUUID().toString();
                SharePreference.setUID( SelectServiceActivity.this, uid );
                SharePreference.setRANDOM( SelectServiceActivity.this, random );
                String vehicle_type = "";
                if (serviceSelectedIs == BIKE_SERVICE) {
                    vehicle_type = "Bike";
                } else if (serviceSelectedIs == CAR_SERVICE) {
                    vehicle_type = "Car";
                } else if (serviceSelectedIs == TEMPO_SERVICE) {
                    vehicle_type = "Tempo";
                } else if (serviceSelectedIs == TRACTOR_SERVICE) {
                    vehicle_type = "Tractor";
                } else if (serviceSelectedIs == TRUCK_SERVICE) {
                    vehicle_type = "Truck";
                } else if (serviceSelectedIs == AUTO_SERVICE) {
                    vehicle_type = "Auto";
                } else if (serviceSelectedIs == OTHER_SERVICE) {
                    vehicle_type = "Other";
                }
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put( VEHICLE_MODEL, vehicle_Model );
                hashMap.put( REACH_TIME, reachTime );
                hashMap.put( VEHICLE_TYPE, vehicle_type );
                hashMap.put( UID, uid );
                hashMap.put( RANDOM, random );
                hashMap.put( WASHING_STATUS, PENDING );
                hashMap.put( RUNNING_NUMBER, String.valueOf( size + 1 ) );
                dr.push().setValue( hashMap ).addOnCompleteListener( task -> {
                    commonProgressbar( false, true );
                    if (task.isSuccessful()) {
                        SharePreference.setRunningNumber( SelectServiceActivity.this,
                                                          String.valueOf( size + 1 ) );
                        goToDashboard();
                    }
                } );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                commonProgressbar( false, true );
                Log.e( TAG, "onCancelled: " + error );
            }
        } );
    }

    private void goToDashboard() {
        Intent intent = new Intent( SelectServiceActivity.this, DashboardActivity.class );
        startActivity( intent );
        finish();
    }

    @Override
    public void showInternetLostFragment(String showOrNot) {
        if (showOrNot != null && !showOrNot.equalsIgnoreCase( "" )) {
            if (showOrNot.equalsIgnoreCase( SHOW )) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.selectServiceFrameLayout, SessionManager.internetLostFragment );
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

    @Override
    public void bottomSheet(String action) {
        if (action != null && !action.equalsIgnoreCase( "" )) {
            if (action.equalsIgnoreCase( SHARE )) {
                share();
            } else if (action.equalsIgnoreCase( REVIEW )) {
                review( this );
            } else if (action.equalsIgnoreCase( LOGOUT )) {
                logout( SelectServiceActivity.this );
            } else if (action.equalsIgnoreCase( FEEDBACK )) {
                Intent intent = new Intent( SelectServiceActivity.this, FeedbackActivity.class );
                startActivityForResult( intent, FEEDBACK_RESULT );
            } else if (action.equalsIgnoreCase( HOW_TO_USE )) {
                Toast.makeText( this, "HOW TO USE THIS APP", Toast.LENGTH_SHORT ).show();
                SharePreference.setHowTo( this, HOW_TO_USE );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        try {
            super.onActivityResult( requestCode, resultCode, data );
            Log.d( TAG, "onActivityResult: " + resultCode + " " + requestCode );
            if (requestCode == resultCode) {
                showSnackBar( "Thanks! We value your time", Snackbar.LENGTH_LONG );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}