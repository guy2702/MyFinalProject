package com.example.myfinalproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.R;
import com.example.myfinalproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> users;      // הרשימה המוצגת
    private List<User> fullList;   // הרשימה המקורית לשמירה

    public UsersAdapter(List<User> users) {
        this.users = users;
        this.fullList = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemuser, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvFullName.setText(user.getFname() + " " + user.getLname());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // פונקציית סינון גמישה
    public void filter(String query) {
        query = query.toLowerCase().replace(" ", ""); // הסרת רווחים בחיפוש
        users.clear();
        if(query.isEmpty()) {
            users.addAll(fullList);
        } else {
            for(User user : fullList) {
                // הסרת רווחים מהשדות לפני השוואה
                String fullName = (user.getFname() + user.getLname()).toLowerCase().replace(" ", "");
                String email = user.getEmail().toLowerCase().replace(" ", "");
                String phone = user.getPhone().toLowerCase().replace(" ", "");

                if(fullName.contains(query) || email.contains(query) || phone.contains(query)) {
                    users.add(user);
                }
            }
        }
        notifyDataSetChanged();
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
}