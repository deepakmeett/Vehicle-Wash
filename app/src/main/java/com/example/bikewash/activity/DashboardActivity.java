package com.example.bikewash.activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikewash.R;
import com.example.bikewash.adapter.DashboardAdapter;
import com.example.bikewash.bottom_sheet.MoreItemsBottomSheet;
import com.example.bikewash.model.DashboardModel;
import com.example.bikewash.model.UserKeyModel;
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

import static com.example.bikewash.utility.Constants.ALL;
import static com.example.bikewash.utility.Constants.FEEDBACK;
import static com.example.bikewash.utility.Constants.GET_BACK;
import static com.example.bikewash.utility.Constants.LOGOUT;
import static com.example.bikewash.utility.Constants.NOT_COMPLETED;
import static com.example.bikewash.utility.Constants.NOT_SHOW;
import static com.example.bikewash.utility.Constants.PENDING;
import static com.example.bikewash.utility.Constants.REACH_TIME;
import static com.example.bikewash.utility.Constants.REFRESH_LAYOUT;
import static com.example.bikewash.utility.Constants.REVIEW;
import static com.example.bikewash.utility.Constants.RUNNING_NUMBER1;
import static com.example.bikewash.utility.Constants.SHARE;
import static com.example.bikewash.utility.Constants.SHOW;
import static com.example.bikewash.utility.Constants.UID;
import static com.example.bikewash.utility.Constants.USER;
import static com.example.bikewash.utility.Constants.VEHICLE_MODEL;
import static com.example.bikewash.utility.Constants.VEHICLE_TYPE;
import static com.example.bikewash.utility.Constants.VEHICLE_WASHER;
import static com.example.bikewash.utility.Constants.WASHING;
import static com.example.bikewash.utility.Constants.WASHING_STATUS;
public class DashboardActivity extends BaseActivity implements View.OnClickListener, DashboardAdapter.GetBack, ShowInternetDialog, MoreItemsBottomSheet.MoreOptionBottom {

    private ImageView moreOptions;
    private TextView vehicleDetails, vehicleModel, vehicleType, runningNumber;
    private RecyclerView dashboardRecycler;
    private String vehicleModelNum, vehicleTyp, reachTime, washingNum, userIdWashing, washerKeySP, keyValue, phoneUID;
    private int isWashing = 0;
    private int pendingPosition = -1;
    //We are storing all data in List<DashboardModel>
    private final List<DashboardModel> dashboardModelList = new ArrayList<>();
    //We are storing all keys in the List<UserKeyModel>
    private final List<UserKeyModel> userKeyModel = new ArrayList<>();
    private final com.example.bikewash.utility.ConnectivityReceiver ConnectivityReceiver
            = new ConnectivityReceiver( this );
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
        moreOptions = findViewById( R.id.moreOptions );
        vehicleModel = findViewById( R.id.vehicleModel );
        vehicleType = findViewById( R.id.vehicleType );
        runningNumber = findViewById( R.id.runningNumber );
        dashboardRecycler = findViewById( R.id.dashboardRecycler );
        dashboardRecycler.setHasFixedSize( true );
        moreOptions.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == moreOptions) {
            MoreItemsBottomSheet moreItemsBottomSheet = new MoreItemsBottomSheet( this );
            moreItemsBottomSheet.show( getSupportFragmentManager(), "MoreOptions" );
        }
    }

    private void setDataToRecyclerview() {
        commonProgressbar( true, false );
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( ALL );
        mUsersDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commonProgressbar( false, true );
                dashboardModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DashboardModel dashboardModel = ds.getValue( DashboardModel.class );
                    dashboardModelList.add( dashboardModel );
                    UserKeyModel userKeyMod = ds.getValue( UserKeyModel.class );
                    keyValue = ds.getKey();
                    Objects.requireNonNull( userKeyMod ).setKey( keyValue );
                    userKeyModel.add( userKeyMod );
                    phoneUID = SharePreference.getUID( DashboardActivity.this );
                    if (Objects.equals( ds.child( UID ).getValue(), phoneUID )) {
                        SessionManager.userKey = ds.getKey();
                    }
                }
                //We are using break inside below for loop that's we didn't put below code in above for loop
                washerKeySP = SharePreference.getWasherKey( DashboardActivity.this );
                if (!washerKeySP.equalsIgnoreCase( "" )) {
                    //set pending data in blue part for washer man 
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (setDataToBluePart( ds, PENDING )) {
                            pendingPosition++;
                            break;
                        }
                        pendingPosition++;
                    }
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (setDataToBluePart( ds, WASHING )) {
                            break;
                        }
                    }
                }
                dashboardRecycler.setAdapter( new DashboardAdapter( DashboardActivity.this, dashboardModelList, DashboardActivity.this, userKeyModel, DashboardActivity.this ) );
                setDataToUi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                commonProgressbar( false, true );
                Log.e( TAG, "onCancelled: " + error );
            }
        } );
    }

    private boolean setDataToBluePart(DataSnapshot ds, String washingPending) {
        if (Objects.equals( ds.child( WASHING_STATUS ).getValue(), washingPending )) {
            //set washing data in blue part for user
            vehicleModelNum = Objects.requireNonNull( ds.child( VEHICLE_MODEL ).getValue() ).toString();
            vehicleTyp = Objects.requireNonNull( ds.child( VEHICLE_TYPE ).getValue() ).toString();
            reachTime = Objects.requireNonNull( ds.child( REACH_TIME ).getValue() ).toString();
            washingNum = Objects.requireNonNull( ds.child( RUNNING_NUMBER1 ).getValue() ).toString();
            userIdWashing = Objects.requireNonNull( ds.child( UID ).getValue() ).toString();
            isWashing++;
            if (washingPending.equalsIgnoreCase( WASHING )) {
                if (phoneUID.equalsIgnoreCase( userIdWashing )) {
                    String userTurn = "This is your turn to wash the vehicle";
                    String currentVehicleDetail = "Current vehicle washing number is " + washingNum;
                    createNotification( userTurn, currentVehicleDetail );
                }
            }
            return true;
        } else {
            isWashing = 0;
        }
        return false;
    }

    private void setDataToUi() {
        commonProgressbar( true, false );
        String ifRunningNumber = SharePreference.getRunningNumber( this );
        if (ifRunningNumber != null && !ifRunningNumber.equalsIgnoreCase( "" )) {
            scrollToPosition( ifRunningNumber, USER );
        } else {
            if (pendingPosition < dashboardModelList.size()) {
                scrollToPosition( String.valueOf( pendingPosition ), VEHICLE_WASHER );
            } else {
                scrollToPosition( String.valueOf( 0 ), VEHICLE_WASHER );
            }
            Log.d( TAG, "setDataToUi: " + pendingPosition );
        }
        if (dashboardModelList.size() != 0) {
            if (isWashing > 0) {
                vehicleDetails.setVisibility( View.VISIBLE );
                if (washerKeySP != null && !washerKeySP.equalsIgnoreCase( "" )) {
                    vehicleDetails.setText( R.string.pending_vehicle_details );
                } else {
                    vehicleDetails.setText( R.string.vehicle_washing_details );
                }
                vehicleModel.setVisibility( View.VISIBLE );
                vehicleType.setVisibility( View.VISIBLE );
                runningNumber.setVisibility( View.VISIBLE );
                
                if (vehicleModelNum != null && !vehicleModelNum.equalsIgnoreCase( "" )) {
                        if (phoneUID.equalsIgnoreCase( userIdWashing )) {
                            vehicleModel.setText( vehicleModelNum );
                        }else { 
                            vehicleModel.setText( R.string.xxx_xxx_xxx );
                        }
                    if (washerKeySP != null && !washerKeySP.equalsIgnoreCase( "" )) {
                        vehicleModel.setText( vehicleModelNum );
                    }
                } else {
                    vehicleModel.setText( R.string.model_number );
                }
                
                if (vehicleTyp != null && !vehicleTyp.equalsIgnoreCase( "" )) {
                    String typeModel = reachTime + " min " + vehicleTyp;
                    vehicleType.setText( typeModel );
                } else {
                    vehicleType.setText( R.string.vehicle );
                }
                if (washingNum != null && !washingNum.equalsIgnoreCase( "" )) {
                    if (washingNum.length() == 1) {
                        String washingNumWithZero = "0" + washingNum;
                        runningNumber.setText( washingNumWithZero );
                    } else {
                        runningNumber.setText( washingNum );
                    }
                } else {
                    runningNumber.setText( R.string._00 );
                }
            } else {
                if (washerKeySP != null && !washerKeySP.equalsIgnoreCase( "" )) {
                    vehicleDetails.setText( R.string.no_vehicle_is_pending );
                } else {
                    vehicleDetails.setText( R.string.vehicle_is_not_washing );
                }
                vehicleModel.setVisibility( View.INVISIBLE );
                vehicleType.setVisibility( View.INVISIBLE );
                runningNumber.setVisibility( View.INVISIBLE );
            }
        } else {
            showSnackBar( "Data not found", Snackbar.LENGTH_SHORT );
            vehicleDetails.setVisibility( View.GONE );
            vehicleModel.setVisibility( View.GONE );
            vehicleType.setVisibility( View.GONE );
            runningNumber.setVisibility( View.GONE );

        }
        commonProgressbar( false, true );
    }

    private void scrollToPosition(String ifRunningNumber, String userWasher) {
        if (dashboardModelList.size() != 0) {
            //Below two line for auto scroll(to reach on this user's card view instantly)
            int getPosition = dashboardModelList.size() - Integer.parseInt( ifRunningNumber );
            if (userWasher.equalsIgnoreCase( USER )) {
                dashboardRecycler.scrollToPosition( dashboardModelList.size() - (getPosition + 1) );
            } else {
                dashboardRecycler.scrollToPosition( dashboardModelList.size() - (getPosition) );
            }
            Log.d( TAG, "scrollToPosition: " + getPosition );
        } else {
            showSnackBar( "Data not found", Snackbar.LENGTH_SHORT );
            vehicleDetails.setVisibility( View.GONE );
            vehicleModel.setVisibility( View.GONE );
            vehicleType.setVisibility( View.GONE );
            runningNumber.setVisibility( View.GONE );
        }
    }

    @Override
    public void BackFromAdapter(int back) {
        if (back == GET_BACK) {
            showSnackBar( "Thanks to choose our service!", Snackbar.LENGTH_LONG );
            Intent intent = new Intent( DashboardActivity.this, SelectServiceActivity.class );
            SharePreference.removeUidKeyRunning( this );
            startActivity( intent );
            finish();
        } else if (back == REFRESH_LAYOUT) {
            pendingPosition = -1;
        }
    }

    private void createNotification(String currentUser, String currentVehicle) {
        Uri soundUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        Intent activityIntent = new Intent( DashboardActivity.this, DashboardActivity.class );
        PendingIntent contentIntent = PendingIntent.getActivity( DashboardActivity.this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT );
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService( Context.NOTIFICATION_SERVICE );
        final String CHANNEL_1 = "channel1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel( CHANNEL_1, "channel1", NotificationManager.IMPORTANCE_HIGH );
            mChannel.setLightColor( Color.BLUE );
            mChannel.enableLights( true );
            mChannel.setVibrationPattern( new long[]{0, 500, 1000} );
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType( AudioAttributes.CONTENT_TYPE_SONIFICATION )
                    .setUsage( AudioAttributes.USAGE_NOTIFICATION )
                    .build();
            mChannel.setSound( soundUri, audioAttributes );
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel( mChannel );
            }
        }
        NotificationManagerCompat nmc = NotificationManagerCompat.from( DashboardActivity.this );
        Notification notification = new NotificationCompat.Builder( DashboardActivity.this, CHANNEL_1 )
                .setSmallIcon( R.mipmap.ic_launcher )
                .setContentTitle( currentUser )
                .setContentText( currentVehicle )
                .setPriority( Notification.PRIORITY_HIGH )
                .setCategory( Notification.CATEGORY_MESSAGE )
                .setVibrate( new long[]{0, 500, 1000} )
                .setSound( soundUri )
                .setVisibility( NotificationCompat.VISIBILITY_PUBLIC )
                .setColor( getResources().getColor( R.color.dark_sky_blue ) )
                .setContentIntent( contentIntent )
                .setAutoCancel( true )
                .setFullScreenIntent( contentIntent, true )
                .build();
        nmc.notify( 1, notification );
    }

    @Override
    public void showInternetLostFragment(String showOrNot) {
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

    @Override
    public void bottomSheet(String action) {
        if (action != null && !action.equalsIgnoreCase( "" )) {
            if (action.equalsIgnoreCase( SHARE )) {
                Toast.makeText( this, "SHARE", Toast.LENGTH_SHORT ).show();
            } else if (action.equalsIgnoreCase( REVIEW )) {
                Toast.makeText( this, "REVIEW", Toast.LENGTH_SHORT ).show();
            } else if (action.equalsIgnoreCase( LOGOUT )) {
                logout( DashboardActivity.this );
            } else if (action.equalsIgnoreCase( FEEDBACK )) {
                goToFeedbackPage();
            } else if (action.equalsIgnoreCase( NOT_COMPLETED )) {
                Toast.makeText( this, "NOT_COMPLETED", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void goToFeedbackPage() {
        Intent intent = new Intent( DashboardActivity.this, FeedbackActivity.class );
        startActivity( intent );
    }
}
