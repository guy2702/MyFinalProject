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
 * מחלקת ה-Adapter אחראית על חיבור הנתונים (מתוך רשימה) אל התצוגה (RecyclerView).
 * כאן מתבצעת הלוגיקה של הצגת הפריט, צביעת הרקע לירוק בעת לחיצה, וקבלת הכמות המוקלדת.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final ArrayList<Item> items;
    private final OnItemClickListener listener;

    // משתנה שקובע האם הרשימה נמצאת במצב "בחירה" (בו אפשר לסמן פריטים).
    private boolean isSelectionMode = false;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(ArrayList<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // פונקציה להפעלת מצב בחירה מרחוק (מופעלת למשל מתוך מסך פירות וירקות)
    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }

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

        // המרת תמונה (ממחרוזת Base64 השמורה במסד הנתונים) לתמונה המוצגת על המסך
        try {
            if (item.getPic() != null && !item.getPic().isEmpty()) {
                holder.ivImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));
            } else {
                holder.ivImage.setImageResource(android.R.color.darker_gray);
            }
        } catch (Exception e) {
            holder.ivImage.setImageResource(android.R.color.darker_gray);
        }

        // ניתוק זמני של המאזין להקלדה, כדי שלא יופעל בטעות בזמן שאנחנו טוענים את התצוגה
        if (holder.amountWatcher != null) {
            holder.etAmount.removeTextChangedListener(holder.amountWatcher);
        }

        // טעינת הכמות הקיימת (אם יש) לתוך שדה ההקלדה
        holder.etAmount.setText(item.getAmount() > 0 ? String.valueOf(item.getAmount()) : "");

        // =========================================================================
        // הסבר: כאן מתרחש הקסם של ה"צביעה לירוק והצגת הכמות"!
        // אם מצב הבחירה דלוק (isSelectionMode) והפריט ספציפית סומן (item.isSelected):
        // =========================================================================
        if (isSelectionMode && item.isSelected()) {
            // 1. צובע את רקע השורה לצבע ירוק בהיר (#C8E6C9)
            holder.itemView.setBackgroundColor(Color.parseColor("#C8E6C9"));
            // 2. הופך את שדה הכמות (EditText) לגלוי (VISIBLE) כדי שהמשתמש יוכל להקליד
            holder.etAmount.setVisibility(View.VISIBLE);
            holder.etAmount.setEnabled(true);
        } else {
            // אם הפריט לא מסומן:
            // 1. מחזיר את הרקע ללבן רגיל
            holder.itemView.setBackgroundColor(Color.WHITE);
            // 2. מסתיר את שדה הכמות בחזרה (GONE)
            holder.etAmount.setVisibility(View.GONE);
            holder.etAmount.setEnabled(false);
        }

        // =========================================================================
        // הסבר: "מאזין ההקלדה" (TextWatcher).
        // תפקידו לעקוב בזמן אמת אחרי מה שהמשתמש מקליד בתוך שדה הגרמים.
        // ברגע שהמשתמש מקליד מספר, ה-TextWatcher ישר לוקח את המספר ושומר אותו
        // בתוך אובייקט ה-Item (setAmount).
        // =========================================================================
        holder.amountWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                String text = s.toString().trim();
                int amount = 0;

                if (!text.isEmpty()) {
                    try {
                        amount = Integer.parseInt(text); // הופך את הטקסט המוקלד למספר
                    } catch (Exception ignored) {
                    }
                }

                // שומר את הכמות באובייקט
                items.get(pos).setAmount(amount);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        // מחברים את מאזין ההקלדה בחזרה לשדה
        holder.etAmount.addTextChangedListener(holder.amountWatcher);

        // =========================================================================
        // הסבר: לחיצה על השורה (אירוע Click).
        // מה קורה כשהמשתמש נוגע עם האצבע בפריט ברשימה?
        // =========================================================================
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Item clickedItem = items.get(pos);

            if (isSelectionMode) {
                // הופך את המצב: אם לא היה מסומן - מסמן, ולהפך
                clickedItem.setSelected(!clickedItem.isSelected());

                // אם המשתמש לחץ כדי *לבטל* את הסימון (להוריד את הירוק)
                // אנחנו מאפסים לו גם את הכמות בחזרה ל-0
                if (!clickedItem.isSelected()) {
                    clickedItem.setAmount(0);
                }

                // הפקודה הכי חשובה כאן: מבקשת מה-Adapter לרענן ולצייר את השורה הזו מחדש!
                // זה מה שגורם לקוד למעלה (זה שעושה את הרקע ירוק) לרוץ שוב ולעדכן את המסך באותו רגע.
                notifyItemChanged(pos);
            }

            // מפעיל את מאזין הלחיצות החיצוני (למקרה שמסך האב רוצה לעשות משהו עם הלחיצה)
            if (listener != null) {
                listener.onItemClick(clickedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // המחלקה שמקשרת בין משתני הקוד (TextView, ImageView) לרכיבים בקובץ ה-XML (item_row.xml)
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