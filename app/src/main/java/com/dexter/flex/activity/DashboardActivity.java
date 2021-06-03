package com.dexter.flex.activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.dexter.flex.R;
import com.dexter.flex.adapter.DashboardAdapter;
import com.dexter.flex.bottom_sheet.SettingsBottomSheet;
import com.dexter.flex.interfaces.ShowInternetDialog;
import com.dexter.flex.model.DashboardModel;
import com.dexter.flex.model.UserKeyModel;
import com.dexter.flex.receiver.ConnectivityReceiver;
import com.dexter.flex.utility.BaseActivity;
import com.dexter.flex.utility.SessionManager;
import com.dexter.flex.utility.SharePreference;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dexter.flex.utility.Constants.ALL;
import static com.dexter.flex.utility.Constants.BOOKING;
import static com.dexter.flex.utility.Constants.CLOSE;
import static com.dexter.flex.utility.Constants.COMPLETED;
import static com.dexter.flex.utility.Constants.FEEDBACK;
import static com.dexter.flex.utility.Constants.GET_BACK;
import static com.dexter.flex.utility.Constants.HOW_TO_USE;
import static com.dexter.flex.utility.Constants.LOGOUT;
import static com.dexter.flex.utility.Constants.NOT_ALLOWED;
import static com.dexter.flex.utility.Constants.NOT_SHOW;
import static com.dexter.flex.utility.Constants.ONE_VEHICLE_AT_A_TIME;
import static com.dexter.flex.utility.Constants.OPEN;
import static com.dexter.flex.utility.Constants.PASSWORD;
import static com.dexter.flex.utility.Constants.PENDING;
import static com.dexter.flex.utility.Constants.PLEASE_WAIT;
import static com.dexter.flex.utility.Constants.RANDOM;
import static com.dexter.flex.utility.Constants.REACH_TIME;
import static com.dexter.flex.utility.Constants.REFRESH_LAYOUT;
import static com.dexter.flex.utility.Constants.REVIEW;
import static com.dexter.flex.utility.Constants.RUNNING_NUMBER;
import static com.dexter.flex.utility.Constants.SHARE;
import static com.dexter.flex.utility.Constants.SHOW;
import static com.dexter.flex.utility.Constants.USER;
import static com.dexter.flex.utility.Constants.VEHICLE_MODEL;
import static com.dexter.flex.utility.Constants.VEHICLE_TYPE;
import static com.dexter.flex.utility.Constants.VEHICLE_WASHER;
import static com.dexter.flex.utility.Constants.WASHING;
import static com.dexter.flex.utility.Constants.WASHING_STATUS;
public class DashboardActivity extends BaseActivity implements View.OnClickListener, DashboardAdapter.GetBack, ShowInternetDialog, SettingsBottomSheet.SettingsBottom {

    private FrameLayout threeDot;
    private TextView vehicleDetails, vehicleModel, vehicleType, runningNumber;
    private RecyclerView dashboardRecycler;
    private String vehicleModelNum, vehicleTyp, reachTime, washingNum, userRandomWashing, washerKeySP, keyValue, phoneRandom;
    private int isWashing = 0;
    private int pendingPosition = -1;
    //We are storing all data in List<DashboardModel>
    private final List<DashboardModel> dashboardModelList = new ArrayList<>();
    //We are storing all keys in the List<UserKeyModel>
    private final List<UserKeyModel> userKeyModel = new ArrayList<>();
    private final ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private DatabaseReference mUsersDatabase;
    private static final String TAG = "DashboardActivity";
    private MediaPlayer mediaPlayer;
    private String random;
    private boolean showNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        // This below code make notification bar disappear
//        requestWindowFeature( Window.FEATURE_NO_TITLE );
//        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                              WindowManager.LayoutParams.FLAG_FULLSCREEN );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard );
        findView();
        setDataToRecyclerview();
    }

    private void findView() {
        vehicleDetails = findViewById( R.id.vehicleDetails );
        threeDot = findViewById( R.id.settings );
        vehicleModel = findViewById( R.id.vehicleModel );
        vehicleType = findViewById( R.id.vehicleType );
        runningNumber = findViewById( R.id.runningNumber );
        dashboardRecycler = findViewById( R.id.dashboardRecycler );
        dashboardRecycler.setHasFixedSize( true );
        threeDot.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == threeDot) {
            SettingsBottomSheet settingsBottomSheet = new SettingsBottomSheet( this );
            settingsBottomSheet.show( getSupportFragmentManager(), "Settings" );
        }
    }

    private void setDataToRecyclerview() {
        commonProgressbar( true, false );
        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase.child( ALL ).addValueEventListener( new ValueEventListener() {
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
                    phoneRandom = SharePreference.getRANDOM( DashboardActivity.this );
                    if (Objects.equals( ds.child( RANDOM ).getValue(), phoneRandom )) {
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
            washingNum = Objects.requireNonNull( ds.child( RUNNING_NUMBER ).getValue() ).toString();
            userRandomWashing = Objects.requireNonNull( ds.child( RANDOM ).getValue() ).toString();
            isWashing++;
            if (washingPending.equalsIgnoreCase( WASHING )) {
                if (phoneRandom.equalsIgnoreCase( userRandomWashing )) {
                    String title = "This is your turn to wash the vehicle";
                    String message = "Current vehicle washing number is " + washingNum;
                    createNotification( title, message );
                }
            }
            if (washingPending.equalsIgnoreCase( PENDING )) {
                if (!washerKeySP.equalsIgnoreCase( "" )) {
                    String title = "Please confirm the booking";
                    String message = "Click pending button to activate washing";
                    createNotification( title, message );
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
                    if (phoneRandom.equalsIgnoreCase( userRandomWashing )) {
                        vehicleModel.setText( vehicleModelNum );
                    } else {
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
        } else if (back == NOT_ALLOWED) {
            showSnackBar( "Not allowed for someone else's vehicle", Snackbar.LENGTH_SHORT );
        } else if (back == PLEASE_WAIT) {
            showSnackBar( "Please wait for your number", Snackbar.LENGTH_SHORT );
        } else if (back == ONE_VEHICLE_AT_A_TIME) {
            showSnackBar( "One vehicle at a time can wash", Snackbar.LENGTH_SHORT );
        }
    }

    private void createNotification(String currentUser, String currentVehicle) {
        mediaPlayer = MediaPlayer.create( getApplicationContext(), R.raw.we_dont_talk_anymore );
        if (showNotification) {
            try {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            try {
                new Handler().postDelayed( () -> mediaPlayer.stop(), 5000 );
            } catch (Exception e) {
                e.printStackTrace();
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
        try {
            new Handler().postDelayed( () -> mediaPlayer.stop(), 8000 );
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        showNotification = false;
        IntentFilter filter = new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION );
        this.registerReceiver( ConnectivityReceiver, filter );
        super.onResume();
    }

    @Override
    public void onPause() {
        showNotification = true;
        this.unregisterReceiver( ConnectivityReceiver );
        super.onPause();

    }

    @Override
    public void bottomSheet(String action) {
        if (action != null && !action.equalsIgnoreCase( "" )) {
            if (action.equalsIgnoreCase( BOOKING )) {
                mUsersDatabase.child( PASSWORD ).child( BOOKING ).get()
                        .addOnCompleteListener( task -> {
                            if (!task.isSuccessful()) {
                                Log.e( "firebase", "Error getting data", task.getException() );
                            } else {
                                Log.d( "firebase", String.valueOf( Objects.requireNonNull( task.getResult() ).getValue() ) );
                                String value = (String) task.getResult().getValue();
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance()
                                        .getReference().child( PASSWORD );
                                if (value == null || value.equalsIgnoreCase( OPEN )) {
                                    mDatabase.child( BOOKING ).setValue( CLOSE );
                                } else if (value.equalsIgnoreCase( CLOSE )) {
                                    mDatabase.child( BOOKING ).setValue( OPEN );
                                }
                            }
                        } );
            } else if (action.equalsIgnoreCase( SHARE )) {
                share();
            } else if (action.equalsIgnoreCase( REVIEW )) {
                Toast.makeText( this, "REVIEW", Toast.LENGTH_SHORT ).show();
                review( this );
            } else if (action.equalsIgnoreCase( LOGOUT )) {
                mUsersDatabase.child( ALL ).child( SessionManager.userKey ).child( RANDOM ).get().addOnCompleteListener( task -> {
                    if (!task.isSuccessful()) {
                        Log.e( "firebase", "Error getting data", task.getException() );
                    } else {
                        random = (String) Objects.requireNonNull( task.getResult() ).getValue();
                    }
                } );
                mUsersDatabase.child( ALL ).child( SessionManager.userKey ).child( WASHING_STATUS ).get().addOnCompleteListener( task -> {
                    if (!task.isSuccessful()) {
                        Log.e( "firebase", "Error getting data", task.getException() );
                    } else {
                        String washingSTU = (String) Objects.requireNonNull( task.getResult() ).getValue();
                        if (random != null) {
                            if (phoneRandom.equalsIgnoreCase( random )) {
                                if (washingSTU != null) {
                                    if (washingSTU.equalsIgnoreCase( WASHING )
                                        || washingSTU.equalsIgnoreCase( PENDING )
                                        || washingSTU.equalsIgnoreCase( COMPLETED )) {
                                        showSnackBar( "Please complete your vehicle washing"
                                                , Snackbar.LENGTH_SHORT );
                                    } else {
                                        logout( DashboardActivity.this );
                                    }
                                }
                            } else {
                                logout( DashboardActivity.this );
                            }
                        } else {
                            logout( DashboardActivity.this );
                        }

                    }
                } );

            } else if (action.equalsIgnoreCase( FEEDBACK )) {
                goToFeedbackPage();
            }  else if (action.equalsIgnoreCase( HOW_TO_USE )) {
                Toast.makeText( this, "HOW TO USE THIS APP", Toast.LENGTH_SHORT ).show();
                SharePreference.setHowTo( this, HOW_TO_USE );
            }
        }
    }

    private void goToFeedbackPage() {
        Intent intent = new Intent( DashboardActivity.this, FeedbackActivity.class );
        startActivity( intent );
    }
}
