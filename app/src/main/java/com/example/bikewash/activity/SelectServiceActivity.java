package com.example.bikewash.activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.example.bikewash.R;
import com.example.bikewash.bottom_sheet.MoreItemsBottomSheet;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.ConnectivityReceiver;
import com.example.bikewash.utility.SessionManager;
import com.example.bikewash.utility.SharePreference;
import com.example.bikewash.utility.ShowInternetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import static com.example.bikewash.utility.Constants.ALL;
import static com.example.bikewash.utility.Constants.AUTO_SERVICE;
import static com.example.bikewash.utility.Constants.BIKE_SERVICE;
import static com.example.bikewash.utility.Constants.CAR_SERVICE;
import static com.example.bikewash.utility.Constants.FEEDBACK;
import static com.example.bikewash.utility.Constants.LOGOUT;
import static com.example.bikewash.utility.Constants.NOT_COMPLETED;
import static com.example.bikewash.utility.Constants.NOT_SHOW;
import static com.example.bikewash.utility.Constants.OTHER_SERVICE;
import static com.example.bikewash.utility.Constants.PENDING;
import static com.example.bikewash.utility.Constants.REACH_TIME;
import static com.example.bikewash.utility.Constants.REVIEW;
import static com.example.bikewash.utility.Constants.RUNNING_NUMBER1;
import static com.example.bikewash.utility.Constants.SHARE;
import static com.example.bikewash.utility.Constants.SHOW;
import static com.example.bikewash.utility.Constants.TEMPO_SERVICE;
import static com.example.bikewash.utility.Constants.TRACTOR_SERVICE;
import static com.example.bikewash.utility.Constants.TRUCK_SERVICE;
import static com.example.bikewash.utility.Constants.UID;
import static com.example.bikewash.utility.Constants.VEHICLE_MODEL;
import static com.example.bikewash.utility.Constants.VEHICLE_TYPE;
import static com.example.bikewash.utility.Constants.WASHING_STATUS;
public class SelectServiceActivity extends BaseActivity implements View.OnClickListener, ShowInternetDialog
        , MoreItemsBottomSheet.MoreOptionBottom {

    private ImageView moreOptions;
    private CardView bikeCard, carCard, tempoCard, tractorCard, truckCard, autoCard, otherCard;
    private CheckBox bikeCheckBox, carCheckBox, tempoCheckBox, tractorCheckBox, truckCheckBox,
            autoCheckBox, otherCheckBox;
    private EditText timeToReach, vehicleModel;
    private Button submitButton;
    int serviceSelectedIs = BIKE_SERVICE;
    private DatabaseReference dr, dr1;
    private FirebaseAuth firebaseAuth;
    private MoreItemsBottomSheet moreItemsBottomSheet;
    private final com.example.bikewash.utility.ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private static final String TAG = "SelectServiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_select_service );
        checkRunningNumber();
        findView();
        initialize();
    }

    private void checkRunningNumber() {
        String ifRunningNumber = SharePreference.getRunningNumber( this );
        if (ifRunningNumber != null && !ifRunningNumber.equalsIgnoreCase( "" )) {
            goToDashboard();
        }
    }

    private void findView() {
        moreOptions = findViewById( R.id.moreOptions );
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
        moreOptions.setOnClickListener( this );
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
        if (v == moreOptions) {
            moreItemsBottomSheet = new MoreItemsBottomSheet( this );
            moreItemsBottomSheet.show( getSupportFragmentManager(), "MoreOptions" );
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
                vehicleModel.setError( "Please provide vehicle name" );
            } else if (reachingTime.equalsIgnoreCase( "" )) {
                timeToReach.setError( "Please provide time to reach at service station" );
            } else {
                hideSoftKeyboard( this );
                sendDataToFireBase( serviceSelectedIs, vehicleModelData, reachingTime );
            }
        }
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
                SharePreference.setUID( SelectServiceActivity.this, uid );
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
                hashMap.put( WASHING_STATUS, PENDING );
                hashMap.put( RUNNING_NUMBER1, String.valueOf( size + 1 ) );
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
    public void showInternetLostDialog(String showOrNot) {
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
                Toast.makeText( this, "SHARE", Toast.LENGTH_SHORT ).show();
            } else if (action.equalsIgnoreCase( REVIEW )) {
                Toast.makeText( this, "REVIEW", Toast.LENGTH_SHORT ).show();
            } else if (action.equalsIgnoreCase( LOGOUT )) {
                logout( SelectServiceActivity.this );
            } else if (action.equalsIgnoreCase( FEEDBACK )) {
                Toast.makeText( this, "FEEDBACK", Toast.LENGTH_SHORT ).show();
            } else if (action.equalsIgnoreCase( NOT_COMPLETED )) {
                Toast.makeText( this, "NOT_COMPLETED", Toast.LENGTH_SHORT ).show();
            }
        }
    }
}