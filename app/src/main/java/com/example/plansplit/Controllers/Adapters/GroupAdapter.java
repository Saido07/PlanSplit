package com.example.plansplit.Controllers.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plansplit.Models.Objects.Groups;
import com.example.plansplit.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Groups> groups = new ArrayList<>();
    Context mContx;
    RecyclerViewClickListener mListener;
    private String group_type_option_home = "ev";
    private String group_type_option_work = "iş";
    private String group_type_option_trip = "seyahat";
    private String group_type_option_other = "diğer";
    int homePicture, workPicture, tripPicture, otherPicture;


    public GroupAdapter(Context mContx, ArrayList<Groups> groups, RecyclerViewClickListener mListener) {
        this.mContx = mContx;
        this.groups = groups;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContx).inflate(R.layout.item_groups_red, parent, false);

        GroupAdapter.MyViewHolder myViewHolder0 = new GroupAdapter.MyViewHolder(view);
        return myViewHolder0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder0 = (MyViewHolder) holder;
        viewHolder0.mTitle.setText(groups.get(position).getGroup_name());

        homePicture = R.drawable.ic_home_black_radius;
        workPicture = R.drawable.ic_suitcase_radius;
        tripPicture = R.drawable.ic_trip_radius;
        otherPicture = R.drawable.ic_other;

        String group_type = groups.get(position).getGroup_type();
        if(group_type.equals(group_type_option_home)){
            viewHolder0.mGroupIcon.setImageResource(homePicture);
        } else if(group_type.equals(group_type_option_work)){
            viewHolder0.mGroupIcon.setImageResource(workPicture);
        } else if(group_type.equals(group_type_option_trip)){
            viewHolder0.mGroupIcon.setImageResource(tripPicture);
        } else if(group_type.equals(group_type_option_other)){
            viewHolder0.mGroupIcon.setImageResource(otherPicture);
        }

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mGroupIcon, mImageView_extra;
        TextView mTitle, mCost;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            this.mGroupIcon = itemView.findViewById(R.id.imageIv);
            this.mImageView_extra = itemView.findViewById(R.id.imageIv_extra);
            this.mTitle = itemView.findViewById(R.id.group_titleTv);
            this.mCost = itemView.findViewById(R.id.costTv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(itemView, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);

    }

}
