package com.example.joe.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by joe on 16/7/18.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    private List<NewsBean> mlist;
    private LayoutInflater minflater;   //用于加载布局文件
    private ImageLoader mImageLoader;


    public NewsAdapter(Context context, List<NewsBean> list) {
        //获取LayoutInflater对象
        this.minflater = LayoutInflater.from(context);
        this.mlist = list;
        this.mImageLoader = new ImageLoader();
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

        //new ImageLoader().showImage(viewHolder.imageView,url);
        mImageLoader.showAsyncTask(viewHolder.imageView,url);
        viewHolder.title.setText(mlist.get(position).newsTitle);
        viewHolder.content.setText(mlist.get(position).newsContent);

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    //封装所有控件
    class viewHolder{
        public ImageView imageView;
        public TextView title;
        public TextView content;
    }
}
