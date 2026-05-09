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

    private final ArrayList<Shake> shakes;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Shake shake);
    }

    public AdminAllShakesAdapter(ArrayList<Shake> shakes, OnItemClickListener listener) {
        this.shakes = shakes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_admin_shake, parent, false);
        return new ShakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShakeViewHolder holder, int position) {
        Shake shake = shakes.get(position);

        // שם המשתמש שיצר את השייק
        String userName = shake.getUserName() != null ? shake.getUserName() : "משתמש לא ידוע";

        // נציג "השייק של X" בתור הכותרת
        holder.tvShakeName.setText("השייק של " + userName);

        // הגדרת המזהה (ID) במערכת לפי הפונקציה getShakeId() שיש במודל שלך
        String shakeId = shake.getShakeId() != null ? shake.getShakeId() : "לא ידוע";
        if (shakeId.length() > 6) {
            shakeId = shakeId.substring(0, 6) + "..."; // מציג מזהה מקוצר שייראה טוב
        }
        holder.tvShakeId.setText("מזהה: " + shakeId);

        // הגדרת יוצר השייק (טקסט קטן יותר למטה)
        holder.tvShakeCreator.setText(userName);

        // טיפול בלחיצה על הכרטיסייה - מוביל לדף המרכיבים!
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(shake);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shakes.size();
    }

    static class ShakeViewHolder extends RecyclerView.ViewHolder {
        TextView tvShakeName, tvShakeId, tvShakeCreator;

        public ShakeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShakeName = itemView.findViewById(R.id.tvShakeName);
            tvShakeId = itemView.findViewById(R.id.tvShakeId);
            tvShakeCreator = itemView.findViewById(R.id.tvShakeCreator);
        }
    }
}