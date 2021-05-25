package com.dexter.flex.bottom_sheet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
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

import static com.dexter.flex.utility.Constants.FEEDBACK;
import static com.dexter.flex.utility.Constants.HOW_TO_USE;
import static com.dexter.flex.utility.Constants.LOGOUT;
import static com.dexter.flex.utility.Constants.REVIEW;
import static com.dexter.flex.utility.Constants.SHARE;
public class MoreItemsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    public interface MoreOptionBottom {

        void bottomSheet(String action);
    }

    private TextView share, review, logout, feedback;
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
        animateTextView();
        return view;
    }

    private void findView(View view) {
        share = view.findViewById( R.id.share );
        review = view.findViewById( R.id.review );
        logout = view.findViewById( R.id.logout );
        feedback = view.findViewById( R.id.feedback );
        howToUse = view.findViewById( R.id.howToUse );
        clickFingerImg = view.findViewById( R.id.clickFingerImg );
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

    private void animateTextView() {
        String howToUse = SharePreference.getHowTo( getContext() );
        if (howToUse.equalsIgnoreCase( HOW_TO_USE )) {
            clickFingerImg.setVisibility( View.GONE );
        }
    }

    @Override
    public void onClick(View v) {
        if (v == share) {
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
