package com.dexter.flex.utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dexter.flex.R;
import com.dexter.flex.activity.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate( savedInstanceState, persistentState );
    }

    public void showSnackBar(String msg, int duration) {
        if (null != msg) {
            Snackbar snackbar = Snackbar.make( findViewById(
                    android.R.id.content )
                    , msg
                    , duration );
            (snackbar.getView()).getLayoutParams().
                    width = ViewGroup.LayoutParams.MATCH_PARENT;
            snackbar.show();
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return (TextUtils.isEmpty( target ) || (!Patterns.EMAIL_ADDRESS.matcher( target ).matches() && !Patterns.PHONE.matcher( target ).matches()));
    }

    public static final Pattern PASSWORD_PATTERN = Pattern.compile( "^" +
                                                                    //"(?=.*[0-9])" +         //at least 1 digit
                                                                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                                                                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                                                                    //"(?=.*[a-zA-Z])" +      //any letter
                                                                    // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                                                                    "(?=\\S+$)" +           //no white spaces
                                                                    ".{6,10}" +               //at least 4 characters
                                                                    "$" );

    public void commonProgressbar(boolean isShown, boolean notShown) {
        if (isShown) {
            progressDialog = new ProgressDialog( this, R.style.progressDialogStyle );
            progressDialog.setMessage( "Please wait..." );
            progressDialog.setCancelable( false );
            try {
                progressDialog.show();
            } catch (Exception e) {
                // WindowManager$BadTokenException will be caught
            }
        }
        if (notShown) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                // WindowManager$BadTokenException will be caught
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE );
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public void share() {
        Intent sharingIntent = new Intent( android.content.Intent.ACTION_SEND );
        sharingIntent.setType( "text/plain" );
        String shareBody = "https://play.google.com/store/apps/details?id=com.dexter.flex";
        sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT, "App link: " );
        sharingIntent.putExtra( android.content.Intent.EXTRA_TEXT, shareBody );
        startActivity( Intent.createChooser( sharingIntent, "Share via" ) );
    }

    public void review(Context context) {
        ReviewManager manager = ReviewManagerFactory.create( this );
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow( (Activity) context, reviewInfo );
                flow.addOnCompleteListener( task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                } );
            }
        } );
    }

    public void logout(Activity activity) {
        FirebaseAuth.getInstance().signOut();
        SharePreference.removeUidKeyRunning( this );
        SharePreference.removeWasherKeyUserExit( this );
        Intent intent = new Intent( activity, LoginActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
        finish();
    }
    
    public void howToUseThisApp(){
        Uri uri = Uri.parse( "https://youtu.be/SqVk7Hi2F7E"); 
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
