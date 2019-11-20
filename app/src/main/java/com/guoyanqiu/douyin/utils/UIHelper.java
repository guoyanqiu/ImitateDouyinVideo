package com.guoyanqiu.douyin.utils;

import android.os.Handler;
import android.os.Looper;

public class UIHelper {

    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static final class SyncRunnable implements Runnable {
        private final Runnable mTarget;
        private boolean mComplete;

        public SyncRunnable(Runnable target) {
            mTarget = target;
        }

        @Override
        public void run() {
            mTarget.run();
            synchronized (this) {
                mComplete = true;
                notifyAll();
            }
        }

        public void waitForComplete() {
            synchronized (this) {
                while (!mComplete) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
    public static Handler getHandler() {
        return sHandler;
    }

    public static Thread getUIThread() {
        return Looper.getMainLooper().getThread();
    }

    public static boolean isOnUIThread() {
        return Thread.currentThread() == getUIThread();
    }

    public static void runOnUIThread(Runnable action) {
        if (!isOnUIThread()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    public static void cancel(Runnable runnable){
        sHandler.removeCallbacks(runnable);
    }

    public static void cancelAll() {
        sHandler.removeCallbacksAndMessages(null);
    }

    public static void runOnUIThreadDelay(Runnable action, long delayMillis) {
        sHandler.postDelayed(action, delayMillis);
    }

    /**
     *在UI Thread里面同步执行
     * @param
     */
    public static void runOnUIThreadSync(Runnable action) {
        if (!isOnUIThread()) {
            SyncRunnable sr = new SyncRunnable(action);
            sHandler.post(sr);
            sr.waitForComplete();
        } else {
            action.run();
        }
    }

}
