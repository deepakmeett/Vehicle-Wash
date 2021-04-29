package com.example.bikewash.utility;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
public class BaseActivity extends AppCompatActivity {

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
}
