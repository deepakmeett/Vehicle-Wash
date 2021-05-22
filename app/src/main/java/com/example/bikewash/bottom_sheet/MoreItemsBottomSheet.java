package com.example.bikewash.bottom_sheet;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bikewash.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.example.bikewash.utility.Constants.FEEDBACK;
import static com.example.bikewash.utility.Constants.LOGOUT;
import static com.example.bikewash.utility.Constants.REVIEW;
import static com.example.bikewash.utility.Constants.SHARE;
public class MoreItemsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    public interface MoreOptionBottom {

        void bottomSheet(String action);
    }

    TextView share, review, logout, feedback;
    MoreOptionBottom moreOptionBottom;

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
        return view;
    }

    private void findView(View view) {
        share = view.findViewById( R.id.share );
        review = view.findViewById( R.id.review );
        logout = view.findViewById( R.id.logout );
        feedback = view.findViewById( R.id.feedback );
        share.setOnClickListener( this );
        review.setOnClickListener( this );
        logout.setOnClickListener( this );
        feedback.setOnClickListener( this );
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
        }
        dismiss();
    }
}
