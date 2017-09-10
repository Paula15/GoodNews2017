package com.java.no16.ui.newslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;

import com.google.android.agera.Receiver;
import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.java.no16.R;
import com.java.no16.protos.SimpleNews;
import com.java.no16.supplier.NewsListSupplier;
import com.java.no16.ui.widget.DividerOffsetDecoration;
import com.java.no16.ui.widget.PullToRefreshLayout;
import com.java.no16.ui.widget.RecyclerItemClickListener;
import com.java.no16.ui.widget.RefreshLayout;
import com.java.no16.util.ThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou9 on 2017/9/9.
 */

public class NewsListFragment extends Fragment implements Updatable {

    Repository<Result<List<SimpleNews>>> repository;
    NewsListObservable observable;
    Receiver<List<SimpleNews>> receiver;
    Receiver<Throwable> throwableReceiver;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<SimpleNews> mNewsList;
    private NewsListAdapter mAdapter;
    private ProgressBar mProgressBar;
    RefreshLayout refreshLayout;

    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newslist, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        initRefreshView(view);
        initRecyclerView(view);
        initRepository(view);
        initAdapter(view);
        getLatestData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        repository.addUpdatable(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        repository.removeUpdatable(this);
    }

    @Override
    public void update() {
        mProgressBar.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        repository.get().ifFailedSendTo(throwableReceiver).ifSucceededSendTo(receiver);
    }

    private void initRefreshView(View view) {
        refreshLayout = (RefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow);
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLatestData();
            }
        });
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                getHistoryData();
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
        observable = new NewsListObservable(supplier);
        repository = Repositories.repositoryWithInitialValue(Result.<List<SimpleNews>>absent())
                .observe(observable)
                .onUpdatesPerLoop()
                .goTo(ThreadPool.executor)
                .thenGetFrom(supplier)
                .compile();

        receiver = new Receiver<List<SimpleNews>>() {

            @Override
            public void accept(@NonNull List<SimpleNews> value) {
                if(page > 1){
                    mAdapter.addData(value);
                } else{
                    mAdapter.updateData(value);
                }
            }
        };

        throwableReceiver = new Receiver<Throwable>() {
            @Override
            public void accept(@NonNull Throwable value) {
                Log.e("throwableReceiver", "getNewsFailed!");
            }
        };
    }

    private void initAdapter(View view) {
        mNewsList = new ArrayList<SimpleNews>();
        mAdapter = new NewsListAdapter(getActivity(), mNewsList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // TODO(zpzhou)
            }
        }));
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            // TODO(zpzhou)
        }
    };

    private void getLatestData() {
        // TODO(zpzhou)
        observable.refreshNews();
        page = 0;
    }

    private void getHistoryData() {
        // TODO(zpzhou)
        observable.refreshNews();
        page += 1;
    }
}
