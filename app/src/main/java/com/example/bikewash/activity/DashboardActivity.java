package com.example.bikewash.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.bikewash.R;
import com.example.bikewash.adapter.DashboardAdapter;
public class DashboardActivity extends AppCompatActivity {

    RecyclerView dashboardRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard );
        findView();
    }

    private void findView() {
        dashboardRecycler = findViewById( R.id.dashboardRecycler );
        dashboardRecycler.setHasFixedSize( true );
        dashboardRecycler.setAdapter( new DashboardAdapter( this ) );
    }
}