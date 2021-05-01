package com.example.bikewash.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikewash.R;
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private final Context context;

    public DashboardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.item_vehicle_washing, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0){
            holder.vehicleWashingLabel.setVisibility( View.VISIBLE );
            holder.inactiveButtonsView.setVisibility( View.GONE );
        }else {
            holder.vehicleWashingLabel.setVisibility( View.GONE );
            holder.inactiveButtonsView.setVisibility( View.VISIBLE );
        }
        
        holder.inactiveButtonsView.setOnClickListener( v -> {
            Toast.makeText( context, "Buttons inactive", Toast.LENGTH_SHORT ).show();
        } );
        
        holder.notYetButton.setOnClickListener( v -> {
            Toast.makeText( context, "Not yet", Toast.LENGTH_SHORT ).show();
        } );

        holder.doneButton.setOnClickListener( v -> {
            Toast.makeText( context, "Done", Toast.LENGTH_SHORT ).show();
        } );
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout vehicleWashingLabel;
        ImageView vehicleImage;
        TextView runningNumber, vehicleModel, reachTime;
        Button notYetButton, doneButton;
        View inactiveButtonsView;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            vehicleWashingLabel = itemView.findViewById( R.id.vehicleWashingLabel );
            vehicleImage = itemView.findViewById( R.id.vehicleImage );
            runningNumber = itemView.findViewById( R.id.runningNumber );
            vehicleModel = itemView.findViewById( R.id.vehicleModel );
            reachTime = itemView.findViewById( R.id.reachTime );
            notYetButton = itemView.findViewById( R.id.notYetButton );
            doneButton = itemView.findViewById( R.id.doneButton );
            inactiveButtonsView = itemView.findViewById( R.id.inactiveButtonsView );
        }
    }
}
