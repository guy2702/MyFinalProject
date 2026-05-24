package com.example.myfinalproject.Adapter;

/**
 * מחלקה זו (Adapter) מנהלת את תצוגת רשימת המשתמשים במערכת (RecyclerView).
 * היא כוללת לוגיקת סינון (Filtering) בזמן אמת ומנגנון עדכון אופטימלי (DiffUtil)
 * כדי לרענן את הרשימה רק בשינויים הרלוונטיים.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.R;
import com.example.myfinalproject.UserDetails;
import com.example.myfinalproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> users;     // רשימת המשתמשים המוצגת כרגע
    private List<User> fullList;  // העתק של הרשימה המלאה לצרכי סינון (Search)

    public UsersAdapter(List<User> users) {
        this.users = new ArrayList<>(users);
        this.fullList = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        // בניית מחרוזת שם מלא וטיפול בערכים ריקים
        String fullName = ((user.getFname() != null ? user.getFname() : "") + " " +
                (user.getLname() != null ? user.getLname() : "")).trim();
        String email = user.getEmail() != null ? user.getEmail() : "";
        String phone = user.getPhone() != null ? user.getPhone() : "";

        holder.tvFullName.setText(fullName);
        holder.tvEmail.setText(email);
        holder.tvPhone.setText(phone);

        // מעבר למסך פרטי משתמש בעת לחיצה
        holder.itemView.setOnClickListener(v -> {
            if (user != null) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserDetails.class);
                intent.putExtra("user", user); // העברת אובייקט המשתמש למסך הבא
                context.startActivity(intent);
            } else {
                Log.e("UsersAdapter", "Clicked user is null at position " + position);
            }
        });
    }

    @Override
    public int getItemCount() { return users.size(); }

    /**
     * פונקציית סינון (Filter) למערכת החיפוש.
     * מסננת את הרשימה לפי שם, אימייל או טלפון.
     */
    public void filter(String query) {
        query = query.toLowerCase().trim();
        List<User> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (User user : fullList) {
                String fullName = ((user.getFname() != null ? user.getFname() : "") +
                        (user.getLname() != null ? user.getLname() : "")).toLowerCase().replace(" ", "");
                String email = user.getEmail() != null ? user.getEmail().toLowerCase().replace(" ", "") : "";
                String phone = user.getPhone() != null ? user.getPhone().toLowerCase().replace(" ", "") : "";

                if (fullName.contains(query) || email.contains(query) || phone.contains(query)) {
                    filteredList.add(user);
                }
            }
        }

        // שימוש ב-DiffUtil לעדכון אנימטיבי וחלק של הרשימה
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new UsersDiffCallback(this.users, filteredList));
        this.users.clear();
        this.users.addAll(filteredList);
        diffResult.dispatchUpdatesTo(this);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvEmail, tvPhone;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }

    /**
     * מחלקה לחישוב ההבדלים בין רשימות עבור DiffUtil.
     */
    static class UsersDiffCallback extends DiffUtil.Callback {
        private final List<User> oldList;
        private final List<User> newList;

        public UsersDiffCallback(List<User> oldList, List<User> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }
        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId()
                    .equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            User oldUser = oldList.get(oldItemPosition);
            User newUser = newList.get(newItemPosition);
            return oldUser.getFname().equals(newUser.getFname()) &&
                    oldUser.getLname().equals(newUser.getLname()) &&
                    oldUser.getEmail().equals(newUser.getEmail()) &&
                    oldUser.getPhone().equals(newUser.getPhone());
        }
    }
}