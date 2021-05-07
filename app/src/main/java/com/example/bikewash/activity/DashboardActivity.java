package com.example.bikewash.activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikewash.R;
import com.example.bikewash.adapter.DashboardAdapter;
import com.example.bikewash.model.DashboardModel;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.SessionManager;
import com.example.bikewash.utility.SharePreference;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.bikewash.utility.Constants.GET_BACK;
import static com.example.bikewash.utility.Constants.RUNNING_NUMBER1;
import static com.example.bikewash.utility.Constants.VEHICLE_MODEL;
import static com.example.bikewash.utility.Constants.VEHICLE_TYPE;
import static com.example.bikewash.utility.Constants.WASHING;
import static com.example.bikewash.utility.Constants.WASHING_STATUS;
public class DashboardActivity extends BaseActivity implements DashboardAdapter.GetBack {

    private TextView vehicleDetails, vehicleModel, vehicleType, runningNumber;
    private RecyclerView dashboardRecycler;
    private final List<DashboardModel> list = new ArrayList<>();
    private static final String TAG = "DashboardActivity";
    private String vehicleModelNum, vehicleTyp, washingNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard );
        findView();
        setDataToRecyclerview();
    }

    private void findView() {
        vehicleDetails = findViewById( R.id.vehicleDetails );
        vehicleModel = findViewById( R.id.vehicleModel );
        vehicleType = findViewById( R.id.vehicleType );
        runningNumber = findViewById( R.id.runningNumber );
        dashboardRecycler = findViewById( R.id.dashboardRecycler );
        dashboardRecycler.setHasFixedSize( true );
    }

    private void setDataToRecyclerview() {
        commonProgressbar( true, false );
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( "all" );
        mUsersDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commonProgressbar( false, true );
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DashboardModel dashboardModel = ds.getValue( DashboardModel.class );
                    list.add( dashboardModel );
                    if (Objects.equals( ds.child( "uid" ).getValue(), SharePreference.getUID( DashboardActivity.this ) )) {
                        SessionManager.userKey = ds.getKey();
                    }
                }
                
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals( ds.child( WASHING_STATUS ).getValue(), WASHING )){
                            vehicleModelNum = Objects.requireNonNull( ds.child( VEHICLE_MODEL ).getValue() ).toString();
                            vehicleTyp = Objects.requireNonNull( ds.child( VEHICLE_TYPE ).getValue() ).toString();
                            washingNum = Objects.requireNonNull( ds.child( RUNNING_NUMBER1 ).getValue() ).toString();
                            
                        break;
                    }
                }
                dashboardRecycler.setAdapter( new DashboardAdapter(
                        DashboardActivity.this, list, DashboardActivity.this ) );
                setDataToUi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                commonProgressbar( false, true );
                Log.e( TAG, "onCancelled: " + error );
            }
        } );
    }

    private void setDataToUi() {
        commonProgressbar( true, false );
        String ifRunningNumber = SharePreference.getRunningNumber( this );
        if (ifRunningNumber != null && !ifRunningNumber.equalsIgnoreCase( "" )) {
            if (list.size() != 0) {
                vehicleDetails.setText( R.string.current_vehicle_details );
                //Below two line for auto scroll(to reach on this user's card view instantly)
                int getPosition = list.size() - Integer.parseInt( ifRunningNumber );
                dashboardRecycler.scrollToPosition( list.size() - (getPosition + 1) );
                
                if (vehicleModelNum != null && !vehicleModelNum.equalsIgnoreCase( "" )) {
                    vehicleModel.setText( vehicleModelNum );
                }else {
                    vehicleModel.setText( R.string.model_number );
                }
                
                if (vehicleTyp != null && !vehicleTyp.equalsIgnoreCase( "" )) {
                    String reachMin = vehicleTyp;
                    vehicleType.setText( reachMin );
                }else {
                    vehicleType.setText( R.string.vehicle );
                }
                
                if (washingNum != null && !washingNum.equalsIgnoreCase( "" )) {
                    if (washingNum.length() == 1) {
                        String washingNumWithZero = "0" + washingNum;
                        runningNumber.setText( washingNumWithZero );
                    } else {
                        runningNumber.setText( washingNum );
                    }
                }else {
                    runningNumber.setText( R.string._00 );
                }
                
            } else {
                showSnackBar( "Data not found", Snackbar.LENGTH_SHORT );
            }
            commonProgressbar( false, true );

        }
    }

    @Override
    public void Back(int back) {
        if (back == GET_BACK) {
            Toast.makeText( DashboardActivity.this,
                            "Thanks to choose our service!", Toast.LENGTH_SHORT ).show();
            Intent intent = new Intent( DashboardActivity.this, SelectServiceActivity.class );
            SharePreference.removeUidKeyRunning( this );
            startActivity( intent );
            finish();
        }
    }
}