package cn.bingoogolapple.refreshlayout.demo.ui.fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildLongClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.demo.R;
import cn.bingoogolapple.refreshlayout.demo.adapter.NormalRecyclerViewAdapter;
import cn.bingoogolapple.refreshlayout.demo.model.RefreshModel;
import cn.bingoogolapple.refreshlayout.demo.ui.activity.MainActivity;
import cn.bingoogolapple.refreshlayout.demo.util.ThreadUtil;
import cn.bingoogolapple.refreshlayout.demo.util.ToastUtil;
import cn.bingoogolapple.refreshlayout.demo.widget.Divider;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/5/22 10:06
 * 描述:
 */
public class RefreshRecyclerViewFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener {
  private static final String TAG = RefreshRecyclerViewFragment.class.getSimpleName();

  private NormalRecyclerViewAdapter mAdapter;

  private BGARefreshLayout mRefreshLayout;

  private RecyclerView mDataRv;

  private int mNewPageNumber = 0;

  private int mMorePageNumber = 0;

  @Override protected void initView(Bundle savedInstanceState) {
    setContentView(R.layout.fragment_recyclerview_refresh);
    mRefreshLayout = getViewById(R.id.rl_recyclerview_refresh);
    mDataRv = getViewById(R.id.rv_recyclerview_data);
  }

  @Override protected void setListener() {
    mRefreshLayout.setDelegate(this);
    mAdapter = new NormalRecyclerViewAdapter(mDataRv);
    mAdapter.setOnRVItemClickListener(this);
    mAdapter.setOnRVItemLongClickListener(this);
    mAdapter.setOnItemChildClickListener(this);
    mAdapter.setOnItemChildLongClickListener(this);
    mDataRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        Log.i(TAG, "\u6d4b\u8bd5\u81ea\u5b9a\u4e49onScrollStateChanged\u88ab\u8c03\u7528");
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        Log.i(TAG, "\u6d4b\u8bd5\u81ea\u5b9a\u4e49onScrolled\u88ab\u8c03\u7528");
      }
    });
  }

  @Override protected void processLogic(Bundle savedInstanceState) {
    View headerView = View.inflate(mApp, R.layout.view_custom_header2, null);
    headerView.findViewById(R.id.btn_custom_header2_test).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ToastUtil.show("\u70b9\u51fb\u4e86\u6d4b\u8bd5\u6309\u94ae");
      }
    });
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        ((TextView) getViewById(R.id.tv_custom_header2_title)).setText(R.string.test_custom_header_title);
        ((TextView) getViewById(R.id.tv_custom_header2_desc)).setText(R.string.test_custom_header_desc);
      }
    }, 2000);
    mRefreshLayout.setCustomHeaderView(headerView, true);


    mRefreshLayout.setRefreshViewHolder(new BGAMoocStyleRefreshViewHolder(mApp, true));
    mDataRv.addItemDecoration(new Divider(mApp));
    mDataRv.setLayoutManager(new LinearLayoutManager(mApp, LinearLayoutManager.VERTICAL, false));
    mDataRv.setAdapter(mAdapter);
  }

  @Override protected void onUserVisible() {
    mNewPageNumber = 0;
    mMorePageNumber = 0;
    mEngine.loadInitDatas().enqueue(new Callback<List<RefreshModel>>() {
      @Override public void onResponse(Response<List<RefreshModel>> response, Retrofit retrofit) {
        mAdapter.setDatas(response.body());
      }

      @Override public void onFailure(Throwable t) {
      }
    });
  }

  @Override public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
    mNewPageNumber++;
    if (mNewPageNumber > 4) {
      mRefreshLayout.endRefreshing();
      showToast("\u6ca1\u6709\u6700\u65b0\u6570\u636e\u4e86");
      return;
    }
    showLoadingDialog();
    mEngine.loadNewData(mNewPageNumber).enqueue(new Callback<List<RefreshModel>>() {
      @Override public void onResponse(final Response<List<RefreshModel>> response, Retrofit retrofit) {
        ThreadUtil.runInUIThread(new Runnable() {
          @Override public void run() {
            mRefreshLayout.endRefreshing();
            dismissLoadingDialog();
            mAdapter.addNewDatas(response.body());
            mDataRv.smoothScrollToPosition(0);
          }
        }, MainActivity.LOADING_DURATION);
      }

      @Override public void onFailure(Throwable t) {
        mRefreshLayout.endRefreshing();
        dismissLoadingDialog();
      }
    });
  }

  @Override public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
    mMorePageNumber++;
    if (mMorePageNumber > 5) {
      mRefreshLayout.endLoadingMore();
      showToast("\u6ca1\u6709\u66f4\u591a\u6570\u636e\u4e86");
      return false;
    }
    showLoadingDialog();
    mEngine.loadMoreData(mMorePageNumber).enqueue(new Callback<List<RefreshModel>>() {
      @Override public void onResponse(final Response<List<RefreshModel>> response, Retrofit retrofit) {
        ThreadUtil.runInUIThread(new Runnable() {
          @Override public void run() {
            mRefreshLayout.endLoadingMore();
            dismissLoadingDialog();
            mAdapter.addMoreDatas(response.body());
          }
        }, MainActivity.LOADING_DURATION);
      }

      @Override public void onFailure(Throwable t) {
        mRefreshLayout.endLoadingMore();
        dismissLoadingDialog();
      }
    });
    return true;
  }

  @Override public void onItemChildClick(ViewGroup parent, View childView, int position) {
    if (childView.getId() == R.id.tv_item_normal_delete) {
      mAdapter.removeItem(position);
    }
  }

  @Override public boolean onItemChildLongClick(ViewGroup parent, View childView, int position) {
    if (childView.getId() == R.id.tv_item_normal_delete) {
      showToast("\u957f\u6309\u4e86\u5220\u9664 " + mAdapter.getItem(position).title);
      return true;
    }
    return false;
  }

  @Override public void onRVItemClick(ViewGroup parent, View itemView, int position) {
    showToast("\u70b9\u51fb\u4e86\u6761\u76ee " + mAdapter.getItem(position).title);
  }

  @Override public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
    showToast("\u957f\u6309\u4e86\u6761\u76ee " + mAdapter.getItem(position).title);
    return true;
  }
}