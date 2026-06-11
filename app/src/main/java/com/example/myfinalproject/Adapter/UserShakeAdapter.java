package com.example.myfinalproject.Adapter;

/**
 * מחלקה זו (Adapter) אחראית על הצגת רשימת השייקים שנוצרו על ידי המשתמש בתוך RecyclerView.
 * היא מבצעת את ההתאמה (Binding) בין אובייקט ה-Shake לבין תצוגת הפריט הבודד במסך.
 */

/**
 * סיכום תהליך עבודה עם אובייקט Shake:
 * * * מבנה הנתונים: השייק מוגדר כאובייקט (Shake) המכיל "מזוודה" (רשימת מרכיבים - ArrayList<Item>).
 * * שליפה מהשרת: האפליקציה שולפת מהדאטה-בייס את רשימת השייקים ומציגה אותם ב-RecyclerView באמצעות Adapter.
 * * פעולת הלחיצה: בלחיצה על שייק, ה-Adapter מזהה את האובייקט ושומר אותו ב"תיבת אחסון" גלובלית (ShakeSelectionManager).
 * * מעבר מסך: האפליקציה עוברת למסך הפרטים (ShakeDetails) באמצעות Intent.
 * * שליפת הנתונים: מסך הפרטים פונה ל-ShakeSelectionManager, שולף את אובייקט השייק ומשתמש ב-Getters כדי לגשת למידע.
 * * חישוב בזמן אמת: המסך עובר בלולאה על רשימת המרכיבים ששלף, מחשב את הערכים התזונתיים (קלוריות/חלבונים) מחדש, ומציג אותם למשתמש.
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

public class UserShakeAdapter extends RecyclerView.Adapter<UserShakeAdapter.ShakeViewHolder> {

    // ממשק להאזנה ללחיצות על פריטים ברשימה
    public interface OnShakeClickListener {
        void onShakeClick(Shake shake);
    }

    private final ArrayList<Shake> shakes;
    private final OnShakeClickListener listener;

    /**
     * בנאי המתאם (Adapter) המקבל את רשימת השייקים ומאזין ללחיצות.
     */
    public UserShakeAdapter(ArrayList<Shake> shakes, OnShakeClickListener listener) {
        this.shakes = shakes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירת התצוגה עבור כל פריט ברשימה (Inflating)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_item_user_shake, parent, false);
        return new ShakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShakeViewHolder holder, int position) {
        // מילוי הנתונים בכל פריט ברשימה
        Shake shake = shakes.get(position);

        holder.tvShakeName.setText("שייק " + (position + 1));

        // חישוב מספר המרכיבים בשייק לצורך הצגה
        int itemsCount = 0;
        if (shake.getItems() != null) {
            itemsCount = shake.getItems().size();
        }

        holder.tvShakeInfo.setText("מספר רכיבים: " + itemsCount);

        // טיפול בלחיצה על הפריט
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

    /**
     * מחלקה פנימית המחזיקה את רכיבי התצוגה של פריט בודד ברשימה.
     */
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