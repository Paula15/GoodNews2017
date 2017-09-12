package com.java.no16.ui.newslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.java.no16.R;
import com.java.no16.protos.Category;
import com.java.no16.protos.SimpleNews;
import com.java.no16.supplier.NewsListSupplier;
import com.java.no16.ui.newsdetail.NewsDetailActivity;
import com.java.no16.ui.widget.DividerOffsetDecoration;
import com.java.no16.ui.widget.RecyclerItemClickListener;
import com.java.no16.util.ThreadPool;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;

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

    private final int PAGE_SIZE = 20;
    enum Status { REFRESHING, LOADING, NORMAL }
    private Status mStatus = Status.REFRESHING;

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
        doRefresh();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRepository.addUpdatable(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRepository.removeUpdatable(this);
    }

    @Override
    public void update() {
        mRepository.get().ifFailedSendTo(mThrowableReceiver).ifSucceededSendTo(mReceiver);
    }

    private void doRefresh() {
        mObservable.refreshNews(1, PAGE_SIZE, mCategory);
    }

    private void doLoadMore() {
        int pageNo = mNewsList.size() / PAGE_SIZE + 1;
        mObservable.refreshNews(pageNo, PAGE_SIZE, mCategory);
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
            }

            @Override
            public void loadMore() {
                if (mStatus == Status.NORMAL &&
                        mLayoutManager.findLastCompletelyVisibleItemPosition() == mLayoutManager.getItemCount() - 1) {
                    mStatus = Status.LOADING;
                    doLoadMore();
                }
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
                switch (mStatus) {
                    case NORMAL:
                        break;
                    case REFRESHING:
                        mAdapter.updateData(value);
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
        };

        mThrowableReceiver = new Receiver<Throwable>() {
            @Override
            public void accept(@NonNull Throwable value) {
                Log.e("mThrowableReceiver", "getNewsFailed!");
            }
        };
    }

    private void initAdapter(View view) {
        mNewsList = new ArrayList<SimpleNews>();
        mAdapter = new NewsListAdapter(getActivity(), mNewsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String newsId = mAdapter.getItem(position).getNewsId();
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view.findViewById(R.id.news_image), getString(R.string.transition_news_img));
                intent.putExtra(NewsDetailActivity.NEWS, newsId);
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        }));
    }
}
