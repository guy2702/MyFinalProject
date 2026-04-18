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

public class AdminAllShakesAdapter extends RecyclerView.Adapter<AdminAllShakesAdapter.ShakeViewHolder> {

    public interface OnShakeClickListener {
        void onShakeClick(Shake shake);
    }

    private ArrayList<Shake> shakeList;
    private OnShakeClickListener listener;

    public AdminAllShakesAdapter(ArrayList<Shake> shakeList, OnShakeClickListener listener) {
        this.shakeList = shakeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_admin_shake, parent, false);
        return new ShakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShakeViewHolder holder, int position) {
        Shake shake = shakeList.get(position);

        holder.tvShakeName.setText("שייק מספר " + (position + 1));

        String userName = (shake.getUserName() != null && !shake.getUserName().isEmpty())
                ? shake.getUserName()
                : "משתמש לא מזוהה";

        holder.tvCreatedBy.setText("נוצר על ידי: " + userName);

        int itemsCount = shake.getItems() != null ? shake.getItems().size() : 0;
        holder.tvItemsCount.setText("כמות רכיבים: " + itemsCount);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShakeClick(shake);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shakeList.size();
    }

    static class ShakeViewHolder extends RecyclerView.ViewHolder {
        TextView tvShakeName, tvCreatedBy, tvItemsCount;

        public ShakeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShakeName = itemView.findViewById(R.id.tvShakeName);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            tvItemsCount = itemView.findViewById(R.id.tvItemsCount);
        }
    }
}