package com.example.bikewash.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextInputEditText loginEmail, loginPass;
    private TextInputLayout EmailTL, passwordTL;
    private Button logLoginBtn;
    private TextView loginNewSignUp;
    private String email, password;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        Initialize();
        findView();
    }

    private void Initialize() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivityForResult( intent, FROM_SIGN_UP );
            finish();
        }
    }
    
    private void findView() {
        loginEmail = findViewById( R.id.loginEmail );
        loginPass = findViewById( R.id.loginPass );
        loginNewSignUp = findViewById( R.id.loginNewSignUp );
        EmailTL = findViewById( R.id.EmailTL );
        passwordTL = findViewById( R.id.passwordTL );
        loginNewSignUp = findViewById( R.id.loginNewSignUp );
        logLoginBtn = findViewById( R.id.logLoginBtn );
        loginNewSignUp.setOnClickListener( this );
        logLoginBtn.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if (v == logLoginBtn) {
            email = Objects.requireNonNull( loginEmail.getText() ).toString();
            password = Objects.requireNonNull( loginPass.getText() ).toString();
            if (isValidEmail( email )) {
                EmailTL.setError( "Enter valid email address" );
                EmailTL.requestFocus();
            } else if (password.isEmpty() && password.equalsIgnoreCase( "" )) {
                passwordTL.setError( "Enter valid password" );
                passwordTL.requestFocus();
            } else {
                loginUser();
            }
        } else if (v == loginNewSignUp){
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivityForResult( intent, FROM_SIGN_UP );
        }
    }

    private void loginUser() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToDashboard();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            showSnackBar( "Authentication failed", Snackbar.LENGTH_SHORT );
                        }
                    }
                });
    }

    private void goToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity( intent );
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == resultCode) {
                showSnackBar( "SignUp Successful" , Snackbar.LENGTH_SHORT);
            }
        } catch (Exception e) {
            Log.e( TAG, "onActivityResult: " + e  );
        }
    }
}