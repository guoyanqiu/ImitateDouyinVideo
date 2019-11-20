package com.guoyanqiu.douyin;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Scroller;

import androidx.recyclerview.widget.RecyclerView;

import com.guoyanqiu.douyin.utils.UIHelper;

/**
 * 实现RecycleView分页滚动的工具类
 */
@Deprecated
public class RecyclerViewFlingListener3 extends RecyclerView.OnFlingListener {

    private RecyclerView mRecyclerView = null;

    private OnScrollStateChangedListener mOnScrollListener = new OnScrollStateChangedListener();

    //竖直方向滚动的总距离
    private int mTotalScrollY = 0;
    //上次滚动的总距离
    private int mPreTotalScrollY = 0;
    private onPageChangeListener mOnPageChangeListener;

    public void bindRecyclerView(RecyclerView recycleView) {
        mRecyclerView = recycleView;
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
                mPreTotalScrollY = mTotalScrollY;
            }
        }, 100);
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        Log.e("--", "onFLing");
        //记录滚动开始和结束的位置
        doFlingByMySelf(velocityY);
//
        return true;
    }

    private void doFlingByMySelf(int velocityY){
        final int startY = mTotalScrollY;
        //获取开始滚动时所在页面的index
        int currentPage = getStartPageIndex();
        if (velocityY < 0) {//上一页
            currentPage--;
        } else if (velocityY > 0) {//下一页
            currentPage++;
        }
        int endY = currentPage* mRecyclerView.getHeight();
        if (endY < 0) {
            endY = 0;
        }
        scroll(startY,endY-startY);
    }

    public class OnScrollStateChangedListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //newState==0表示滚动停止，此时需要处理惯性
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.e("--", "onScrollStateChanged 滚动结束");
                    int absY = Math.abs(mTotalScrollY - mPreTotalScrollY);
                    //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
                    boolean move = absY > recyclerView.getHeight() / 2;
                    int velocityY = 0;
                    if (move) {
                        velocityY = mTotalScrollY - mPreTotalScrollY < 0 ? -1000 : 1000;
                    }
                    doFlingByMySelf(velocityY);
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mTotalScrollY += dy;
        }
    }

    private int getPageIndex() {
        if (mRecyclerView.getHeight() == 0) {
            return 0;
        }
        return mTotalScrollY / mRecyclerView.getHeight();
    }

    private int getStartPageIndex() {
        if (mRecyclerView.getHeight() == 0) {
            //没有宽高无法处理
            return 0;
        }
        return mPreTotalScrollY / mRecyclerView.getHeight();
    }

    public void setOnPageChangeListener(onPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public interface onPageChangeListener {
        void onPageChange(int currentPage);
    }

    private Scroller scroller;

    private void scroll(int startY,int dy) {
        mHandler.removeCallbacksAndMessages(null);
        scroller.forceFinished(true);
        scroller = new Scroller(mRecyclerView.getContext());
        scroller.startScroll(0, startY, 0, dy, 300);
        isStopByMySelf=false;
        autoScroll();
    }

    private void autoScroll() {
        Message msg = Message.obtain();
        msg.arg1 = 0;
        mHandler.sendMessage(msg);
    }

    private boolean isStopByMySelf = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (scroller.computeScrollOffset()) {//滚动尚未结束

                //获取已经滚动的位置
                int currentY = scroller.getCurrY();
                mRecyclerView.scrollBy(0, currentY-mTotalScrollY);
                autoScroll();
            }else{
                isStopByMySelf=true;
                Log.e("--", "scrollBy滚动结束");
                mRecyclerView.stopScroll();
                mPreTotalScrollY=mTotalScrollY;
            }
        }

        ;
    };

}
