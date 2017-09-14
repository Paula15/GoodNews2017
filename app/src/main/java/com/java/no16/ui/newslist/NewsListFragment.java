package com.java.no16.ui.newslist;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.java.no16.R;
import com.java.no16.protos.Category;
import com.java.no16.protos.NewsException;
import com.java.no16.protos.SimpleNews;
import com.java.no16.service.CacheService;
import com.java.no16.service.GetNewsListService;
import com.java.no16.supplier.NewsListSupplier;
import com.java.no16.ui.newsdetail.NewsDetailActivity;
import com.java.no16.ui.util.widget.DividerOffsetDecoration;
import com.java.no16.ui.util.widget.RecyclerItemClickListener;
import com.java.no16.util.ThreadPool;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.ViewStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListFragment extends Fragment implements Updatable {

    static private final String KEY_CATEGORY = "category";
    private Category mCategory = Category.ALL;

    Repository<Result<List<SimpleNews>>> mRepository;
    NewsListObservable mObservable;
    Receiver<List<SimpleNews>> mReceiver;
    Receiver<Throwable> mThrowableReceiver;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<SimpleNews> mNewsList;
    private NewsListAdapter mAdapter;
    private PullToRefreshLayout mRefreshLayout;

    private final int PAGE_SIZE = 3;
    enum Status { REFRESHING, LOADING, NORMAL }
    private Status mStatus = Status.REFRESHING;

    private FloatingActionButton mBtnSearch;
    static private String mSearchKey = "";

    static private boolean isFavoriteMode = false;

    public static NewsListFragment newInstance(Category category) {
        NewsListFragment fragment = new NewsListFragment();

        Bundle args = new Bundle();
        args.putSerializable(KEY_CATEGORY, category);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newslist, null);

        initCategory();
        initRefreshLayout(view);
        initRecyclerView(view);
        initRepository(view);
        initAdapter(view);
        initSwipeCallback();
        doRefresh();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRepository.addUpdatable(this);
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mRepository.removeUpdatable(this);
    }

    @Override
    public void update() {
        mRepository.get().ifFailedSendTo(mThrowableReceiver).ifSucceededSendTo(mReceiver);
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "update");
    }

    public void doRefresh() {
        mStatus = Status.REFRESHING;
        mObservable.refreshNews(mSearchKey, 1, PAGE_SIZE, mCategory, isFavoriteMode);
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "doRefresh");
    }

    public void doLoadMore() {
        int pageNo = mNewsList.size() / PAGE_SIZE + 1;
        mObservable.refreshNews(mSearchKey, pageNo, PAGE_SIZE, mCategory, isFavoriteMode);
    }

    public void doSearch(String searchKey) {
        mSearchKey = searchKey;
//        mAdapter.clearItems();
        doRefresh();
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "doSearch");
    }

    public void doFavorite() {
        mSearchKey = "";
//        mAdapter.clearItems();
        setFavoriteMode(true);
        doRefresh();
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "doFavorite");
    }

    public void doHome() {
//        mAdapter.clearItems();
        setFavoriteMode(false);
        doRefresh();
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "doHome");
    }

    public void doShowImage(boolean isShowImage) {
        mAdapter.notifyDataSetChanged();
        //Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "doShowImage");
    }

    private void doReceive(List<SimpleNews> value) {
        mRefreshLayout.showView(ViewStatus.CONTENT_STATUS);
        //Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "receive" + value.size() + mSearchKey);
        switch (mStatus) {
            case NORMAL:
            case REFRESHING:
                mAdapter.updateData(value);
                if (value.isEmpty()) mRefreshLayout.showView(ViewStatus.EMPTY_STATUS);
                break;
            case LOADING:
                mAdapter.addData(value);
                break;
            default:
                break;
        }
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
        mStatus = Status.NORMAL;
    }

    private void initCategory() {
        Bundle args = getArguments();
        mCategory = (args == null) ? Category.ALL : (Category) args.get(KEY_CATEGORY);
    }

    private void initRefreshLayout(View view) {
        mRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                if (mStatus == Status.NORMAL) {
                    mStatus = Status.REFRESHING;
                    doRefresh();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束刷新
                        mRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                if (mStatus == Status.NORMAL &&
                        mLayoutManager.findLastCompletelyVisibleItemPosition() == mLayoutManager.getItemCount() - 1) {
                    mStatus = Status.LOADING;
                    doLoadMore();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束加载更多
                        mRefreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });
    }

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerOffsetDecoration());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
    }

    private void initRepository(View view) {
        NewsListSupplier supplier = new NewsListSupplier();
        mObservable = new NewsListObservable(supplier);
        mRepository = Repositories.repositoryWithInitialValue(Result.<List<SimpleNews>>absent())
                .observe(mObservable)
                .onUpdatesPerLoop()
                .goTo(ThreadPool.executor)
                .thenGetFrom(supplier)
                .compile();

        mReceiver = new Receiver<List<SimpleNews>>() {

            @Override
            public void accept(@NonNull List<SimpleNews> value) {
                Log.e("SUCCESS", "!");
                doReceive(value);
            }
        };

        mThrowableReceiver = new Receiver<Throwable>() {
            @Override
            public void accept(@NonNull Throwable value) {
                Log.e("FAILURE", "!");
                int pageNo = mNewsList.size() / PAGE_SIZE + 1;
                try {
                    List<SimpleNews> offlineNews = GetNewsListService.getOfflineNewsList(pageNo, PAGE_SIZE, mCategory);
                    doReceive(offlineNews);
                    if (!isFavoriteMode) {
                        Toast.makeText(getActivity(), R.string.toast_offline, Toast.LENGTH_SHORT).show();
                    }
                } catch (NewsException e) {
                    mRefreshLayout.showView(ViewStatus.EMPTY_STATUS);
                    if (!isFavoriteMode) {
                        Toast.makeText(getActivity(), R.string.toast_offline_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    private void initAdapter(View view) {
        mNewsList = new ArrayList<SimpleNews>();
        mAdapter = new NewsListAdapter(getActivity(), mNewsList, mCategory);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SimpleNews simpleNews = mAdapter.getItem(position);
                String newsId = simpleNews.getNewsId();
                simpleNews.setMark(true);

                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.news_image), getString(R.string.transition_news_img));
                intent.putExtra(NewsDetailActivity.NEWS, newsId);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

                mAdapter.notifyDataSetChanged();
            }
        }));
    }

    private void initSwipeCallback() {
        //0则不执行拖动或者滑动
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CacheService.setFavorite(mNewsList.get(position).getNewsId(), false);
                mNewsList.remove(position);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //左右滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return isFavoriteMode;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public String getCategory() {
        return mCategory.getName();
    }

    static public void setFavoriteMode(boolean favoriteMode) {
        isFavoriteMode = favoriteMode;
    }

    static public boolean getFavoriteMode() {
        return isFavoriteMode;
    }
}
