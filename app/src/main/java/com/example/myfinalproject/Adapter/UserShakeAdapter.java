package com.example.myfinalproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.R;
import com.example.myfinalproject.model.Shake;

import java.util.ArrayList;

public class UserShakeAdapter extends RecyclerView.Adapter<UserShakeAdapter.ShakeViewHolder> {

    public interface OnShakeClickListener {
        void onShakeClick(Shake shake);
    }

    private final ArrayList<Shake> shakes;
    private final OnShakeClickListener listener;

    public UserShakeAdapter(ArrayList<Shake> shakes, OnShakeClickListener listener) {
        this.shakes = shakes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_item_user_shake, parent, false);
        return new ShakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShakeViewHolder holder, int position) {
        Shake shake = shakes.get(position);

        holder.tvShakeName.setText("שייק " + (position + 1));

        int itemsCount = 0;
        if (shake.getItems() != null) {
            itemsCount = shake.getItems().size();
        }

        holder.tvShakeInfo.setText("מספר רכיבים: " + itemsCount);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShakeClick(shake);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shakes.size();
    }

    static class ShakeViewHolder extends RecyclerView.ViewHolder {
        TextView tvShakeName;
        TextView tvShakeInfo;

        public ShakeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShakeName = itemView.findViewById(R.id.tvShakeName);
            tvShakeInfo = itemView.findViewById(R.id.tvShakeInfo);
        }
    }
}