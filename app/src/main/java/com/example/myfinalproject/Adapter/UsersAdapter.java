package com.example.myfinalproject.Adapter;

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

    private List<User> users;
    private List<User> fullList;

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

        String fullName = ((user.getFname() != null ? user.getFname() : "") + " " +
                (user.getLname() != null ? user.getLname() : "")).trim();
        String email = user.getEmail() != null ? user.getEmail() : "";
        String phone = user.getPhone() != null ? user.getPhone() : "";

        holder.tvFullName.setText(fullName);
        holder.tvEmail.setText(email);
        holder.tvPhone.setText(phone);

        // לחיצה על פריט פותחת את UserDetails
        holder.itemView.setOnClickListener(v -> {
            if (user != null) {
                Context context = v.getContext();
                Intent intent = new Intent(context, UserDetails.class);
                intent.putExtra("user", user); // שולחים את האובייקט כולו
                context.startActivity(intent);
            } else {
                Log.e("UsersAdapter", "Clicked user is null at position " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // חיפוש
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
