package com.example.myfinalproject.Adapter;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.R;
import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;

import java.util.ArrayList;

/**
 * מחלקה זו (Adapter) אחראית על ניהול והצגת רשימת פריטים (Items) ב-RecyclerView.
 * המחלקה תומכת במצב "בחירה" (Selection Mode), שבו המשתמש יכול לבחור פריטים ולהזין להם כמות.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    /** רשימת הפריטים המוצגת ברשימה */
    private final ArrayList<Item> items;
    /** ממשק למעקב אחר לחיצות משתמש על פריטים */
    private final OnItemClickListener listener;
    /** משתנה המציין האם האפליקציה במצב בחירת רכיבים או בתצוגה רגילה */
    private boolean isSelectionMode = false;

    /**
     * ממשק להגדרת פעולות לחיצה על פריט.
     */
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    /**
     * בנאי המחלקה לאתחול רשימת הפריטים והמאזין.
     */
    public ItemAdapter(ArrayList<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * מעדכן את מצב הבחירה של הפריטים ומנחה את ה-Adapter לרענן את התצוגה.
     */
    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }

    /**
     * מחזיר את רשימת הפריטים הנוכחית.
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.tvName.setText(item.getName() != null ? item.getName() : "-");
        holder.tvDescription.setText(item.getType() != null ? item.getType() : "-");

        // המרת תמונה מ-Base64 ל-Bitmap והצגתה
        try {
            if (item.getPic() != null && !item.getPic().isEmpty()) {
                holder.ivImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));
            } else {
                holder.ivImage.setImageResource(android.R.color.darker_gray);
            }
        } catch (Exception e) {
            holder.ivImage.setImageResource(android.R.color.darker_gray);
        }

        // ניקוי מאזין קודם כדי למנוע טעויות בעת טעינת התצוגה מחדש (Recycling)
        if (holder.amountWatcher != null) {
            holder.etAmount.removeTextChangedListener(holder.amountWatcher);
        }

        holder.etAmount.setText(item.getAmount() > 0 ? String.valueOf(item.getAmount()) : "");

        // הגדרת נראות שדה הכמות בהתאם למצב הבחירה
        if (isSelectionMode && item.isSelected()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#C8E6C9")); // צבע ירוק רך לבחירה
            holder.etAmount.setVisibility(View.VISIBLE);
            holder.etAmount.setEnabled(true);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.etAmount.setVisibility(View.GONE);
            holder.etAmount.setEnabled(false);
        }

        // יצירת מאזין לעדכון הכמות בזמן אמת
        holder.amountWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                String text = s.toString().trim();
                int amount = 0;
                if (!text.isEmpty()) {
                    try { amount = Integer.parseInt(text); } catch (Exception ignored) {}
                }
                items.get(pos).setAmount(amount);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.etAmount.addTextChangedListener(holder.amountWatcher);

        // לחיצה על הפריט - שינוי מצב בחירה ועדכון מודל הנתונים
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Item clickedItem = items.get(pos);
            if (isSelectionMode) {
                clickedItem.setSelected(!clickedItem.isSelected());
                if (!clickedItem.isSelected()) clickedItem.setAmount(0);
                notifyItemChanged(pos);
            }
            if (listener != null) listener.onItemClick(clickedItem);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    /**
     * מחלקת עזר השומרת הפניות לרכיבי התצוגה עבור כל שורה ב-RecyclerView.
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageView ivImage;
        EditText etAmount;
        TextWatcher amountWatcher;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            ivImage = itemView.findViewById(R.id.ivItemImage);
            etAmount = itemView.findViewById(R.id.etItemAmount);
        }
    }
}