package com.guoyanqiu.douyin;

class Blog {
      /*

      SnapHelper源码解析

      想要了解SnapHelper的工作原理，就要知道Android View的滚动原理和RecyclerView的滚动原理，
      刚好博主对这方面写了详细的博客，在阅读本篇博文之前，建议对于View的滚动原理尚不熟悉的猿人们读一下博主的下面几篇博客，算是知识储备，当然不读也基本不影响本片博文的阅读：
  《[View的滚动原理简单解析](https://blog.csdn.net/chunqiuwei/article/details/50679568)》
  《[View的滚动原理简单解析（二）](https://blog.csdn.net/chunqiuwei/article/details/50698054)》
  《[ViewDragHelper的简单分析（一)](https://blog.csdn.net/chunqiuwei/article/details/50778842)》
  《[ViewDragHelper的简单分析及应用（二)](https://blog.csdn.net/chunqiuwei/article/details/50826748)》
  《[RecyclerView的滚动原理](https://yanchen.blog.csdn.net/article/details/79983625)》


  通过《[RecyclerView的滚动原理](https://yanchen.blog.csdn.net/article/details/79983625)》分析可以知道，RecyclerView的滚动有三种状态：



  SCROLL_STATE_IDLE：RecyclerView不再滚动或者停止滚动的状态，当RecyclerView不在滚动或者惯性滚动结束后的状态
  SCROLL_STATE_DRAGGING：RecyclerView随着手指拖动而滚动的状态
  SCROLL_STATE_SETTLING：RecyclerView随着手指的离开而发生惯性滚动状态，也即是fling滚动状态。
   （所谓惯性滚动是指手指离开屏幕后，RecyclerView持续滚动直到停止的滚动，就像骑自行车，双脚离开脚踏后自行车仍然可以因为惯性而保持行驶状态一样）


  但是惯性滚动有一个特点：滚动结束后，停止到什么位置是不确定的，具有随机性！对于一些场景来说是不合适的，比如用过抖音的都知道，是需要让RecyclerView实现一页一页的滚动！

  那么类似抖音的滚动功能是怎么实现的呢？其原理是如何？下面就来掰扯掰扯。

  最核心的思路就是打破RecyclerView的默认惯性滚动，让我们自己来处理惯性滚动！手指离开屏幕是惯性滚动的开始，所以看看RecyerView onTouchEvent的ACTION_UP事件都做了什么：




      case MotionEvent.ACTION_UP: {
          //省略部分代码
          if (!((xvel != 0 || yvel != 0) && fling((int) xvel, (int) yvel))) {
              setScrollState(SCROLL_STATE_IDLE);
          }
      } break;  */

  /**
   代码整体逻辑很简单，就是在手指离开屏幕后调用fling方法执行惯性滚动，惯性滚动结束后,设置滚动状态为SCROLL_STATE_IDLE，并通知客户端滚动结束。

   再来看看fing方法，删除了大量与本文无关的代码之后，可以看出端倪：



   public boolean fling(int velocityX, int velocityY) {

   if (!dispatchNestedPreFling(velocityX, velocityY)) {

   //如果设置了mOnFlingListener并且其onFling方法返回true，则让客户端自己执行惯性滚动
   if (mOnFlingListener != null && mOnFlingListener.onFling(velocityX, velocityY)) {
   return true;
   }

   //让RecyclerView自己执行惯性滚动
   mViewFlinger.fling(velocityX, velocityY);
   return true;
   }
   return false;
   }
   */
  /**

   通过fling方法可以看出RecyclerView是优先执行客户端自己的惯性逻辑的，也即是mOnFlingListener.onFling。如果onFling方法返回发true的话则不会执行RecyclerView
   自己的惯性滚动，也就是不会调用 mViewFlinger.fling方法。

   所以想要实现类似抖音的页面效果，思路就来了，最核心代码就是如下：
   recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
  @Override
  public boolean onFling(int velocityX, int velocityY) {
  return true;
  }
  });

   recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
  @Override
  public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
  if(newState == RecyclerView.SCROLL_STATE_IDLE){
  //   //RecyclerView主动调用这个方法之后，这个方法返回true之后会立即调用OnScrollStateChangedListener的
  //   //onScrollStateChanged方法，此时newState为 SCROLL_STATE_IDLE
  }
  }

  @Override
  public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
  }
  });


   */
}
