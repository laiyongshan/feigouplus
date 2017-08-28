package com.example.youhe.youhecheguanjiaplus.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youhe.youhecheguanjiaplus.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * @version 2.20
 * @Description: 上拉加载更多数据
 * @date 2014年6月17日 上午11:10:13
 */
public class RefreshExpandListView extends ExpandableListView implements OnScrollListener {

    private boolean pullRefreshEnable = false;

    private boolean canLoadMore;
    private OnLoadMoreListener onLoadMoreListener;
    private OnSizeChangedListener onSizeChangedListener;
    private View footerParent;

    private WeakReference<SwipeRefreshLayout> mParentLayout;

    private Context mContext;

    private NoMoreHandler mNoMoreHandler;

    private Toast mToast;

    private List<OnScrollListener> scrollListenerList = new ArrayList<OnScrollListener>();
    private boolean canPause = true;

    private enum LoadingState {
        LOADING_STATE_START, LOADING_STATE_FINISH
    }

    private Object lockObject = new Object();

    private LoadingState mCurrentState = LoadingState.LOADING_STATE_FINISH;

    public static enum NoMoreHandler {
        NO_MORE_LOAD_SHOW_FOOTER_VIEW, NO_MORE_LOAD_NOT_SHOW_FOOTER_VIEW, NO_MORE_LOAD_SHOW_TOAST
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshExpandListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context, attrs, 0);
    }

    /**
     *
     */
    public RefreshExpandListView(Context context) {
        super(context);
        this.mContext = context;
        init(context, null, 0);
    }

    public RefreshExpandListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        initFooterView(context);
        super.setOnScrollListener(this);
        /*setOnSwipeListener(new OnSwipeListener() {

			private boolean refreshEnable;
			@Override
			public void onSwipeStart(int position) {
				refreshEnable = pullRefreshEnable;
				if(pullRefreshEnable) {
					setPullRefreshEnable(false);
				}
				
			}
			
			@Override
			public void onSwipeEnd(int position) {
				if(refreshEnable) {
					setPullRefreshEnable(true);
				}
			}
		});*/
    }

    /**
     * @param listener
     * @Description: 下拉刷新事件监听
     */
    public void setOnPullRefreshListener(OnRefreshListener listener) {
        if (mParentLayout == null || mParentLayout.get() != null) {
            initRefreshLayout();
        }
        setPullRefreshEnable(listener != null);
        mParentLayout.get().setOnRefreshListener(listener);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }

    public void removeOnSizeChangedListener() {
        this.onSizeChangedListener = null;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        scrollListenerList.add(l);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }

    /**
     * @Description: 设置是否允许下拉刷新
     */
    public void setPullRefreshEnable(boolean isEnable) {
        pullRefreshEnable = isEnable;
        if (mParentLayout == null || mParentLayout.get() != null) {
            initRefreshLayout();
//			if (pullRefreshEnable) {
            mParentLayout.get().setEnabled(pullRefreshEnable);
//			}
        } else {
            mParentLayout.get().setEnabled(pullRefreshEnable);
        }
    }

    /**
     * @param refreshing
     * @Description: 设置下拉刷新状态
     */
    public void setPullRefreshing(boolean refreshing) {
        if (mParentLayout == null || mParentLayout.get() != null) {
            initRefreshLayout();
        }
        mParentLayout.get().setRefreshing(refreshing);
    }

    private void initRefreshLayout() {
        if (getParent() != null) {
            if (getParent() instanceof SwipeRefreshLayout) {
                mParentLayout = new WeakReference<SwipeRefreshLayout>((SwipeRefreshLayout) getParent());
            } else if (getParent() instanceof ViewGroup) {
                SwipeRefreshLayout refreshLayout = new SwipeRefreshLayout(getContext());
                refreshLayout.setColorSchemeResources(R.color.pull_refresh_color_1, R.color.pull_refresh_color_2, R.color.pull_refresh_color_3);
                if (this.getLayoutParams() != null) {
                    refreshLayout.setLayoutParams(this.getLayoutParams());
                }
                ViewGroup vp = (ViewGroup) getParent();
                for (int i = 0; i < vp.getChildCount(); i++) {
                    if (vp.getChildAt(i) == this) {
                        vp.removeViewAt(i);
                        refreshLayout.addView(this);
                        vp.addView(refreshLayout, i);
                    }
                }
                mParentLayout = new WeakReference<SwipeRefreshLayout>(refreshLayout);
            } else {
                throw new RuntimeException("You must attach this listview to a ViewGroup!!");
            }
            mParentLayout.get().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    onTouchEvent(event);
                    if (event.getPointerCount() > 1
                            && event.getAction() != MotionEvent.ACTION_POINTER_UP
                            && event.getAction() != MotionEvent.ACTION_POINTER_DOWN) {
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    //加载更多的布局

    private LinearLayout ll_load_more;
    private ProgressBar pb;
    private TextView tv;
    private View initFooterView(Context context) {
        footerParent = LayoutInflater.from(context).inflate(R.layout.elv_load_more, null);
        ll_load_more = (LinearLayout) footerParent.findViewById(R.id.ll_load_more);
        pb = (ProgressBar) footerParent.findViewById(R.id.pb);
        tv = (TextView) footerParent.findViewById(R.id.tv);



        //避免代码中由于addFooterView晚于setAdapter，造成footerView不显示
        setFooterDividersEnabled(false);
        this.addFooterView(footerParent);

        this.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        return footerParent;
    }

    public void removeDefaultFooter() {
        if (getFooterViewsCount() > 0 && footerParent != null) {
            removeFooterView(footerParent);
            footerParent = null;
        }
    }

    /**
     * @param onLoadMoreListener
     * @Description: 添加上拉加载数据的监听器，用于在回调方法里加载数据
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        setCanLoadMore(true);
        if (ll_load_more!=null){
            ll_load_more.setVisibility(VISIBLE);
        }
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == view.getCount() - 1) {
            if (this.canLoadMore) {

                if (onLoadMoreListener != null) {
                    if (ll_load_more!=null){
                        ll_load_more.setVisibility(VISIBLE);
                    }
					/*footerPanel.setVisibility(View.VISIBLE);
					footerPanel.setHintText(R.string.loading_wait);
					footerPanel.setAnimImageVisibility(View.VISIBLE);*/

                    synchronized (lockObject) {
                        if (mCurrentState != LoadingState.LOADING_STATE_START) {
                            mCurrentState = LoadingState.LOADING_STATE_START;
                            this.onLoadMoreListener.loadMoreImpl();
                        }
                    }
                }
            } else {
                if (this.mNoMoreHandler == NoMoreHandler.NO_MORE_LOAD_SHOW_TOAST && this.getFirstVisiblePosition() > 0) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(mContext, mContext.getString(R.string.days), Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        }

        if (canPause) {
            if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
//                ImageLoader.getInstance().pause();
            } else {
//                ImageLoader.getInstance().resume();
            }
        }

        for (OnScrollListener listener : scrollListenerList) {
            listener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * @return the canLoadMore
     */
    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    /**
     * @param canLoadMore the canLoadMore to set
     */
    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
        if (!canLoadMore) {

			/*if(this.footerPanel != null){
				this.footerPanel.setOnClickListener(null);
				this.footerPanel.setEnabled(false);
			}*/
        } else {

			/*footerPanel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(onLoadMoreListener != null){
						onLoadMoreListener.loadMoreImpl();
					}
				}
			});*/
        }
    }

    public void onLoadFailed(int tipsResId) {
        this.onLoadCallback();
        if (getFirstVisiblePosition() != 0) {
			/*footerPanel.setVisibility(View.VISIBLE);
			footerPanel.setHintText(tipsResId);
			footerPanel.setAnimImageVisibility(View.GONE);*/
        }
    }

    /**
     * @Description: 加载完数据后, 还有更多时调用该方法
     */
    public void onLoadComplete() {
        this.onLoadCallback();
        setCanLoadMore(true);
	/*	footerPanel.setVisibility(View.VISIBLE);
		footerPanel.setHintText(R.string.pull_up_more);
		footerPanel.setAnimImageVisibility(View.GONE);*/
    }

    /**
     * @param handler
     * @Description: 没有更多时调用该方法
     */
    public void onLoadCompleteNoMore(NoMoreHandler handler) {
        this.onLoadCallback();
        this.mNoMoreHandler = handler;
        switch (handler) {
            case NO_MORE_LOAD_NOT_SHOW_FOOTER_VIEW:
                if (getAdapter() != null && getFooterViewsCount() > 0 && footerParent != null) {
                    removeFooterView(footerParent);
                }
                break;
            case NO_MORE_LOAD_SHOW_FOOTER_VIEW:
                if (ll_load_more!=null){
                    ll_load_more.setVisibility(GONE);
                }
			/*if(footerPanel != null){
				footerPanel.setHintText(R.string.no_more);
				footerPanel.setAnimImageVisibility(View.GONE);
				footerPanel.setVisibility(View.VISIBLE);
			}*/
                break;
            case NO_MORE_LOAD_SHOW_TOAST:
                if (getAdapter() != null && getFooterViewsCount() > 0 && footerParent != null) {
                    removeFooterView(footerParent);
                }
                break;
            default:
                break;
        }
        setCanLoadMore(false);
    }

    private void onLoadCallback() {
        synchronized (lockObject) {
            mCurrentState = LoadingState.LOADING_STATE_FINISH;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        for (OnScrollListener listener : scrollListenerList) {
            listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public static abstract class OnLoadMoreListener {

        private int delayTime;
        private Handler handler = new Handler(Looper.getMainLooper());

        public OnLoadMoreListener(int t) {
            this.delayTime = t;
        }

        public OnLoadMoreListener() {

        }

//		public int getDelayTime() {
//			return delayTime;
//		}
//		

        /**
         * @Description: 在此方法里加载数据，若耗时过久，请异步处理
         */
        public abstract void loadMore();

        void loadMoreImpl() {
            if (delayTime <= 0) {
                loadMore();
            } else {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        loadMore();
                    }
                }, delayTime);
            }
        }
    }

    public interface OnSizeChangedListener {
        public void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    @TargetApi(16)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= 16) {
            this.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        } else {
            this.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
        }
    }

    private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            if (getAdapter() == null) {
                return;
            }

            if (mNoMoreHandler == NoMoreHandler.NO_MORE_LOAD_NOT_SHOW_FOOTER_VIEW || mNoMoreHandler == NoMoreHandler.NO_MORE_LOAD_SHOW_TOAST) {
                if (getFooterViewsCount() > 0 && footerParent != null) {
                    //footerPanel.setVisibility(View.GONE);
                }
                return;
            }

            if (getFirstVisiblePosition() == 0 && getLastVisiblePosition() == getAdapter().getCount() - 1 && !canLoadMore) {
                if (getFooterViewsCount() > 0 && footerParent != null) {
                    //footerPanel.setVisibility(View.GONE);
                }
            }
        }
    };

    public void setCanPause(boolean canPause) {
        this.canPause = canPause;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
