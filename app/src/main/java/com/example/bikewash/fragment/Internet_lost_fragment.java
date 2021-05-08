package com.example.bikewash.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.bikewash.R;
public class Internet_lost_fragment extends Fragment implements View.OnClickListener {

    CardView cardView;
    FrameLayout frameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_internet_lost_fragment, container, false );
        findView( view );
        return view;
    }

    private void findView(View view) {
        frameLayout = view.findViewById( R.id.productFrameLayout );
        cardView = view.findViewById( R.id.fragmentCardView );
        frameLayout.setOnClickListener( this );
        cardView.setOnClickListener( this );
    }

    @Override
    public void onClick(View view) {
        if (view == cardView) {
            //do nothing
        } else if (view == frameLayout) {
            //do nothing
        }
    }
}