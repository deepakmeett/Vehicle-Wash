package com.example.bikewash.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bikewash.R;
import com.example.bikewash.Utility.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import static com.example.bikewash.Utility.Constants.FROM_SIGN_UP;
public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextInputLayout registerEmailTL, registerPasswordTL;
    private TextInputEditText registerEmail, registerPass;
    private Button registerBtn;
    private TextView backToLogin;
    private String email, password;
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
            email = Objects.requireNonNull( registerEmail.getText() ).toString();
            password = Objects.requireNonNull( registerPass.getText() ).toString();
            if (isValidEmail( email )) {
                registerEmailTL.setError( "Enter valid email address" );
                registerEmailTL.requestFocus();
            } else if (password.isEmpty() && password.equalsIgnoreCase( "" )) {
                registerPasswordTL.setError( "Enter valid password" );
                registerPasswordTL.requestFocus();
            } else {
                registerUser();
            }
        } else if (v == backToLogin) {
            onBackPressed();
        }
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d( TAG, "createUserWithEmail:success" );
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d( TAG, "onComplete: " + user);
                            setResult(FROM_SIGN_UP);
                            onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w( TAG, "createUserWithEmail:failure", task.getException() );
                            showSnackBar( "Authentication failed", Snackbar.LENGTH_SHORT );
                        }
                    }
                } );
    }
}