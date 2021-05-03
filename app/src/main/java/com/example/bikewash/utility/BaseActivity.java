package com.example.bikewash.utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bikewash.R;
import com.google.android.material.snackbar.Snackbar;
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
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

    public void commonProgressbar(boolean isShown, boolean notShown) {
        if (isShown) {
            progressDialog = new ProgressDialog( this, R.style.progressDialogStyle);
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
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

}
