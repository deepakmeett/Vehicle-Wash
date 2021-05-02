package com.example.bikewash.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bikewash.R;
import com.example.bikewash.model.DashboardModel;
import com.example.bikewash.utility.SharePreference;

import java.util.List;

import static com.example.bikewash.utility.Constants.GET_BACK;
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    public interface GetBack{
        void Back(int back);
    }
    private final Context context;
    private final List<DashboardModel> list;
    private final GetBack getBack;

    public DashboardAdapter(Context context, List<DashboardModel> list, GetBack GetBack) {
        this.context = context;
        this.list = list;
        this.getBack = GetBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.item_vehicle_washing, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardModel model = list.get( position );
        
        String vehicleType = model.getVehicle_type();
        if (vehicleType != null && !vehicleType.equalsIgnoreCase( "" )){
            if (vehicleType.equalsIgnoreCase( "bike" )){
                Glide.with( context ).load( R.drawable.ic_bike ).into( holder.vehicleImage );
            } else if (vehicleType.equalsIgnoreCase( "car" )){
                Glide.with( context ).load( R.drawable.ic_car ).into( holder.vehicleImage );
            }else if (vehicleType.equalsIgnoreCase( "tempo" )){
                Glide.with( context ).load( R.drawable.ic_tempo ).into( holder.vehicleImage );
            }else if (vehicleType.equalsIgnoreCase( "tractor" )){
                Glide.with( context ).load( R.drawable.ic_tractor ).into( holder.vehicleImage );
            }else if (vehicleType.equalsIgnoreCase( "truck" )){
                Glide.with( context ).load( R.drawable.ic_truck ).into( holder.vehicleImage );
            }else if (vehicleType.equalsIgnoreCase( "auto" )){
                Glide.with( context ).load( R.drawable.ic_auto ).into( holder.vehicleImage );
            }else if (vehicleType.equalsIgnoreCase( "other" )){
                Glide.with( context ).load( R.drawable.ic_other ).into( holder.vehicleImage );
            }
        }
        
        String vehicleModelNum = model.getVehicle_model();
        if (vehicleModelNum != null && !vehicleModelNum.equalsIgnoreCase( "" )) {
            holder.vehicleModel.setText( vehicleModelNum );
        }
        String reachTiming = model.getReach_time();
        if (reachTiming != null && !reachTiming.equalsIgnoreCase( "" )) {
            String reachMin = "Reach time : " + reachTiming + " min";
            holder.reachTime.setText( reachMin );
        }
        String washingNum = model.getRunning_number();
        if (washingNum != null && !washingNum.equalsIgnoreCase( "" )) {
            if (washingNum.length() == 1){
                String washingNumWithZero = "0" + washingNum;
                holder.runningNumber.setText( washingNumWithZero );
            }else{
                holder.runningNumber.setText( washingNum );
            }
        }
        
        String userRunningNum = SharePreference.getRunningNumber( context );
        
        if (position == Integer.parseInt( userRunningNum )-1) {
            holder.vehicleWashingLabel.setVisibility( View.VISIBLE );
            holder.inactiveButtonsView.setVisibility( View.GONE );
        } else {
            holder.vehicleWashingLabel.setVisibility( View.GONE );
            holder.inactiveButtonsView.setVisibility( View.VISIBLE );
        }
        holder.inactiveButtonsView.setOnClickListener( v -> { } );
        holder.doneButton.setOnClickListener( v -> getBack.Back( GET_BACK ) );
    }

    @Override
    public int getItemCount() {
        if (list.size() != 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout vehicleWashingLabel;
        ImageView vehicleImage;
        TextView runningNumber, vehicleModel, reachTime;
        Button doneButton;
        View inactiveButtonsView;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            vehicleWashingLabel = itemView.findViewById( R.id.vehicleWashingLabel );
            vehicleImage = itemView.findViewById( R.id.vehicleImage );
            runningNumber = itemView.findViewById( R.id.runningNumber );
            vehicleModel = itemView.findViewById( R.id.vehicleModel );
            reachTime = itemView.findViewById( R.id.reachTime );
            doneButton = itemView.findViewById( R.id.doneButton );
            inactiveButtonsView = itemView.findViewById( R.id.inactiveButtonsView );
        }
    }
}
