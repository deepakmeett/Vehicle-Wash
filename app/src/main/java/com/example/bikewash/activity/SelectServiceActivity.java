package com.example.bikewash.activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.bikewash.R;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.SharePreference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import static com.example.bikewash.utility.Constants.AUTO_SERVICE;
import static com.example.bikewash.utility.Constants.BIKE_SERVICE;
import static com.example.bikewash.utility.Constants.CAR_SERVICE;
import static com.example.bikewash.utility.Constants.OTHER_SERVICE;
import static com.example.bikewash.utility.Constants.TEMPO_SERVICE;
import static com.example.bikewash.utility.Constants.TRACTOR_SERVICE;
import static com.example.bikewash.utility.Constants.TRUCK_SERVICE;
public class SelectServiceActivity extends BaseActivity implements View.OnClickListener {

    private CardView bikeCard, carCard, tempoCard, tractorCard, truckCard, autoCard, otherCard;
    private CheckBox bikeCheckBox, carCheckBox, tempoCheckBox, tractorCheckBox, truckCheckBox,
            autoCheckBox, otherCheckBox;
    private EditText timeToReach, vehicleModel;
    private Button submitButton;
    int serviceSelectedIs = BIKE_SERVICE;
    private DatabaseReference dr, dr1;
    private FirebaseAuth firebaseAuth;
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
        Log.d( TAG, "checkRunningNumber: " + ifRunningNumber );
        if (ifRunningNumber != null && !ifRunningNumber.equalsIgnoreCase( "" )){
            goToDashboard();
        }
    }

    private void findView() {
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
        dr = FirebaseDatabase.getInstance().getReference().child( "all" );
        dr1 = FirebaseDatabase.getInstance().getReference().child( "all" );
    }

    @Override
    public void onClick(View v) {
        if (v == bikeCard) {
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
            String reachingTime = timeToReach.getText().toString();
            String vehicleModelData = vehicleModel.getText().toString();
            if (vehicleModelData.equalsIgnoreCase( "" )) {
                vehicleModel.setError( "Please provide vehicle name" );
            }else if (vehicleModelData.length() < 10){
                vehicleModel.setError( "Please provide valid vehicle name" );
            }
            else if (reachingTime.equalsIgnoreCase( "" )) {
                timeToReach.setError( "Please provide time to reach at service station" );
            } else {
                //  Toast.makeText( this, String.valueOf( serviceSelectedIs ), Toast.LENGTH_SHORT ).show();
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
                String vehicle_type = "";
                if (serviceSelectedIs == BIKE_SERVICE) {
                    vehicle_type = "bike";
                } else if (serviceSelectedIs == CAR_SERVICE) {
                    vehicle_type = "car";
                } else if (serviceSelectedIs == TEMPO_SERVICE) {
                    vehicle_type = "tempo";
                } else if (serviceSelectedIs == TRACTOR_SERVICE) {
                    vehicle_type = "tractor";
                } else if (serviceSelectedIs == TRUCK_SERVICE) {
                    vehicle_type = "truck";
                } else if (serviceSelectedIs == AUTO_SERVICE) {
                    vehicle_type = "auto";
                } else if (serviceSelectedIs == OTHER_SERVICE) {
                    vehicle_type = "other";
                }
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put( "vehicle_model", vehicle_Model );
                hashMap.put( "reach_time", reachTime );
                hashMap.put( "vehicle_type", vehicle_type );
                hashMap.put( "uid", uid );
                hashMap.put( "running_number", String.valueOf( size + 1 ) );
                dr.push().setValue( hashMap ).addOnCompleteListener( task -> {
                    commonProgressbar( false, true);
                    if (task.isSuccessful()) {
                        SharePreference.setRunningNumber( SelectServiceActivity.this, 
                                                          String.valueOf( size + 1 ) );
                        goToDashboard();
                    }
                } );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                commonProgressbar( false, true);
                Log.e( TAG, "onCancelled: " + error );
            }
        } );
    }

    private void goToDashboard() {
        Intent intent = new Intent( SelectServiceActivity.this, DashboardActivity.class);
        startActivity( intent );
        finish();
    }
}