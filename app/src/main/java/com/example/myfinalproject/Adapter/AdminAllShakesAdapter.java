package com.example.myfinalproject.Adapter;

/**
 * מחלקה זו (Adapter) אחראית על ניהול והצגת רשימת כל השייקים הקיימים במערכת עבור מנהל המערכת.
 * היא מחברת בין אובייקטי ה-Shake לבין תצוגת הכרטיסייה (ViewHolder) במסך הניהול.
 */

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

    /**
     * ממשק להאזנה ללחיצות על שייק ספציפי בטבלת הניהול.
     */
    public interface OnItemClickListener {
        void onItemClick(Shake shake);
    }

    /**
     * בנאי המתאם המקבל את רשימת השייקים והמאזין.
     */
    public AdminAllShakesAdapter(ArrayList<Shake> shakes, OnItemClickListener listener) {
        this.shakes = shakes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // טעינת ה-Layout המעוצב לכל שורה ברשימת המנהל
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_admin_shake, parent, false);
        return new ShakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShakeViewHolder holder, int position) {
        Shake shake = shakes.get(position);

        // שם המשתמש שיצר את השייק - טיפול בערך null
        String userName = shake.getUserName() != null ? shake.getUserName() : "משתמש לא ידוע";

        // הצגת כותרת הפריט
        holder.tvShakeName.setText("השייק של " + userName);

        // קיצור המזהה הייחודי לצורך הצגה אסתטית בטבלה
        String shakeId = shake.getShakeId() != null ? shake.getShakeId() : "לא ידוע";
        if (shakeId.length() > 6) {
            shakeId = shakeId.substring(0, 6) + "...";
        }
        holder.tvShakeId.setText("מזהה: " + shakeId);

        // הגדרת טקסט המציג את שם יוצר השייק בפרטי הפריט
        holder.tvShakeCreator.setText(userName);

        // טיפול בלחיצה על הכרטיסייה - מעבר לפרטי השייק המלאים
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

    /**
     * מחלקה פנימית לניהול תצוגת כל שורה ברשימת השייקים של המנהל.
     */
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