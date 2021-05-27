package com.dexter.flex.bottom_sheet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dexter.flex.R;
import com.dexter.flex.utility.SharePreference;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.dexter.flex.utility.Constants.CLOSE;
import static com.dexter.flex.utility.Constants.FEEDBACK;
import static com.dexter.flex.utility.Constants.HOW_TO_USE;
import static com.dexter.flex.utility.Constants.LOGOUT;
import static com.dexter.flex.utility.Constants.OPEN;
import static com.dexter.flex.utility.Constants.PASSWORD;
import static com.dexter.flex.utility.Constants.REVIEW;
import static com.dexter.flex.utility.Constants.BOOKING;
import static com.dexter.flex.utility.Constants.SHARE;
public class MoreItemsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    public interface MoreOptionBottom {

        void bottomSheet(String action);
    }

    private TextView bookingOpenClose, share, review, logout, feedback;
    private LinearLayout howToUse;
    private ImageView clickFingerImg;
    private final MoreOptionBottom moreOptionBottom;

    public MoreItemsBottomSheet(MoreOptionBottom moreOptionBottom) {
        this.moreOptionBottom = moreOptionBottom;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setStyle( BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.more_items_bottom_sheet, container, false );
        findView( view );
        clickFingerShowOrNot();
        serviceOpenClose();
        return view;
    }

    private void findView(View view) {
        bookingOpenClose = view.findViewById( R.id.bookingOpenClose );
        share = view.findViewById( R.id.share );
        review = view.findViewById( R.id.review );
        logout = view.findViewById( R.id.logout );
        feedback = view.findViewById( R.id.feedback );
        howToUse = view.findViewById( R.id.howToUse );
        clickFingerImg = view.findViewById( R.id.clickFingerImg );
        bookingOpenClose.setOnClickListener( this );
        share.setOnClickListener( this );
        review.setOnClickListener( this );
        logout.setOnClickListener( this );
        feedback.setOnClickListener( this );
        howToUse.setOnClickListener( this );
        ObjectAnimator buttonAnimator = ObjectAnimator.ofFloat( howToUse, "translationX", 400f, 0f );
        buttonAnimator.setDuration( 3000 );
        buttonAnimator.setInterpolator( new BounceInterpolator() );
        buttonAnimator.start();

    }

    private void clickFingerShowOrNot() {
        String howToUse = SharePreference.getHowTo( getContext() );
        if (howToUse.equalsIgnoreCase( HOW_TO_USE )) {
            clickFingerImg.setVisibility( View.GONE );
        }
    }

    private void serviceOpenClose() {
        String washerKey = SharePreference.getWasherKey( getContext() );
        if (!washerKey.equalsIgnoreCase( "" )){
            bookingOpenClose.setVisibility( View.VISIBLE );
        }
        
        
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase.child( PASSWORD ).child( BOOKING ).get()
                .addOnCompleteListener( task -> {
                    if (!task.isSuccessful()) {
                        Log.e( "firebase", "Error getting data", task.getException() );
                    } else {
                        Log.d( "firebase", String.valueOf( task.getResult().getValue() ) );
                        String value = (String) task.getResult().getValue();
                        if (value != null) {
                            if (value.equalsIgnoreCase( OPEN )) {
                                bookingOpenClose.setText( "Booking open" );
                                bookingOpenClose.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_baseline_check_circle_24, 0, 0, 0 );
    
                            } else if (value.equalsIgnoreCase( CLOSE )) {
                                bookingOpenClose.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_baseline_cancel_24, 0, 0, 0 );
                                bookingOpenClose.setText( "Booking close" );
                            }
                        }
                    }
                } );
    }

    @Override
    public void onClick(View v) {
        if (v == bookingOpenClose) {
            moreOptionBottom.bottomSheet( BOOKING );
        } else if (v == share) {
            moreOptionBottom.bottomSheet( SHARE );
        } else if (v == review) {
            moreOptionBottom.bottomSheet( REVIEW );
        } else if (v == logout) {
            moreOptionBottom.bottomSheet( LOGOUT );
        } else if (v == feedback) {
            moreOptionBottom.bottomSheet( FEEDBACK );
        } else if (v == howToUse) {
            moreOptionBottom.bottomSheet( HOW_TO_USE );
        }
        dismiss();
    }
}
