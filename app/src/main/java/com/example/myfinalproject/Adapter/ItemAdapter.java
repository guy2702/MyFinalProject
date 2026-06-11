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
 * --- מחלקת ItemAdapter (תבנית עיצוב Adapter Pattern) ---
 * תפקיד: לשמש כ"גשר" (מתרגם) בין מקור הנתונים (הרשימה בזיכרון) לבין התצוגה (RecyclerView במסך).
 * המחלקה יורשת מ-RecyclerView.Adapter ועובדת עם מודל של ViewHolder כדי לייעל ביצועים.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    // מקור הנתונים (Data Source) - הרשימה שעליה האדפטר מסתכל
    private final ArrayList<Item> items;

    // מאזין לחיצות שמאפשר להעביר אירועים החוצה ל-Activity (Callback)
    private final OnItemClickListener listener;

    // דגל (Flag) שקובע את הסטטוס (State) של הרשימה - האם ניתן לסמן פריטים (למשל בהרכבת שייק)
    private boolean isSelectionMode = false;

    /**
     * Interface (ממשק) - מגדיר "חוזה" מול ה-Activity.
     * כל מי שמשתמש באדפטר יכול לממש את הפונקציה הזו כדי לדעת מתי לחצו על פריט.
     */
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    /**
     * בנאי (Constructor) - מופעל בזמן יצירת האדפטר (new ItemAdapter).
     * כאן אנחנו מכניסים לאדפטר את הרשימה המקורית מה-Activity.
     */
    public ItemAdapter(ArrayList<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * פונקציה לשינוי מצב האדפטר בזמן ריצה (למשל הדלקת אפשרות בחירה מרובה).
     * notifyDataSetChanged() פוקד על המסך למחוק הכל ולצייר מחדש לפי החוקים החדשים.
     */
    public void setSelectionMode(boolean selectionMode) {
        this.isSelectionMode = selectionMode;
        notifyDataSetChanged();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * פעולת חובה 1: יצירת השורה הפיזית (מופעלת רק כמה פעמים בודדות עבור השורות שרואים על המסך).
     * תהליך ה"ניפוח" (Inflate) - לוקח קובץ XML ובונה ממנו אובייקט View בזיכרון.
     */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    /**
     * פעולת חובה 2: חיבור הנתונים לתצוגה (Data Binding).
     * מופעלת עבור כל שורה בנפרד, רגע לפני שהיא נגללת ונכנסת למסך.
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // שליפת אובייקט הנתונים לפי המיקום הנוכחי שלו ברשימה
        Item item = items.get(position);

        // השמת טקסטים תוך הגנה (Defensive Programming) מפני ערכי Null
        holder.tvName.setText(item.getName() != null ? item.getName() : "-");
        holder.tvDescription.setText(item.getType() != null ? item.getType() : "-");

        // המרת תמונה (ממחרוזת Base64 השמורה במסד הנתונים) חזרה לתמונה דיגיטלית מסוג Bitmap
        try {
            if (item.getPic() != null && !item.getPic().isEmpty()) {
                holder.ivImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));
            } else {
                holder.ivImage.setImageResource(android.R.color.darker_gray);
            }
        } catch (Exception e) {
            holder.ivImage.setImageResource(android.R.color.darker_gray);
        }

        // =========================================================================
        // מניעת באגים של מיחזור (Recycling):
        // בגלל ששורות ממוחזרות בזמן גלילה, אנחנו חייבים לנתק מאזינים ישנים
        // כדי שטקסט משורה אחת לא ייכתב בטעות לשורה אחרת בזיכרון.
        // =========================================================================
        if (holder.amountWatcher != null) {
            holder.etAmount.removeTextChangedListener(holder.amountWatcher);
        }

        // טעינת הכמות הקיימת באובייקט לתוך שדה ההקלדה
        holder.etAmount.setText(item.getAmount() > 0 ? String.valueOf(item.getAmount()) : "");

        // =========================================================================
        // לוגיקת תצוגה חזותית - צביעה לירוק והצגת תיבת כמות:
        // אם מצב הבחירה מופעל, וגם הפריט הספציפי הזה מסומן בזיכרון (isSelected)
        // =========================================================================
        if (isSelectionMode && item.isSelected()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#C8E6C9")); // צובע רקע לירוק
            holder.etAmount.setVisibility(View.VISIBLE); // מציג את תיבת הטקסט (כמות)
            holder.etAmount.setEnabled(true);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE); // מחזיר ללבן
            holder.etAmount.setVisibility(View.GONE); // מעלים את תיבת הטקסט
            holder.etAmount.setEnabled(false);
        }

        // =========================================================================
        // מאזין בזמן אמת להקלדת כמויות (TextWatcher):
        // מקשיב למקלדת. כל מספר שמוקלד נשמר מיד בתוך האובייקט (Data Model).
        // =========================================================================
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
                    try {
                        amount = Integer.parseInt(text); // המרת מחרוזת מוקלדת למספר (Integer)
                    } catch (Exception ignored) {}
                }

                // עדכון האובייקט בזיכרון בזמן אמת!
                items.get(pos).setAmount(amount);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // חיבור מאזין ההקלדה לשדה הפיזי שבמסך
        holder.etAmount.addTextChangedListener(holder.amountWatcher);

        // =========================================================================
        // מאזין לחיצה על כל השורה (Click Listener):
        // הקוד כאן מופעל באלפית השנייה שבה האצבע של המשתמש נוגעת בשורה במסך.
        // =========================================================================
        holder.itemView.setOnClickListener(v -> {

            // 1. שמירת המיקום: שואל את המערכת "על איזה מספר שורה (אינדקס) המשתמש לחץ עכשיו?"
            int pos = holder.getAdapterPosition();

            // 2. תכנות מגננתי (Defensive Programming):
            // הגנה מקריסה! אם המשתמש לחץ בדיוק כשהשורה נמחקת או זזה, המיקום יהיה לא חוקי (NO_POSITION).
            // ה-return עוצר את הפעולה מיד ומונע מהאפליקציה לקרוס (Crash).
            if (pos == RecyclerView.NO_POSITION) return;

            // 3. שליפת הפריט: הולך לרשימה בזיכרון ושולף את האובייקט הספציפי לפי המיקום ששמרנו (pos)
            Item clickedItem = items.get(pos);

            // אם אנחנו במצב של הרכבת שייק (מותר לסמן פריטים)
            if (isSelectionMode) {

                // 4. פעולת מתג (Toggle):
                // משנה את התכונה בזיכרון להיפך ממה שהייתה קודם (נבחר <-> לא נבחר).
                // ה-! (NOT) הופך false ל-true ולהיפך.
                clickedItem.setSelected(!clickedItem.isSelected());

                // 5. שמירה על היגיון (איפוס):
                // אם המשתמש לחץ כדי *לבטל* בחירה (הוריד את ה-V), אנחנו מאפסים את הכמות ל-0.
                // זה מונע באג שבו הפריט לא מסומן אבל נשארו לו "50 גרם" בזיכרון.
                if (!clickedItem.isSelected()) {
                    clickedItem.setAmount(0);
                }

                // 6. רענון חכם (Optimization - ייעול ביצועים):
                // במקום לעשות notifyDataSetChanged שימחק ויצייר את *כל* הרשימה מחדש (מעמיס על המעבד),
                // אנחנו אומרים לאדפטר: "תצייר מחדש *רק* את השורה במיקום pos".
                // כשהיא תצויר מחדש, הקוד למעלה יזהה שהיא עכשיו נבחרה, ויצבע אותה מיד בירוק.
                notifyItemChanged(pos);
            }

            // 7. תקשורת החוצה (Callback):
            // אם ה-Activity הראשי העביר לנו "מכשיר קשר" (listener),
            // אנחנו שולחים לו הודעה: "היי, המשתמש לחץ על הפריט הזה, תעשה עם זה מה שאתה רוצה".
            if (listener != null) {
                listener.onItemClick(clickedItem);
            }
        });
    }

    /**
     * פעולת חובה 3: אומרת ל-RecyclerView כמה שורות הוא צריך לייצר בסך הכל.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * --- מחלקת ItemViewHolder ---
     * תפקיד: אופטימיזציה (שיפור ביצועים) - "שומר התצוגה".
     * פעולת findViewById היא כבדה למעבד. ה-ViewHolder עושה אותה רק פעם אחת,
     * ושומר (Cache) את ההפניות לכל רכיבי ה-XML, מה שמאפשר גלילה חלקה של הרשימה.
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