package com.example.bikewash.activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.bikewash.R;
import com.example.bikewash.utility.BaseActivity;
import com.example.bikewash.utility.ConnectivityReceiver;
import com.example.bikewash.utility.SessionManager;
import com.example.bikewash.utility.ShowInternetDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import static com.example.bikewash.utility.Constants.AUTHENTICATION_FAILED;
import static com.example.bikewash.utility.Constants.ENTER_VALID_EMAIL_ADDRESS;
import static com.example.bikewash.utility.Constants.ENTER_VALID_PASSWORD;
import static com.example.bikewash.utility.Constants.FROM_SIGN_UP;
import static com.example.bikewash.utility.Constants.NOT_SHOW;
import static com.example.bikewash.utility.Constants.RC_SIGN_IN;
import static com.example.bikewash.utility.Constants.SHOW;
public class LoginActivity extends BaseActivity implements View.OnClickListener, ShowInternetDialog {

    private FirebaseAuth mAuth;
    private TextInputEditText loginEmail, loginPass;
    private TextInputLayout EmailTL, passwordTL;
    private Button logLoginBtn;
    private TextView loginNewSignUp;
    private LinearLayout googleSignInButton;
    private String email, password;
    private GoogleSignInClient mGoogleSignInClient;
    private final com.example.bikewash.utility.ConnectivityReceiver ConnectivityReceiver = new ConnectivityReceiver( this );
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        Initialize();
        findView();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent( LoginActivity.this, UserOrWasherActivity.class );
            startActivityForResult( intent, FROM_SIGN_UP );
            finish();
        }
    }

    private void Initialize() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestIdToken( getString( R.string.default_web_client_id ) )
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient( this, gso );
    }

    private void findView() {
        loginEmail = findViewById( R.id.loginEmail );
        loginPass = findViewById( R.id.loginPass );
        loginNewSignUp = findViewById( R.id.loginNewSignUp );
        EmailTL = findViewById( R.id.EmailTL );
        passwordTL = findViewById( R.id.passwordTL );
        googleSignInButton = findViewById( R.id.googleSignInButton );
        loginNewSignUp = findViewById( R.id.loginNewSignUp );
        logLoginBtn = findViewById( R.id.logLoginBtn );
        loginNewSignUp.setOnClickListener( this );
        logLoginBtn.setOnClickListener( this );
        googleSignInButton.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == logLoginBtn) {
            email = Objects.requireNonNull( loginEmail.getText() ).toString();
            password = Objects.requireNonNull( loginPass.getText() ).toString();
            if (isValidEmail( email )) {
                EmailTL.setError( ENTER_VALID_EMAIL_ADDRESS );
                EmailTL.requestFocus();
            } else if (password.isEmpty() && password.equalsIgnoreCase( "" )) {
                passwordTL.setError( ENTER_VALID_PASSWORD );
                passwordTL.requestFocus();
            } else {
                hideSoftKeyboard( this );
                loginUser();
            }
        } else if (v == loginNewSignUp) {
            Intent intent = new Intent( LoginActivity.this, SignUpActivity.class );
            startActivityForResult( intent, FROM_SIGN_UP );
        } else if (v == googleSignInButton) {
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult( signInIntent, RC_SIGN_IN );
    }

    private void loginUser() {
        commonProgressbar( true, false );
        mAuth.signInWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, task -> {
                    commonProgressbar( false, true );
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d( TAG, "signInWithEmail:success" );
//                            FirebaseUser user = mAuth.getCurrentUser();
                        goToDashboard();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w( TAG, "signInWithEmail:failure", task.getException() );
                        showSnackBar( AUTHENTICATION_FAILED, Snackbar.LENGTH_SHORT );
                    }
                } );
    }

    private void goToDashboard() {
        Intent intent = new Intent( LoginActivity.this, UserOrWasherActivity.class );
        startActivity( intent );
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        try {
            super.onActivityResult( requestCode, resultCode, data );
            if (requestCode == resultCode) {
                showSnackBar( "SignUp Successful", Snackbar.LENGTH_SHORT );
            } else  // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                if (requestCode == RC_SIGN_IN) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent( data );
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult( ApiException.class );
//                        Log.d( TAG, "firebaseAuthWithGoogle:" + account.getId() );
                        assert account != null;
                        firebaseAuthWithGoogle( account.getIdToken() );
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w( TAG, "Google sign in failed", e );
                    }
                }
        } catch (Exception e) {
            Log.e( TAG, "onActivityResult: " + e );
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        commonProgressbar( true, false );
        AuthCredential credential = GoogleAuthProvider.getCredential( idToken, null );
        mAuth.signInWithCredential( credential )
                .addOnCompleteListener( this, task -> {
                    commonProgressbar( false, true );
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d( TAG, "signInWithCredential:success" );
//                        FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        goToDashboard();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w( TAG, "signInWithCredential:failure", task.getException() );
//                            updateUI(null);
                    }
                } );
    }

    @Override
    public void showInternetLostDialog(String showOrNot) {
        if (showOrNot != null && !showOrNot.equalsIgnoreCase( "" )) {
            if (showOrNot.equalsIgnoreCase( SHOW )) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace( R.id.loginFrameLayout, SessionManager.internetLostFragment );
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

    }}