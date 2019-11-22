package com.guoyanqiu.douyin;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.recyclerview.widget.RecyclerView;

import com.guoyanqiu.douyin.utils.UIHelper;

/**
 * 实现RecycleView分页滚动的工具类
 */
public class RecyclerViewFlingListener extends RecyclerView.OnFlingListener {

    private RecyclerView mRecyclerView = null;

    private OnScrollStateChangedListener mOnScrollListener = new OnScrollStateChangedListener();

    //竖直方向滚动的总距离
    private int mTotalScrollY = 0;
    //当前滚动的位置
    private int mStartScrollY = 0;

    private onPageChangeListener mOnPageChangeListener;

    public void bindRecyclerView(RecyclerView recycleView) {
        mRecyclerView = recycleView;
        mRecyclerView.setOnTouchListener(new OnTouchListener());
        scroller = new Scroller(mRecyclerView.getContext());
        //处理滑动
        recycleView.setOnFlingListener(this);
        //设置滚动监听，记录滚动的状态，和总的偏移量
        recycleView.setOnScrollListener(mOnScrollListener);
    }

    public void setTotalScrollY(final int position) {
        //需要延迟,要不然拿不到RecycleView的高度
        UIHelper.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                mTotalScrollY = mRecyclerView.getHeight() * position;
                mStartScrollY = mTotalScrollY;
            }
        }, 100);
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        doFlingByMySelf(velocityY,true);
        return true;
    }


    private class OnScrollStateChangedListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }
            int diff = mTotalScrollY - mStartScrollY;
            //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
            boolean move = Math.abs(diff) > recyclerView.getHeight() / 2;
            int velocityY = 0;
            if (move) {
                velocityY = diff < 0 ? -1 : 1;

            }
            doFlingByMySelf(velocityY,false);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dy!=0){
                mTotalScrollY += dy;
            }
        }
    }

    @Deprecated
    private boolean isFling = false;//暂时用不到
    private void doFlingByMySelf(int velocityY,boolean isFling) {
        this.isFling = isFling;
        //获取开始滚动时所在页面的index
        int currentPage = getCurrentPageIndex();
        if (velocityY < 0) {//上一页
            currentPage--;
        } else if (velocityY > 0) {//下一页
            currentPage++;
        }
        int endY = currentPage * mRecyclerView.getHeight();
        if (endY < 0) {
            endY = 0;
        }
        //剩下的距离
        int scrollDistance = endY-mTotalScrollY;
        scroll(mTotalScrollY, scrollDistance);

    }

    /**
     * 获取即将出来的最新页
     * @return
     */
    private int getNewPageIndex() {
        if (mRecyclerView.getHeight() == 0) {
            return 0;
        }
        return mTotalScrollY / mRecyclerView.getHeight();
    }

    /**
     * 获取当前为第几页
     * @return
     */
    private int getCurrentPageIndex() {
        if (mRecyclerView.getHeight() == 0) {
            //没有宽高无法处理
            return 0;
        }
        return mStartScrollY / mRecyclerView.getHeight();
    }

    public void setOnPageChangeListener(onPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    private Scroller scroller;
    private void scroll(int startY, int dy) {
        scroller.forceFinished(true);
        scroller = new Scroller(mRecyclerView.getContext());
        scroller.startScroll(0, startY, 0, dy, 300);
        autoScroll();
    }

    private void autoScroll() {
        Message msg = Message.obtain();
        mHandler.sendMessage(msg);
    }
    private Handler mHandler = new Handler() {
        int mCurrentPage=0;
        public void handleMessage(Message msg) {
            if (scroller.computeScrollOffset()) {//滚动尚未结束
                //获取已经滚动的位置
                int currentY = scroller.getCurrY();
                mRecyclerView.scrollBy(0, currentY - mTotalScrollY);
                autoScroll();
            } else {
                if (null != mOnPageChangeListener) {
                    int currentPage = getNewPageIndex();
                    if (mCurrentPage != currentPage) {
                        mOnPageChangeListener.onPageChange(currentPage);
                    }
                    mCurrentPage = currentPage;
                }
                //主要是设置mRecyclerView的滚动状态为SCROLL_STATE_IDLE
                mRecyclerView.stopScroll();
                mStartScrollY = mTotalScrollY;
            }
        }
    };

    private class OnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                scroller.forceFinished(true);
                mStartScrollY = mTotalScrollY;
            }
            return false;
        }

    }
}

/**
 所以分页加载的思路就很简单：
 假设RecyclerView的高度为h,且item的高度也是h,那么上次滚动的距离scrollY/h就是当前页数

 */
