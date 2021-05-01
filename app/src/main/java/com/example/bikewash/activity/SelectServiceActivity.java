package com.example.bikewash.activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.bikewash.R;
import com.example.bikewash.utility.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
    private static final String TAG = "SelectServiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_select_service );
        findView();
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
            } else if (reachingTime.equalsIgnoreCase( "" )) {
                timeToReach.setError( "Please provide time to reach at service station" );
            } else {
                Toast.makeText( this, String.valueOf( serviceSelectedIs ), Toast.LENGTH_SHORT ).show();
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
        // Write a message to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child( "All" );
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = Objects.requireNonNull( firebaseAuth.getCurrentUser() ).getUid();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child( "totalNumber" );
        DatabaseReference dr1 = FirebaseDatabase.getInstance().getReference().child( "totalNumber" );
        dr.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String vehicle_type = "";
                if (serviceSelectedIs == BIKE_SERVICE) {
                    vehicle_type = "Bike";
                } else if (serviceSelectedIs == CAR_SERVICE) {
                    vehicle_type = "Car";
                } else if (serviceSelectedIs == TEMPO_SERVICE) {
                    vehicle_type = "Tempo 407";
                } else if (serviceSelectedIs == TRACTOR_SERVICE) {
                    vehicle_type = "Tractor";
                } else if (serviceSelectedIs == TRUCK_SERVICE) {
                    vehicle_type = "Mini Truck";
                } else if (serviceSelectedIs == AUTO_SERVICE) {
                    vehicle_type = "Auto";
                } else if (serviceSelectedIs == OTHER_SERVICE) {
                    vehicle_type = "Other";
                }
                String number = snapshot.getValue().toString();
                int total = Integer.parseInt( number );
                total = total + 1;
                String totalNumber = total + "";
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put( "vehicle", vehicle_Model );
                hashMap.put( "reach_time", reachTime );
                hashMap.put( "type", vehicle_type );
                hashMap.put( "uid", uid );
                hashMap.put( "number", totalNumber );
                databaseReference.push().setValue( hashMap ).addOnCompleteListener(
                        SelectServiceActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // data submitted
                                //write your code here
                                //changing total number
                                dr1.setValue( totalNumber ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        showSnackBar( "GotoNextScreen", Snackbar.LENGTH_SHORT );
                                    }
                                } );

                            } else {
                                showSnackBar( "Server error", Snackbar.LENGTH_SHORT );
                            }
                        } );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e( TAG, "onCancelled: " + error );
            }
        } );
    }
}