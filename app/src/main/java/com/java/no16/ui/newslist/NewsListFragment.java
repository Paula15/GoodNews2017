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
import com.java.no16.protos.NewsDetail;
import com.java.no16.protos.SimpleNews;
import com.java.no16.service.GetNewsDetailService;
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

    Repository<Result<List<SimpleNews>>> mRepository;
    NewsListObservable mObservable;
    Receiver<List<SimpleNews>> mReceiver;
    Receiver<Throwable> mThrowableReceiver;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<SimpleNews> mNewsList;
    private NewsListAdapter mAdapter;
    private PullToRefreshLayout mRefreshLayout;

    private boolean mIsLoading = true;
    private int mPastNewsNum, mCurrentNewsNum, mTotalNewsNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newslist, null);

        initRefreshLayout(view);
        initRecyclerView(view);
        initRepository(view);
        initAdapter(view);
        getLatestData();

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

    private void initRefreshLayout(View view) {
        mRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                getLatestData();
                Log.e("refresh", "REFRESH!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void loadMore() {
                getLatestData();
                Log.e("loadMore", "LOAD!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
                mAdapter.addData(value);
                mRefreshLayout.finishRefresh();
                mRefreshLayout.finishLoadMore();
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
                // TODO(zpzhou)
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

    private void getLatestData() {
        // TODO(zpzhou)
        mObservable.refreshNews();
    }

    private void getHistoryData() {
        // TODO(zpzhou)
        mObservable.refreshNews();
    }
}
