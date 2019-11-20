package com.guoyanqiu.douyin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Message;
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
    //上次滚动的总距离
    private int mPreTotalScrollY = 0;
    private ValueAnimator mAnimator = null;
    private onPageChangeListener mOnPageChangeListener;

    public void bindRecyclerView(RecyclerView recycleView) {
        mRecyclerView = recycleView;
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

    private void initAnimator(int startPoint, int endPoint) {
        mAnimator = new ValueAnimator().ofInt(startPoint, endPoint);
        mAnimator.setDuration(300);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int nowPoint = (int) animation.getAnimatedValue();
                int dy = nowPoint - mTotalScrollY;
                //这里通过RecyclerView的scrollBy方法实现滚动。
                mRecyclerView.scrollBy(0, dy);

            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            int mPrePageIndex = 0;

            @Override
            public void onAnimationEnd(Animator animation) {
                //回调监听
                if (null != mOnPageChangeListener) {
                    int currentPage = getPageIndex();
                    if (mPrePageIndex != currentPage) {
                        //currentPage > mPrePageIndex 说明是向上滑动
                        mOnPageChangeListener.onPageChange(currentPage);
                    }
                    mPrePageIndex = currentPage;
                }

                mRecyclerView.stopScroll();
                mPreTotalScrollY = mTotalScrollY;
            }
        });
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        //记录滚动开始和结束的位置
        final int startPoint = mTotalScrollY;
        //获取开始滚动时所在页面的index
        int currentPage = getStartPageIndex();
        if (velocityY < 0) {//上一页
            currentPage--;
        } else if (velocityY > 0) {//下一页
            currentPage++;
        }

        //更具不同的速度判断需要滚动的方向
        //注意，此处有一个技巧，就是当速度为0的时候就滚动会开始的页面，即实现页面复位
        int endPoint = currentPage* mRecyclerView.getHeight();
        if (endPoint < 0) {
            endPoint = 0;
        }

//        //使用动画处理滚动
        if (mAnimator == null) {
            initAnimator(startPoint, endPoint);
        } else {
            mAnimator.cancel();
            mAnimator.setIntValues(startPoint, endPoint);
        }

        mAnimator.start();
        //RecyclerView主动调用这个方法之后，这个方法返回true之后会立即调用OnScrollStateChangedListener的
        //onScrollStateChanged方法，此时newState为 SCROLL_STATE_IDLE
        return true;
    }


    public class OnScrollStateChangedListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //newState==0表示滚动停止，此时需要处理惯性
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int absY = Math.abs(mTotalScrollY - mPreTotalScrollY);
                //如果滑动的距离超过屏幕的一半表示需要滑动到下一页
                boolean move = absY > recyclerView.getHeight() / 2;
                int velocityY = 0;
                if (move) {
                    velocityY = mTotalScrollY - mPreTotalScrollY < 0 ? -1000 : 1000;
                }
                onFling(0, velocityY);
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


}
