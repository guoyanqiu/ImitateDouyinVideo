package com.guoyanqiu.douyin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewFlingListener recyclerViewFlingListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerViewFlingListener = new RecyclerViewFlingListener();
        recyclerViewFlingListener.setOnPageChangeListener(new onPageChangeListener() {
            @Override
            public void onPageChange(int currentPage) {
                Toast.makeText(MainActivity.this,"当前是第"+currentPage+"页",Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewFlingListener.bindRecyclerView(recyclerView);
        recyclerView.setAdapter(new RecycleViewAdapter());
    }


}
