package com.example.joe.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by joe on 16/7/18.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private List<NewsBean> mlist;       //NewsBean数据
    private LayoutInflater minflater;   //用于加载布局文件
    private ImageLoader mImageLoader;   //图片加载器
    private int mStart,mEnd;            //保存可视范围
    public static String[] URLs;        //保存图片url
    private boolean mFirstLoad;         //判断当前是否第一次启动


    public NewsAdapter(Context context, List<NewsBean> list, ListView listView) {
        //获取LayoutInflater对象
        this.minflater = LayoutInflater.from(context);
        this.mlist = list;
        this.mImageLoader = new ImageLoader(listView);
        this.URLs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            URLs[i] = list.get(i).newsImageRes;
        }
        mFirstLoad = true;//第一次启动
        // 注册滑动事件
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder;
        if(convertView == null){
            //加载布局文件
            convertView = minflater.inflate(R.layout.item,null);
            viewHolder = new viewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.content = (TextView) convertView.findViewById((R.id.tv_content));
            //绑定
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (NewsAdapter.viewHolder) convertView.getTag();
        }
        //作为初始图标
        viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);

        String url = mlist.get(position).newsImageRes;
        //绑定url作为验证
        viewHolder.imageView.setTag(url);

        mImageLoader.showIamge(viewHolder.imageView,url);
        viewHolder.title.setText(mlist.get(position).newsTitle);
        viewHolder.content.setText(mlist.get(position).newsContent);

        return convertView;
    }

    //监听listview滚动状态,滑动时停止加载,停止时开始加载可见项
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE) {     //停止滚动
            mImageLoader.loadImage(mStart, mEnd);
        }else {
            mImageLoader.cancelAllTask();
        }
    }

    //获取当前可视范围中的元素标记
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 第一个可见元素
        mStart = firstVisibleItem;
        //最后一个可见元素
        mEnd = firstVisibleItem+visibleItemCount;
        //预加载第一屏图片,onScroll()初始化时被调用,所以写在该函数中
        if (mFirstLoad && visibleItemCount > 0) {   //第一次加载且可视数量大于0
            mImageLoader.loadImage(mStart,mEnd);
            mFirstLoad = false;
        }
    }

    //封装所有控件
    class viewHolder{
        public ImageView imageView;
        public TextView title;
        public TextView content;
    }
}
