package com.example.bikewash.activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.example.bikewash.R;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.ConnectivityReceiver;
import com.example.bikewash.utility.SessionManager;
import com.example.bikewash.utility.ShowInternetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import static com.example.bikewash.utility.Constants.AUTHENTICATION_FAILED;
import static com.example.bikewash.utility.Constants.ENTER_VALID_EMAIL_ADDRESS;
import static com.example.bikewash.utility.Constants.ENTER_VALID_PASSWORD;
import static com.example.bikewash.utility.Constants.FROM_SIGN_UP;
import static com.example.bikewash.utility.Constants.NOT_SHOW;
import static com.example.bikewash.utility.Constants.SHOW;
public class SignUpActivity extends BaseActivity implements View.OnClickListener, ShowInternetDialog {

    private FirebaseAuth mAuth;
    private TextInputLayout registerEmailTL, registerPasswordTL;
    private TextInputEditText registerEmail, registerPass;
    private Button registerBtn;
    private TextView backToLogin;
    private String email, password;
    private final com.example.bikewash.utility.ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );
        Initialize();
        findView();
    }

    private void Initialize() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void findView() {
        registerEmailTL = findViewById( R.id.registerEmailTL );
        registerEmail = findViewById( R.id.registerEmail );
        registerPasswordTL = findViewById( R.id.registerPasswordTL );
        registerPass = findViewById( R.id.registerPass );
        registerBtn = findViewById( R.id.registerBtn );
        backToLogin = findViewById( R.id.backToLogin );
        registerBtn.setOnClickListener( this );
        backToLogin.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            email = Objects.requireNonNull( registerEmail.getText() ).toString().trim();
            password = Objects.requireNonNull( registerPass.getText() ).toString().trim();
            if (isValidEmail( email )) {
                registerEmailTL.setError( ENTER_VALID_EMAIL_ADDRESS );
                registerEmailTL.requestFocus();
            } else if (password.isEmpty() && password.equalsIgnoreCase( "" )) {
                registerPasswordTL.setError( ENTER_VALID_PASSWORD );
                registerPasswordTL.requestFocus();
            } else if (!PASSWORD_PATTERN.matcher( password).matches()) {
                showSnackBar( "Password length should be minimum 6 and maximum 10", Snackbar.LENGTH_SHORT );
            } else {
                hideSoftKeyboard( this );
                registerUser();
            }
        } else if (v == backToLogin) {
            onBackPressed();
            hideSoftKeyboard( this );
        }
    }

    private void registerUser() {
        commonProgressbar( true, false );
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, task -> {
                    commonProgressbar( false, true );
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d( TAG, "createUserWithEmail:success" );
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d( TAG, "onComplete: " + user );
                        setResult( FROM_SIGN_UP );
                        onBackPressed();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w( TAG, "createUserWithEmail:failure", task.getException() );
                        showSnackBar( AUTHENTICATION_FAILED, Snackbar.LENGTH_SHORT );
                    }
                } );
    }

    @Override
    public void showInternetLostFragment(String showOrNot) {
        if (showOrNot != null && !showOrNot.equalsIgnoreCase( "" )) {
            if (showOrNot.equalsIgnoreCase( SHOW )) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.signUpFrameLayout, SessionManager.internetLostFragment );
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

