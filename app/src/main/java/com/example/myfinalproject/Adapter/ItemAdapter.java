package com.example.myfinalproject.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinalproject.R;
import com.example.myfinalproject.model.Item;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final ArrayList<Item> items;

    public ItemAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);

        holder.tvName.setText(item.getName() != null ? item.getName() : "-");
        holder.tvDescription.setText(item.getType() != null ? item.getType() : "-");

        // טיפול בכל הערכים המספריים, גם אם הם 0
        holder.tvCalories.setText(item.getCalories() > 0 ? item.getCalories() + " קלוריות" : "-");
        holder.tvProtein.setText(item.getProtein() > 0 ? item.getProtein() + " גרם חלבון" : "-");
        holder.tvFat.setText(item.getFat() > 0 ? item.getFat() + " גרם שומן" : "-");
        holder.tvCarbs.setText(item.getCarbs() > 0 ? item.getCarbs() + " גרם פחמימות" : "-");

        // טעינת תמונה מה-URL
        if (item.getPic() != null && !item.getPic().isEmpty()) {
            new LoadImageTask(holder.ivImage).execute(item.getPic());
        } else {
            // אם אין תמונה, נשתמש בתמונה ברירת מחדל
            holder.ivImage.setImageResource(android.R.color.darker_gray);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvCalories, tvProtein, tvFat, tvCarbs;
        ImageView ivImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            tvCalories = itemView.findViewById(R.id.tvItemCalories);
            tvProtein = itemView.findViewById(R.id.tvItemProtein);
            tvFat = itemView.findViewById(R.id.tvItemFat);
            tvCarbs = itemView.findViewById(R.id.tvItemCarbs);
            ivImage = itemView.findViewById(R.id.ivItemImage);
        }
    }

    // AsyncTask להורדת תמונה מה-URL
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlStr = strings[0];
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(android.R.color.darker_gray);
            }
        }
    }
}
