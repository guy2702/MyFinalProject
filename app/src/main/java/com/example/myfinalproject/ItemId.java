package com.example.myfinalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfinalproject.Adapter.ItemAdapter;
import com.example.myfinalproject.Utils.ImageUtil;
import com.example.myfinalproject.model.Item;
import com.example.myfinalproject.services.DatabaseService;

import java.net.URL;

public class ItemId extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvName, tvType, tvCalories, tvProtein, tvFat, tvCarbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_id);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivImage = findViewById(R.id.ivItemImageDetail);
        tvName = findViewById(R.id.tvItemNameDetail);
        tvType = findViewById(R.id.tvItemTypeDetail);
        tvCalories = findViewById(R.id.tvItemCaloriesDetail);
        tvProtein = findViewById(R.id.tvItemProteinDetail);
        tvFat = findViewById(R.id.tvItemFatDetail);
        tvCarbs = findViewById(R.id.tvItemCarbsDetail);

        String itemId = getIntent().getStringExtra("itemId");

        DatabaseService.getInstance().getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
            @Override
            public void onCompleted(Item item) {
                tvName.setText(item.getName());
                tvType.setText(item.getType());
                tvCalories.setText("קלוריות: " + item.getCalories());
                tvProtein.setText("חלבון: " + item.getProtein());
                tvFat.setText("שומן: " + item.getFat());
                tvCarbs.setText("פחמימות: " + item.getCarbs());

                // שימוש בפונקציה סטטית לטעינת תמונה
                ivImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
            }
        });
    }
}