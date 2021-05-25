package com.dexter.flex.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dexter.flex.R;
import com.dexter.flex.model.DashboardModel;
import com.dexter.flex.model.UserKeyModel;
import com.dexter.flex.utility.BaseActivity;
import com.dexter.flex.utility.SessionManager;
import com.dexter.flex.utility.SharePreference;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.dexter.flex.utility.Constants.ABSENT;
import static com.dexter.flex.utility.Constants.ALL;
import static com.dexter.flex.utility.Constants.COMPLETED;
import static com.dexter.flex.utility.Constants.GET_BACK;
import static com.dexter.flex.utility.Constants.NOT_ALLOWED;
import static com.dexter.flex.utility.Constants.PENDING;
import static com.dexter.flex.utility.Constants.PLEASE_WAIT;
import static com.dexter.flex.utility.Constants.REFRESH_LAYOUT;
import static com.dexter.flex.utility.Constants.WASHING;
import static com.dexter.flex.utility.Constants.WASHING_STATUS;
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    public interface GetBack {

        void BackFromAdapter(int back);
    }

    private final Context context;
    private final List<DashboardModel> list;
    private final List<UserKeyModel> userKeyModelList;
    private final GetBack getBack;
    private final BaseActivity baseActivity;
    private DatabaseReference mDatabase;

    public DashboardAdapter(Context context, List<DashboardModel> list, GetBack GetBack, List<UserKeyModel> userKeyModels, BaseActivity baseActivity) {
        this.context = context;
        this.list = list;
        this.getBack = GetBack;
        this.userKeyModelList = userKeyModels;
        this.baseActivity = baseActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.item_vehicle_washing, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        mDatabase = database1.getReference();
        DashboardModel model = list.get( position );
        UserKeyModel keyList = userKeyModelList.get( position );
        String vehicleType = model.getVehicle_type();
        String vehicleModelNum = model.getVehicle_model();
        String reachTiming = model.getReach_time();
        String washingNum = model.getRunning_number();
        String washing_status = model.getWashing_status();
        String washerKey = SharePreference.getWasherKey( context );
        String phoneUID = SharePreference.getUID( context );
        String userUId = model.getUid();
        final boolean phoneAndUserIdNotNull = phoneUID != null && !phoneUID.equalsIgnoreCase( "" )
                                              && userUId != null && !userUId.equalsIgnoreCase( "" );
        if (vehicleType != null && !vehicleType.equalsIgnoreCase( "" )) {
            if (vehicleType.equalsIgnoreCase( "bike" )) {
                Glide.with( context ).load( R.drawable.ic_bike ).into( holder.vehicleImage );
            } else if (vehicleType.equalsIgnoreCase( "car" )) {
                Glide.with( context ).load( R.drawable.ic_car ).into( holder.vehicleImage );
            } else if (vehicleType.equalsIgnoreCase( "tempo" )) {
                Glide.with( context ).load( R.drawable.ic_tempo ).into( holder.vehicleImage );
            } else if (vehicleType.equalsIgnoreCase( "tractor" )) {
                Glide.with( context ).load( R.drawable.ic_tractor ).into( holder.vehicleImage );
            } else if (vehicleType.equalsIgnoreCase( "truck" )) {
                Glide.with( context ).load( R.drawable.ic_truck ).into( holder.vehicleImage );
            } else if (vehicleType.equalsIgnoreCase( "auto" )) {
                Glide.with( context ).load( R.drawable.ic_auto ).into( holder.vehicleImage );
            } else if (vehicleType.equalsIgnoreCase( "other" )) {
                Glide.with( context ).load( R.drawable.ic_other ).into( holder.vehicleImage );
            }
        }
        if (vehicleModelNum != null && !vehicleModelNum.equalsIgnoreCase( "" )) {
            if (phoneAndUserIdNotNull) {
                if (phoneUID.equalsIgnoreCase( userUId )) {
                    holder.vehicleModel.setText( vehicleModelNum );
                } else {
                    holder.vehicleModel.setText( R.string.xxx_xxx_xxx );
                }
            }
            if (washerKey != null && !washerKey.equalsIgnoreCase( "" )) {
                holder.vehicleModel.setText( vehicleModelNum );
            }
        } else {
            holder.vehicleModel.setText( R.string.vehicle_model_no_ );
        }
        if (reachTiming != null && !reachTiming.equalsIgnoreCase( "" )) {
            String reachMin = "Reach time : " + reachTiming + " min";
            holder.reachTime.setText( reachMin );
        } else {
            holder.reachTime.setText( R.string.reach_time_00_min );
        }
        if (washingNum != null && !washingNum.equalsIgnoreCase( "" )) {
            if (washingNum.length() == 1) {
                String washingNumWithZero = "0" + washingNum;
                holder.runningNumber.setText( washingNumWithZero );
            } else {
                holder.runningNumber.setText( washingNum );
            }
        } else {
            holder.runningNumber.setText( R.string._00 );
        }
        String isWashing = model.getWashing_status();
        if (isWashing != null && isWashing.equalsIgnoreCase( WASHING )) {
            holder.vehicleWashingLabel.setVisibility( View.VISIBLE );
            holder.labelBg.setBackgroundColor( context.getResources().getColor( R.color.purple_200 ) );
            holder.labelText.setText( R.string.vehicle_washing );
        }
        if (washing_status != null && !washing_status.equalsIgnoreCase( "" )) {
            if (washing_status.equalsIgnoreCase( PENDING )) {
                holder.doneButton.setText( washing_status );
                holder.doneButton.setBackground( ResourcesCompat.getDrawable( context.getResources(), R.drawable.round_yellow_btn, context.getTheme() ) );
            } else if (washing_status.equalsIgnoreCase( WASHING )) {
                holder.doneButton.setText( washing_status );
                holder.doneButton.setBackground( ResourcesCompat.getDrawable( context.getResources(), R.drawable.round_blue_btn, context.getTheme() ) );
            } else if (washing_status.equalsIgnoreCase( ABSENT )) {
                holder.doneButton.setText( washing_status );
                holder.doneButton.setBackground( ResourcesCompat.getDrawable( context.getResources(), R.drawable.round_red_btn, context.getTheme() ) );
            } else if (washing_status.equalsIgnoreCase( COMPLETED )) {
                holder.doneButton.setText( washing_status );
                holder.doneButton.setBackground( ResourcesCompat.getDrawable( context.getResources(), R.drawable.round_green_btn, context.getTheme() ) );
            }
        }
        if (washerKey != null && !washerKey.equalsIgnoreCase( "" )) {
            if (washing_status != null && washing_status.equalsIgnoreCase( PENDING )) {
                holder.cutImageView.setVisibility( View.VISIBLE );
            }
        }
        if (phoneAndUserIdNotNull) {
            if (phoneUID.equalsIgnoreCase( userUId )) {
                holder.vehicleWashingLabel.setVisibility( View.VISIBLE );
                holder.labelBg.setBackgroundColor( context.getResources().getColor( R.color.rich_carmine ) );
                holder.labelText.setText( R.string.your_vehicle );
                holder.inactiveButtonsView.setVisibility( View.GONE );
            }
        } else if (washerKey != null && !washerKey.equalsIgnoreCase( "" )) {
            holder.inactiveButtonsView.setVisibility( View.GONE );
        } else {
            holder.inactiveButtonsView.setVisibility( View.VISIBLE );
        }
        holder.cutImageView.setOnClickListener( v -> {
            if (washing_status != null && washing_status.equalsIgnoreCase( PENDING )) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mDatabase = database.getReference();
                mDatabase.child( ALL ).child( keyList.getKey() ).child( WASHING_STATUS ).setValue( ABSENT );
                getBack.BackFromAdapter( REFRESH_LAYOUT );
            }
        } );
        holder.doneButton.setOnClickListener( v -> {
            if (washerKey != null && !washerKey.equalsIgnoreCase( "" )) {
                //Vehicle washer click will happen here
                if (washing_status != null) {
                    if (washing_status.equalsIgnoreCase( PENDING )) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference mDatabase = database.getReference();
                        mDatabase.child( ALL ).child( keyList.getKey() ).child( WASHING_STATUS ).setValue( WASHING );
                        getBack.BackFromAdapter( REFRESH_LAYOUT );
                    } else if (washing_status.equalsIgnoreCase( WASHING )) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference mDatabase = database.getReference();
                        mDatabase.child( ALL ).child( keyList.getKey() ).child( WASHING_STATUS ).setValue( COMPLETED );
                        getBack.BackFromAdapter( REFRESH_LAYOUT );
                    } else if (washing_status.equalsIgnoreCase( COMPLETED )) {
                        baseActivity.showSnackBar( "This vehicle is washed", Snackbar.LENGTH_SHORT );
                    }
                }
            } else {
                //User click will happen here
                getBack.BackFromAdapter( NOT_ALLOWED );
                if (washing_status != null) {
                    if (phoneAndUserIdNotNull) {
                        if (phoneUID.equalsIgnoreCase( userUId )) {
                            if (washing_status.equalsIgnoreCase( PENDING )) {
                                getBack.BackFromAdapter( PLEASE_WAIT );
                            } else if (washing_status.equalsIgnoreCase( WASHING )) {
                                mDatabase.child( ALL ).child( SessionManager.userKey ).child( WASHING_STATUS ).setValue( COMPLETED );
                            } else if (washing_status.equalsIgnoreCase( COMPLETED )) {
                                if (list.size() > position + 1) {
                                    UserKeyModel nextKeyList = userKeyModelList.get( position + 1 );
                                    mDatabase.child( ALL ).child( nextKeyList.getKey() ).child( WASHING_STATUS ).setValue( WASHING );
                                }
                                getBack.BackFromAdapter( GET_BACK );
                            }
                        }
                    }
                }
            }

        } );
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
        ImageView cutImageView, vehicleImage, labelBg;
        TextView labelText, runningNumber, vehicleModel, reachTime;
        Button doneButton;
        View inactiveButtonsView;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            vehicleWashingLabel = itemView.findViewById( R.id.vehicleWashingLabel );
            labelBg = itemView.findViewById( R.id.labelBg );
            labelText = itemView.findViewById( R.id.labelText );
            cutImageView = itemView.findViewById( R.id.cutImageView );
            vehicleImage = itemView.findViewById( R.id.vehicleImage );
            runningNumber = itemView.findViewById( R.id.runningNumber );
            vehicleModel = itemView.findViewById( R.id.vehicleModel );
            reachTime = itemView.findViewById( R.id.vehicleType );
            doneButton = itemView.findViewById( R.id.doneButton );
            inactiveButtonsView = itemView.findViewById( R.id.inactiveButtonsView );
        }
    }
}