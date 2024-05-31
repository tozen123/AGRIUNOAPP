package com.doublehammerstudio.agriunoapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doublehammerstudio.agriunoapp.Activity.ViewFarmActivity;
import com.doublehammerstudio.agriunoapp.Objects.Farm;
import com.doublehammerstudio.agriunoapp.R;

import java.util.List;

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.FarmViewHolder> {
    private Context context;
    private List<Farm> farmList;

    public FarmAdapter(List<Farm> farmList, Context context) {
        this.farmList = farmList;
        this.context = context;
    }

    @NonNull
    @Override
    public FarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farm, parent, false);
        return new FarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmViewHolder holder, int position) {
        Farm farm = farmList.get(position);
        holder.farmNameTextView.setText(farm.getName());
        holder.farmDescriptionTextView.setText(farm.getDescription());
        holder.farmOwnerTextView.setText(farm.getOwnerName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewFarmActivity.class);
            intent.putExtra("farmName", farm.getName());
            intent.putExtra("farmDescription", farm.getDescription());
            intent.putExtra("farmAreaOfLand", farm.getAreaOfLand());
            intent.putExtra("farmOwnerName", farm.getOwnerName());
            intent.putExtra("farmRecording", farm.getRecording());
            intent.putExtra("farmMunicipality", farm.getMunicipality());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return farmList.size();
    }

    static class FarmViewHolder extends RecyclerView.ViewHolder {
        TextView farmNameTextView;
        TextView farmDescriptionTextView;
        TextView farmOwnerTextView;

        public FarmViewHolder(@NonNull View itemView) {
            super(itemView);
            farmNameTextView = itemView.findViewById(R.id.farmNameTextView);
            farmDescriptionTextView = itemView.findViewById(R.id.farmDescriptionTextView);
            farmOwnerTextView = itemView.findViewById(R.id.farmOwnerTextView);
        }
    }
}
