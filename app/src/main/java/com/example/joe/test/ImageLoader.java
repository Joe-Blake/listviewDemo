package com.example.joe.test;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import java.util.Set;


/**
 * Created by joe on 16/7/22.
 */
public class ImageLoader {

    private LruCache<String, Bitmap> mCache;    //近期最少使用缓存(底层使用LinkedHashMap保存)
    private ListView mListView;
    private Set<NewsAsyncTask> mTask;           //保存当前任务

    public ImageLoader(ListView listview) {

        mListView = listview;
        mTask = new HashSet<>();

        //最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //设置当前缓存
        int CacheSize = maxMemory / 5;
        mCache = new LruCache<String, Bitmap>(CacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回当前图片大小
                return value.getByteCount();
            }
        };
    }

    //添加bitmap到缓存
    public void addBitmapToCache(String url,Bitmap bitmap) {

        //缓存中未包含此图片
        if (getBitmapFromCache(url) == null) {
            //将bitmap保存到缓存
            mCache.put(url, bitmap);
        }

    }
    //从缓存获取bitmap
    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);
    }

    //通过url获取bitmap
    public Bitmap getBitmapFromURL(String url) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showIamge(ImageView imageView, String url) {

        Bitmap bitmap = getBitmapFromCache(url);
        //检查是否存在缓存
        if (bitmap == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    //加载可视区域的图片
    public void loadImage(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = NewsAdapter.URLs[i];
            Bitmap bitmap = getBitmapFromCache(url);
            //检查是否存在缓存
            if (bitmap == null) {
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            } else {
                // 通过url查找图片缓存
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //停止当前任务
    public void cancelAllTask() {
        if (mTask != null) {
            for (NewsAsyncTask task : mTask) {
                task.cancel(false);
            }
        }

    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String murl;
//        private ImageView mImageView;

        public NewsAsyncTask(String url) {
            this.murl = url;
//            this.mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            String url = params[0];
            Bitmap bitmap = getBitmapFromURL(url);
            //添加到缓存
            if (bitmap != null) {
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(murl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }
}