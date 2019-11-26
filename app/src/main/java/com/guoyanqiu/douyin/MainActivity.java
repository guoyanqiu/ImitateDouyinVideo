package com.guoyanqiu.douyin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new RecycleViewAdapter());
        test2();
    }

    /**
     *使用PageSnapHelperl
     */
    private void test2() {
        final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentPage = -1;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(newState== RecyclerView.SCROLL_STATE_IDLE){//如果滚动结束
                    View snapView = pagerSnapHelper.findSnapView(linearLayoutManager);
                    int currentPageIndex = linearLayoutManager.getPosition(snapView);
                    if(currentPage!=currentPageIndex){
                        currentPage = currentPageIndex;
                        Toast.makeText(MainActivity.this, "当前是第" + currentPageIndex + "页", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * 自定义分页
     */
    private void test1() {
        RecyclerViewFlingListener recyclerViewFlingListener = new RecyclerViewFlingListener();
        recyclerViewFlingListener.setOnPageChangeListener(new onPageChangeListener() {
            @Override
            public void onPageChange(int currentPage) {
                Toast.makeText(MainActivity.this, "当前是第" + currentPage + "页", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewFlingListener.bindRecyclerView(recyclerView);
    }

}
