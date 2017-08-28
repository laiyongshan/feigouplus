package com.example.youhe.youhecheguanjiaplus.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.youhe.youhecheguanjiaplus.R;


/**
 * 上拉刷新
 */
public class RefreshListView extends SwipeRefreshLayout {

    private ListView listView;

    private BaseAdapter adapter;
    private View foot;

    private RefreshListener refreshListener;

    //是否正在刷新
    private boolean isRefreshing;

    private boolean canPullUp;

    private int pullUpY;

    private Mode mode;

    public int getHeaderViewsCount() {

        return listView.getHeaderViewsCount();
    }

    public int getFooterViewsCount() {

        return listView.getFooterViewsCount();
    }

    public void addHeaderView(View headerView) {
        listView.addHeaderView(headerView);
    }


    public static enum Mode {
        UP,
        BOTTOM,
        BOTH,
        NONE
    }

    public static interface RefreshListener {

        void onRefreshing(Mode mode);

    }


    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode == null || mode == Mode.NONE) {
            setEnabled(false);
        } else
            setEnabled(true);
    }

    public RefreshListView(Context context) {
        super(context);
        initListView(null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initListView(attrs);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            String s[] = new String[20];
            for (int i = 0; i < s.length; i++) {
                s[i] = "item" + i;
            }
            listView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, s));
            return;
        }


        pullUpY = 200;
        mode = Mode.BOTH;
        int[] list = getResources().getIntArray(R.array.refresh_color);
        setColorSchemeColors(list);
        setProgressViewEndTarget(true, 200);
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                if (refreshListener != null)
                    refreshListener.onRefreshing(Mode.UP);
            }
        });

    }

    public ListView getListView() {
        return listView;
    }

    /**
     * 不用使用这个方法
     *
     * @param listener
     */
    @Deprecated
    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
    }

    public boolean isRefreshing() {
        return isRefreshing && super.isRefreshing();
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    private void initListView(AttributeSet attrs) {


        if (attrs != null)
            listView = new ListView(getContext(), attrs);
        else
            listView = new ListView(getContext());
        addView(listView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        listView.setId(R.id.refreshInnerListView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                         boolean isLastRow = false;

                                         @Override
                                         public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                                             if (mode == null || mode == Mode.NONE)
                                                 return;

                                             if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                                                 isLastRow = true;
                                             }
                                             getParent().requestDisallowInterceptTouchEvent(false);


                                             if (!isRefreshing && (firstVisibleItem == -1 || firstVisibleItem == 0 & (mode == Mode.UP || mode == Mode.BOTH))) {
                                                 setEnabled(true);
                                             } else if (isRefreshing)
                                                 setEnabled(false);
                                         }

                                         @Override
                                         public void onScrollStateChanged(AbsListView view, int scrollState) {

                                             if (mode == null || mode == Mode.NONE)
                                                 return;

                                             setEnabled(true);

                                             if (isLastRow && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isRefreshing && canPullUp && (mode == Mode.BOTTOM || mode == Mode.BOTH)) {
                                                 showFootView();
                                                 isLastRow = false;
                                             }

                                         }
                                     }

        );

        foot = LayoutInflater.from(getContext()).inflate(R.layout.refresh_foot, null);
        listView.addFooterView(foot, null, false);
        listView.setFooterDividersEnabled(false);
        listView.setOnTouchListener(new OnTouchListener() {
            float y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = event.getY();
                        canPullUp = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float ty = event.getY();
                        if (y > ty && y - ty > pullUpY)
                            canPullUp = true;
                        break;


                }
                return false;
            }
        });
        foot.setVisibility(GONE);
    }


    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        listView.setAdapter(adapter);

    }


    public BaseAdapter getAdapter() {
        return adapter;
    }


    public void setOnItemClickListener(AdapterView.OnItemClickListener o) {
        listView.setOnItemClickListener(o);


    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener o) {
        listView.setOnItemLongClickListener(o);

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        listView.setVisibility(visibility);
    }

    private void showFootView() {

        if (!isRefreshing) {

            isRefreshing = true;

            foot.setVisibility(VISIBLE);
            foot.findViewById(R.id.pb).setVisibility(GONE);
            ((TextView) foot.findViewById(R.id.tv)).setText("查看更多");
            foot.postDelayed(new Runnable() {
                @Override
                public void run() {
                    foot.findViewById(R.id.pb).setVisibility(VISIBLE);
                    ((TextView) foot.findViewById(R.id.tv)).setText("正在加载中...");
                }
            }, 500);

            if (refreshListener != null)
                refreshListener.onRefreshing(Mode.BOTTOM);

        }


    }

    public void removeFootView() {
        listView.removeFooterView(foot);
    }

    public void addFootView() {
        listView.addFooterView(foot);
    }

    @Override
    public void setRefreshing(boolean refreshing) {


        super.setRefreshing(refreshing);
        isRefreshing = refreshing;
        if (refreshing) {
            if (refreshListener != null)
                refreshListener.onRefreshing(Mode.UP);
        }
        if (!refreshing) {
//            isRefreshing = true;
            foot.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isRefreshing = false;
                    foot.setVisibility(GONE);
                }
            }, 1000);

        }

    }

    public void setSelection(int position){
        listView.setSelection(position);
    }
    public int getSelectedItemPosition(){
        return listView.getSelectedItemPosition();
    }

    public void setLastRefreshing(boolean refreshing) {
        if (refreshing)
            if (isRefreshing)
                return;

        if (!refreshing) {
            foot.setVisibility(GONE);
        } else
            showFootView();

        isRefreshing = refreshing;
    }


}
