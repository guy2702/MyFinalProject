package com.example.myfinalproject.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.R;
import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;

import java.net.URL;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final ArrayList<Item> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(ArrayList<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
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


        holder.ivImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));




        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        ImageView ivImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            ivImage = itemView.findViewById(R.id.ivItemImage);
        }
    }

    // פונקציה סטטית לטעינת תמונה מכל ImageView
    public static void loadImageFromUrl(ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            new Thread(() -> {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                    imageView.post(() -> {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(android.R.color.darker_gray);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    imageView.post(() -> imageView.setImageResource(android.R.color.darker_gray));
                }
            }).start();
        } else {
            imageView.setImageResource(android.R.color.darker_gray);
        }
    }
}