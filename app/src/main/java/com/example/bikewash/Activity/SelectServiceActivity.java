package com.example.bikewash.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.bikewash.R;
import com.example.bikewash.Utility.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.bikewash.Utility.Constants.AUTO_SERVICE;
import static com.example.bikewash.Utility.Constants.BIKE_SERVICE;
import static com.example.bikewash.Utility.Constants.CAR_SERVICE;
import static com.example.bikewash.Utility.Constants.OTHER_SERVICE;
import static com.example.bikewash.Utility.Constants.TEMPO_SERVICE;
import static com.example.bikewash.Utility.Constants.TRACTOR_SERVICE;
import static com.example.bikewash.Utility.Constants.TRUCK_SERVICE;
public class SelectServiceActivity extends BaseActivity implements View.OnClickListener {

    private CardView bikeCard, carCard, tempoCard, tractorCard, truckCard, autoCard, otherCard;
    private CheckBox bikeCheckBox, carCheckBox, tempoCheckBox, tractorCheckBox, truckCheckBox,
            autoCheckBox, otherCheckBox;
    private EditText timeToReach, vehicleName;
    private Button submitButton;
    int serviceSelectedIs = BIKE_SERVICE;

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
        vehicleName = findViewById( R.id.vehicleName );
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
            vehicleName.setText( "" );
        } else if (v == carCard) {
            checkBoxChecked( false, true, false, false, false, false, false );
            serviceSelectedIs = CAR_SERVICE;
            vehicleName.setText( "" );
        } else if (v == tempoCard) {
            checkBoxChecked( false, false, true, false, false, false, false );
            serviceSelectedIs = TEMPO_SERVICE;
            vehicleName.setText( "" );
        } else if (v == truckCard) {
            checkBoxChecked( false, false, false, true, false, false, false );
            serviceSelectedIs = TRUCK_SERVICE;
            vehicleName.setText( "" );
        } else if (v == tractorCard) {
            checkBoxChecked( false, false, false, false, true, false, false );
            serviceSelectedIs = TRACTOR_SERVICE;
            vehicleName.setText( "" );
        } else if (v == autoCard) {
            checkBoxChecked( false, false, false, false, false, true, false );
            serviceSelectedIs = AUTO_SERVICE;
            vehicleName.setText( "" );
        } else if (v == otherCard) {
            checkBoxChecked( false, false, false, false, false, false, true );
            serviceSelectedIs = OTHER_SERVICE;
            vehicleName.setText( "" );
        } else if (v == submitButton) {
            String reachingTime = timeToReach.getText().toString();
            String vehicleNameData = vehicleName.getText().toString();
            if (vehicleNameData.equalsIgnoreCase( "" )) {
                vehicleName.setError( "Please provide vehicle name" );
            } else if (reachingTime.equalsIgnoreCase( "" )) {
                timeToReach.setError( "Please provide time to reach at service station" );
            } else {
                Toast.makeText( this, String.valueOf( serviceSelectedIs ), Toast.LENGTH_SHORT ).show();
                sendDataToFireBase( serviceSelectedIs, vehicleNameData, reachingTime );
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

    private void sendDataToFireBase(int serviceSelectedIs, String vehicle_Name, String reachTime) {
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
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference( "Database" );
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        mDatabase.child( uid ).child( "reach_time" ).setValue( reachTime );
        mDatabase.child( uid ).child( "vehicle_name" ).setValue( vehicle_Name );
        mDatabase.child( uid ).child( "vehicle_type" ).setValue( vehicle_type );

    }
}