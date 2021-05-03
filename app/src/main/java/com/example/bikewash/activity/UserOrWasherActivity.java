package com.example.bikewash.activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bikewash.R;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.SharePreference;

import static com.example.bikewash.utility.Constants.USER_EXIST;
public class UserOrWasherActivity extends BaseActivity implements View.OnClickListener {

    private Button userButton, vehicleWasherButton;
    private EditText washerKeyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_or_washer );
        checkUserExist();
        findView();
    }

    private void checkUserExist() {
        String ifUserExist = SharePreference.getUserData( this );
        if (ifUserExist != null && !ifUserExist.equalsIgnoreCase( "" )){
            goToSelectService();
        }
    }

    private void findView() {
        userButton = findViewById( R.id.userButton );
        washerKeyEditText = findViewById( R.id.washerKeyEditText );
        vehicleWasherButton = findViewById( R.id.vehicleWasherButton );
        userButton.setOnClickListener( this );
        vehicleWasherButton.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == userButton){
            SharePreference.setUserData( this, USER_EXIST );
            goToSelectService();
        } else if (v == vehicleWasherButton){
            String key = washerKeyEditText.getText().toString();
            if (key.equalsIgnoreCase( "" )){
                washerKeyEditText.setError( "Please provide vehicle washer key" );
            }else {
                hideSoftKeyboard( this );
                Toast.makeText( this, "Goto VehicleWasherProfileUpdate Page", Toast.LENGTH_SHORT ).show();
                //Goto VehicleWasherProfileUpdate Page
            }
        }
    }

    private void goToSelectService() {
        Intent intent = new Intent( UserOrWasherActivity.this, SelectServiceActivity.class);
        startActivity( intent );
        finish();
    }
}