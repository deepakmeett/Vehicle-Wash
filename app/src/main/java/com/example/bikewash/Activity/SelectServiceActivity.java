package com.example.bikewash.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikewash.Adapter.SelectServiceAdapter;
import com.example.bikewash.R;
public class SelectServiceActivity extends AppCompatActivity {

    private RecyclerView uploadImagesOfVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_select_service );
        findView();
    }

    private void findView() {
        
    }
}