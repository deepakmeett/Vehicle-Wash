package com.example.bikewash.activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.bikewash.R;
import com.example.bikewash.utility.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.example.bikewash.utility.Constants.FEEDBACK;
import static com.example.bikewash.utility.Constants.FEEDBACK_RESULT;
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private EditText queryET;
    private Button submitButton;
    private static final String TAG = "FeedbackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feedback );
        findView();
    }

    private void findView() {
        queryET = findViewById( R.id.queryET );
        submitButton = findViewById( R.id.submitButton );
        submitButton.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == submitButton) {
            String query = queryET.getText().toString();
            if (!query.equalsIgnoreCase( "" )) {
                commonProgressbar( true, false );
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mDatabase = database.getReference().child( FEEDBACK );
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                mDatabase.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Date date = Calendar.getInstance().getTime();
                        String currentTime = date.toString();
                        String email = Objects.requireNonNull( firebaseAuth.getCurrentUser() ).getEmail();
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put( "Email", email );
                        hashMap.put( "Time", currentTime );
                        hashMap.put( "Query", query );
                        mDatabase.push().setValue( hashMap ).addOnCompleteListener( task -> {
                        } );
                        commonProgressbar( false, true );
                        hideSoftKeyboard( FeedbackActivity.this );
                        setResult( FEEDBACK_RESULT );
                        onBackPressed();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        commonProgressbar( false, true );
                        Log.e( TAG, "onCancelled: " + error );
                    }
                } );
            } else {
                queryET.setError( "Please type feedback or suggestion" );
            }
        }
    }
}