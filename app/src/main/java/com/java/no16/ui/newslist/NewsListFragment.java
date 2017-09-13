package com.java.no16.ui.newslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.java.no16.ui.util.widget.DividerOffsetDecoration;
import com.java.no16.ui.util.widget.RecyclerItemClickListener;
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

    private final int PAGE_SIZE = 15;
    enum Status { REFRESHING, LOADING, NORMAL }
    private Status mStatus = Status.REFRESHING;

    private FloatingActionButton mBtnSearch;
    private String mSearchKey = "";

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
        initButtons(view);
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
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "update\n");
//        Log.e("", "--------------------------------------------------------------------------");
    }

    private void doRefresh() {
        mObservable.refreshNews(mSearchKey, 1, PAGE_SIZE, mCategory);
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "doRefresh");
    }

    private void doLoadMore() {
        int pageNo = mNewsList.size() / PAGE_SIZE + 1;
        mObservable.refreshNews(mSearchKey, pageNo, PAGE_SIZE, mCategory);
    }

    private void doSearch(String searchKey) {
        mSearchKey = searchKey;
        mAdapter.clearItems();
        mStatus = Status.REFRESHING;
        doRefresh();
        Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "doSearch");
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
                Log.e("@" + Thread.currentThread().getName() + " => " + mCategory.getName(), "receive" + value.size() + mSearchKey);
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

    private void initButtons(View view) {
        mBtnSearch = (FloatingActionButton) view.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.title_search)
                        .widgetColorRes(R.color.colorPrimary)
                        .positiveColorRes(R.color.colorPrimary)
                        .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                if (!TextUtils.isEmpty(input)) {
                                    doSearch(input.toString());
                                } else {
                                    doSearch("");
                                }
                            }
                        }).show();
            }
        });
    }

}
