package com.jiyouliang.fmap.ui.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.jiyouliang.fmap.R;
import com.jiyouliang.fmap.ui.BaseActivity;
import com.jiyouliang.fmap.util.LogUtil;
import com.jiyouliang.fmap.util.StatusBarUtils;
import com.jiyouliang.fmap.view.widget.SettingItemView;
import com.jiyouliang.fmap.view.widget.TopTitleView;

/**
 * 用户详情页
 */
public class UserDetailActivity extends BaseActivity {

    private static final String TAG = "UserDetailActivity";

    private RecyclerView mRecycleView;
    /**
     * 头部图片
     */
    private static final int TYPE_HEADER = 0;

    /**
     * 点击登录部分
     */
    private static final int TYPE_LOGIN = 1;

    /**
     * 收藏夹、离线地图等工具
     */
    private static final int TYPE_FAVORITE = 2;

    /**
     * 我的成就、勋章
     */
    private static final int TYPE_MEDAL = 3;

    /**
     * 数据贡献
     */
    private static final int TYPE_DATA_CONTRIBUTE = 4;

    /**
     * 常规列
     */
    private static final int TYPE_NORMAL = 5;
    private TopTitleView mTopTitleView;
    private int mTitleHeight;
    private int mTotalDy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initView();
        initData();
    }

    private void initView() {
        mRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mTopTitleView = (TopTitleView) findViewById(R.id.ttv);

        StatusBarUtils.getInstance().enableTranslucentStatusBar(this, mTitleHeight);
        setListener();
    }

    private void setListener() {
        mTopTitleView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTitleHeight = mTopTitleView.getMeasuredHeight();
            }
        });

        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalDy -= dy;
                log(String.format("onScrolled, dy=%s, mTotalDy=%s", dy, mTotalDy));

                int t = Math.abs(mTotalDy);
                if (t <= 0) {
                    //顶部图处于最顶部，标题栏透明
                    mTopTitleView.setBackgroundColor(Color.argb(0, 255, 255, 255));
                    getWindow().setStatusBarColor(Color.argb((int) 0, 255, 255, 255));
                    mTopTitleView.setRightDrawable(R.drawable.user_detail_setting_light_icon);
                    mTopTitleView.setLeftDrawable(R.drawable.user_detail_close_light_icon);
                } else if (t > 0 && t < mTitleHeight) {
                    //滑动过程中，渐变
                    float scale = (float) t / mTitleHeight;//算出滑动距离比例
                    float alpha = (255 * scale);//得到透明度
                    log(String.format("alpha=%s", alpha));
                    mTopTitleView.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                    getWindow().setStatusBarColor(Color.argb((int) alpha, 255, 255, 255));
                    // 标题栏深色icon
                    mTopTitleView.setRightDrawable(R.drawable.user_detail_setting_black_icon);
                    mTopTitleView.setLeftDrawable(R.drawable.user_detail_close_black_icon);
                } else {
                    //过顶部图区域，标题栏定色
                    mTopTitleView.setBackgroundColor(Color.argb(255, 255, 255, 255));
                    getWindow().setStatusBarColor(Color.argb(255, 255, 255, 255));
                    mTopTitleView.setRightDrawable(R.drawable.user_detail_setting_black_icon);
                    mTopTitleView.setLeftDrawable(R.drawable.user_detail_close_black_icon);
                }
            }
        });
    }

    private void initData() {
        UserDetailAdapter adapter = new UserDetailAdapter();
        mRecycleView.setAdapter(adapter);
    }

    /**
     * 用户详情页Adapter
     */
    private static class UserDetailAdapter extends RecyclerView.Adapter<UserDetailViewHolder> {

        private static final String[] TITLES = new String[]{"我是商家", "我的反馈", "我的订单", "我的钱包", "我的小程序", "我的评论", "特别鸣谢", "帮助中心"};
        private static final String[] SUBTITLES = new String[]{"【免费】新增地点、认领店铺", "", "查看我的全部订单", "", "", "", "感谢高德热心用户", ""};

        private Context mContext;

        @NonNull
        @Override
        public UserDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            this.mContext = parent.getContext();
            int viewType = getItemViewType(position);
            View itemView = null;
            switch (viewType) {
                case TYPE_HEADER:
                    itemView = inflateLayout(parent, R.layout.user_detail_header_recycle_item);
                    break;
                case TYPE_LOGIN:
                    itemView = inflateLayout(parent, R.layout.user_detail_login_recycle_item);
                    break;
                case TYPE_FAVORITE:
                    itemView = inflateLayout(parent, R.layout.user_detail_fav_tools_recycle_item);
                    break;
                case TYPE_MEDAL:
                    itemView = inflateLayout(parent, R.layout.user_detail_medal_level_recycle_item);
                    break;
                case TYPE_DATA_CONTRIBUTE:
                    itemView = inflateLayout(parent, R.layout.user_detail_data_contribute_recycle_item);
                    break;
                default:
                    itemView = inflateLayout(parent, R.layout.user_detail_normal_recycle_item);
                    break;
            }

            return new UserDetailViewHolder(mContext, itemView, viewType);
        }

        /**
         * 布局生成View
         *
         * @param parent
         * @param resId
         * @return
         */
        private View inflateLayout(ViewGroup parent, int resId) {
            return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(resId, parent, false);
        }

        @Override
        public void onBindViewHolder(@NonNull UserDetailViewHolder viewHolder, int position) {
            //int viewType = getItemViewType(position);
            // 末尾常规项
            if (position >= 5) {
                String title = TITLES[position - 5];
                String subtitle = SUBTITLES[position - 5];
                if (TextUtils.isEmpty(subtitle)) {
                    viewHolder.mSettingItemView.setSubTitleVisiable(false);
                } else {
                    viewHolder.mSettingItemView.setSubTitleVisiable(true);
                    viewHolder.mSettingItemView.setSubTitleText(subtitle);
                }
                viewHolder.mSettingItemView.setTitleText(title);
            }
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 5 + TITLES.length;
        }
    }

    /**
     * ViewHolder
     */
    private static class UserDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int mViewType;
        private Context mContext;
        ImageView ivLogo;
        SettingItemView mSettingItemView;

        private UserDetailViewHolder(Context context, @NonNull View itemView, int viewType) {
            super(itemView);
            this.mContext = context;
            mViewType = viewType;
            switch (viewType) {
                case TYPE_HEADER:
                    break;
                case TYPE_LOGIN:
                    ivLogo = itemView.findViewById(R.id.iv_user_logo);
                    itemView.setOnClickListener(this);
                    break;
                case TYPE_FAVORITE:
                    break;
                case TYPE_MEDAL:
                    break;
                case TYPE_DATA_CONTRIBUTE:
                    break;
                default:
                    //常规列
                    mSettingItemView = itemView.findViewById(R.id.siv);
                    if (mSettingItemView != null && mSettingItemView.getParent() != null) {
                        ((ViewGroup) mSettingItemView.getParent()).setOnClickListener(this);
                    }
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            if (v != null && mViewType == TYPE_LOGIN) {
                showUserLoginPage();
            }
        }

        private void showUserLoginPage() {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        }
    }

    private void log(String msg){
        LogUtil.d(TAG, msg);
    }


}
