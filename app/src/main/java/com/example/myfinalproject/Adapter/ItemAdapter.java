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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final ArrayList<Item> items;
    private final OnItemClickListener listener;
    private boolean isSelectionMode = false;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(ArrayList<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

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

        holder.tvSugar.setText("סוכר: " + item.getSugar());

        try {
            if (item.getPic() != null && !item.getPic().isEmpty()) {
                holder.ivImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));
            } else {
                holder.ivImage.setImageResource(android.R.color.darker_gray);
            }
        } catch (Exception e) {
            holder.ivImage.setImageResource(android.R.color.darker_gray);
        }

        if (holder.amountWatcher != null) {
            holder.etAmount.removeTextChangedListener(holder.amountWatcher);
        }

        holder.etAmount.setText(item.getAmount() > 0 ? String.valueOf(item.getAmount()) : "");

        if (isSelectionMode && item.isSelected()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#C8E6C9"));

            holder.etAmount.setVisibility(View.VISIBLE);
            holder.etAmount.setEnabled(true);

            holder.tvSugar.setVisibility(View.GONE);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);

            holder.etAmount.setVisibility(View.GONE);
            holder.etAmount.setEnabled(false);

            holder.tvSugar.setVisibility(View.VISIBLE);
        }

        holder.amountWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                String text = s.toString().trim();
                int amount = 0;

                if (!text.isEmpty()) {
                    try {
                        amount = Integer.parseInt(text);
                    } catch (Exception ignored) {}
                }

                items.get(pos).setAmount(amount);
            }

            @Override public void afterTextChanged(Editable s) {}
        };

        holder.etAmount.addTextChangedListener(holder.amountWatcher);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Item clickedItem = items.get(pos);

            if (isSelectionMode) {
                clickedItem.setSelected(!clickedItem.isSelected());

                if (!clickedItem.isSelected()) {
                    clickedItem.setAmount(0);
                }

                notifyItemChanged(pos);
            }

            if (listener != null) {
                listener.onItemClick(clickedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvSugar;
        ImageView ivImage;
        EditText etAmount;

        TextWatcher amountWatcher;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            tvSugar = itemView.findViewById(R.id.tvItemSugar);
            ivImage = itemView.findViewById(R.id.ivItemImage);
            etAmount = itemView.findViewById(R.id.etItemAmount);
        }
    }
}