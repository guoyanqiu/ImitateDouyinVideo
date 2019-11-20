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
        //记录滚动开始和结束的位置
        doFlingByMySelf(velocityY);
//        resetPosition(endPoint);
        //RecyclerView主动调用这个方法之后，这个方法返回true之后会立即调用OnScrollStateChangedListener的
        //onScrollStateChanged方法，此时newState为 SCROLL_STATE_IDLE
        return true;
    }

    private void doFlingByMySelf(int velocityY){
        final int startY = mTotalScrollY;
        //获取开始滚动时所在页面的index
        int currentPage = getStartPageIndex();
        if (velocityY < 0) {//上一页
            currentPage--;
            Log.e("--", "上一页currentPage"+currentPage);
        } else if (velocityY > 0) {//下一页
            currentPage++;
            Log.e("--", "下一页"+currentPage);
        }

        if(currentPage<0){
            currentPage=0;
        }

        int endY = currentPage* mRecyclerView.getHeight();
        if (endY < 0) {
            endY = 0;
        }
        Log.e("--", "startY==="+startY + "   endY=="+endY);

        scroll(endY-startY);
    }

    public class OnScrollStateChangedListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //newState==0表示滚动停止，此时需要处理惯性
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.e("--", "fling滚动结束");
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

    private void scroll(int dy) {
        mHandler.removeCallbacksAndMessages(null);
        scroller.abortAnimation();
        scroller = new Scroller(mRecyclerView.getContext());
        scroller.startScroll(0, mTotalScrollY, 0, dy, 300);
        autoScroll();
    }

    private void autoScroll() {
        Message msg = Message.obtain();
        msg.arg1 = 0;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (scroller.computeScrollOffset()) {//滚动尚未结束

                //获取已经滚动的位置
                int currentY = scroller.getCurrY();
                Log.e("--", "autoStrollView=="+currentY);
                mRecyclerView.scrollBy(0, currentY-mTotalScrollY);
                autoScroll();
            }else{
                Log.e("--", "scrollBy滚动结束");
                mRecyclerView.stopScroll();
                mPreTotalScrollY=mTotalScrollY;
            }
        }

        ;
    };

}
