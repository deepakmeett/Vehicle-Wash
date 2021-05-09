package com.example.bikewash.activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikewash.R;
import com.example.bikewash.adapter.DashboardAdapter;
import com.example.bikewash.model.DashboardModel;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.ConnectivityReceiver;
import com.example.bikewash.utility.SessionManager;
import com.example.bikewash.utility.SharePreference;
import com.example.bikewash.utility.ShowInternetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.bikewash.utility.NotificationChl.CHANNEL_1;
import static com.example.bikewash.utility.Constants.ALL;
import static com.example.bikewash.utility.Constants.GET_BACK;
import static com.example.bikewash.utility.Constants.NOT_SHOW;
import static com.example.bikewash.utility.Constants.RUNNING_NUMBER1;
import static com.example.bikewash.utility.Constants.SHOW;
import static com.example.bikewash.utility.Constants.UID;
import static com.example.bikewash.utility.Constants.VEHICLE_MODEL;
import static com.example.bikewash.utility.Constants.VEHICLE_TYPE;
import static com.example.bikewash.utility.Constants.WASHING;
import static com.example.bikewash.utility.Constants.WASHING_STATUS;
public class DashboardActivity extends BaseActivity implements DashboardAdapter.GetBack, ShowInternetDialog {

    private TextView vehicleDetails, vehicleModel, vehicleType, runningNumber;
    private RecyclerView dashboardRecycler;
    private final List<DashboardModel> list = new ArrayList<>();
    private String vehicleModelNum, vehicleTyp, washingNum;
    private boolean matchUID;
    private final com.example.bikewash.utility.ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private int isWashing = 0;
    private static final String TAG = "DashboardActivity";

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
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( ALL );
        mUsersDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commonProgressbar( false, true );
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DashboardModel dashboardModel = ds.getValue( DashboardModel.class );
                    list.add( dashboardModel );
                    if (Objects.equals( ds.child( UID ).getValue(),
                                        SharePreference.getUID( DashboardActivity.this ) )) {
                        SessionManager.userKey = ds.getKey();
                        String phoneUID = SharePreference.getUID( DashboardActivity.this );
                        String userUId = Objects.requireNonNull( ds.child( UID ).getValue() ).toString();
                        matchUID = phoneUID != null && !phoneUID.equalsIgnoreCase( "" ) 
                                   && !userUId.equalsIgnoreCase( "" );
                    }
                }
                
                //We are using break inside below for loop that's we didn't put below code in above for loop
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals( ds.child( WASHING_STATUS ).getValue(), WASHING )){
                            vehicleModelNum = Objects.requireNonNull( ds.child( VEHICLE_MODEL ).getValue() ).toString();
                            vehicleTyp = Objects.requireNonNull( ds.child( VEHICLE_TYPE ).getValue() ).toString();
                            washingNum = Objects.requireNonNull( ds.child( RUNNING_NUMBER1 ).getValue() ).toString();
                            isWashing++;
                            String userTurn = "This is your turn to wash the vehicle";
                            String currentVehicleDetail = "Current vehicle washing number is " + washingNum; 
                            createNotification(userTurn, currentVehicleDetail);
                        break;
                    }else {
                        isWashing = 0;
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
                //Below two line for auto scroll(to reach on this user's card view instantly)
                int getPosition = list.size() - Integer.parseInt( ifRunningNumber );
                dashboardRecycler.scrollToPosition( list.size() - (getPosition + 1) );
                if (isWashing > 0) {
                    vehicleDetails.setText( R.string.current_vehicle_details );
                    vehicleModel.setVisibility( View.VISIBLE );
                    vehicleType.setVisibility( View.VISIBLE );
                    runningNumber.setVisibility( View.VISIBLE );
                    if (matchUID) {
                        if (vehicleModelNum != null && !vehicleModelNum.equalsIgnoreCase( "" )) {
                            vehicleModel.setText( vehicleModelNum );
                        }else {
                            vehicleModel.setText( R.string.xxx_xxx_xxx);
                        }
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
                    vehicleDetails.setText( R.string.there_is_no_vehicle_to_wash );
                    vehicleModel.setVisibility( View.INVISIBLE );
                    vehicleType.setVisibility( View.INVISIBLE );
                    runningNumber.setVisibility( View.INVISIBLE );
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
    
    private void createNotification(String currentUser, String currentVehicle){
        Intent activityIntent = new Intent(DashboardActivity.this, DashboardActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity( DashboardActivity.this, 0, activityIntent, 0 );
        NotificationManagerCompat nmc = NotificationManagerCompat.from( DashboardActivity.this );
        Notification notification = new NotificationCompat.Builder(DashboardActivity.this, CHANNEL_1)
                .setSmallIcon( R.mipmap.ic_launcher )
                .setContentTitle( currentUser )
                .setContentText( currentVehicle )
                .setPriority( Notification.PRIORITY_HIGH)
                .setCategory( Notification.CATEGORY_MESSAGE )
                .setColor( getResources().getColor( R.color.dark_sky_blue  ))
                .setContentIntent( contentIntent )
                .setAutoCancel( true )
                .build();
        nmc.notify(1, notification);
    }

    @Override
    public void showInternetLostDialog(String showOrNot) {
        if (showOrNot != null && !showOrNot.equalsIgnoreCase( "" )) {
            if (showOrNot.equalsIgnoreCase( SHOW )) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.dashboardFrameLayout, SessionManager.internetLostFragment );
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








