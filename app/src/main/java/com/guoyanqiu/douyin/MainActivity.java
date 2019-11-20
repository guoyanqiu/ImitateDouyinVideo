package com.guoyanqiu.douyin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewFlingListener3 recyclerViewFlingListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerViewFlingListener = new RecyclerViewFlingListener3();
        recyclerViewFlingListener.setOnPageChangeListener(new RecyclerViewFlingListener3.onPageChangeListener() {
            @Override
            public void onPageChange(int currentPage) {
                Toast.makeText(MainActivity.this,"当前是第"+currentPage+"页",Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewFlingListener.bindRecyclerView(recyclerView);
        recyclerView.setAdapter(new RecycleViewAdapter());
    }


}
